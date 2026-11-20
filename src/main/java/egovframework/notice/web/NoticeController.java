package egovframework.notice.web;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import egovframework.cmm.DefaultSearchVO;
import egovframework.cmm.web.PagingSupport;
import egovframework.notice.service.NoticeService;

@Controller
public class NoticeController {

	@Resource(name = "noticeService")
	private NoticeService noticeService;

	@Resource(name = "propertiesService")
	private EgovPropertyService propertiesService;

	@GetMapping("/notice/list.do")
	public String list(@ModelAttribute("searchVO") DefaultSearchVO searchVO, Model model) throws Exception {
		PaginationInfo paging = PagingSupport.prepare(searchVO, propertiesService.getInt("pageUnit"), propertiesService.getInt("pageSize"));
		paging.setTotalRecordCount(noticeService.selectNoticeListTotCnt(searchVO));
		model.addAttribute("resultList", noticeService.selectNoticeList(searchVO));
		model.addAttribute("paginationInfo", paging);
		return "notice/list";
	}

	@GetMapping("/notice/detail.do")
	public String detail(@RequestParam Long nttSn, Model model) throws Exception {
		model.addAttribute("notice", noticeService.selectNotice(nttSn, true));
		return "notice/detail";
	}
}
