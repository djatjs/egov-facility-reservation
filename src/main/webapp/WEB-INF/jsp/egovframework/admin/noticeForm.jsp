<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="${empty notice.nttSn ? '공지사항 등록' : '공지사항 수정'}" />
<c:set var="menu" value="notice" />
<c:set var="crumbName" value="공지사항" />
<c:set var="crumbUrl" value="/notice/list.do" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<form method="post" action="${ctx}/admin/notice/save.do" class="panel">
	<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
	<c:if test="${not empty notice.nttSn}"><input type="hidden" name="nttSn" value="${notice.nttSn}"></c:if>
	<div class="field">
		<label for="nttSj">제목 <span class="hint">(100자 이내)</span></label>
		<input type="text" id="nttSj" name="nttSj" value="<c:out value='${notice.nttSj}'/>" maxlength="100" required>
	</div>
	<div class="field">
		<label for="nttCn">내용</label>
		<textarea id="nttCn" name="nttCn" rows="12" required><c:out value="${notice.nttCn}" /></textarea>
	</div>
	<div class="actions">
		<button type="submit" class="btn primary">저장</button>
		<a class="btn" href="${ctx}/notice/list.do">취소</a>
	</div>
</form>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
