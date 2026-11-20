<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="요청을 처리하지 못했습니다" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>
<%-- 원인은 서버 로그에만 남기고 화면에는 노출하지 않는다 --%>
<p class="alert error" role="alert">일시적인 오류로 요청을 처리하지 못했습니다. 잠시 후 다시 시도해 주세요.</p>
<div class="actions"><a class="btn primary" href="${ctx}/main.do">처음으로</a></div>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
