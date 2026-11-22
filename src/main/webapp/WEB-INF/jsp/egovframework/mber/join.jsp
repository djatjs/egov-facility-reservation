<%@ page pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="회원가입" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<form method="post" action="${ctx}/mber/join.do" class="panel narrow">
	<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
	<div class="field">
		<label for="mberId">아이디</label>
		<input type="text" id="mberId" name="mberId" pattern="[a-z0-9]{4,20}" maxlength="20" required autocomplete="username" aria-describedby="mberIdHint">
		<span class="hint" id="mberIdHint">영문 소문자와 숫자로 4~20자</span>
	</div>
	<div class="field">
		<label for="password">비밀번호</label>
		<input type="password" id="password" name="password" minlength="8" maxlength="64" required autocomplete="new-password" aria-describedby="passwordHint">
		<span class="hint" id="passwordHint">8~64자</span>
	</div>
	<div class="field">
		<label for="mberNm">이름</label>
		<input type="text" id="mberNm" name="mberNm" maxlength="16" required autocomplete="name">
	</div>
	<button type="submit" class="btn primary block">가입하기</button>
</form>

<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
