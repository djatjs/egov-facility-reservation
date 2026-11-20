package egovframework.notice.service;

import java.util.List;

import egovframework.cmm.DefaultSearchVO;

public interface NoticeService {

	List<NoticeVO> selectNoticeList(DefaultSearchVO searchVO) throws Exception;

	int selectNoticeListTotCnt(DefaultSearchVO searchVO) throws Exception;

	/** 조회수 증가 후 반환. 없으면 업무 예외 */
	NoticeVO selectNotice(Long nttSn, boolean countView) throws Exception;

	long insertNotice(NoticeVO vo) throws Exception;

	void updateNotice(NoticeVO vo) throws Exception;

	/** 사용여부를 N으로 바꾸는 논리 삭제 */
	void deleteNotice(Long nttSn, String updusrId) throws Exception;
}
