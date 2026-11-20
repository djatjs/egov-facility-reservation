package egovframework.cmm.web;

import org.egovframe.rte.ptl.mvc.tags.ui.pagination.AbstractPaginationRenderer;

/**
 * 표준프레임워크 페이징 태그(ui:pagination) 렌더러.
 * 템플릿의 이미지 버튼(대체 텍스트 없음) 대신 텍스트 링크와 aria-label로 그려
 * 스크린리더에서도 "이전 페이지", "3페이지"처럼 읽히게 한다.
 * {0}=JS 함수명, {1}=이동할 페이지, {2}=표시 번호 (MessageFormat이라 작은따옴표는 쓰지 않는다)
 */
public class TextPaginationRenderer extends AbstractPaginationRenderer {

	public TextPaginationRenderer() {
		firstPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\" aria-label=\"첫 페이지\">«</a>";
		previousPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\" aria-label=\"이전 페이지\">‹</a>";
		currentPageLabel = "<strong aria-current=\"page\">{0}</strong>";
		otherPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\" aria-label=\"{2}페이지\">{2}</a>";
		nextPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\" aria-label=\"다음 페이지\">›</a>";
		lastPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\" aria-label=\"마지막 페이지\">»</a>";
	}
}
