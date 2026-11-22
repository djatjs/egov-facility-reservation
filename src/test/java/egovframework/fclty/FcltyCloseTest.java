package egovframework.fclty;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import egovframework.cmm.TimeUtil;
import egovframework.fclty.service.FcltyCloseVO;
import egovframework.fclty.service.FcltyService;
import egovframework.resve.ConcurrentRunner;
import egovframework.resve.ResveTestSupport;
import egovframework.resve.service.ResveVO;

// SR-03: 휴관일에는 예약을 받지 않는다
public class FcltyCloseTest extends ResveTestSupport {

	@Resource(name = "fcltyService")
	private FcltyService fcltyService;

	private FcltyCloseVO close(long fcltySn, String de, String rsn) {
		FcltyCloseVO vo = new FcltyCloseVO();
		vo.setFcltySn(fcltySn);
		vo.setCloseDe(de);
		vo.setCloseRsn(rsn);
		vo.setRegisterId(TEST_REGISTER);
		return vo;
	}

	private void assertBiz(String key, Executable call) {
		Throwable t = assertThrows(Exception.class, call);
		assertEquals(key, messageKey(t));
	}

	private int closeCount(long fcltySn) {
		return jdbc.queryForObject("SELECT COUNT(*) FROM TB_FCLTY_CLOSE WHERE FCLTY_SN = ?", Integer.class, fcltySn);
	}

	@Test
	@DisplayName("휴관일에는 예약이 거부되고, 다른 날은 예약된다")
	void closedDayRejectsReservation() throws Exception {
		long f = createFclty("휴관");
		String a = createMber("a");
		fcltyService.insertClose(close(f, resveDe, "정기 점검"));

		assertBiz("resve.closed", () -> resveService.insertResve(req(f, a, "1000", "1100")));

		ResveVO otherDay = req(f, a, "1000", "1100");
		otherDay.setResveDe(TimeUtil.toDe(LocalDate.now(ZoneId.of("Asia/Seoul")).plusDays(4)));
		assertDoesNotThrow(() -> resveService.insertResve(otherDay));
	}

	@Test
	@DisplayName("그날 신청·승인 예약이 있으면 휴관일 등록 거부, 예약을 취소하면 등록된다")
	void activeReservationBlocksClose() throws Exception {
		long f = createFclty("예약있음");
		String a = createMber("a");
		long sn = resveService.insertResve(req(f, a, "1000", "1100"));

		assertBiz("fclty.close.has.resve", () -> fcltyService.insertClose(close(f, resveDe, "점검")));
		assertEquals(0, closeCount(f));

		resveService.cancelResve(sn, login(a, false));
		assertDoesNotThrow(() -> fcltyService.insertClose(close(f, resveDe, "점검")));
	}

	@Test
	@DisplayName("중복 날짜, 오늘·과거·366일 뒤, 빈 사유·101자 사유는 거부")
	void validation() throws Exception {
		long f = createFclty("검증");
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
		fcltyService.insertClose(close(f, resveDe, "점검"));

		assertBiz("fclty.close.duplicate", () -> fcltyService.insertClose(close(f, resveDe, "다시")));
		for (LocalDate d : new LocalDate[] { today, today.minusDays(1), today.plusDays(366) }) {
			assertBiz("fclty.close.invalid.date", () -> fcltyService.insertClose(close(f, TimeUtil.toDe(d), "점검")));
		}
		String next = TimeUtil.toDe(today.plusDays(5));
		assertBiz("fclty.close.invalid.reason", () -> fcltyService.insertClose(close(f, next, "  ")));
		assertBiz("fclty.close.invalid.reason", () -> fcltyService.insertClose(close(f, next, "가".repeat(101))));
		assertDoesNotThrow(() -> fcltyService.insertClose(close(f, next, "가".repeat(100))));
	}

	@Test
	@DisplayName("앞으로의 휴관일을 삭제하면 다시 예약할 수 있다")
	void deleteReopens() throws Exception {
		long f = createFclty("삭제");
		String a = createMber("a");
		FcltyCloseVO c = close(f, resveDe, "점검");
		fcltyService.insertClose(c);

		fcltyService.deleteClose(c.getCloseSn());
		assertDoesNotThrow(() -> resveService.insertResve(req(f, a, "1000", "1100")));
		assertBiz("fclty.close.not.deletable", () -> fcltyService.deleteClose(c.getCloseSn()));
	}

	@RepeatedTest(5)
	@DisplayName("동시성: 휴관일 등록과 예약 신청 20건이 동시에 와도 '휴관일인데 예약이 남는' 상태가 생기지 않는다")
	void closeVsReservationsRace() throws Exception {
		long f = createFclty("동시성-휴관");
		String[] slots = { "0900", "1000", "1100", "1200", "1300", "1400", "1500", "1600", "1700", "1800" };
		List<ConcurrentRunner.Task> tasks = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			String mber = createMber("c" + i);
			String begin = slots[i % slots.length];
			String end = TimeUtil.toHhmi(TimeUtil.toMinutes(begin) + 60);
			tasks.add(() -> resveService.insertResve(req(f, mber, begin, end)));
		}
		AtomicReference<String> closeResult = new AtomicReference<>("등록됨");
		tasks.add(10, () -> {
			try {
				fcltyService.insertClose(close(f, resveDe, "긴급 점검"));
			} catch (Exception e) {
				closeResult.set(messageKey(e));
				throw e;
			}
		});

		ConcurrentRunner.Result r = ConcurrentRunner.run(tasks, ResveTestSupport::messageKey);
		int closes = closeCount(f);
		int active = countActive(f);
		System.out.println("[휴관 경합] 휴관일 " + closeResult.get() + ", 남은 예약 " + active + "건, " + r);

		// 둘 중 하나만 성립: 휴관일이 등록됐으면 예약 0건, 아니면 예약이 있어서 휴관일 등록이 거부됨
		assertTrue(closes == 0 || active == 0, "휴관일과 예약이 함께 남으면 안 됨");
		if (closes == 0) {
			assertEquals("fclty.close.has.resve", closeResult.get());
		}
	}
}
