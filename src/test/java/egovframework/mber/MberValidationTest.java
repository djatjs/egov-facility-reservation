package egovframework.mber;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.annotation.Resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import egovframework.mber.service.MberService;
import egovframework.mber.service.MberVO;
import egovframework.resve.ResveTestSupport;

// SR-02: 회원 이름이 DB 바이트 한도(50B)를 넘기 전에 서버 검증에서 막히는지 테스트
public class MberValidationTest extends ResveTestSupport {

	/**
	 * 가입 규칙상 아이디에 '_'를 쓸 수 없어 공통 정리 대상(zt_)을 못 쓴다.
	 * 영문 소문자 접두어 ztm 을 쓰고 이 클래스에서 직접 지운다. (샘플 회원 admin·citizen·user 와 겹치지 않음)
	 */
	private static final String ID_PREFIX = "ztm";

	@Resource(name = "mberService")
	private MberService mberService;

	@BeforeEach
	@AfterEach
	void cleanMembers() {
		jdbc.update("DELETE FROM TB_MBER WHERE MBER_ID LIKE ?", ID_PREFIX + "%");
	}

	private MberVO member(String idSuffix, String name) {
		MberVO vo = new MberVO();
		vo.setMberId(ID_PREFIX + idSuffix);
		vo.setPassword("abcdefgh1");
		vo.setMberNm(name);
		return vo;
	}

	@Test
	@DisplayName("회원 이름: 한글 16자는 가입되고 17자는 업무 검증에서 막힌다")
	void mberNmBoundary() {
		assertDoesNotThrow(() -> mberService.insertMber(member("ok16", "가".repeat(16))));

		Throwable t = assertThrows(Exception.class, () -> mberService.insertMber(member("over17", "가".repeat(17))));
		assertEquals("mber.invalid.name", messageKey(t), "한글 17자는 업무 검증에서 막혀야 함"); // DB 오류(ORA-12899)가 아니라 업무 메시지
	}
}
