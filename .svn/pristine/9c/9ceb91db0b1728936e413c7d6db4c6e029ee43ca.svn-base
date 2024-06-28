package able.cmm.trans.service.dao;

import java.util.List;

import able.com.mybatis.Mapper;
import able.com.vo.HMap;

/**
 * @ClassName   : TransactionSampleMDAO.java
 * @Description : 트랜잭션 샘플에 관한 데이터처리 매퍼 클래스
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
@Mapper("transactionSampleMDAO")
public interface TransactionSampleMDAO {

	/**
	 * 글 목록을 조회한다.
	 * @param searchVO - 조회할 정보가 담긴 VO
	 * @return 글 목록
	 * @exception Exception
	 */
	public List<?> selectTransactionList(HMap hmap) throws Exception;
		
	/**
	 * myBatis 데이터를 등록한다.
	 * @param hmap
	 * @throws Exception
	 */
	public void insertTransaction(HMap hmap) throws Exception;
	
}

