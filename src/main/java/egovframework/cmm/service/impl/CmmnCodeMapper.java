package egovframework.cmm.service.impl;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.mapper.Mapper;

import egovframework.cmm.service.CmmnCodeVO;

@Mapper
public interface CmmnCodeMapper {

	List<CmmnCodeVO> selectCodeList(String codeId);
}
