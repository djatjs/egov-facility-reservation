package egovframework.cmm.service;

import java.util.List;

public interface CmmnCodeService {

	String FCLTY_TY = "FCLTY_TY";
	String RESVE_STTUS = "RESVE_STTUS";

	List<CmmnCodeVO> selectCodeList(String codeId) throws Exception;
}
