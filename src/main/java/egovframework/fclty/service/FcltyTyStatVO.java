package egovframework.fclty.service;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/** 시설 유형별 운영 중인 시설 수 (메인 화면 카드) */
@Getter
@Setter
public class FcltyTyStatVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;
	private String codeNm;
	private int cnt;
}
