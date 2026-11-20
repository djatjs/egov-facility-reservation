<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="나의 예약" />
<c:set var="menu" value="my" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<form name="searchForm" action="${ctx}/resve/myList.do" method="get" class="search" role="search">
	<input type="hidden" name="pageIndex" value="1">
	<div class="field">
		<label for="resveSttusCd">상태</label>
		<select id="resveSttusCd" name="resveSttusCd">
			<option value="">전체</option>
			<c:forEach var="c" items="${sttusList}">
				<option value="${c.code}" ${c.code eq searchVO.resveSttusCd ? 'selected' : ''}><c:out value="${c.codeNm}" /></option>
			</c:forEach>
		</select>
	</div>
	<button type="submit" class="btn primary">조회</button>
	<a class="btn right" href="${ctx}/fclty/list.do">새 예약하기</a>
</form>

<p class="count">총 <strong>${paginationInfo.totalRecordCount}</strong>건</p>
<div class="table-wrap">
	<table class="list">
		<caption class="sr-only">나의 예약 목록: 번호, 시설, 이용일, 시간, 상태</caption>
		<thead><tr><th scope="col">번호</th><th scope="col">시설</th><th scope="col">이용일</th><th scope="col">시간</th><th scope="col">상태</th></tr></thead>
		<tbody>
		<c:forEach var="r" items="${resultList}">
			<tr>
				<td>${r.resveSn}</td>
				<td class="left"><a href="${ctx}/resve/detail.do?resveSn=${r.resveSn}"><c:out value="${r.fcltyNm}" /></a></td>
				<td>${fn:substring(r.resveDe,0,4)}-${fn:substring(r.resveDe,4,6)}-${fn:substring(r.resveDe,6,8)}</td>
				<td>${fn:substring(r.beginTm,0,2)}:${fn:substring(r.beginTm,2,4)}~${fn:substring(r.endTm,0,2)}:${fn:substring(r.endTm,2,4)}</td>
				<td><span class="badge s${r.resveSttusCd}"><c:out value="${r.resveSttusNm}" /></span></td>
			</tr>
		</c:forEach>
		<c:if test="${empty resultList}"><tr><td colspan="5" class="empty">예약 내역이 없습니다.</td></tr></c:if>
		</tbody>
	</table>
</div>
<nav class="paging" aria-label="페이지 이동"><ui:pagination paginationInfo="${paginationInfo}" type="text" jsFunction="fn_link_page" /></nav>
<script>
function fn_link_page(p) { document.searchForm.pageIndex.value = p; document.searchForm.submit(); }
</script>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
