package egovframework.cmm.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import egovframework.cmm.DefaultSearchVO;
import egovframework.cmm.LoginVO;
import egovframework.cmm.service.CmmnCodeService;
import egovframework.fclty.service.FcltyService;
import egovframework.notice.service.NoticeService;
import egovframework.resve.service.ResveService;

@Controller
public class MainController {

	@Resource(name = "noticeService")
	private NoticeService noticeService;

	@Resource(name = "fcltyService")
	private FcltyService fcltyService;

	@Resource(name = "resveService")
	private ResveService resveService;

	@Resource(name = "cmmnCodeService")
	private CmmnCodeService cmmnCodeService;

	@GetMapping("/main.do")
	public String main(HttpSession session, Model model) throws Exception {
		DefaultSearchVO recent = new DefaultSearchVO();
		recent.setFirstIndex(0);
		recent.setLastIndex(5);
		model.addAttribute("noticeList", noticeService.selectNoticeList(recent));
		model.addAttribute("fcltyTyList", cmmnCodeService.selectCodeList(CmmnCodeService.FCLTY_TY));
		model.addAttribute("fcltyTyStatList", fcltyService.selectFcltyTyStatList());

		LoginVO login = (LoginVO) session.getAttribute(LoginVO.SESSION_KEY);
		if (login != null) {
			model.addAttribute("upcomingList", resveService.selectUpcomingResveList(login.getMberId()));
		}
		return "main";
	}
}
