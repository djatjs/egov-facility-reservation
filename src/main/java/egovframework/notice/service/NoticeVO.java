package egovframework.notice.service;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long nttSn;
	private String nttSj;
	private String nttCn;
	private Integer rdcnt;
	private String registerId;
	private Date registDt;
	private String updusrId;
	private Date updtDt;
}
