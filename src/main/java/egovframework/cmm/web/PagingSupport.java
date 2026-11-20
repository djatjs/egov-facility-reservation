package egovframework.cmm.web;

import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

import egovframework.cmm.DefaultSearchVO;

/** 검색 VO에 ROWNUM 범위를 채우고, 화면 페이징 태그용 PaginationInfo를 돌려준다 */
public final class PagingSupport {

	private PagingSupport() {
	}

	public static PaginationInfo prepare(DefaultSearchVO searchVO, int pageUnit, int pageSize) {
		if (searchVO.getPageIndex() < 1) {
			searchVO.setPageIndex(1);
		}
		searchVO.setPageUnit(pageUnit);
		searchVO.setPageSize(pageSize);

		PaginationInfo info = new PaginationInfo();
		info.setCurrentPageNo(searchVO.getPageIndex());
		info.setRecordCountPerPage(pageUnit);
		info.setPageSize(pageSize);

		searchVO.setFirstIndex(info.getFirstRecordIndex());
		searchVO.setLastIndex(info.getLastRecordIndex());
		searchVO.setRecordCountPerPage(pageUnit);
		return info;
	}
}
