package egovframework.resve.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.springframework.stereotype.Service;

import egovframework.cmm.LoginVO;
import egovframework.cmm.TimeUtil;
import egovframework.fclty.service.FcltyVO;
import egovframework.resve.service.ResveHistVO;
import egovframework.resve.service.ResveSearchVO;
import egovframework.resve.service.ResveService;
import egovframework.resve.service.ResveSttus;
import egovframework.resve.service.ResveVO;

@Service("resveService")
public class ResveServiceImpl extends EgovAbstractServiceImpl implements ResveService {

	static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

	@Resource(name = "resveMapper")
	private ResveMapper resveMapper;

	@Resource(name = "resveIdGnrService")
	private EgovIdGnrService resveIdGnrService;

	@Resource(name = "propertiesService")
	private EgovPropertyService propertiesService;

	/**
	 * 예약 신청.
	 * <p>
	 * "조회해서 없으면 INSERT"는 동시에 들어온 두 요청이 같은 조회 결과를 보고 둘 다 INSERT할 수 있다.
	 * 그래서 검사 전에 행 잠금(SELECT ... FOR UPDATE)으로 같은 대상의 신청을 한 줄로 세운다.
	 * <ul>
	 * <li>회원 행 잠금 → 같은 사람이 여러 시설에 동시에 신청해 일일 한도를 넘는 것을 막는다</li>
	 * <li>시설 행 잠금 → 여러 사람이 같은 시설·시간에 동시에 신청해 겹치는 것을 막는다</li>
	 * </ul>
	 * 잠금 순서는 항상 회원 → 시설로 고정해 교착 상태(deadlock)를 피한다.
	 * 잠금은 이 메서드의 트랜잭션이 끝날 때 풀린다 (context-transaction.xml).
	 */
	@Override
	public long insertResve(ResveVO vo) throws Exception {
		LocalDate today = LocalDate.now(ZONE);
		int maxDays = propertiesService.getInt("resveMaxDaysAhead");
		LocalDate resveDe = TimeUtil.parseDe(vo.getResveDe());
		if (resveDe == null || !resveDe.isAfter(today) || resveDe.isAfter(today.plusDays(maxDays))) {
			throw processException("resve.invalid.date", new String[] { String.valueOf(maxDays) });
		}
		int begin = TimeUtil.toMinutes(vo.getBeginTm());
		int end = TimeUtil.toMinutes(vo.getEndTm());
		if (begin < 0 || end < 0 || begin >= end) {
			throw processException("resve.invalid.time");
		}
		if (vo.getUsePurps() == null || vo.getUsePurps().trim().isEmpty() || vo.getUsePurps().length() > 200) {
			throw processException("resve.invalid.purps");
		}

		// 1) 회원 잠금 (순서 고정: 회원 → 시설)
		resveMapper.lockMber(vo.getMberId());
		// 2) 시설 잠금
		FcltyVO fclty = resveMapper.lockFclty(vo.getFcltySn());
		if (fclty == null) {
			throw processException("fclty.not.available");
		}

		int operBegin = TimeUtil.toMinutes(fclty.getOperBeginTm());
		int operEnd = TimeUtil.toMinutes(fclty.getOperEndTm());
		int unit = fclty.getResveUnitMin();
		if (begin < operBegin || end > operEnd || (begin - operBegin) % unit != 0 || (end - begin) % unit != 0) {
			throw processException("resve.invalid.time");
		}
		if (vo.getUseNmpr() == null || vo.getUseNmpr() < 1 || vo.getUseNmpr() > fclty.getAcmdNmpr()) {
			throw processException("resve.invalid.nmpr");
		}

		// 3) 시간 겹침 (시설 잠금 아래에서 검사)
		if (resveMapper.countOverlap(vo) > 0) {
			throw processException("resve.duplicate");
		}

		// 4) 1인 일일 한도 (회원 잠금 아래에서 검사)
		int limit = propertiesService.getInt("resveDailyLimitMin");
		int used = resveMapper.selectMberDailyMinutes(vo.getMberId(), vo.getResveDe());
		if (used + (end - begin) > limit) {
			throw processException("resve.daily.limit",
					new String[] { String.valueOf(limit), String.valueOf(Math.max(limit - used, 0)) });
		}

		long sn = resveIdGnrService.getNextLongId();
		vo.setResveSn(sn);
		vo.setUsePurps(vo.getUsePurps().trim());
		vo.setResveSttusCd(ResveSttus.APPLY);
		resveMapper.insertResve(vo);
		insertHist(sn, null, ResveSttus.APPLY, null, vo.getMberId());
		return sn;
	}

	@Override
	public List<ResveVO> selectResveList(ResveSearchVO searchVO) throws Exception {
		return resveMapper.selectResveList(searchVO);
	}

	@Override
	public int selectResveListTotCnt(ResveSearchVO searchVO) throws Exception {
		return resveMapper.selectResveListTotCnt(searchVO);
	}

	@Override
	public ResveVO selectResve(Long resveSn, LoginVO login) throws Exception {
		ResveVO vo = resveSn == null ? null : resveMapper.selectResve(resveSn);
		if (vo == null) {
			throw processException("info.nodata.msg");
		}
		// 주소의 예약번호만 바꿔 남의 예약을 보는 것(IDOR)을 막는다
		if (!login.isAdmin() && !vo.getMberId().equals(login.getMberId())) {
			throw processException("resve.forbidden");
		}
		return vo;
	}

	@Override
	public List<ResveHistVO> selectResveHistList(Long resveSn) throws Exception {
		return resveMapper.selectResveHistList(resveSn);
	}

	@Override
	public List<ResveVO> selectDayResveList(Long fcltySn, String resveDe) throws Exception {
		return resveMapper.selectDayResveList(fcltySn, resveDe);
	}

	@Override
	public void cancelResve(Long resveSn, LoginVO login) throws Exception {
		ResveVO vo = resveSn == null ? null : resveMapper.selectResve(resveSn);
		if (vo == null) {
			throw processException("info.nodata.msg");
		}
		if (!vo.getMberId().equals(login.getMberId())) {
			throw processException("resve.forbidden");
		}
		if (!ResveSttus.APPLY.equals(vo.getResveSttusCd()) && !ResveSttus.APPROVE.equals(vo.getResveSttusCd())) {
			throw processException("resve.already.processed");
		}
		if (!TimeUtil.parseDe(vo.getResveDe()).isAfter(LocalDate.now(ZONE))) {
			throw processException("resve.cancel.deadline");
		}
		changeSttus(vo.getResveSn(), vo.getResveSttusCd(), ResveSttus.CANCEL, null, login.getMberId());
	}

	@Override
	public void approveResve(Long resveSn, LoginVO admin) throws Exception {
		changeSttus(resveSn, ResveSttus.APPLY, ResveSttus.APPROVE, null, admin.getMberId());
	}

	@Override
	public void rejectResve(Long resveSn, String returnRsn, LoginVO admin) throws Exception {
		if (returnRsn == null || returnRsn.trim().isEmpty() || returnRsn.length() > 500) {
			throw processException("resve.reject.reason");
		}
		changeSttus(resveSn, ResveSttus.APPLY, ResveSttus.REJECT, returnRsn.trim(), admin.getMberId());
	}

	/** 조건부 UPDATE로 상태를 바꾼다. 그 사이 다른 처리가 먼저 끝났으면 0건이 바뀌므로 업무 예외 */
	private void changeSttus(Long resveSn, String from, String to, String rsn, String changerId) throws Exception {
		if (resveSn == null || resveMapper.updateResveSttus(resveSn, from, to, rsn, changerId) != 1) {
			throw processException("resve.already.processed");
		}
		insertHist(resveSn, from, to, rsn, changerId);
	}

	private void insertHist(Long resveSn, String from, String to, String rsn, String changerId) {
		ResveHistVO hist = new ResveHistVO();
		hist.setResveSn(resveSn);
		hist.setBfrSttusCd(from);
		hist.setAftSttusCd(to);
		hist.setChangeRsn(rsn);
		hist.setChangerId(changerId);
		resveMapper.insertResveHist(hist);
	}
}
