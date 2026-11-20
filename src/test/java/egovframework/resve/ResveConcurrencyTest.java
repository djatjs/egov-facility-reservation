package egovframework.resve;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import egovframework.resve.ConcurrentRunner.Result;

/**
 * 동시 신청 경합 테스트 (실제 Oracle, 커넥션 풀 최대 20개).
 * 락이 없을 때의 결과는 NaiveResveBench 로 따로 측정한다.
 */
class ResveConcurrencyTest extends ResveTestSupport {

	@RepeatedTest(3)
	@DisplayName("경합①: 100명이 같은 시설·같은 시간에 동시에 신청하면 1건만 성공한다")
	void sameSlot_onlyOneSucceeds() throws Exception {
		long fclty = createFclty("동시성-같은시간");
		List<ConcurrentRunner.Task> tasks = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			String mber = createMber("s" + i);
			tasks.add(() -> resveService.insertResve(req(fclty, mber, "1000", "1200")));
		}

		Result r = ConcurrentRunner.run(tasks, ResveTestSupport::messageKey);
		System.out.println("[경합①] " + r);

		assertEquals(1, r.success);
		assertEquals(99, r.failures.getOrDefault("resve.duplicate", 0));
		assertEquals(1, countActive(fclty));
	}

	@RepeatedTest(3)
	@DisplayName("경합①-부분겹침: 10~12시, 11~13시, 11~12시 신청이 섞여 동시에 들어와도 겹치는 예약은 생기지 않는다")
	void partialOverlap_noOverlapStored() throws Exception {
		long fclty = createFclty("동시성-부분겹침");
		String[][] slots = { { "1000", "1200" }, { "1100", "1300" }, { "1100", "1200" } };
		List<ConcurrentRunner.Task> tasks = new ArrayList<>();
		for (int i = 0; i < 90; i++) {
			String mber = createMber("p" + i);
			String[] slot = slots[i % 3];
			tasks.add(() -> resveService.insertResve(req(fclty, mber, slot[0], slot[1])));
		}

		Result r = ConcurrentRunner.run(tasks, ResveTestSupport::messageKey);
		System.out.println("[경합①-부분겹침] " + r);

		// 세 구간은 서로 모두 겹치므로 셋 중 하나만 남아야 한다
		assertEquals(1, r.success);
		int overlapping = jdbc.queryForObject("SELECT COUNT(*) FROM TB_RESVE A JOIN TB_RESVE B "
				+ "ON A.FCLTY_SN = B.FCLTY_SN AND A.RESVE_DE = B.RESVE_DE AND A.RESVE_SN < B.RESVE_SN "
				+ "AND A.BEGIN_TM < B.END_TM AND A.END_TM > B.BEGIN_TM "
				+ "WHERE A.FCLTY_SN = ? AND A.RESVE_STTUS_CD IN ('01','02') AND B.RESVE_STTUS_CD IN ('01','02')",
				Integer.class, fclty);
		assertEquals(0, overlapping);
	}

	@RepeatedTest(3)
	@DisplayName("경합②: 한 사람이 서로 다른 시설 10곳에 1시간씩 동시에 신청하면 일일 한도(2시간)만큼만 성공한다")
	void dailyLimit_oneMemberManyFacilities() throws Exception {
		List<Long> fclties = createFclties(10);
		String mber = createMber("limit");
		List<ConcurrentRunner.Task> tasks = new ArrayList<>();
		for (long fclty : fclties) {
			tasks.add(() -> resveService.insertResve(req(fclty, mber, "1400", "1500")));
		}

		Result r = ConcurrentRunner.run(tasks, ResveTestSupport::messageKey);
		System.out.println("[경합②] " + r);

		assertEquals(2, r.success);
		assertEquals(8, r.failures.getOrDefault("resve.daily.limit", 0));
		int minutes = jdbc.queryForObject("SELECT COUNT(*) * 60 FROM TB_RESVE WHERE MBER_ID = ? AND RESVE_DE = ? "
				+ "AND RESVE_STTUS_CD IN ('01','02')", Integer.class, mber, resveDe);
		assertEquals(120, minutes);
	}

	@RepeatedTest(3)
	@DisplayName("락 범위 확인: 서로 다른 사람이 서로 다른 시설에 동시에 신청하면 모두 성공한다")
	void differentMembersDifferentFacilities_allSucceed() throws Exception {
		List<Long> fclties = createFclties(20);
		List<ConcurrentRunner.Task> tasks = new ArrayList<>();
		for (int i = 0; i < fclties.size(); i++) {
			long fclty = fclties.get(i);
			String mber = createMber("d" + i);
			tasks.add(() -> resveService.insertResve(req(fclty, mber, "1000", "1200")));
		}

		Result r = ConcurrentRunner.run(tasks, ResveTestSupport::messageKey);
		System.out.println("[락 범위] " + r);

		assertEquals(20, r.success);
	}
}
