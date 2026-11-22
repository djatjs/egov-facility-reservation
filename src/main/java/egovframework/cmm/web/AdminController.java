package egovframework.cmm.web;

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
import egovframework.fclty.service.FcltySearchVO;
import egovframework.fclty.service.FcltyService;
import egovframework.fclty.service.FcltyVO;
import egovframework.notice.service.NoticeService;
import egovframework.notice.service.NoticeVO;
import egovframework.resve.service.ResveSearchVO;
import egovframework.resve.service.ResveService;

/** 관리자 화면 (/admin/** 는 관리자만: AuthInterceptor) */
@Controller
public class AdminController {

	@Resource(name = "fcltyService")
	private FcltyService fcltyService;

	@Resource(name = "resveService")
	private ResveService resveService;

	@Resource(name = "noticeService")
	private NoticeService noticeService;

	@Resource(name = "cmmnCodeService")
	private CmmnCodeService cmmnCodeService;

	@Resource(name = "propertiesService")
	private EgovPropertyService propertiesService;

	/** 등록자·수정자는 세션에서 채운다 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setDisallowedFields("registerId", "updusrId", "mberId");
	}

	private PaginationInfo paging(egovframework.cmm.DefaultSearchVO searchVO) {
		return PagingSupport.prepare(searchVO, propertiesService.getInt("pageUnit"), propertiesService.getInt("pageSize"));
	}

	// ===== 대시보드 =====

	@GetMapping("/admin/dashboard.do")
	public String dashboard(Model model) throws Exception {
		model.addAttribute("stat", resveService.selectDashboard());
		model.addAttribute("pendingList", resveService.selectPendingResveList(5));
		model.addAttribute("todayList", resveService.selectTodayResveList());
		return "admin/dashboard";
	}

	// ===== 시설 =====

	@GetMapping("/admin/fclty/list.do")
	public String fcltyList(@ModelAttribute("searchVO") FcltySearchVO searchVO, Model model) throws Exception {
		PaginationInfo paging = paging(searchVO);
		paging.setTotalRecordCount(fcltyService.selectFcltyListTotCnt(searchVO));
		model.addAttribute("resultList", fcltyService.selectFcltyList(searchVO));
		model.addAttribute("paginationInfo", paging);
		model.addAttribute("fcltyTyList", cmmnCodeService.selectCodeList(CmmnCodeService.FCLTY_TY));
		return "admin/fcltyList";
	}

	@GetMapping("/admin/fclty/form.do")
	public String fcltyForm(@RequestParam(required = false) Long fcltySn, Model model) throws Exception {
		FcltyVO fclty;
		if (fcltySn == null) {
			fclty = new FcltyVO();
			fclty.setOperBeginTm("0900");
			fclty.setOperEndTm("2200");
			fclty.setResveUnitMin(60);
			fclty.setUseYn("Y");
		} else {
			fclty = fcltyService.selectFclty(fcltySn);
		}
		model.addAttribute("fclty", fclty);
		model.addAttribute("fcltyTyList", cmmnCodeService.selectCodeList(CmmnCodeService.FCLTY_TY));
		return "admin/fcltyForm";
	}

	@PostMapping("/admin/fclty/save.do")
	public String fcltySave(@ModelAttribute FcltyVO fcltyVO, @SessionAttribute(LoginVO.SESSION_KEY) LoginVO login,
			RedirectAttributes redirect) throws Exception {
		if (fcltyVO.getFcltySn() == null) {
			fcltyVO.setRegisterId(login.getMberId());
			fcltyService.insertFclty(fcltyVO);
		} else {
			fcltyVO.setUpdusrId(login.getMberId());
			fcltyService.updateFclty(fcltyVO);
		}
		redirect.addFlashAttribute("message", "시설 정보를 저장했습니다.");
		return "redirect:/admin/fclty/list.do";
	}

	// ===== 예약 =====

	@GetMapping("/admin/resve/list.do")
	public String resveList(@ModelAttribute("searchVO") ResveSearchVO searchVO, Model model) throws Exception {
		searchVO.setMberId(null);
		searchVO.setBeginDe(searchVO.getBeginDe() == null ? "" : searchVO.getBeginDe().replace("-", ""));
		searchVO.setEndDe(searchVO.getEndDe() == null ? "" : searchVO.getEndDe().replace("-", ""));
		PaginationInfo paging = paging(searchVO);
		paging.setTotalRecordCount(resveService.selectResveListTotCnt(searchVO));

		FcltySearchVO all = new FcltySearchVO();
		all.setFirstIndex(0);
		all.setLastIndex(1000);
		model.addAttribute("resultList", resveService.selectResveList(searchVO));
		model.addAttribute("paginationInfo", paging);
		model.addAttribute("sttusList", cmmnCodeService.selectCodeList(CmmnCodeService.RESVE_STTUS));
		model.addAttribute("fcltyList", fcltyService.selectFcltyList(all));
		return "admin/resveList";
	}

	@GetMapping("/admin/resve/detail.do")
	public String resveDetail(@RequestParam Long resveSn, @SessionAttribute(LoginVO.SESSION_KEY) LoginVO login, Model model) throws Exception {
		model.addAttribute("resve", resveService.selectResve(resveSn, login));
		model.addAttribute("histList", resveService.selectResveHistList(resveSn));
		return "admin/resveDetail";
	}

	@PostMapping("/admin/resve/approve.do")
	public String approve(@RequestParam Long resveSn, @SessionAttribute(LoginVO.SESSION_KEY) LoginVO login,
			RedirectAttributes redirect) throws Exception {
		resveService.approveResve(resveSn, login);
		redirect.addFlashAttribute("message", "승인했습니다.");
		return "redirect:/admin/resve/detail.do?resveSn=" + resveSn;
	}

	@PostMapping("/admin/resve/reject.do")
	public String reject(@RequestParam Long resveSn, @RequestParam(required = false) String returnRsn,
			@SessionAttribute(LoginVO.SESSION_KEY) LoginVO login, RedirectAttributes redirect) throws Exception {
		resveService.rejectResve(resveSn, returnRsn, login);
		redirect.addFlashAttribute("message", "반려했습니다.");
		return "redirect:/admin/resve/detail.do?resveSn=" + resveSn;
	}

	// ===== 시설 유형 (SR-01) =====

	@GetMapping("/admin/fcltyTy/list.do")
	public String fcltyTyList(Model model) throws Exception {
		model.addAttribute("fcltyTyList", cmmnCodeService.selectCodeList(CmmnCodeService.FCLTY_TY));
		model.addAttribute("nameMax", CmmnCodeService.FCLTY_TY_NM_MAX);
		return "admin/fcltyTyList";
	}

	@PostMapping("/admin/fcltyTy/save.do")
	public String fcltyTySave(@RequestParam String code, @RequestParam(required = false) String codeNm,
			RedirectAttributes redirect) throws Exception {
		cmmnCodeService.updateFcltyTyNm(code, codeNm);
		redirect.addFlashAttribute("message", "시설 유형 이름을 저장했습니다.");
		return "redirect:/admin/fcltyTy/list.do";
	}

	// ===== 공지 =====

	@GetMapping("/admin/notice/form.do")
	public String noticeForm(@RequestParam(required = false) Long nttSn, Model model) throws Exception {
		model.addAttribute("notice", nttSn == null ? new NoticeVO() : noticeService.selectNotice(nttSn, false));
		return "admin/noticeForm";
	}

	@PostMapping("/admin/notice/save.do")
	public String noticeSave(@ModelAttribute NoticeVO noticeVO, @SessionAttribute(LoginVO.SESSION_KEY) LoginVO login,
			RedirectAttributes redirect) throws Exception {
		if (noticeVO.getNttSn() == null) {
			noticeVO.setRegisterId(login.getMberId());
			noticeService.insertNotice(noticeVO);
		} else {
			noticeVO.setUpdusrId(login.getMberId());
			noticeService.updateNotice(noticeVO);
		}
		redirect.addFlashAttribute("message", "공지사항을 저장했습니다.");
		return "redirect:/notice/detail.do?nttSn=" + noticeVO.getNttSn();
	}

	@PostMapping("/admin/notice/delete.do")
	public String noticeDelete(@RequestParam Long nttSn, @SessionAttribute(LoginVO.SESSION_KEY) LoginVO login,
			RedirectAttributes redirect) throws Exception {
		noticeService.deleteNotice(nttSn, login.getMberId());
		redirect.addFlashAttribute("message", "공지사항을 삭제했습니다.");
		return "redirect:/notice/list.do";
	}
}
