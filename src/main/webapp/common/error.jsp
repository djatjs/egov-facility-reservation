<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" isErrorPage="true"%>
<%-- 원인과 스택 트레이스는 서버 로그에만 남기고 화면에는 노출하지 않는다 --%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>오류 | 공공시설 예약</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fclty.css">
</head>
<body>
<main class="wrap" style="padding-top:40px">
	<h1>요청을 처리할 수 없습니다</h1>
	<p class="alert error">페이지가 없거나 접근 권한이 없습니다. 문제가 계속되면 관리자에게 문의하세요.</p>
	<p><a class="btn" href="${pageContext.request.contextPath}/main.do">처음으로</a></p>
</main>
</body>
</html>
