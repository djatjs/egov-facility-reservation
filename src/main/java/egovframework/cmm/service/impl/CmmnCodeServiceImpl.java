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
}
