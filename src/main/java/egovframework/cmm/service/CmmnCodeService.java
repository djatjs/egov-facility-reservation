package egovframework.cmm.service;

import java.util.List;

public interface CmmnCodeService {

	String FCLTY_TY = "FCLTY_TY";
	String RESVE_STTUS = "RESVE_STTUS";

	/** 시설 유형 이름 최대 길이 (CODE_NM 100바이트 안, 메인 카드 폭 고려) */
	int FCLTY_TY_NM_MAX = 20;

	List<CmmnCodeVO> selectCodeList(String codeId) throws Exception;

	/**
	 * SR-01: 시설 유형의 표시 이름만 바꾼다. 유형 코드는 바꾸지 않는다.
	 * 코드 ID를 FCLTY_TY로 고정해, 요청을 조작해도 예약 상태 코드 이름은 바꿀 수 없다.
	 */
	void updateFcltyTyNm(String code, String codeNm) throws Exception;
}
