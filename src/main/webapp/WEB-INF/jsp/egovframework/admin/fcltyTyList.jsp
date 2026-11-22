<%@ page pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="시설 유형 관리" />
<c:set var="menu" value="adminFcltyTy" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<p class="hint" style="margin-top:24px">표시 이름만 바꿀 수 있습니다. 유형 코드는 시설과 연결되어 있어 바꿀 수 없으며, 바꾼 이름은 모든 화면에 바로 반영됩니다. (${nameMax}자 이내, 다른 유형과 같은 이름 불가)</p>

<div class="table-wrap">
	<table class="list">
		<caption class="sr-only">시설 유형 목록: 코드, 표시 이름, 저장</caption>
		<thead><tr><th scope="col">코드</th><th scope="col">표시 이름</th><th scope="col">저장</th></tr></thead>
		<tbody>
		<c:forEach var="t" items="${fcltyTyList}">
			<tr>
				<td><c:out value="${t.code}" /></td>
				<td class="left">
					<form id="form${t.code}" method="post" action="${ctx}/admin/fcltyTy/save.do" class="inline">
						<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
						<input type="hidden" name="code" value="<c:out value='${t.code}'/>">
						<label for="codeNm${t.code}" class="sr-only">유형 <c:out value="${t.code}" /> 표시 이름</label>
						<input type="text" id="codeNm${t.code}" name="codeNm" value="<c:out value='${t.codeNm}'/>" maxlength="${nameMax}" required>
					</form>
				</td>
				<td><button type="submit" form="form${t.code}" class="btn small primary">저장</button></td>
			</tr>
		</c:forEach>
		<c:if test="${empty fcltyTyList}"><tr><td colspan="3" class="empty">등록된 시설 유형이 없습니다.</td></tr></c:if>
		</tbody>
	</table>
</div>

<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
