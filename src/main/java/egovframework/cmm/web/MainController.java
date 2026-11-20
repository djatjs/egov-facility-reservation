package egovframework.cmm.web;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import egovframework.cmm.DefaultSearchVO;
import egovframework.cmm.service.CmmnCodeService;
import egovframework.notice.service.NoticeService;

@Controller
public class MainController {

	@Resource(name = "noticeService")
	private NoticeService noticeService;

	@Resource(name = "cmmnCodeService")
	private CmmnCodeService cmmnCodeService;

	@GetMapping("/main.do")
	public String main(Model model) throws Exception {
		DefaultSearchVO recent = new DefaultSearchVO();
		recent.setFirstIndex(0);
		recent.setLastIndex(5);
		model.addAttribute("noticeList", noticeService.selectNoticeList(recent));
		model.addAttribute("fcltyTyList", cmmnCodeService.selectCodeList(CmmnCodeService.FCLTY_TY));
		return "main";
	}
}
