<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="요청을 처리하지 못했습니다" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>
<p class="msg error" role="alert"><c:out value="${exception.message}" /></p>
<p><button type="button" class="btn" onclick="history.back()">이전 화면으로</button></p>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
