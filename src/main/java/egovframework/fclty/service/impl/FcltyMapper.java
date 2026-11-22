package egovframework.fclty.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.egovframe.rte.psl.dataaccess.mapper.Mapper;

import egovframework.fclty.service.FcltyCloseVO;
import egovframework.fclty.service.FcltySearchVO;
import egovframework.fclty.service.FcltyTyStatVO;
import egovframework.fclty.service.FcltyVO;

@Mapper
public interface FcltyMapper {

	List<FcltyTyStatVO> selectFcltyTyStatList();

	List<FcltyVO> selectFcltyList(FcltySearchVO searchVO);

	int selectFcltyListTotCnt(FcltySearchVO searchVO);

	FcltyVO selectFclty(Long fcltySn);

	void insertFclty(FcltyVO vo);

	int updateFclty(FcltyVO vo);

	/** 시설 행 잠금: 휴관일 등록을 같은 시설의 예약 신청과 한 줄로 세운다 */
	Long lockFclty(Long fcltySn);

	int countActiveResveOn(@Param("fcltySn") Long fcltySn, @Param("closeDe") String closeDe);

	FcltyCloseVO selectCloseOn(@Param("fcltySn") Long fcltySn, @Param("closeDe") String closeDe);

	List<FcltyCloseVO> selectUpcomingCloseList(@Param("fcltySn") Long fcltySn, @Param("today") String today);

	void insertClose(FcltyCloseVO vo);

	int deleteClose(@Param("closeSn") Long closeSn, @Param("today") String today);
}
