package egovframework.resve.service;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/** 관리자 대시보드 집계 */
@Getter
@Setter
public class DashboardVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 승인 대기 (이용일이 지나지 않은 신청) */
	private int pendingCnt;
	/** 오늘 이용 예정 (승인) */
	private int todayCnt;
	/** 최근 7일 신청 */
	private int weekApplyCnt;
	/** 이번 달 승인·반려 처리 건수 */
	private int monthApproveCnt;
	private int monthReturnCnt;

	public int getMonthDecidedCnt() {
		return monthApproveCnt + monthReturnCnt;
	}

	/** 이번 달 반려율(%) */
	public int getMonthReturnRate() {
		int decided = getMonthDecidedCnt();
		return decided == 0 ? 0 : Math.round(monthReturnCnt * 100f / decided);
	}
}
