package egovframework.resve.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.egovframe.rte.psl.dataaccess.mapper.Mapper;

import egovframework.fclty.service.FcltyVO;
import egovframework.resve.service.DashboardVO;
import egovframework.resve.service.ResveHistVO;
import egovframework.resve.service.ResveSearchVO;
import egovframework.resve.service.ResveVO;

@Mapper
public interface ResveMapper {

	/** 회원 행 잠금: 같은 회원의 신청을 한 줄로 세운다 (1인 일일 한도용) */
	String lockMber(String mberId);

	/** 시설 행 잠금: 같은 시설의 신청을 한 줄로 세운다 (시간 겹침용). 사용 중지 시설이면 null */
	FcltyVO lockFclty(Long fcltySn);

	int countOverlap(ResveVO vo);

	int countClose(@Param("fcltySn") Long fcltySn, @Param("resveDe") String resveDe);

	int selectMberDailyMinutes(@Param("mberId") String mberId, @Param("resveDe") String resveDe);

	void insertResve(ResveVO vo);

	void insertResveHist(ResveHistVO vo);

	/** 상태가 bfrSttusCd일 때만 바꾼다. 그 사이 다른 사람이 처리했으면 0 */
	int updateResveSttus(@Param("resveSn") Long resveSn, @Param("bfrSttusCd") String bfrSttusCd,
			@Param("aftSttusCd") String aftSttusCd, @Param("returnRsn") String returnRsn,
			@Param("updusrId") String updusrId);

	ResveVO selectResve(Long resveSn);

	List<ResveVO> selectResveList(ResveSearchVO searchVO);

	int selectResveListTotCnt(ResveSearchVO searchVO);

	List<ResveVO> selectDayResveList(@Param("fcltySn") Long fcltySn, @Param("resveDe") String resveDe);

	List<ResveHistVO> selectResveHistList(Long resveSn);

	List<ResveVO> selectUpcomingResveList(@Param("mberId") String mberId, @Param("today") String today);

	DashboardVO selectDashboard(String today);

	List<ResveVO> selectPendingResveList(@Param("today") String today, @Param("count") int count);

	List<ResveVO> selectTodayResveList(String today);
}
