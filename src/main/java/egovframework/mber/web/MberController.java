package egovframework.mber.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import egovframework.cmm.LoginVO;
import egovframework.mber.service.MberService;
import egovframework.mber.service.MberVO;

@Controller
public class MberController {

	@Resource(name = "mberService")
	private MberService mberService;

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	@GetMapping("/mber/loginView.do")
	public String loginView() {
		return "mber/login";
	}

	@PostMapping("/mber/login.do")
	public String login(@RequestParam(required = false) String mberId, @RequestParam(required = false) String password,
			HttpServletRequest request, Model model) throws Exception {
		LoginVO login = mberService.login(mberId, password);
		if (login == null) {
			model.addAttribute("errorMessage", messageSource.getMessage("mber.login.fail", null, request.getLocale()));
			model.addAttribute("mberId", mberId);
			return "mber/login";
		}
		// 세션 고정 공격 방지: 로그인 전 세션을 버리고 새 세션을 발급한다
		HttpSession old = request.getSession(false);
		if (old != null) {
			old.invalidate();
		}
		request.getSession(true).setAttribute(LoginVO.SESSION_KEY, login);
		return login.isAdmin() ? "redirect:/admin/dashboard.do" : "redirect:/main.do";
	}

	@PostMapping("/mber/logout.do")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return "redirect:/main.do";
	}

	@GetMapping("/mber/joinView.do")
	public String joinView() {
		return "mber/join";
	}

	@PostMapping("/mber/join.do")
	public String join(@ModelAttribute MberVO mberVO, RedirectAttributes redirect) throws Exception {
		mberService.insertMber(mberVO);
		redirect.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인하세요.");
		return "redirect:/mber/loginView.do";
	}
}
