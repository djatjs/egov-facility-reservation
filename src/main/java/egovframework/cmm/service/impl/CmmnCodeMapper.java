package egovframework.cmm.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.egovframe.rte.psl.dataaccess.mapper.Mapper;

import egovframework.cmm.service.CmmnCodeVO;

@Mapper
public interface CmmnCodeMapper {

	List<CmmnCodeVO> selectCodeList(String codeId);

	/** 코드 이름만 바꾼다. 해당 코드가 없으면 0 */
	int updateCodeNm(@Param("codeId") String codeId, @Param("code") String code, @Param("codeNm") String codeNm);

	/** 같은 코드 ID 안에서 자기 자신을 뺀 같은 이름의 수 */
	int countSameCodeNm(@Param("codeId") String codeId, @Param("code") String code, @Param("codeNm") String codeNm);
}
