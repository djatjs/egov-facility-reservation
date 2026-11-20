package egovframework.cmm.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import egovframework.cmm.LoginVO;

/** 인증·인가·CSRF 인터셉터 (DB 없이 실행) */
class SecurityInterceptorTest {

	private final AuthInterceptor auth = new AuthInterceptor();
	private final CsrfInterceptor csrf = new CsrfInterceptor();

	private static MockHttpServletRequest request(String method, String uri, String authorCd) {
		MockHttpServletRequest req = new MockHttpServletRequest(method, uri);
		MockHttpSession session = new MockHttpSession();
		if (authorCd != null) {
			LoginVO login = new LoginVO();
			login.setMberId("u1");
			login.setAuthorCd(authorCd);
			session.setAttribute(LoginVO.SESSION_KEY, login);
		}
		req.setSession(session);
		return req;
	}

	@Test
	@DisplayName("비로그인 사용자는 로그인 화면으로 보낸다")
	void anonymousRedirected() throws Exception {
		MockHttpServletResponse res = new MockHttpServletResponse();
		assertFalse(auth.preHandle(request("GET", "/resve/myList.do", null), res, null));
		assertEquals("/mber/loginView.do", res.getRedirectedUrl());
	}

	@Test
	@DisplayName("일반 회원이 /admin/** 에 접근하면 403")
	void userBlockedFromAdmin() throws Exception {
		MockHttpServletResponse res = new MockHttpServletResponse();
		assertFalse(auth.preHandle(request("GET", "/admin/resve/list.do", LoginVO.ROLE_USER), res, null));
		assertEquals(403, res.getStatus());
	}

	@Test
	@DisplayName("관리자는 /admin/**, 회원은 /resve/** 통과")
	void allowed() throws Exception {
		assertTrue(auth.preHandle(request("GET", "/admin/resve/list.do", LoginVO.ROLE_ADMIN), new MockHttpServletResponse(), null));
		assertTrue(auth.preHandle(request("GET", "/resve/myList.do", LoginVO.ROLE_USER), new MockHttpServletResponse(), null));
	}

	@Test
	@DisplayName("POST에 CSRF 토큰이 없거나 틀리면 403, 맞으면 통과. GET은 토큰만 발급")
	void csrfToken() throws Exception {
		MockHttpServletRequest get = request("GET", "/fclty/detail.do", null);
		assertTrue(csrf.preHandle(get, new MockHttpServletResponse(), null));
		String token = (String) get.getSession().getAttribute(CsrfInterceptor.SESSION_KEY);

		MockHttpServletRequest noToken = request("POST", "/resve/insert.do", null);
		noToken.setSession(get.getSession());
		MockHttpServletResponse res1 = new MockHttpServletResponse();
		assertFalse(csrf.preHandle(noToken, res1, null));
		assertEquals(403, res1.getStatus());

		MockHttpServletRequest wrong = request("POST", "/resve/insert.do", null);
		wrong.setSession(get.getSession());
		wrong.setParameter(CsrfInterceptor.PARAM, token + "x");
		assertFalse(csrf.preHandle(wrong, new MockHttpServletResponse(), null));

		MockHttpServletRequest ok = request("POST", "/resve/insert.do", null);
		ok.setSession(get.getSession());
		ok.setParameter(CsrfInterceptor.PARAM, token);
		assertTrue(csrf.preHandle(ok, new MockHttpServletResponse(), null));
	}
}
