package egovframework.notice.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.springframework.stereotype.Service;

import egovframework.cmm.DefaultSearchVO;
import egovframework.notice.service.NoticeService;
import egovframework.notice.service.NoticeVO;

@Service("noticeService")
public class NoticeServiceImpl extends EgovAbstractServiceImpl implements NoticeService {

	@Resource(name = "noticeMapper")
	private NoticeMapper noticeMapper;

	@Resource(name = "noticeIdGnrService")
	private EgovIdGnrService noticeIdGnrService;

	@Override
	public List<NoticeVO> selectNoticeList(DefaultSearchVO searchVO) throws Exception {
		return noticeMapper.selectNoticeList(searchVO);
	}

	@Override
	public int selectNoticeListTotCnt(DefaultSearchVO searchVO) throws Exception {
		return noticeMapper.selectNoticeListTotCnt(searchVO);
	}

	@Override
	public NoticeVO selectNotice(Long nttSn, boolean countView) throws Exception {
		NoticeVO vo = nttSn == null ? null : noticeMapper.selectNotice(nttSn);
		if (vo == null) {
			throw processException("info.nodata.msg");
		}
		if (countView) {
			noticeMapper.updateRdcnt(nttSn);
		}
		return vo;
	}

	@Override
	public long insertNotice(NoticeVO vo) throws Exception {
		validate(vo);
		long sn = noticeIdGnrService.getNextLongId();
		vo.setNttSn(sn);
		noticeMapper.insertNotice(vo);
		return sn;
	}

	@Override
	public void updateNotice(NoticeVO vo) throws Exception {
		validate(vo);
		if (noticeMapper.updateNotice(vo) != 1) {
			throw processException("info.nodata.msg");
		}
	}

	@Override
	public void deleteNotice(Long nttSn, String updusrId) throws Exception {
		if (nttSn == null || noticeMapper.deleteNotice(nttSn, updusrId) != 1) {
			throw processException("info.nodata.msg");
		}
	}

	private void validate(NoticeVO vo) throws Exception {
		if (vo.getNttSj() == null || vo.getNttSj().trim().isEmpty() || vo.getNttSj().length() > 100
				|| vo.getNttCn() == null || vo.getNttCn().trim().isEmpty()) {
			throw processException("notice.invalid.input");
		}
	}
}
