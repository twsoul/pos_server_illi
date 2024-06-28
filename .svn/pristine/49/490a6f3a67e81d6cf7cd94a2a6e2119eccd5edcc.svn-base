package able.cmm.mybatis.service.dao;

import java.util.List;

import able.com.mybatis.Mapper;
import able.com.vo.HMap;

/**
 * @ClassName   : MyBatisSampleMDAO.java
 * @Description : Sample에 관한 데이터처리 매퍼 클래스
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

@Mapper("myBatisSampleMDAO")
public interface MyBatisSampleMDAO {

	/**
	 * 글 목록을 조회한다.
	 * @param searchVO - 조회할 정보가 담긴 VO
	 * @return 글 목록
	 * @exception Exception
	 */
	public List<?> selectMyBatisList(HMap hmap) throws Exception;
	
	/**
	 * myBatis를 KEY값으로 조회한다.
	 * @param hmap
	 * @return
	 * @throws Exception
	 */
	public HMap selectMyBatisByKey(HMap hmap) throws Exception;
	
	/**
	 * myBatis 데이터를 등록한다.
	 * @param hmap
	 * @throws Exception
	 */
	public void insertMyBatis(HMap hmap) throws Exception;
	
	/**
	 * 정보를 수정한다.
	 * @param hmap
	 * @return
	 * @throws Exception
	 */
	public int updateMyBatis(HMap hmap) throws Exception;
	
	/**
	 * 정보를 한건 삭제한다.
	 * @param hmap
	 * @return
	 * @throws Exception
	 */
	public int deleteMyBatisByKey(HMap hmap) throws Exception;
	
	/**
	 * List 총 갯수
	 * @param hmap
	 * @return
	 * @throws Exception
	 */
	public int selectMyBatisListTotCnt(HMap hmap) throws Exception;

}

