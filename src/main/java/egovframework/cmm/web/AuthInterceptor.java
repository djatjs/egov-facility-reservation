package egovframework.cmm.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

import egovframework.cmm.LoginVO;

/**
 * 인증·인가 인터셉터.
 * /admin/** 는 관리자만, 그 외 매핑된 경로(/resve/**)는 로그인 사용자만 통과시킨다.
 * 본인 예약인지 같은 데이터 단위 인가는 서비스 계층에서 한 번 더 확인한다.
 */
public class AuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession(false);
		LoginVO login = session == null ? null : (LoginVO) session.getAttribute(LoginVO.SESSION_KEY);

		if (login == null) {
			response.sendRedirect(request.getContextPath() + "/mber/loginView.do");
			return false;
		}

		String path = request.getRequestURI().substring(request.getContextPath().length());
		if (path.startsWith("/admin/") && !login.isAdmin()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return false;
		}
		return true;
	}
}
