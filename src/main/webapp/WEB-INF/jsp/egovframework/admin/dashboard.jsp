<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="대시보드" />
<c:set var="menu" value="adminDash" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<div class="stats">
	<a class="stat ${stat.pendingCnt gt 0 ? 'attention' : ''}" href="${ctx}/admin/resve/list.do?resveSttusCd=01">
		<p class="label">승인 대기</p>
		<p class="value">${stat.pendingCnt}<small>건</small></p>
		<span class="hint">처리가 필요한 신청</span>
	</a>
	<div class="stat">
		<p class="label">오늘 이용 예정</p>
		<p class="value">${stat.todayCnt}<small>건</small></p>
		<span class="hint">승인된 오늘 예약</span>
	</div>
	<div class="stat">
		<p class="label">최근 7일 신청</p>
		<p class="value">${stat.weekApplyCnt}<small>건</small></p>
		<span class="hint">오늘 포함 7일</span>
	</div>
	<div class="stat">
		<p class="label">이번 달 반려율</p>
		<p class="value">${stat.monthReturnRate}<small>%</small></p>
		<span class="hint">처리 ${stat.monthDecidedCnt}건 중 반려 ${stat.monthReturnCnt}건</span>
	</div>
</div>

<div class="two-col">
	<section aria-labelledby="pendingTitle">
		<div class="section-head">
			<h2 id="pendingTitle">먼저 처리할 신청</h2>
			<a href="${ctx}/admin/resve/list.do?resveSttusCd=01">전체 보기</a>
		</div>
		<ul class="list-box">
			<c:forEach var="r" items="${pendingList}">
				<li>
					<span><a href="${ctx}/admin/resve/detail.do?resveSn=${r.resveSn}"><c:out value="${r.fcltyNm}" /></a>
						· <c:out value="${r.mberNm}" /><br>
						<span class="date">이용 ${fn:substring(r.resveDe,0,4)}-${fn:substring(r.resveDe,4,6)}-${fn:substring(r.resveDe,6,8)}
						${fn:substring(r.beginTm,0,2)}:${fn:substring(r.beginTm,2,4)}~${fn:substring(r.endTm,0,2)}:${fn:substring(r.endTm,2,4)}
						· 신청 <fmt:formatDate value="${r.registDt}" pattern="MM-dd HH:mm" /></span></span>
					<a class="btn small" href="${ctx}/admin/resve/detail.do?resveSn=${r.resveSn}">처리</a>
				</li>
			</c:forEach>
			<c:if test="${empty pendingList}"><li class="empty">승인 대기 중인 신청이 없습니다.</li></c:if>
		</ul>
	</section>

	<section aria-labelledby="todayTitle">
		<h2 id="todayTitle">오늘 이용 예정</h2>
		<div class="table-wrap">
			<table class="list">
				<caption class="sr-only">오늘 이용 예정 예약: 시간, 시설, 신청자, 인원</caption>
				<thead><tr><th scope="col">시간</th><th scope="col">시설</th><th scope="col">신청자</th><th scope="col">인원</th></tr></thead>
				<tbody>
				<c:forEach var="r" items="${todayList}">
					<tr>
						<td>${fn:substring(r.beginTm,0,2)}:${fn:substring(r.beginTm,2,4)}~${fn:substring(r.endTm,0,2)}:${fn:substring(r.endTm,2,4)}</td>
						<td class="left"><a href="${ctx}/admin/resve/detail.do?resveSn=${r.resveSn}"><c:out value="${r.fcltyNm}" /></a></td>
						<td><c:out value="${r.mberNm}" /></td>
						<td>${r.useNmpr}명</td>
					</tr>
				</c:forEach>
				<c:if test="${empty todayList}"><tr><td colspan="4" class="empty">오늘 이용 예정인 예약이 없습니다.</td></tr></c:if>
				</tbody>
			</table>
		</div>
	</section>
</div>

<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
