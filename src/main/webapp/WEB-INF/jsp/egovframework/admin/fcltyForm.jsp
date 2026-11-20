<%@ include file="/WEB-INF/jsp/egovframework/cmmn/taglibs.jspf" %>
<c:set var="pageTitle" value="${empty fclty.fcltySn ? '시설 등록' : '시설 수정'}" />
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/header.jspf" %>

<form method="post" action="${ctx}/admin/fclty/save.do" class="card">
	<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.CSRF_TOKEN}'/>">
	<c:if test="${not empty fclty.fcltySn}"><input type="hidden" name="fcltySn" value="${fclty.fcltySn}"></c:if>

	<label for="fcltyTyCd">유형</label>
	<select id="fcltyTyCd" name="fcltyTyCd" required>
		<c:forEach var="c" items="${fcltyTyList}">
			<option value="${c.code}" ${c.code eq fclty.fcltyTyCd ? 'selected' : ''}><c:out value="${c.codeNm}" /></option>
		</c:forEach>
	</select>
	<label for="fcltyNm">시설명</label>
	<input type="text" id="fcltyNm" name="fcltyNm" value="<c:out value='${fclty.fcltyNm}'/>" maxlength="100" required>
	<label for="adres">주소</label>
	<input type="text" id="adres" name="adres" value="<c:out value='${fclty.adres}'/>" maxlength="200" required>
	<div class="row">
		<div>
			<label for="acmdNmpr">수용 인원</label>
			<input type="number" id="acmdNmpr" name="acmdNmpr" value="${fclty.acmdNmpr}" min="1" max="10000" required>
		</div>
		<div>
			<label for="operBeginTm">운영 시작 (HHMM)</label>
			<input type="text" id="operBeginTm" name="operBeginTm" value="<c:out value='${fclty.operBeginTm}'/>" pattern="\d{4}" maxlength="4" required>
		</div>
		<div>
			<label for="operEndTm">운영 종료 (HHMM)</label>
			<input type="text" id="operEndTm" name="operEndTm" value="<c:out value='${fclty.operEndTm}'/>" pattern="\d{4}" maxlength="4" required>
		</div>
		<div>
			<label for="resveUnitMin">예약 단위</label>
			<select id="resveUnitMin" name="resveUnitMin">
				<c:forEach var="u" items="30,60,120"><option value="${u}" ${u eq fclty.resveUnitMin ? 'selected' : ''}>${u}분</option></c:forEach>
			</select>
		</div>
	</div>
	<label for="fcltyDc">설명</label>
	<textarea id="fcltyDc" name="fcltyDc" rows="4" maxlength="2000"><c:out value="${fclty.fcltyDc}" /></textarea>
	<c:choose>
		<c:when test="${empty fclty.fcltySn}"><input type="hidden" name="useYn" value="Y"></c:when>
		<c:otherwise>
			<label for="useYn">사용 여부</label>
			<select id="useYn" name="useYn">
				<option value="Y" ${fclty.useYn eq 'Y' ? 'selected' : ''}>사용</option>
				<option value="N" ${fclty.useYn eq 'N' ? 'selected' : ''}>중지 (신규 예약 불가)</option>
			</select>
		</c:otherwise>
	</c:choose>
	<button type="submit" class="btn primary">저장</button>
	<a class="btn" href="${ctx}/admin/fclty/list.do">목록</a>
</form>
<%@ include file="/WEB-INF/jsp/egovframework/cmmn/footer.jspf" %>
