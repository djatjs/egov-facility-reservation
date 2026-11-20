<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="예약 관리" />
<c:set var="menu" value="adminResve" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>
<c:set var="beginIso" value="${empty searchVO.beginDe ? '' : fn:substring(searchVO.beginDe,0,4).concat('-').concat(fn:substring(searchVO.beginDe,4,6)).concat('-').concat(fn:substring(searchVO.beginDe,6,8))}" />
<c:set var="endIso" value="${empty searchVO.endDe ? '' : fn:substring(searchVO.endDe,0,4).concat('-').concat(fn:substring(searchVO.endDe,4,6)).concat('-').concat(fn:substring(searchVO.endDe,6,8))}" />

<form name="searchForm" action="${ctx}/admin/resve/list.do" method="get" class="search" role="search">
	<input type="hidden" name="pageIndex" value="1">
	<div class="field">
		<label for="fcltySn">시설</label>
		<select id="fcltySn" name="fcltySn">
			<option value="">전체</option>
			<c:forEach var="f" items="${fcltyList}">
				<option value="${f.fcltySn}" ${f.fcltySn eq searchVO.fcltySn ? 'selected' : ''}><c:out value="${f.fcltyNm}" /></option>
			</c:forEach>
		</select>
	</div>
	<div class="field">
		<label for="resveSttusCd">상태</label>
		<select id="resveSttusCd" name="resveSttusCd">
			<option value="">전체</option>
			<c:forEach var="c" items="${sttusList}">
				<option value="${c.code}" ${c.code eq searchVO.resveSttusCd ? 'selected' : ''}><c:out value="${c.codeNm}" /></option>
			</c:forEach>
		</select>
	</div>
	<div class="field">
		<label for="beginDe">이용일 시작</label>
		<input type="date" id="beginDe" name="beginDe" value="${beginIso}">
	</div>
	<div class="field">
		<label for="endDe">이용일 끝</label>
		<input type="date" id="endDe" name="endDe" value="${endIso}">
	</div>
	<div class="field">
		<label for="searchKeyword">신청자</label>
		<input type="text" id="searchKeyword" name="searchKeyword" value="<c:out value='${searchVO.searchKeyword}'/>" maxlength="50" placeholder="이름 또는 아이디">
	</div>
	<button type="submit" class="btn primary">검색</button>
</form>

<p class="count">총 <strong>${paginationInfo.totalRecordCount}</strong>건</p>
<div class="table-wrap">
	<table class="list">
		<caption class="sr-only">예약 목록: 번호, 시설, 신청자, 이용일, 시간, 인원, 상태</caption>
		<thead><tr><th scope="col">번호</th><th scope="col">시설</th><th scope="col">신청자</th><th scope="col">이용일</th><th scope="col">시간</th><th scope="col">인원</th><th scope="col">상태</th></tr></thead>
		<tbody>
		<c:forEach var="r" items="${resultList}">
			<tr>
				<td><a href="${ctx}/admin/resve/detail.do?resveSn=${r.resveSn}">${r.resveSn}</a></td>
				<td class="left"><a href="${ctx}/admin/resve/detail.do?resveSn=${r.resveSn}"><c:out value="${r.fcltyNm}" /></a></td>
				<td><c:out value="${r.mberNm}" /></td>
				<td>${fn:substring(r.resveDe,0,4)}-${fn:substring(r.resveDe,4,6)}-${fn:substring(r.resveDe,6,8)}</td>
				<td>${fn:substring(r.beginTm,0,2)}:${fn:substring(r.beginTm,2,4)}~${fn:substring(r.endTm,0,2)}:${fn:substring(r.endTm,2,4)}</td>
				<td>${r.useNmpr}명</td>
				<td><span class="badge s${r.resveSttusCd}"><c:out value="${r.resveSttusNm}" /></span></td>
			</tr>
		</c:forEach>
		<c:if test="${empty resultList}"><tr><td colspan="7" class="empty">조건에 맞는 예약이 없습니다.</td></tr></c:if>
		</tbody>
	</table>
</div>
<nav class="paging" aria-label="페이지 이동"><ui:pagination paginationInfo="${paginationInfo}" type="text" jsFunction="fn_link_page" /></nav>
<script>
function fn_link_page(p) { document.searchForm.pageIndex.value = p; document.searchForm.submit(); }
</script>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
