package egovframework.fclty;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Consumer;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import egovframework.fclty.service.FcltyService;
import egovframework.fclty.service.FcltyVO;
import egovframework.resve.ResveTestSupport;

// SR-02: 한글 입력 길이가 DB 바이트 한도를 넘기 전에 서버 검증에서 막히는지 테스트
public class FcltyValidationTest extends ResveTestSupport {

	@Resource(name = "fcltyService")
	private FcltyService fcltyService;

	/** 검증을 통과하는 기본 시설. 테스트마다 한 항목만 바꿔서 쓴다 */
	private FcltyVO valid() {
		FcltyVO vo = new FcltyVO();
		vo.setFcltyTyCd("01");
		vo.setFcltyNm("테스트 시설");
		vo.setAdres("테스트 주소");
		vo.setAcmdNmpr(10);
		vo.setOperBeginTm("0900");
		vo.setOperEndTm("1800");
		vo.setResveUnitMin(60);
		vo.setRegisterId(TEST_REGISTER); // "ztest": 테스트 후 자동 삭제 대상
		return vo;
	}

	/** 최대 길이는 저장되고, 한 글자 넘으면 DB 오류(ORA-12899)가 아니라 업무 메시지로 막히는지 */
	private void assertBoundary(int max, Consumer<FcltyVO> okSetter, Consumer<FcltyVO> overSetter) {
		FcltyVO ok = valid();
		okSetter.accept(ok);
		assertDoesNotThrow(() -> fcltyService.insertFclty(ok));

		FcltyVO over = valid();
		overSetter.accept(over);
		Throwable t = assertThrows(Exception.class, () -> fcltyService.insertFclty(over));
		assertEquals("fclty.invalid.input", messageKey(t), "한글 " + (max + 1) + "자는 업무 검증에서 막혀야 함");
	}

	@Test
	@DisplayName("시설명: 한글 33자는 저장되고 34자는 업무 검증에서 막힌다")
	void fcltyNmBoundary() {
		assertBoundary(33, vo -> vo.setFcltyNm("가".repeat(33)), vo -> vo.setFcltyNm("가".repeat(34)));
	}

	@Test
	@DisplayName("주소: 한글 66자는 저장되고 67자는 업무 검증에서 막힌다")
	void adresBoundary() {
		assertBoundary(66, vo -> vo.setAdres("가".repeat(66)), vo -> vo.setAdres("가".repeat(67)));
	}

	@Test
	@DisplayName("시설 설명: 한글 666자는 저장되고 667자는 업무 검증에서 막힌다")
	void fcltyDcBoundary() {
		assertBoundary(666, vo -> vo.setFcltyDc("가".repeat(666)), vo -> vo.setFcltyDc("가".repeat(667)));
	}
}
