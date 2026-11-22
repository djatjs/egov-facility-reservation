package egovframework.fclty.service;

import java.util.List;

public interface FcltyService {

	/** 유형별 운영 중인 시설 수 */
	List<FcltyTyStatVO> selectFcltyTyStatList() throws Exception;

	List<FcltyVO> selectFcltyList(FcltySearchVO searchVO) throws Exception;

	int selectFcltyListTotCnt(FcltySearchVO searchVO) throws Exception;

	/** 없으면 업무 예외 */
	FcltyVO selectFclty(Long fcltySn) throws Exception;

	long insertFclty(FcltyVO vo) throws Exception;

	void updateFclty(FcltyVO vo) throws Exception;

	// ===== SR-03 휴관일 =====

	/** 해당 날짜 휴관일, 없으면 null */
	FcltyCloseVO selectCloseOn(Long fcltySn, String closeDe) throws Exception;

	/** 오늘 이후 휴관일 목록 (관리자 화면) */
	List<FcltyCloseVO> selectUpcomingCloseList(Long fcltySn) throws Exception;

	/** 그날 신청·승인 예약이 남아 있으면 업무 예외 (관리자가 먼저 반려하도록) */
	void insertClose(FcltyCloseVO vo) throws Exception;

	/** 오늘 이후 휴관일만 삭제할 수 있다 */
	void deleteClose(Long closeSn) throws Exception;
}
