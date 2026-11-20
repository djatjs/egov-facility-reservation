package egovframework.fclty.service;

import java.util.List;

public interface FcltyService {

	List<FcltyVO> selectFcltyList(FcltySearchVO searchVO) throws Exception;

	int selectFcltyListTotCnt(FcltySearchVO searchVO) throws Exception;

	/** 없으면 업무 예외 */
	FcltyVO selectFclty(Long fcltySn) throws Exception;

	long insertFclty(FcltyVO vo) throws Exception;

	void updateFclty(FcltyVO vo) throws Exception;
}
