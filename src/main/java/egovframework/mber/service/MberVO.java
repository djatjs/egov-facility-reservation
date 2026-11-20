package egovframework.mber.service;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MberVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String mberId;
	private String password;
	private String mberNm;
	private String authorCd;
	private String useYn;
}
