package egovframework.fclty.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.springframework.stereotype.Service;

import egovframework.cmm.TimeUtil;
import egovframework.cmm.service.CmmnCodeService;
import egovframework.fclty.service.FcltySearchVO;
import egovframework.fclty.service.FcltyService;
import egovframework.fclty.service.FcltyVO;

@Service("fcltyService")
public class FcltyServiceImpl extends EgovAbstractServiceImpl implements FcltyService {

	@Resource(name = "fcltyMapper")
	private FcltyMapper fcltyMapper;

	@Resource(name = "fcltyIdGnrService")
	private EgovIdGnrService fcltyIdGnrService;

	@Resource(name = "cmmnCodeService")
	private CmmnCodeService cmmnCodeService;

	@Override
	public List<FcltyVO> selectFcltyList(FcltySearchVO searchVO) throws Exception {
		return fcltyMapper.selectFcltyList(searchVO);
	}

	@Override
	public int selectFcltyListTotCnt(FcltySearchVO searchVO) throws Exception {
		return fcltyMapper.selectFcltyListTotCnt(searchVO);
	}

	@Override
	public FcltyVO selectFclty(Long fcltySn) throws Exception {
		FcltyVO vo = fcltySn == null ? null : fcltyMapper.selectFclty(fcltySn);
		if (vo == null) {
			throw processException("info.nodata.msg");
		}
		return vo;
	}

	@Override
	public long insertFclty(FcltyVO vo) throws Exception {
		validate(vo);
		long sn = fcltyIdGnrService.getNextLongId();
		vo.setFcltySn(sn);
		vo.setUseYn("Y");
		fcltyMapper.insertFclty(vo);
		return sn;
	}

	@Override
	public void updateFclty(FcltyVO vo) throws Exception {
		validate(vo);
		if (!"Y".equals(vo.getUseYn()) && !"N".equals(vo.getUseYn())) {
			throw processException("fclty.invalid.input");
		}
		if (fcltyMapper.updateFclty(vo) != 1) {
			throw processException("info.nodata.msg");
		}
	}

	private void validate(FcltyVO vo) throws Exception {
		if (isBlank(vo.getFcltyNm(), 100) || isBlank(vo.getAdres(), 200)
				|| vo.getAcmdNmpr() == null || vo.getAcmdNmpr() < 1 || vo.getAcmdNmpr() > 10000
				|| (vo.getFcltyDc() != null && vo.getFcltyDc().length() > 2000)) {
			throw processException("fclty.invalid.input");
		}
		boolean knownType = cmmnCodeService.selectCodeList(CmmnCodeService.FCLTY_TY).stream()
				.anyMatch(c -> c.getCode().equals(vo.getFcltyTyCd()));
		if (!knownType) {
			throw processException("fclty.invalid.input");
		}
		int begin = TimeUtil.toMinutes(vo.getOperBeginTm());
		int end = TimeUtil.toMinutes(vo.getOperEndTm());
		Integer unit = vo.getResveUnitMin();
		if (begin < 0 || end < 0 || begin >= end || unit == null
				|| !(unit == 30 || unit == 60 || unit == 120) || (end - begin) % unit != 0) {
			throw processException("fclty.invalid.time");
		}
	}

	private static boolean isBlank(String s, int maxLen) {
		return s == null || s.trim().isEmpty() || s.length() > maxLen;
	}
}
