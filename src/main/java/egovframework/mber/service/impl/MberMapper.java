package egovframework.mber.service.impl;

import org.egovframe.rte.psl.dataaccess.mapper.Mapper;

import egovframework.mber.service.MberVO;

@Mapper
public interface MberMapper {

	MberVO selectMber(String mberId);

	int countMber(String mberId);

	void insertMber(MberVO vo);
}
