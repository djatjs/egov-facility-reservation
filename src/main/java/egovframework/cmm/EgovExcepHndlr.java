package egovframework.cmm;

import org.egovframe.rte.fdl.cmmn.exception.EgovBizException;
import org.egovframe.rte.fdl.cmmn.exception.handler.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 서비스 계층 예외 처리기. 업무 예외(검증 실패)는 INFO, 그 외는 ERROR로 남긴다.
 */
public class EgovExcepHndlr implements ExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(EgovExcepHndlr.class);

	@Override
	public void occur(Exception ex, String packageName) {
		if (ex instanceof EgovBizException) {
			LOGGER.info("[{}] 업무 예외: {}", packageName, ex.getMessage());
		} else {
			LOGGER.error("[{}] 시스템 예외", packageName, ex);
		}
	}
}
