package egovframework.fclty.web;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import egovframework.cmm.TimeUtil;
import egovframework.cmm.service.CmmnCodeService;
import egovframework.cmm.web.PagingSupport;
import egovframework.fclty.service.FcltySearchVO;
import egovframework.fclty.service.FcltyService;
import egovframework.fclty.service.FcltyVO;
import egovframework.resve.service.ResveService;
import egovframework.resve.service.ResveVO;

/** 시민용 시설 목록·상세 (예약 가능 시간표 포함) */
@Controller
public class FcltyController {

	@Resource(name = "fcltyService")
	private FcltyService fcltyService;

	@Resource(name = "resveService")
	private ResveService resveService;

	@Resource(name = "cmmnCodeService")
	private CmmnCodeService cmmnCodeService;

	@Resource(name = "propertiesService")
	private EgovPropertyService propertiesService;

	@GetMapping("/fclty/list.do")
	public String list(@ModelAttribute("searchVO") FcltySearchVO searchVO, Model model) throws Exception {
		searchVO.setUseYn("Y"); // 시민 화면은 사용 중인 시설만
		PaginationInfo paging = PagingSupport.prepare(searchVO, propertiesService.getInt("pageUnit"), propertiesService.getInt("pageSize"));
		paging.setTotalRecordCount(fcltyService.selectFcltyListTotCnt(searchVO));

		model.addAttribute("resultList", fcltyService.selectFcltyList(searchVO));
		model.addAttribute("paginationInfo", paging);
		model.addAttribute("fcltyTyList", cmmnCodeService.selectCodeList(CmmnCodeService.FCLTY_TY));
		return "fclty/list";
	}

	@GetMapping("/fclty/detail.do")
	public String detail(@RequestParam Long fcltySn, @RequestParam(required = false) String resveDe, Model model) throws Exception {
		FcltyVO fclty = fcltyService.selectFclty(fcltySn);
		if (!"Y".equals(fclty.getUseYn())) {
			return "redirect:/fclty/list.do";
		}

		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
		LocalDate min = today.plusDays(1);
		LocalDate max = today.plusDays(propertiesService.getInt("resveMaxDaysAhead"));
		LocalDate day = TimeUtil.parseDe(resveDe == null ? null : resveDe.replace("-", ""));
		if (day == null || day.isBefore(min) || day.isAfter(max)) {
			day = min;
		}
		String de = TimeUtil.toDe(day);

		List<ResveVO> booked = resveService.selectDayResveList(fcltySn, de);
		int open = TimeUtil.toMinutes(fclty.getOperBeginTm());
		int close = TimeUtil.toMinutes(fclty.getOperEndTm());
		int unit = fclty.getResveUnitMin();

		List<Map<String, Object>> slots = new ArrayList<>();
		for (int t = open; t < close; t += unit) {
			int begin = t, end = t + unit;
			boolean taken = booked.stream().anyMatch(r -> TimeUtil.toMinutes(r.getBeginTm()) < end && TimeUtil.toMinutes(r.getEndTm()) > begin);
			Map<String, Object> slot = new LinkedHashMap<>();
			slot.put("beginTm", TimeUtil.toHhmi(begin));
			slot.put("endTm", TimeUtil.toHhmi(end));
			slot.put("taken", taken);
			slots.add(slot);
		}

		model.addAttribute("fclty", fclty);
		model.addAttribute("resveDe", de);
		model.addAttribute("resveDeIso", day.toString());
		model.addAttribute("minDe", min.toString());
		model.addAttribute("maxDe", max.toString());
		model.addAttribute("slots", slots);
		model.addAttribute("dailyLimitMin", propertiesService.getInt("resveDailyLimitMin"));
		return "fclty/detail";
	}
}
