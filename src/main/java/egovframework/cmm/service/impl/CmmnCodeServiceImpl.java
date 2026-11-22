package egovframework.cmm.service.impl;

import java.util.List;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import egovframework.cmm.service.CmmnCodeService;
import egovframework.cmm.service.CmmnCodeVO;
import lombok.RequiredArgsConstructor;

@Service("cmmnCodeService")
@RequiredArgsConstructor
public class CmmnCodeServiceImpl extends EgovAbstractServiceImpl implements CmmnCodeService {

	private final CmmnCodeMapper cmmnCodeMapper;

	@Override
	public List<CmmnCodeVO> selectCodeList(String codeId) throws Exception {
		return cmmnCodeMapper.selectCodeList(codeId);
	}

	@Override
	public void updateFcltyTyNm(String code, String codeNm) throws Exception {
		String name = codeNm == null ? "" : codeNm.trim();
		if (name.isEmpty() || name.length() > FCLTY_TY_NM_MAX) {
			throw processException("cmmn.code.invalid.name", new String[] { String.valueOf(FCLTY_TY_NM_MAX) });
		}
		if (code == null || code.isEmpty()) {
			throw processException("info.nodata.msg");
		}
		if (cmmnCodeMapper.countSameCodeNm(FCLTY_TY, code, name) > 0) {
			throw processException("cmmn.code.duplicate.name");
		}
		if (cmmnCodeMapper.updateCodeNm(FCLTY_TY, code, name) != 1) {
			throw processException("info.nodata.msg");
		}
	}
}
