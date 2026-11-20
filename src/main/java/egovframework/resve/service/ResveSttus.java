package egovframework.resve.service;

/** 예약 상태 코드 (공통코드 RESVE_STTUS) */
public final class ResveSttus {

	public static final String APPLY = "01";   // 신청
	public static final String APPROVE = "02"; // 승인
	public static final String REJECT = "03";  // 반려
	public static final String CANCEL = "04";  // 취소
	public static final String DONE = "05";    // 이용완료

	private ResveSttus() {
	}
}
