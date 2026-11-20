<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="${empty notice.nttSn ? '공지사항 등록' : '공지사항 수정'}" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<form method="post" action="${ctx}/admin/notice/save.do" class="card">
	<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
	<c:if test="${not empty notice.nttSn}"><input type="hidden" name="nttSn" value="${notice.nttSn}"></c:if>
	<label for="nttSj">제목</label>
	<input type="text" id="nttSj" name="nttSj" value="<c:out value='${notice.nttSj}'/>" maxlength="100" required>
	<label for="nttCn">내용</label>
	<textarea id="nttCn" name="nttCn" rows="12" required><c:out value="${notice.nttCn}" /></textarea>
	<button type="submit" class="btn primary">저장</button>
	<a class="btn" href="${ctx}/notice/list.do">취소</a>
</form>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
