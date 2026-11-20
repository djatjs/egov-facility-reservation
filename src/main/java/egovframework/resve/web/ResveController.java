package egovframework.resve.web;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import egovframework.cmm.LoginVO;
import egovframework.cmm.service.CmmnCodeService;
import egovframework.cmm.web.PagingSupport;
import egovframework.resve.service.ResveSearchVO;
import egovframework.resve.service.ResveService;
import egovframework.resve.service.ResveVO;

/** 시민 예약 신청·조회·취소 (/resve/** 는 로그인 필수: AuthInterceptor) */
@Controller
public class ResveController {

	@Resource(name = "resveService")
	private ResveService resveService;

	@Resource(name = "cmmnCodeService")
	private CmmnCodeService cmmnCodeService;

	@Resource(name = "propertiesService")
	private EgovPropertyService propertiesService;

	/** 신청자·상태·번호는 요청 파라미터로 받지 않는다 (세션과 서버가 정한다) */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setDisallowedFields("mberId", "resveSttusCd", "resveSn", "returnRsn");
	}

	@PostMapping("/resve/insert.do")
	public String insert(@ModelAttribute ResveVO resveVO, @SessionAttribute(LoginVO.SESSION_KEY) LoginVO login,
			RedirectAttributes redirect) throws Exception {
		if (resveVO.getResveDe() != null) {
			resveVO.setResveDe(resveVO.getResveDe().replace("-", ""));
		}
		resveVO.setMberId(login.getMberId());
		resveService.insertResve(resveVO);
		redirect.addFlashAttribute("message", "예약을 신청했습니다. 관리자 승인 후 확정됩니다.");
		return "redirect:/resve/myList.do";
	}

	@GetMapping("/resve/myList.do")
	public String myList(@ModelAttribute("searchVO") ResveSearchVO searchVO, @SessionAttribute(LoginVO.SESSION_KEY) LoginVO login,
			Model model) throws Exception {
		searchVO.setMberId(login.getMberId());
		PaginationInfo paging = PagingSupport.prepare(searchVO, propertiesService.getInt("pageUnit"), propertiesService.getInt("pageSize"));
		paging.setTotalRecordCount(resveService.selectResveListTotCnt(searchVO));

		model.addAttribute("resultList", resveService.selectResveList(searchVO));
		model.addAttribute("paginationInfo", paging);
		model.addAttribute("sttusList", cmmnCodeService.selectCodeList(CmmnCodeService.RESVE_STTUS));
		return "resve/myList";
	}

	@GetMapping("/resve/detail.do")
	public String detail(@RequestParam Long resveSn, @SessionAttribute(LoginVO.SESSION_KEY) LoginVO login, Model model) throws Exception {
		model.addAttribute("resve", resveService.selectResve(resveSn, login));
		model.addAttribute("histList", resveService.selectResveHistList(resveSn));
		return "resve/detail";
	}

	@PostMapping("/resve/cancel.do")
	public String cancel(@RequestParam Long resveSn, @SessionAttribute(LoginVO.SESSION_KEY) LoginVO login,
			RedirectAttributes redirect) throws Exception {
		resveService.cancelResve(resveSn, login);
		redirect.addFlashAttribute("message", "예약을 취소했습니다.");
		return "redirect:/resve/myList.do";
	}
}
