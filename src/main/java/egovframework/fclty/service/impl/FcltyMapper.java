package egovframework.fclty.service.impl;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.mapper.Mapper;

import egovframework.fclty.service.FcltySearchVO;
import egovframework.fclty.service.FcltyVO;

@Mapper
public interface FcltyMapper {

	List<FcltyVO> selectFcltyList(FcltySearchVO searchVO);

	int selectFcltyListTotCnt(FcltySearchVO searchVO);

	FcltyVO selectFclty(Long fcltySn);

	void insertFclty(FcltyVO vo);

	int updateFclty(FcltyVO vo);
}
