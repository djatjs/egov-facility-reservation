package egovframework.fclty.service;

import egovframework.cmm.DefaultSearchVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcltySearchVO extends DefaultSearchVO {

	private static final long serialVersionUID = 1L;

	private String fcltyTyCd = "";
	/** 시민 화면은 "Y" 고정, 관리자 화면은 빈 값(전체) */
	private String useYn = "";
}
