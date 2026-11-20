package egovframework.resve.service;

import java.util.List;

import egovframework.cmm.LoginVO;

public interface ResveService {

	/** 예약 신청. 시간 겹침·1인 일일 한도를 락으로 직렬화해 검사한다 */
	long insertResve(ResveVO vo) throws Exception;

	List<ResveVO> selectResveList(ResveSearchVO searchVO) throws Exception;

	int selectResveListTotCnt(ResveSearchVO searchVO) throws Exception;

	/** 본인 예약이거나 관리자일 때만 조회 */
	ResveVO selectResve(Long resveSn, LoginVO login) throws Exception;

	List<ResveHistVO> selectResveHistList(Long resveSn) throws Exception;

	/** 시설 상세 화면의 시간표용: 해당 일자의 신청·승인 예약 (시간만 사용) */
	List<ResveVO> selectDayResveList(Long fcltySn, String resveDe) throws Exception;

	void cancelResve(Long resveSn, LoginVO login) throws Exception;

	void approveResve(Long resveSn, LoginVO admin) throws Exception;

	void rejectResve(Long resveSn, String returnRsn, LoginVO admin) throws Exception;
}
