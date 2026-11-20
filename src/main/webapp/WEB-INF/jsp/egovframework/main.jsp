<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="공공시설 예약" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<section class="hero">
	<p>체육관, 회의실, 강당, 운동장을 온라인으로 예약하세요. 이용일 하루 전부터 30일 이내까지 신청할 수 있습니다.</p>
	<form action="${ctx}/fclty/list.do" method="get" class="search">
		<label for="fcltyTyCd">시설 유형</label>
		<select id="fcltyTyCd" name="fcltyTyCd">
			<option value="">전체</option>
			<c:forEach var="c" items="${fcltyTyList}"><option value="${c.code}"><c:out value="${c.codeNm}" /></option></c:forEach>
		</select>
		<label for="searchKeyword">시설명</label>
		<input type="text" id="searchKeyword" name="searchKeyword" maxlength="50">
		<button type="submit" class="btn primary">검색</button>
	</form>
</section>

<section>
	<h2>공지사항</h2>
	<ul class="notice">
		<c:forEach var="n" items="${noticeList}">
			<li><a href="${ctx}/notice/detail.do?nttSn=${n.nttSn}"><c:out value="${n.nttSj}" /></a>
				<span class="date"><fmt:formatDate value="${n.registDt}" pattern="yyyy-MM-dd" /></span></li>
		</c:forEach>
		<c:if test="${empty noticeList}"><li>등록된 공지사항이 없습니다.</li></c:if>
	</ul>
</section>

<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
