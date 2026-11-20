package egovframework.cmm;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/** 세션에 보관하는 로그인 사용자 정보 (비밀번호는 담지 않는다) */
@Getter
@Setter
public class LoginVO implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String SESSION_KEY = "loginVO";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_USER = "ROLE_USER";

	private String mberId;
	private String mberNm;
	private String authorCd;

	public boolean isAdmin() {
		return ROLE_ADMIN.equals(authorCd);
	}
}
