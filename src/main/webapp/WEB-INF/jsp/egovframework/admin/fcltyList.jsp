<%@ page pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="시설 관리" />
<c:set var="menu" value="adminFclty" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<form name="searchForm" action="${ctx}/admin/fclty/list.do" method="get" class="search" role="search">
	<input type="hidden" name="pageIndex" value="1">
	<div class="field">
		<label for="fcltyTyCd">유형</label>
		<select id="fcltyTyCd" name="fcltyTyCd">
			<option value="">전체</option>
			<c:forEach var="c" items="${fcltyTyList}">
				<option value="${c.code}" ${c.code eq searchVO.fcltyTyCd ? 'selected' : ''}><c:out value="${c.codeNm}" /></option>
			</c:forEach>
		</select>
	</div>
	<div class="field">
		<label for="useYn">사용 여부</label>
		<select id="useYn" name="useYn">
			<option value="">전체</option>
			<option value="Y" ${searchVO.useYn eq 'Y' ? 'selected' : ''}>사용</option>
			<option value="N" ${searchVO.useYn eq 'N' ? 'selected' : ''}>중지</option>
		</select>
	</div>
	<div class="field">
		<label for="searchKeyword">시설명</label>
		<input type="text" id="searchKeyword" name="searchKeyword" value="<c:out value='${searchVO.searchKeyword}'/>" maxlength="50">
	</div>
	<button type="submit" class="btn primary">검색</button>
	<a class="btn right" href="${ctx}/admin/fclty/form.do">시설 등록</a>
</form>

<p class="count">총 <strong>${paginationInfo.totalRecordCount}</strong>곳</p>
<div class="table-wrap">
	<table class="list">
		<caption class="sr-only">시설 관리 목록: 번호, 유형, 시설명, 운영시간, 예약 단위, 사용 여부</caption>
		<thead><tr><th scope="col">번호</th><th scope="col">유형</th><th scope="col">시설명</th><th scope="col">운영시간</th><th scope="col">단위</th><th scope="col">사용</th></tr></thead>
		<tbody>
		<c:forEach var="f" items="${resultList}">
			<tr>
				<td>${f.fcltySn}</td>
				<td><c:out value="${f.fcltyTyNm}" /></td>
				<td class="left"><a href="${ctx}/admin/fclty/form.do?fcltySn=${f.fcltySn}"><c:out value="${f.fcltyNm}" /></a></td>
				<td>${fn:substring(f.operBeginTm,0,2)}:${fn:substring(f.operBeginTm,2,4)}~${fn:substring(f.operEndTm,0,2)}:${fn:substring(f.operEndTm,2,4)}</td>
				<td>${f.resveUnitMin}분</td>
				<td><span class="badge ${f.useYn eq 'Y' ? 's02' : 's04'}">${f.useYn eq 'Y' ? '사용' : '중지'}</span></td>
			</tr>
		</c:forEach>
		<c:if test="${empty resultList}"><tr><td colspan="6" class="empty">시설이 없습니다.</td></tr></c:if>
		</tbody>
	</table>
</div>
<nav class="paging" aria-label="페이지 이동"><ui:pagination paginationInfo="${paginationInfo}" type="text" jsFunction="fn_link_page" /></nav>
<script>
function fn_link_page(p) { document.searchForm.pageIndex.value = p; document.searchForm.submit(); }
</script>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
