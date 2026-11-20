package egovframework.cmm.web;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * CSRF 방어. 세션마다 난수 토큰을 발급하고, 상태를 바꾸는 POST 요청은 폼의 _csrf 값이 일치해야 통과한다.
 * (상태 변경은 모두 POST로만 받는다)
 */
public class CsrfInterceptor implements HandlerInterceptor {

	public static final String SESSION_KEY = "CSRF_TOKEN";
	public static final String PARAM = "_csrf";

	private static final SecureRandom RANDOM = new SecureRandom();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		String token = (String) session.getAttribute(SESSION_KEY);
		if (token == null) {
			token = newToken();
			session.setAttribute(SESSION_KEY, token);
		}

		if ("POST".equalsIgnoreCase(request.getMethod())) {
			String sent = request.getParameter(PARAM);
			if (sent == null || !MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8), sent.getBytes(StandardCharsets.UTF_8))) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return false;
			}
		}
		return true;
	}

	static String newToken() {
		byte[] bytes = new byte[32];
		RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}
