package egovframework.fclty.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import egovframework.cmm.TimeUtil;
import egovframework.cmm.service.CmmnCodeService;
import egovframework.fclty.service.FcltyCloseVO;
import egovframework.fclty.service.FcltySearchVO;
import egovframework.fclty.service.FcltyService;
import egovframework.fclty.service.FcltyTyStatVO;
import egovframework.fclty.service.FcltyVO;

@Service("fcltyService")
public class FcltyServiceImpl extends EgovAbstractServiceImpl implements FcltyService {

	@Resource(name = "fcltyMapper")
	private FcltyMapper fcltyMapper;

	@Resource(name = "cmmnCodeService")
	private CmmnCodeService cmmnCodeService;

	@Override
	public List<FcltyTyStatVO> selectFcltyTyStatList() throws Exception {
		return fcltyMapper.selectFcltyTyStatList();
	}

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
		vo.setUseYn("Y");
		fcltyMapper.insertFclty(vo);
		return vo.getFcltySn();
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

	// ===== SR-03 휴관일 =====

	private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
	private static final int CLOSE_RSN_MAX = 100;   // CLOSE_RSN 300바이트 ÷ 3
	private static final int CLOSE_DAYS_AHEAD = 365;

	private static String today() {
		return TimeUtil.toDe(LocalDate.now(ZONE));
	}

	@Override
	public FcltyCloseVO selectCloseOn(Long fcltySn, String closeDe) throws Exception {
		return fcltyMapper.selectCloseOn(fcltySn, closeDe);
	}

	@Override
	public List<FcltyCloseVO> selectUpcomingCloseList(Long fcltySn) throws Exception {
		return fcltyMapper.selectUpcomingCloseList(fcltySn, today());
	}

	/**
	 * 예약 신청과 같은 시설 행 잠금(FOR UPDATE)을 먼저 잡는다.
	 * 잠금 없이 "예약 없음 확인 → 휴관일 등록"을 하면, 그 사이 들어온 예약이 휴관일에 남을 수 있다.
	 */
	@Override
	public void insertClose(FcltyCloseVO vo) throws Exception {
		LocalDate today = LocalDate.now(ZONE);
		LocalDate de = TimeUtil.parseDe(vo.getCloseDe());
		if (de == null || !de.isAfter(today) || de.isAfter(today.plusDays(CLOSE_DAYS_AHEAD))) {
			throw processException("fclty.close.invalid.date", new String[] { String.valueOf(CLOSE_DAYS_AHEAD) });
		}
		String rsn = vo.getCloseRsn() == null ? "" : vo.getCloseRsn().trim();
		if (rsn.isEmpty() || rsn.length() > CLOSE_RSN_MAX) {
			throw processException("fclty.close.invalid.reason", new String[] { String.valueOf(CLOSE_RSN_MAX) });
		}
		if (vo.getFcltySn() == null || fcltyMapper.lockFclty(vo.getFcltySn()) == null) {
			throw processException("info.nodata.msg");
		}
		if (fcltyMapper.selectCloseOn(vo.getFcltySn(), vo.getCloseDe()) != null) {
			throw processException("fclty.close.duplicate");
		}
		int active = fcltyMapper.countActiveResveOn(vo.getFcltySn(), vo.getCloseDe());
		if (active > 0) {
			throw processException("fclty.close.has.resve", new String[] { String.valueOf(active) });
		}
		vo.setCloseRsn(rsn);
		fcltyMapper.insertClose(vo);
	}

	@Override
	public void deleteClose(Long closeSn) throws Exception {
		if (closeSn == null || fcltyMapper.deleteClose(closeSn, today()) != 1) {
			throw processException("fclty.close.not.deletable");
		}
	}

	private void validate(FcltyVO vo) throws Exception {
		if (isBlank(vo.getFcltyNm(), 33) || isBlank(vo.getAdres(), 66)
				|| vo.getAcmdNmpr() == null || vo.getAcmdNmpr() < 1 || vo.getAcmdNmpr() > 10000
				|| (vo.getFcltyDc() != null && vo.getFcltyDc().length() > 666)) {
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
