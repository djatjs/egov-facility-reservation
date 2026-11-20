package egovframework.resve;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import egovframework.resve.ConcurrentRunner.Result;
import egovframework.resve.service.ResveVO;
import egovframework.resve.service.impl.ResveMapper;

/**
 * 측정용 (기본 테스트 실행에서 제외: 이름이 *Test 가 아님).
 * <pre>
 * mvnw test -Dtest=ResveRaceBench -Dsurefire.failIfNoSpecifiedTests=false
 * </pre>
 * 세 가지 구현을 같은 조건으로 10회씩 돌려 성공 건수를 비교한다.
 * <ol>
 * <li>락 없음: 조회 후 INSERT (검사 로직은 같고 FOR UPDATE만 뺀 것)</li>
 * <li>락 + 별도 커넥션 채번: 표준 ID 생성 서비스(EgovSequenceIdGnrServiceImpl)가 하는 것처럼
 *     트랜잭션 안에서 dataSource.getConnection()으로 새 커넥션을 받아 시퀀스를 읽는 방식 (커밋 b7d2b1e)</li>
 * <li>락 + 같은 커넥션 채번: 현재 코드 (ResveServiceImpl, selectKey)</li>
 * </ol>
 * 조건: Oracle 21c XE 로컬, 커넥션 풀 최대 20, 대기 최대 5초.
 */
class ResveRaceBench extends ResveTestSupport {

	private static final int ROUNDS = 10;

	@Resource(name = "resveMapper")
	private ResveMapper resveMapper;

	@Resource(name = "txManager")
	private DataSourceTransactionManager txManager;

	private int rowNo;

	interface Inserter {
		void insert(ResveVO vo) throws Exception;
	}

	@Test
	void compare() throws Exception {
		TransactionTemplate tx = new TransactionTemplate(txManager);

		Inserter noLock = vo -> tx.executeWithoutResult(s -> {
			if (resveMapper.countOverlap(vo) > 0) {
				throw new IllegalStateException("resve.duplicate");
			}
			if (resveMapper.selectMberDailyMinutes(vo.getMberId(), vo.getResveDe()) + 60 > 120) {
				throw new IllegalStateException("resve.daily.limit");
			}
			vo.setResveSttusCd("01");
			resveMapper.insertResve(vo);
		});

		Inserter lockSeparateConn = vo -> tx.executeWithoutResult(s -> {
			resveMapper.lockMber(vo.getMberId());
			resveMapper.lockFclty(vo.getFcltySn());
			if (resveMapper.countOverlap(vo) > 0) {
				throw new IllegalStateException("resve.duplicate");
			}
			if (resveMapper.selectMberDailyMinutes(vo.getMberId(), vo.getResveDe()) + 60 > 120) {
				throw new IllegalStateException("resve.daily.limit");
			}
			try (Connection c = dataSource.getConnection(); Statement st = c.createStatement();
					ResultSet rs = st.executeQuery("SELECT SEQ_RESVE.NEXTVAL FROM DUAL")) {
				rs.next(); // 별도 커넥션으로 채번 (값은 버린다: 측정 대상은 커넥션 대기)
			} catch (Exception e) {
				throw new IllegalStateException("pool.timeout");
			}
			vo.setResveSttusCd("01");
			resveMapper.insertResve(vo);
		});

		Inserter current = vo -> resveService.insertResve(vo);

		System.out.println("| 구현 | 경합① 같은 시간 100명 (정답 1) | 경합② 1명이 10개 시설 (정답 2) |");
		System.out.println("| --- | --- | --- |");
		row("락 없음", noLock);
		row("락 + 별도 커넥션 채번", lockSeparateConn);
		row("락 + 같은 커넥션 채번 (현재)", current);
	}

	private void row(String name, Inserter inserter) throws Exception {
		List<Integer> r1 = new ArrayList<>(), r2 = new ArrayList<>();
		List<String> notes = new ArrayList<>();
		rowNo++;
		for (int round = 0; round < ROUNDS; round++) {
			Result a = sameSlot(inserter, round);
			Result b = dailyLimit(inserter, round);
			r1.add(a.success);
			r2.add(b.success);
			notes.add(a.failures + " / " + b.failures + " (" + a.elapsedMs + "ms, " + b.elapsedMs + "ms)");
			resetData();
		}
		System.out.println("| " + name + " | " + summary(r1) + " | " + summary(r2) + " |");
		notes.forEach(n -> System.out.println("    " + n));
	}

	private Result sameSlot(Inserter inserter, int round) throws Exception {
		long fclty = createFclty("벤치①-" + round);
		List<ConcurrentRunner.Task> tasks = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			String mber = createMber(rowNo + "b" + round + "_" + i);
			tasks.add(() -> inserter.insert(req(fclty, mber, "1000", "1100")));
		}
		return ConcurrentRunner.run(tasks, ResveRaceBench::key);
	}

	private Result dailyLimit(Inserter inserter, int round) throws Exception {
		List<Long> fclties = createFclties(10);
		String mber = createMber(rowNo + "l" + round);
		List<ConcurrentRunner.Task> tasks = new ArrayList<>();
		for (long fclty : fclties) {
			tasks.add(() -> inserter.insert(req(fclty, mber, "1400", "1500")));
		}
		return ConcurrentRunner.run(tasks, ResveRaceBench::key);
	}

	private void resetData() {
		jdbc.update("DELETE FROM TB_RESVE_HIST WHERE RESVE_SN IN (SELECT RESVE_SN FROM TB_RESVE WHERE MBER_ID LIKE 'zt\\_%' ESCAPE '\\')");
		jdbc.update("DELETE FROM TB_RESVE WHERE MBER_ID LIKE 'zt\\_%' ESCAPE '\\'");
	}

	private static String key(Throwable t) {
		if (t instanceof IllegalStateException && t.getMessage() != null) {
			return t.getMessage();
		}
		String k = messageKey(t);
		return k.startsWith("resve.") ? k : k.substring(k.lastIndexOf('.') + 1);
	}

	/** 10회 결과를 "최소~최대 (평균)"으로 */
	private static String summary(List<Integer> xs) {
		int min = xs.stream().mapToInt(i -> i).min().orElse(0);
		int max = xs.stream().mapToInt(i -> i).max().orElse(0);
		double avg = xs.stream().mapToInt(i -> i).average().orElse(0);
		return "성공 " + min + "~" + max + "건 (평균 " + String.format("%.1f", avg) + ") " + xs;
	}
}
