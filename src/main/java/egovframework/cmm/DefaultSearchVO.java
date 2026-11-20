package egovframework.cmm;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/** 목록 검색·페이징 공통 VO (PaginationInfo 값과 ROWNUM 범위를 담는다) */
@Getter
@Setter
public class DefaultSearchVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String searchKeyword = "";
	private int pageIndex = 1;
	private int pageUnit = 10;
	private int pageSize = 10;
	private int firstIndex = 0;
	private int lastIndex = 10;
	private int recordCountPerPage = 10;
}
