<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="공공시설 예약" />
<c:set var="noPageHead" value="${true}" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>
</div><%-- 메인은 상단 배너를 화면 너비로 쓰기 위해 공통 wrap을 잠시 닫는다 --%>

<section class="hero" aria-labelledby="heroTitle">
	<div class="wrap">
		<h1 id="heroTitle">공공시설, 온라인으로 간편하게 예약하세요</h1>
		<p>체육관·회의실·강당·운동장을 이용일 하루 전부터 30일 이내까지 신청할 수 있습니다.</p>
		<form action="${ctx}/fclty/list.do" method="get" class="search" role="search">
			<div class="field">
				<label for="fcltyTyCd">시설 유형</label>
				<select id="fcltyTyCd" name="fcltyTyCd">
					<option value="">전체</option>
					<c:forEach var="c" items="${fcltyTyList}"><option value="${c.code}"><c:out value="${c.codeNm}" /></option></c:forEach>
				</select>
			</div>
			<div class="field">
				<label for="searchKeyword">시설명</label>
				<input type="text" id="searchKeyword" name="searchKeyword" maxlength="50" placeholder="예: 체육관">
			</div>
			<button type="submit" class="btn primary">시설 검색</button>
		</form>
	</div>
</section>

<div class="wrap">
	<h2>유형별 시설</h2>
	<div class="cards">
		<c:forEach var="t" items="${fcltyTyStatList}">
			<a class="card" href="${ctx}/fclty/list.do?fcltyTyCd=${t.code}">
				<span class="icon" aria-hidden="true">${fn:substring(t.codeNm,0,1)}</span>
				<h3><c:out value="${t.codeNm}" /></h3>
				<span class="num">운영 중 <strong>${t.cnt}</strong>곳</span>
			</a>
		</c:forEach>
	</div>

	<div class="two-col">
		<section aria-labelledby="myTitle">
			<div class="section-head">
				<h2 id="myTitle">다가오는 나의 예약</h2>
				<c:if test="${not empty login}"><a href="${ctx}/resve/myList.do">전체 보기</a></c:if>
			</div>
			<ul class="list-box">
				<c:choose>
					<c:when test="${empty login}">
						<li class="empty"><a href="${ctx}/mber/loginView.do">로그인</a>하면 예약 일정을 볼 수 있습니다.</li>
					</c:when>
					<c:when test="${empty upcomingList}">
						<li class="empty">예정된 예약이 없습니다.</li>
					</c:when>
					<c:otherwise>
						<c:forEach var="r" items="${upcomingList}">
							<li>
								<span><a href="${ctx}/resve/detail.do?resveSn=${r.resveSn}"><c:out value="${r.fcltyNm}" /></a><br>
									<span class="date">${fn:substring(r.resveDe,0,4)}-${fn:substring(r.resveDe,4,6)}-${fn:substring(r.resveDe,6,8)}
									${fn:substring(r.beginTm,0,2)}:${fn:substring(r.beginTm,2,4)}~${fn:substring(r.endTm,0,2)}:${fn:substring(r.endTm,2,4)}</span></span>
								<span class="badge s${r.resveSttusCd}"><c:out value="${r.resveSttusNm}" /></span>
							</li>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</ul>
		</section>

		<section aria-labelledby="noticeTitle">
			<div class="section-head">
				<h2 id="noticeTitle">공지사항</h2>
				<a href="${ctx}/notice/list.do">전체 보기</a>
			</div>
			<ul class="list-box">
				<c:forEach var="n" items="${noticeList}">
					<li><a href="${ctx}/notice/detail.do?nttSn=${n.nttSn}"><c:out value="${n.nttSj}" /></a>
						<span class="date"><fmt:formatDate value="${n.registDt}" pattern="yyyy-MM-dd" /></span></li>
				</c:forEach>
				<c:if test="${empty noticeList}"><li class="empty">등록된 공지사항이 없습니다.</li></c:if>
			</ul>
		</section>
	</div>

<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
