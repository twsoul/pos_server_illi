package able.cmm.msg.service.dao;

import java.util.List;

import able.com.mybatis.Mapper;
import able.com.vo.HMap;

/**
 * @ClassName   : MessageMngMDAO.java
 * @Description : sample에 관한 데이터처리 매퍼 클래스
 * @author ADM기술팀
 * @since 2016. 7. 1.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.       ADM기술팀                                      최초 생성
 * </pre>
 */
@Mapper("messageMngMDAO")
public interface MessageMngMDAO {

	/**
	 * 글 목록을 조회한다.
	 * @param searchVO - 조회할 정보가 담긴 VO
	 * @return 글 목록
	 * @exception Exception
	 */
	public List<?> selectMessageList(HMap hmap) throws Exception;
	
	public List<?> selectMessageByKey(HMap hmap) throws Exception;
	
	public void insertMessage(HMap hmap) throws Exception;
	
	public int updateMessage(HMap hmap) throws Exception;
	
	public int deleteMessageByKey(HMap hmap) throws Exception;

}

