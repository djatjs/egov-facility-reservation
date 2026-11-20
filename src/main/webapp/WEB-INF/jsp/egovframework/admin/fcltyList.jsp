<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="시설 관리" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<form name="searchForm" action="${ctx}/admin/fclty/list.do" method="get" class="search">
	<input type="hidden" name="pageIndex" value="1">
	<label for="fcltyTyCd">유형</label>
	<select id="fcltyTyCd" name="fcltyTyCd">
		<option value="">전체</option>
		<c:forEach var="c" items="${fcltyTyList}">
			<option value="${c.code}" ${c.code eq searchVO.fcltyTyCd ? 'selected' : ''}><c:out value="${c.codeNm}" /></option>
		</c:forEach>
	</select>
	<label for="useYn">사용</label>
	<select id="useYn" name="useYn">
		<option value="">전체</option>
		<option value="Y" ${searchVO.useYn eq 'Y' ? 'selected' : ''}>사용</option>
		<option value="N" ${searchVO.useYn eq 'N' ? 'selected' : ''}>중지</option>
	</select>
	<label for="searchKeyword">시설명</label>
	<input type="text" id="searchKeyword" name="searchKeyword" value="<c:out value='${searchVO.searchKeyword}'/>" maxlength="50">
	<button type="submit" class="btn primary">검색</button>
	<a class="btn" href="${ctx}/admin/fclty/form.do">시설 등록</a>
</form>

<table class="list">
	<caption class="sr-only">시설 관리 목록</caption>
	<thead><tr><th scope="col">번호</th><th scope="col">유형</th><th scope="col">시설명</th><th scope="col">운영시간</th><th scope="col">단위</th><th scope="col">사용</th></tr></thead>
	<tbody>
	<c:forEach var="f" items="${resultList}">
		<tr>
			<td>${f.fcltySn}</td>
			<td><c:out value="${f.fcltyTyNm}" /></td>
			<td class="left"><a href="${ctx}/admin/fclty/form.do?fcltySn=${f.fcltySn}"><c:out value="${f.fcltyNm}" /></a></td>
			<td>${fn:substring(f.operBeginTm,0,2)}:${fn:substring(f.operBeginTm,2,4)} ~ ${fn:substring(f.operEndTm,0,2)}:${fn:substring(f.operEndTm,2,4)}</td>
			<td>${f.resveUnitMin}분</td>
			<td>${f.useYn eq 'Y' ? '사용' : '중지'}</td>
		</tr>
	</c:forEach>
	<c:if test="${empty resultList}"><tr><td colspan="6">시설이 없습니다.</td></tr></c:if>
	</tbody>
</table>
<div class="paging"><ui:pagination paginationInfo="${paginationInfo}" type="image" jsFunction="fn_link_page" /></div>
<script>
function fn_link_page(p) { document.searchForm.pageIndex.value = p; document.searchForm.submit(); }
</script>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
