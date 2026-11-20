<%@ page pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="공지사항" />
<c:set var="menu" value="notice" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<form name="searchForm" action="${ctx}/notice/list.do" method="get" class="search" role="search">
	<input type="hidden" name="pageIndex" value="1">
	<div class="field">
		<label for="searchKeyword">제목</label>
		<input type="text" id="searchKeyword" name="searchKeyword" value="<c:out value='${searchVO.searchKeyword}'/>" maxlength="50">
	</div>
	<button type="submit" class="btn primary">검색</button>
	<c:if test="${login.admin}"><a class="btn right" href="${ctx}/admin/notice/form.do">글쓰기</a></c:if>
</form>

<p class="count">총 <strong>${paginationInfo.totalRecordCount}</strong>건</p>
<div class="table-wrap">
	<table class="list">
		<caption class="sr-only">공지사항 목록: 번호, 제목, 등록일, 조회수</caption>
		<thead><tr><th scope="col">번호</th><th scope="col">제목</th><th scope="col">등록일</th><th scope="col">조회</th></tr></thead>
		<tbody>
		<c:forEach var="n" items="${resultList}">
			<tr>
				<td>${n.nttSn}</td>
				<td class="left"><a href="${ctx}/notice/detail.do?nttSn=${n.nttSn}"><c:out value="${n.nttSj}" /></a></td>
				<td><fmt:formatDate value="${n.registDt}" pattern="yyyy-MM-dd" /></td>
				<td>${n.rdcnt}</td>
			</tr>
		</c:forEach>
		<c:if test="${empty resultList}"><tr><td colspan="4" class="empty">등록된 공지사항이 없습니다.</td></tr></c:if>
		</tbody>
	</table>
</div>
<nav class="paging" aria-label="페이지 이동"><ui:pagination paginationInfo="${paginationInfo}" type="text" jsFunction="fn_link_page" /></nav>
<script>
function fn_link_page(p) { document.searchForm.pageIndex.value = p; document.searchForm.submit(); }
</script>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
