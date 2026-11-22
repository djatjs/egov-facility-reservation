package egovframework.cmm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import egovframework.cmm.service.CmmnCodeService;
import egovframework.cmm.service.CmmnCodeVO;
import egovframework.resve.ResveTestSupport;

// SR-01: 시설 유형 표시 이름 수정
public class CmmnCodeServiceTest extends ResveTestSupport {

	@Resource(name = "cmmnCodeService")
	private CmmnCodeService cmmnCodeService;

	/** 테스트가 바꾼 이름을 되돌리기 위한 원래 이름 */
	private List<CmmnCodeVO> original;

	@BeforeEach
	void remember() {
		original = jdbc.query("SELECT CODE_ID, CODE, CODE_NM FROM TB_CMMN_CODE",
				(rs, i) -> {
					CmmnCodeVO vo = new CmmnCodeVO();
					vo.setCodeId(rs.getString(1));
					vo.setCode(rs.getString(2));
					vo.setCodeNm(rs.getString(3));
					return vo;
				});
	}

	@AfterEach
	void restore() {
		for (CmmnCodeVO vo : original) {
			jdbc.update("UPDATE TB_CMMN_CODE SET CODE_NM = ? WHERE CODE_ID = ? AND CODE = ?", vo.getCodeNm(), vo.getCodeId(), vo.getCode());
		}
	}

	private String nameOf(String codeId, String code) {
		return jdbc.queryForObject("SELECT CODE_NM FROM TB_CMMN_CODE WHERE CODE_ID = ? AND CODE = ?", String.class, codeId, code);
	}

	private void assertBiz(String key, Executable call) {
		Throwable t = assertThrows(Exception.class, call);
		assertEquals(key, messageKey(t));
	}

	@Test
	@DisplayName("이름만 바뀌고 코드와 시설 연결은 그대로다 (앞뒤 공백 제거)")
	void rename() throws Exception {
		int before = jdbc.queryForObject("SELECT COUNT(*) FROM TB_FCLTY WHERE FCLTY_TY_CD = '01'", Integer.class);

		cmmnCodeService.updateFcltyTyNm("01", "  실내체육관  ");

		assertEquals("실내체육관", nameOf(CmmnCodeService.FCLTY_TY, "01"));
		assertEquals(before, jdbc.queryForObject("SELECT COUNT(*) FROM TB_FCLTY WHERE FCLTY_TY_CD = '01'", Integer.class));
		assertEquals("실내체육관", cmmnCodeService.selectCodeList(CmmnCodeService.FCLTY_TY).get(0).getCodeNm()); // 캐시 없이 바로 반영
	}

	@Test
	@DisplayName("현재 이름 그대로 저장은 허용, 다른 유형과 같은 이름은 거부")
	void duplicate() throws Exception {
		String current = nameOf(CmmnCodeService.FCLTY_TY, "01");
		cmmnCodeService.updateFcltyTyNm("01", current);

		String other = nameOf(CmmnCodeService.FCLTY_TY, "02");
		assertBiz("cmmn.code.duplicate.name", () -> cmmnCodeService.updateFcltyTyNm("01", other));
	}

	@Test
	@DisplayName("빈 값·공백만·21자는 거부, 20자는 허용")
	void length() throws Exception {
		assertBiz("cmmn.code.invalid.name", () -> cmmnCodeService.updateFcltyTyNm("01", null));
		assertBiz("cmmn.code.invalid.name", () -> cmmnCodeService.updateFcltyTyNm("01", "   "));
		assertBiz("cmmn.code.invalid.name", () -> cmmnCodeService.updateFcltyTyNm("01", "가".repeat(21)));
		cmmnCodeService.updateFcltyTyNm("01", "가".repeat(20));
		assertEquals("가".repeat(20), nameOf(CmmnCodeService.FCLTY_TY, "01"));
	}

	@Test
	@DisplayName("없는 유형 코드는 거부하고, 예약 상태 코드 이름은 바뀌지 않는다")
	void scope() {
		assertBiz("info.nodata.msg", () -> cmmnCodeService.updateFcltyTyNm("99", "없는유형"));

		String sttus = nameOf(CmmnCodeService.RESVE_STTUS, "01");
		// 예약 상태에만 있는 코드(05)를 보내도 FCLTY_TY에서만 찾으므로 바뀌지 않는다
		assertBiz("info.nodata.msg", () -> cmmnCodeService.updateFcltyTyNm("05", "변조시도"));
		assertEquals(sttus, nameOf(CmmnCodeService.RESVE_STTUS, "01"));
		assertEquals("이용완료", nameOf(CmmnCodeService.RESVE_STTUS, "05"));
	}
}
