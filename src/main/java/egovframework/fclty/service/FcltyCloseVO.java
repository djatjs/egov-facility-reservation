package egovframework.fclty.service;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/** SR-03: 시설 휴관일 */
@Getter
@Setter
public class FcltyCloseVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long closeSn;
	private Long fcltySn;
	private String closeDe;
	private String closeRsn;
	private String registerId;
	private Date registDt;
}
