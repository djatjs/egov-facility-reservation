package egovframework.resve.service;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResveHistVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long histSn;
	private Long resveSn;
	private String bfrSttusCd;
	private String bfrSttusNm;
	private String aftSttusCd;
	private String aftSttusNm;
	private String changeRsn;
	private String changerId;
	private Date changeDt;
}
