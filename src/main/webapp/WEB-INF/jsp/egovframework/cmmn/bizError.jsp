<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="요청을 처리하지 못했습니다" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>
<p class="alert error" role="alert"><c:out value="${exception.message}" /></p>
<div class="actions">
	<button type="button" class="btn primary" onclick="history.back()">이전 화면으로</button>
	<a class="btn" href="${ctx}/main.do">처음으로</a>
</div>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
