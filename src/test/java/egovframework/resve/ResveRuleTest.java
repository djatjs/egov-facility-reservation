package egovframework.resve;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import egovframework.cmm.TimeUtil;
import egovframework.resve.service.ResveVO;

/** 예약 업무 규칙과 데이터 단위 인가 (실제 Oracle) */
class ResveRuleTest extends ResveTestSupport {

	private void assertBiz(String key, Executable call) {
		Throwable t = assertThrows(Exception.class, call);
		assertEquals(key, messageKey(t));
	}

	@Test
	@DisplayName("부분만 겹쳐도 거절, 끝과 시작이 맞닿으면 허용")
	void overlapBoundary() throws Exception {
		long f = createFclty("경계");
		String a = createMber("a"), b = createMber("b"), c = createMber("c");
		resveService.insertResve(req(f, a, "1000", "1200"));

		assertBiz("resve.duplicate", () -> resveService.insertResve(req(f, b, "1100", "1300")));
		assertDoesNotThrow(() -> resveService.insertResve(req(f, b, "1200", "1400")));
		assertDoesNotThrow(() -> resveService.insertResve(req(f, c, "0900", "1000")));
	}

	@Test
	@DisplayName("취소·반려된 예약 시간은 다시 신청할 수 있다")
	void cancelledSlotIsReusable() throws Exception {
		long f = createFclty("재신청");
		String a = createMber("a"), b = createMber("b");
		long sn = resveService.insertResve(req(f, a, "1000", "1200"));
		resveService.cancelResve(sn, login(a, false));

		assertDoesNotThrow(() -> resveService.insertResve(req(f, b, "1000", "1200")));
	}

	@Test
	@DisplayName("예약일은 내일부터 30일 이내")
	void dateRange() throws Exception {
		long f = createFclty("날짜");
		String a = createMber("a");
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
		for (LocalDate d : new LocalDate[] { today, today.minusDays(1), today.plusDays(31) }) {
			ResveVO vo = req(f, a, "1000", "1100");
			vo.setResveDe(TimeUtil.toDe(d));
			assertBiz("resve.invalid.date", () -> resveService.insertResve(vo));
		}
		ResveVO wrong = req(f, a, "1000", "1100");
		wrong.setResveDe("20260231");
		assertBiz("resve.invalid.date", () -> resveService.insertResve(wrong));
	}

	@Test
	@DisplayName("운영시간 밖, 예약 단위에 안 맞는 시간, 정원 초과는 거절")
	void timeAndCapacity() throws Exception {
		long f = createFclty("시간");
		String a = createMber("a");
		assertBiz("resve.invalid.time", () -> resveService.insertResve(req(f, a, "0800", "1000")));
		assertBiz("resve.invalid.time", () -> resveService.insertResve(req(f, a, "2100", "2300")));
		assertBiz("resve.invalid.time", () -> resveService.insertResve(req(f, a, "1030", "1130")));
		assertBiz("resve.invalid.time", () -> resveService.insertResve(req(f, a, "1200", "1000")));

		ResveVO many = req(f, a, "1000", "1100");
		many.setUseNmpr(101);
		assertBiz("resve.invalid.nmpr", () -> resveService.insertResve(many));
	}

	@Test
	@DisplayName("1인 일일 한도 2시간: 같은 날 다른 시설이라도 합산")
	void dailyLimitSequential() throws Exception {
		long f1 = createFclty("한도1"), f2 = createFclty("한도2");
		String a = createMber("a");
		resveService.insertResve(req(f1, a, "1000", "1100"));
		resveService.insertResve(req(f2, a, "1300", "1400"));
		assertBiz("resve.daily.limit", () -> resveService.insertResve(req(f2, a, "1500", "1600")));
	}

	@Test
	@DisplayName("남의 예약은 조회·취소할 수 없다 (예약번호만 바꾸는 접근 차단). 관리자는 조회 가능")
	void ownershipCheck() throws Exception {
		long f = createFclty("인가");
		String owner = createMber("own"), other = createMber("oth");
		long sn = resveService.insertResve(req(f, owner, "1000", "1100"));

		assertBiz("resve.forbidden", () -> resveService.selectResve(sn, login(other, false)));
		assertBiz("resve.forbidden", () -> resveService.cancelResve(sn, login(other, false)));
		assertDoesNotThrow(() -> resveService.selectResve(sn, login("admin", true)));
		assertEquals(1, countActive(f));
	}

	@Test
	@DisplayName("대시보드·다가오는 예약: 신청은 승인 대기에, 승인은 처리 건수와 다가오는 예약에 반영")
	void dashboardAndUpcoming() throws Exception {
		long f = createFclty("대시보드");
		String a = createMber("a");
		int pendingBefore = resveService.selectDashboard().getPendingCnt();
		int approveBefore = resveService.selectDashboard().getMonthApproveCnt();

		long s1 = resveService.insertResve(req(f, a, "1000", "1100"));
		assertEquals(pendingBefore + 1, resveService.selectDashboard().getPendingCnt());
		assertEquals(true, resveService.selectPendingResveList(100).stream().anyMatch(r -> r.getResveSn() == s1));

		resveService.approveResve(s1, login("admin", true));
		assertEquals(pendingBefore, resveService.selectDashboard().getPendingCnt());
		assertEquals(approveBefore + 1, resveService.selectDashboard().getMonthApproveCnt());
		assertEquals(s1, resveService.selectUpcomingResveList(a).get(0).getResveSn());
	}

	@Test
	@DisplayName("상태 전이:신청→승인은 한 번만, 반려는 사유 필수, 처리된 예약은 다시 처리 불가, 이력이 남는다")
	void sttusTransition() throws Exception {
		long f = createFclty("상태");
		String a = createMber("a"), b = createMber("b");
		long s1 = resveService.insertResve(req(f, a, "1000", "1100"));
		long s2 = resveService.insertResve(req(f, b, "1300", "1400"));

		resveService.approveResve(s1, login("admin", true));
		assertBiz("resve.already.processed", () -> resveService.approveResve(s1, login("admin", true)));
		assertBiz("resve.already.processed", () -> resveService.rejectResve(s1, "사유", login("admin", true)));

		assertBiz("resve.reject.reason", () -> resveService.rejectResve(s2, " ", login("admin", true)));
		resveService.rejectResve(s2, "단체 행사와 겹칩니다", login("admin", true));
		assertBiz("resve.already.processed", () -> resveService.cancelResve(s2, login(b, false)));

		resveService.cancelResve(s1, login(a, false)); // 승인된 예약도 전날까지는 취소 가능
		assertEquals(3, resveService.selectResveHistList(s1).size()); // 신청 → 승인 → 취소
		assertEquals(2, resveService.selectResveHistList(s2).size()); // 신청 → 반려
	}
}
