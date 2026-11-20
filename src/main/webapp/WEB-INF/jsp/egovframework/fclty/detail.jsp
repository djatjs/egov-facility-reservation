<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="${fclty.fcltyNm}" />
<c:set var="menu" value="fclty" />
<c:set var="crumbName" value="시설 찾기" />
<c:set var="crumbUrl" value="/fclty/list.do" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<dl class="info">
	<dt>유형</dt><dd><c:out value="${fclty.fcltyTyNm}" /></dd>
	<dt>주소</dt><dd><c:out value="${fclty.adres}" /></dd>
	<dt>수용 인원</dt><dd>${fclty.acmdNmpr}명</dd>
	<dt>운영시간</dt><dd>${fn:substring(fclty.operBeginTm,0,2)}:${fn:substring(fclty.operBeginTm,2,4)} ~ ${fn:substring(fclty.operEndTm,0,2)}:${fn:substring(fclty.operEndTm,2,4)} (${fclty.resveUnitMin}분 단위)</dd>
	<dt>설명</dt><dd class="pre"><c:out value="${fclty.fcltyDc}" /></dd>
</dl>

<h2>예약 현황</h2>
<%-- 날짜를 바꿔도 바로 이동하지 않고 [조회]를 눌러야 바뀐다 (입력만으로 화면이 바뀌지 않게: 접근성) --%>
<form action="${ctx}/fclty/detail.do" method="get" class="search">
	<input type="hidden" name="fcltySn" value="${fclty.fcltySn}">
	<div class="field">
		<label for="resveDeView">이용일</label>
		<input type="date" id="resveDeView" name="resveDe" value="${resveDeIso}" min="${minDe}" max="${maxDe}">
	</div>
	<button type="submit" class="btn">조회</button>
	<span class="hint">내일(${minDe})부터 ${maxDe}까지 신청할 수 있습니다.</span>
</form>

<p class="legend" aria-hidden="true"><span>신청 가능</span><span class="l-taken">예약됨</span><span class="l-sel">선택한 시간</span></p>
<ul class="slots" id="slots" aria-label="${resveDeIso} 시간대별 예약 현황. 신청할 시간을 차례로 선택하세요">
	<c:forEach var="s" items="${slots}">
		<c:set var="label" value="${fn:substring(s.beginTm,0,2)}:${fn:substring(s.beginTm,2,4)}~${fn:substring(s.endTm,0,2)}:${fn:substring(s.endTm,2,4)}" />
		<li>
			<button type="button" class="slot" data-begin="${s.beginTm}" data-end="${s.endTm}" aria-pressed="false" ${s.taken ? 'disabled' : ''}>
				${label}<span class="st">${s.taken ? '예약됨' : '가능'}</span>
			</button>
		</li>
	</c:forEach>
</ul>

<h2 id="applyTitle">예약 신청</h2>
<c:choose>
	<c:when test="${empty login}">
		<div class="alert">로그인한 회원만 신청할 수 있습니다. <a href="${ctx}/mber/loginView.do">로그인하기</a></div>
	</c:when>
	<c:otherwise>
		<form method="post" action="${ctx}/resve/insert.do" class="panel" aria-labelledby="applyTitle">
			<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
			<input type="hidden" name="fcltySn" value="${fclty.fcltySn}">
			<input type="hidden" name="resveDe" value="${resveDe}">
			<p class="summary" id="summary" aria-live="polite">이용일 ${resveDeIso} · 위 시간표에서 시간을 선택하세요</p>
			<div class="grid">
				<div class="field">
					<label for="beginTm">시작 시간</label>
					<select id="beginTm" name="beginTm" class="full" required>
						<c:forEach var="s" items="${slots}"><option value="${s.beginTm}" ${s.taken ? 'disabled' : ''}>${fn:substring(s.beginTm,0,2)}:${fn:substring(s.beginTm,2,4)}</option></c:forEach>
					</select>
				</div>
				<div class="field">
					<label for="endTm">종료 시간</label>
					<select id="endTm" name="endTm" class="full" required>
						<c:forEach var="s" items="${slots}"><option value="${s.endTm}">${fn:substring(s.endTm,0,2)}:${fn:substring(s.endTm,2,4)}</option></c:forEach>
					</select>
				</div>
				<div class="field">
					<label for="useNmpr">이용 인원 <span class="hint">(최대 ${fclty.acmdNmpr}명)</span></label>
					<input type="number" id="useNmpr" name="useNmpr" min="1" max="${fclty.acmdNmpr}" value="1" required>
				</div>
			</div>
			<div class="field">
				<label for="usePurps">사용 목적 <span class="hint">(200자 이내)</span></label>
				<input type="text" id="usePurps" name="usePurps" maxlength="200" required>
			</div>
			<p class="hint">1인 1일 최대 <fmt:formatNumber value="${dailyLimitMin / 60}" maxFractionDigits="1" />시간까지 신청할 수 있으며, 관리자 승인 후 확정됩니다.</p>
			<button type="submit" class="btn primary">예약 신청</button>
		</form>
	</c:otherwise>
</c:choose>

<script>
(function () {
	var slots = Array.prototype.slice.call(document.querySelectorAll('#slots .slot'));
	var begin = document.getElementById('beginTm');
	var end = document.getElementById('endTm');
	var summary = document.getElementById('summary');
	var from = -1, to = -1; // 선택한 슬롯 범위 (인덱스)

	function fmt(t) { return t.substring(0, 2) + ':' + t.substring(2); }

	function render() {
		slots.forEach(function (b, i) { b.setAttribute('aria-pressed', from >= 0 && i >= from && i <= to ? 'true' : 'false'); });
		if (from < 0 || !begin) { return; }
		begin.value = slots[from].dataset.begin;
		end.value = slots[to].dataset.end;
		var hours = (to - from + 1) * ${fclty.resveUnitMin} / 60;
		summary.textContent = '이용일 ${resveDeIso} · ' + fmt(begin.value) + '~' + fmt(end.value) + ' (' + hours + '시간)';
	}

	// 첫 클릭은 한 칸 선택, 이어서 뒤쪽 칸을 누르면 그 사이가 모두 비어 있을 때만 범위로 늘린다
	slots.forEach(function (b, i) {
		b.addEventListener('click', function () {
			var extend = from >= 0 && i > to && slots.slice(from, i + 1).every(function (s) { return !s.disabled; });
			if (extend) { to = i; } else if (from === i && to === i) { from = to = -1; } else { from = to = i; }
			render();
		});
	});
})();
</script>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
