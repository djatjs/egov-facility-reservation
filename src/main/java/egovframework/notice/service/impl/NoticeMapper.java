package egovframework.notice.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.egovframe.rte.psl.dataaccess.mapper.Mapper;

import egovframework.cmm.DefaultSearchVO;
import egovframework.notice.service.NoticeVO;

@Mapper
public interface NoticeMapper {

	List<NoticeVO> selectNoticeList(DefaultSearchVO searchVO);

	int selectNoticeListTotCnt(DefaultSearchVO searchVO);

	NoticeVO selectNotice(Long nttSn);

	void updateRdcnt(Long nttSn);

	void insertNotice(NoticeVO vo);

	int updateNotice(NoticeVO vo);

	int deleteNotice(@Param("nttSn") Long nttSn, @Param("updusrId") String updusrId);
}
