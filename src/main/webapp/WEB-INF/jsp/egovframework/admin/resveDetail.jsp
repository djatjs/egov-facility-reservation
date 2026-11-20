<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="예약 처리" />
<c:set var="menu" value="adminResve" />
<c:set var="crumbName" value="예약 관리" />
<c:set var="crumbUrl" value="/admin/resve/list.do" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<%@ include file="/WEB-INF/jsp/egovframework/resve/resveInfo.jspf" %>

<c:if test="${resve.resveSttusCd eq '01'}">
	<h2 id="decideTitle">처리</h2>
	<div class="panel" role="group" aria-labelledby="decideTitle">
		<form method="post" action="${ctx}/admin/resve/approve.do">
			<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
			<input type="hidden" name="resveSn" value="${resve.resveSn}">
			<button type="submit" class="btn primary">승인</button>
		</form>
		<form method="post" action="${ctx}/admin/resve/reject.do" style="margin-top:24px">
			<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
			<input type="hidden" name="resveSn" value="${resve.resveSn}">
			<div class="field">
				<label for="returnRsn">반려 사유 <span class="hint">(신청자에게 그대로 보여집니다, 500자 이내)</span></label>
				<textarea id="returnRsn" name="returnRsn" rows="3" maxlength="500" required></textarea>
			</div>
			<button type="submit" class="btn danger">반려</button>
		</form>
	</div>
</c:if>
<div class="actions"><a class="btn" href="${ctx}/admin/resve/list.do">목록</a></div>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
