package egovframework.resve;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.egovframe.rte.fdl.cmmn.exception.EgovBizException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import egovframework.cmm.LoginVO;
import egovframework.cmm.TimeUtil;
import egovframework.resve.service.ResveService;
import egovframework.resve.service.ResveVO;

/**
 * 실제 Oracle(FCLTY 스키마)에 붙는 통합 테스트 공통 부분.
 * 테스트가 만든 데이터는 회원 아이디 접두어 zt_, 시설 등록자 ztest 로 구분해 매번 지운다.
 */
@SpringJUnitConfig(locations = "classpath*:egovframework/spring/context-*.xml")
public abstract class ResveTestSupport {

	protected static final String TEST_REGISTER = "ztest";

	@Resource(name = "resveService")
	protected ResveService resveService;

	@Resource(name = "dataSource")
	protected DataSource dataSource;

	protected JdbcTemplate jdbc;

	/** 신청 가능한 날짜 (3일 뒤) */
	protected String resveDe;

	@BeforeEach
	void setUpSupport() {
		jdbc = new JdbcTemplate(dataSource);
		cleanUp();
		resveDe = TimeUtil.toDe(LocalDate.now(ZoneId.of("Asia/Seoul")).plusDays(3));
	}

	@AfterEach
	void tearDownSupport() {
		cleanUp();
	}

	private void cleanUp() {
		String testResve = "SELECT RESVE_SN FROM TB_RESVE WHERE MBER_ID LIKE 'zt\\_%' ESCAPE '\\' "
				+ "OR FCLTY_SN IN (SELECT FCLTY_SN FROM TB_FCLTY WHERE REGISTER_ID = '" + TEST_REGISTER + "')";
		jdbc.update("DELETE FROM TB_RESVE_HIST WHERE RESVE_SN IN (" + testResve + ")");
		jdbc.update("DELETE FROM TB_RESVE WHERE RESVE_SN IN (" + testResve + ")");
		jdbc.update("DELETE FROM TB_FCLTY_CLOSE WHERE FCLTY_SN IN (SELECT FCLTY_SN FROM TB_FCLTY WHERE REGISTER_ID = ?)", TEST_REGISTER);
		jdbc.update("DELETE FROM TB_FCLTY WHERE REGISTER_ID = ?", TEST_REGISTER);
		jdbc.update("DELETE FROM TB_MBER WHERE MBER_ID LIKE 'zt\\_%' ESCAPE '\\'");
	}

	/** 09:00~22:00, 60분 단위, 수용 100명 시설 */
	protected long createFclty(String name) {
		long sn = jdbc.queryForObject("SELECT SEQ_FCLTY.NEXTVAL FROM DUAL", Long.class);
		jdbc.update("INSERT INTO TB_FCLTY (FCLTY_SN, FCLTY_TY_CD, FCLTY_NM, ADRES, ACMD_NMPR, OPER_BEGIN_TM, OPER_END_TM, "
				+ "RESVE_UNIT_MIN, REGISTER_ID) VALUES (?, '01', ?, '테스트 주소', 100, '0900', '2200', 60, ?)",
				sn, "[TEST] " + name, TEST_REGISTER);
		return sn;
	}

	protected List<Long> createFclties(int count) {
		List<Long> sns = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			sns.add(createFclty("시설" + i));
		}
		return sns;
	}

	protected String createMber(String suffix) {
		String id = "zt_" + suffix;
		jdbc.update("INSERT INTO TB_MBER (MBER_ID, PASSWORD, MBER_NM, AUTHOR_CD) VALUES (?, 'x', '테스트', 'ROLE_USER')", id);
		return id;
	}

	protected LoginVO login(String mberId, boolean admin) {
		LoginVO vo = new LoginVO();
		vo.setMberId(mberId);
		vo.setMberNm("테스트");
		vo.setAuthorCd(admin ? LoginVO.ROLE_ADMIN : LoginVO.ROLE_USER);
		return vo;
	}

	protected ResveVO req(long fcltySn, String mberId, String begin, String end) {
		ResveVO vo = new ResveVO();
		vo.setFcltySn(fcltySn);
		vo.setMberId(mberId);
		vo.setResveDe(resveDe);
		vo.setBeginTm(begin);
		vo.setEndTm(end);
		vo.setUseNmpr(5);
		vo.setUsePurps("동호회 연습");
		return vo;
	}

	/** 신청·승인 상태로 남아 있는 예약 수 */
	protected int countActive(long fcltySn) {
		return jdbc.queryForObject("SELECT COUNT(*) FROM TB_RESVE WHERE FCLTY_SN = ? AND RESVE_DE = ? "
				+ "AND RESVE_STTUS_CD IN ('01', '02')", Integer.class, fcltySn, resveDe);
	}

	protected static String messageKey(Throwable t) {
		return t instanceof EgovBizException ? ((EgovBizException) t).getMessageKey() : t.getClass().getName();
	}
}
