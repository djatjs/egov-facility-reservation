<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="예약 처리" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<%@ include file="/WEB-INF/jsp/egovframework/resve/resveInfo.jspf" %>

<c:if test="${resve.resveSttusCd eq '01'}">
	<div class="card">
		<form method="post" action="${ctx}/admin/resve/approve.do" class="inline">
			<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
			<input type="hidden" name="resveSn" value="${resve.resveSn}">
			<button type="submit" class="btn primary">승인</button>
		</form>
		<form method="post" action="${ctx}/admin/resve/reject.do">
			<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
			<input type="hidden" name="resveSn" value="${resve.resveSn}">
			<label for="returnRsn">반려 사유</label>
			<textarea id="returnRsn" name="returnRsn" rows="3" maxlength="500" required></textarea>
			<button type="submit" class="btn danger">반려</button>
		</form>
	</div>
</c:if>
<div class="actions"><a class="btn" href="${ctx}/admin/resve/list.do">목록</a></div>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
