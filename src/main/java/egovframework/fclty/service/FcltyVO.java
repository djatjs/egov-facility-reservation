package egovframework.fclty.service;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcltyVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long fcltySn;
	private String fcltyTyCd;
	private String fcltyTyNm;
	private String fcltyNm;
	private String adres;
	private Integer acmdNmpr;
	private String operBeginTm;
	private String operEndTm;
	private Integer resveUnitMin;
	private String fcltyDc;
	private String useYn;
	private String registerId;
	private Date registDt;
	private String updusrId;
	private Date updtDt;
}
