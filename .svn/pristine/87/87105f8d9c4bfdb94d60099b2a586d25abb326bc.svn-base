package able.cmm.trans.service;

import java.util.List;

import able.com.vo.HMap;

/**
 * @ClassName   : TransactionSampleService.java
 * @Description : 멀티 트랜잭션 샘플 서비스 Class
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
public interface TransactionSampleService {

	/**
	 * 글을 등록한다.
	 * @param vo - 등록할 정보가 담긴 SampleVO
	 * @return 등록 결과
	 * @exception Exception
	 */
	void insertTransaction(HMap hmap) throws Exception;

	/**
	 * 글 목록을 조회한다.
	 * @param searchVO - 조회할 정보가 담긴 VO
	 * @return 글 목록
	 * @exception Exception
	 */
	List<?> selectTransactionList(HMap map) throws Exception;
	
	/**
	 * Rollback 하는 트랜잭션 
	 * @param hmap
	 * @throws Exception
	 */
	void insertTransRollback(HMap hmap) throws Exception;
	
	/**
	 * Rollback 하지 않는 트랜잭션 
	 * @param hmap
	 * @throws Exception
	 */
	void noRollbackInsertTrans(HMap hmap) throws Exception;
}
