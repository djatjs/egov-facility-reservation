<%@ page pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="${empty fclty.fcltySn ? '시설 등록' : '시설 수정'}" />
<c:set var="menu" value="adminFclty" />
<c:set var="crumbName" value="시설 관리" />
<c:set var="crumbUrl" value="/admin/fclty/list.do" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<form method="post" action="${ctx}/admin/fclty/save.do" class="panel">
	<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
	<c:if test="${not empty fclty.fcltySn}"><input type="hidden" name="fcltySn" value="${fclty.fcltySn}"></c:if>

	<div class="grid">
		<div class="field">
			<label for="fcltyTyCd">유형</label>
			<select id="fcltyTyCd" name="fcltyTyCd" class="full" required>
				<c:forEach var="c" items="${fcltyTyList}">
					<option value="${c.code}" ${c.code eq fclty.fcltyTyCd ? 'selected' : ''}><c:out value="${c.codeNm}" /></option>
				</c:forEach>
			</select>
		</div>
		<div class="field">
			<label for="fcltyNm">시설명</label>
			<input type="text" id="fcltyNm" name="fcltyNm" value="<c:out value='${fclty.fcltyNm}'/>" maxlength="33" required>
		</div>
	</div>
	<div class="field">
		<label for="adres">주소</label>
		<input type="text" id="adres" name="adres" value="<c:out value='${fclty.adres}'/>" maxlength="66" required>
	</div>
	<div class="grid">
		<div class="field">
			<label for="acmdNmpr">수용 인원</label>
			<input type="number" id="acmdNmpr" name="acmdNmpr" value="${fclty.acmdNmpr}" min="1" max="10000" required>
		</div>
		<div class="field">
			<label for="operBeginTm">운영 시작</label>
			<input type="text" id="operBeginTm" name="operBeginTm" value="<c:out value='${fclty.operBeginTm}'/>" pattern="\d{4}" maxlength="4" required aria-describedby="tmHint">
		</div>
		<div class="field">
			<label for="operEndTm">운영 종료</label>
			<input type="text" id="operEndTm" name="operEndTm" value="<c:out value='${fclty.operEndTm}'/>" pattern="\d{4}" maxlength="4" required aria-describedby="tmHint">
		</div>
		<div class="field">
			<label for="resveUnitMin">예약 단위</label>
			<select id="resveUnitMin" name="resveUnitMin" class="full">
				<c:forEach var="u" items="30,60,120"><option value="${u}" ${u eq fclty.resveUnitMin ? 'selected' : ''}>${u}분</option></c:forEach>
			</select>
		</div>
	</div>
	<p class="hint" id="tmHint">시간은 24시간제 4자리로 입력합니다 (예: 0900, 2200). 운영시간은 예약 단위로 나누어떨어져야 합니다.</p>
	<div class="field">
		<label for="fcltyDc">설명</label>
		<textarea id="fcltyDc" name="fcltyDc" rows="4" maxlength="666"><c:out value="${fclty.fcltyDc}" /></textarea>
	</div>
	<c:choose>
		<c:when test="${empty fclty.fcltySn}"><input type="hidden" name="useYn" value="Y"></c:when>
		<c:otherwise>
			<div class="field">
				<label for="useYn">사용 여부</label>
				<select id="useYn" name="useYn">
					<option value="Y" ${fclty.useYn eq 'Y' ? 'selected' : ''}>사용</option>
					<option value="N" ${fclty.useYn eq 'N' ? 'selected' : ''}>중지 (신규 예약 불가)</option>
				</select>
			</div>
		</c:otherwise>
	</c:choose>
	<div class="actions">
		<button type="submit" class="btn primary">저장</button>
		<a class="btn" href="${ctx}/admin/fclty/list.do">목록</a>
	</div>
</form>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
