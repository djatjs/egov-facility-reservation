<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="시설 찾기" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<form name="searchForm" action="${ctx}/fclty/list.do" method="get" class="search">
	<input type="hidden" name="pageIndex" value="1">
	<label for="fcltyTyCd">시설 유형</label>
	<select id="fcltyTyCd" name="fcltyTyCd">
		<option value="">전체</option>
		<c:forEach var="c" items="${fcltyTyList}">
			<option value="${c.code}" ${c.code eq searchVO.fcltyTyCd ? 'selected' : ''}><c:out value="${c.codeNm}" /></option>
		</c:forEach>
	</select>
	<label for="searchKeyword">시설명</label>
	<input type="text" id="searchKeyword" name="searchKeyword" value="<c:out value='${searchVO.searchKeyword}'/>" maxlength="50">
	<button type="submit" class="btn primary">검색</button>
</form>

<p class="count">총 <strong>${paginationInfo.totalRecordCount}</strong>곳</p>
<table class="list">
	<caption class="sr-only">시설 목록: 유형, 시설명, 주소, 수용 인원, 운영시간</caption>
	<thead><tr><th scope="col">유형</th><th scope="col">시설명</th><th scope="col">주소</th><th scope="col">수용 인원</th><th scope="col">운영시간</th></tr></thead>
	<tbody>
	<c:forEach var="f" items="${resultList}">
		<tr>
			<td><c:out value="${f.fcltyTyNm}" /></td>
			<td class="left"><a href="${ctx}/fclty/detail.do?fcltySn=${f.fcltySn}"><c:out value="${f.fcltyNm}" /></a></td>
			<td class="left"><c:out value="${f.adres}" /></td>
			<td>${f.acmdNmpr}명</td>
			<td>${fn:substring(f.operBeginTm,0,2)}:${fn:substring(f.operBeginTm,2,4)} ~ ${fn:substring(f.operEndTm,0,2)}:${fn:substring(f.operEndTm,2,4)}</td>
		</tr>
	</c:forEach>
	<c:if test="${empty resultList}"><tr><td colspan="5">조건에 맞는 시설이 없습니다.</td></tr></c:if>
	</tbody>
</table>
<div class="paging"><ui:pagination paginationInfo="${paginationInfo}" type="image" jsFunction="fn_link_page" /></div>

<script>
function fn_link_page(p) { document.searchForm.pageIndex.value = p; document.searchForm.submit(); }
</script>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
