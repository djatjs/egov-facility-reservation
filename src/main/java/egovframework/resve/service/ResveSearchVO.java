package egovframework.resve.service;

import egovframework.cmm.DefaultSearchVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResveSearchVO extends DefaultSearchVO {

	private static final long serialVersionUID = 1L;

	private Long fcltySn;
	private String resveSttusCd = "";
	private String beginDe = "";
	private String endDe = "";
	/** 나의 예약 목록은 컨트롤러가 세션 아이디로 채운다 (요청 파라미터로 받지 않음) */
	private String mberId;
}
