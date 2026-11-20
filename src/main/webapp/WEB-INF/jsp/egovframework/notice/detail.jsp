<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="공지사항" />
<c:set var="menu" value="notice" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<article class="panel">
	<h2 style="margin-top:0"><c:out value="${notice.nttSj}" /></h2>
	<p class="meta">등록일 <fmt:formatDate value="${notice.registDt}" pattern="yyyy-MM-dd" /> · 조회 ${notice.rdcnt + 1}</p>
	<div class="pre"><c:out value="${notice.nttCn}" /></div>
</article>

<div class="actions">
	<a class="btn" href="${ctx}/notice/list.do">목록</a>
	<c:if test="${login.admin}">
		<a class="btn right" href="${ctx}/admin/notice/form.do?nttSn=${notice.nttSn}">수정</a>
		<form method="post" action="${ctx}/admin/notice/delete.do" class="inline" onsubmit="return confirm('공지사항을 삭제할까요?');">
			<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
			<input type="hidden" name="nttSn" value="${notice.nttSn}">
			<button type="submit" class="btn danger">삭제</button>
		</form>
	</c:if>
</div>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
