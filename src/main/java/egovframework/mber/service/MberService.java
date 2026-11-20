package egovframework.mber.service;

import egovframework.cmm.LoginVO;

public interface MberService {

	/** 아이디·비밀번호가 맞으면 LoginVO, 아니면 null */
	LoginVO login(String mberId, String password) throws Exception;

	/** 일반 회원(ROLE_USER) 가입 */
	void insertMber(MberVO vo) throws Exception;
}
