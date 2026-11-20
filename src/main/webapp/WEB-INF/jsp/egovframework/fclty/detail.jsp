<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="${fclty.fcltyNm}" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<dl class="info">
	<dt>유형</dt><dd><c:out value="${fclty.fcltyTyNm}" /></dd>
	<dt>주소</dt><dd><c:out value="${fclty.adres}" /></dd>
	<dt>수용 인원</dt><dd>${fclty.acmdNmpr}명</dd>
	<dt>운영시간</dt><dd>${fn:substring(fclty.operBeginTm,0,2)}:${fn:substring(fclty.operBeginTm,2,4)} ~ ${fn:substring(fclty.operEndTm,0,2)}:${fn:substring(fclty.operEndTm,2,4)} (${fclty.resveUnitMin}분 단위)</dd>
	<dt>설명</dt><dd class="pre"><c:out value="${fclty.fcltyDc}" /></dd>
</dl>

<h2>예약 현황</h2>
<form action="${ctx}/fclty/detail.do" method="get" class="search">
	<input type="hidden" name="fcltySn" value="${fclty.fcltySn}">
	<label for="resveDeView">날짜</label>
	<input type="date" id="resveDeView" name="resveDe" value="${resveDeIso}" min="${minDe}" max="${maxDe}">
	<button type="submit" class="btn">조회</button>
</form>

<ul class="slots" aria-label="${resveDeIso} 시간대별 예약 현황">
	<c:forEach var="s" items="${slots}">
		<li class="${s.taken ? 'taken' : 'free'}">
			${fn:substring(s.beginTm,0,2)}:${fn:substring(s.beginTm,2,4)}~${fn:substring(s.endTm,0,2)}:${fn:substring(s.endTm,2,4)}
			<span>${s.taken ? '예약됨' : '가능'}</span>
		</li>
	</c:forEach>
</ul>

<h2>예약 신청</h2>
<c:choose>
	<c:when test="${empty login}">
		<p><a class="btn primary" href="${ctx}/mber/loginView.do">로그인 후 신청할 수 있습니다</a></p>
	</c:when>
	<c:otherwise>
		<form method="post" action="${ctx}/resve/insert.do" class="card">
			<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
			<input type="hidden" name="fcltySn" value="${fclty.fcltySn}">
			<input type="hidden" name="resveDe" value="${resveDe}">
			<p>예약일: <strong>${resveDeIso}</strong> · 1인 1일 최대 ${dailyLimitMin}분</p>
			<div class="row">
				<div>
					<label for="beginTm">시작</label>
					<select id="beginTm" name="beginTm" required>
						<c:forEach var="s" items="${slots}"><option value="${s.beginTm}" ${s.taken ? 'disabled' : ''}>${fn:substring(s.beginTm,0,2)}:${fn:substring(s.beginTm,2,4)}</option></c:forEach>
					</select>
				</div>
				<div>
					<label for="endTm">종료</label>
					<select id="endTm" name="endTm" required>
						<c:forEach var="s" items="${slots}"><option value="${s.endTm}">${fn:substring(s.endTm,0,2)}:${fn:substring(s.endTm,2,4)}</option></c:forEach>
					</select>
				</div>
				<div>
					<label for="useNmpr">이용 인원</label>
					<input type="number" id="useNmpr" name="useNmpr" min="1" max="${fclty.acmdNmpr}" value="1" required>
				</div>
			</div>
			<label for="usePurps">사용 목적</label>
			<input type="text" id="usePurps" name="usePurps" maxlength="200" required>
			<button type="submit" class="btn primary">신청하기</button>
		</form>
	</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
