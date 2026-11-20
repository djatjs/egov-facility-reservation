package egovframework.resve.service;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResveVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long resveSn;
	private Long fcltySn;
	private String fcltyNm;
	private String mberId;
	private String mberNm;
	private String resveDe;
	private String beginTm;
	private String endTm;
	private Integer useNmpr;
	private String usePurps;
	private String resveSttusCd;
	private String resveSttusNm;
	private String returnRsn;
	private Date registDt;
	private String updusrId;
	private Date updtDt;
}
