<%@ page pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="로그인" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<form method="post" action="${ctx}/mber/login.do" class="panel narrow">
	<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
	<div class="field">
		<label for="mberId">아이디</label>
		<input type="text" id="mberId" name="mberId" value="<c:out value='${mberId}'/>" maxlength="20" required autocomplete="username">
	</div>
	<div class="field">
		<label for="password">비밀번호</label>
		<input type="password" id="password" name="password" maxlength="64" required autocomplete="current-password">
	</div>
	<button type="submit" class="btn primary block">로그인</button>
	<p class="hint">계정이 없으면 <a href="${ctx}/mber/joinView.do">회원가입</a></p>
</form>

<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
