package egovframework.mber.service.impl;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import egovframework.cmm.LoginVO;
import egovframework.mber.service.MberService;
import egovframework.mber.service.MberVO;
import lombok.RequiredArgsConstructor;

@Service("mberService")
@RequiredArgsConstructor
public class MberServiceImpl extends EgovAbstractServiceImpl implements MberService {

	private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();
	// 아이디가 없을 때도 해시 비교를 한 번 해서, 응답 시간으로 아이디 존재 여부를 알 수 없게 한다
	private static final String DUMMY_HASH = ENCODER.encode("dummy-password");

	private final MberMapper mberMapper;

	@Override
	public LoginVO login(String mberId, String password) throws Exception {
		if (mberId == null || password == null) {
			return null;
		}
		MberVO mber = mberMapper.selectMber(mberId);
		boolean matched = ENCODER.matches(password, mber == null ? DUMMY_HASH : mber.getPassword());
		if (mber == null || !matched || !"Y".equals(mber.getUseYn())) {
			return null;
		}
		LoginVO login = new LoginVO();
		login.setMberId(mber.getMberId());
		login.setMberNm(mber.getMberNm());
		login.setAuthorCd(mber.getAuthorCd());
		return login;
	}

	@Override
	public void insertMber(MberVO vo) throws Exception {
		if (vo.getMberId() == null || !vo.getMberId().matches("[a-z0-9]{4,20}")) {
			throw processException("mber.invalid.id");
		}
		if (vo.getPassword() == null || vo.getPassword().length() < 8 || vo.getPassword().length() > 64) {
			throw processException("mber.invalid.password");
		}
		if (vo.getMberNm() == null || vo.getMberNm().trim().isEmpty() || vo.getMberNm().length() > 50) {
			throw processException("mber.invalid.name");
		}
		if (mberMapper.countMber(vo.getMberId()) > 0) {
			throw processException("mber.duplicate.id");
		}
		vo.setMberNm(vo.getMberNm().trim());
		vo.setPassword(ENCODER.encode(vo.getPassword()));
		vo.setAuthorCd(LoginVO.ROLE_USER); // 가입 경로로는 관리자 권한을 만들 수 없다
		mberMapper.insertMber(vo);
	}
}
