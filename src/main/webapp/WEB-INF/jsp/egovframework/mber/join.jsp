<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="회원가입" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<form method="post" action="${ctx}/mber/join.do" class="card narrow">
	<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
	<label for="mberId">아이디 <small>(영문 소문자·숫자 4~20자)</small></label>
	<input type="text" id="mberId" name="mberId" pattern="[a-z0-9]{4,20}" maxlength="20" required autocomplete="username">
	<label for="password">비밀번호 <small>(8~64자)</small></label>
	<input type="password" id="password" name="password" minlength="8" maxlength="64" required autocomplete="new-password">
	<label for="mberNm">이름</label>
	<input type="text" id="mberNm" name="mberNm" maxlength="50" required autocomplete="name">
	<button type="submit" class="btn primary block">가입하기</button>
</form>

<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
