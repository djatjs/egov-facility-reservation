package egovframework.cmm.service;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmmnCodeVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String codeId;
	private String code;
	private String codeNm;
}
