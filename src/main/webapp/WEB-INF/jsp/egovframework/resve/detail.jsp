<%@ page pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="예약 상세" />
<c:set var="menu" value="my" />
<c:set var="crumbName" value="나의 예약" />
<c:set var="crumbUrl" value="/resve/myList.do" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<%@ include file="/WEB-INF/jsp/egovframework/resve/resveInfo.jspf" %>

<div class="actions">
	<a class="btn" href="${ctx}/resve/myList.do">목록</a>
	<c:if test="${resve.resveSttusCd eq '01' or resve.resveSttusCd eq '02'}">
		<form method="post" action="${ctx}/resve/cancel.do" class="inline right" onsubmit="return confirm('예약을 취소할까요? 취소한 예약은 되돌릴 수 없습니다.');">
			<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
			<input type="hidden" name="resveSn" value="${resve.resveSn}">
			<button type="submit" class="btn danger">예약 취소</button>
		</form>
	</c:if>
</div>

<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
