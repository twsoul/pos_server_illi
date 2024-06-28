package able.cmm.trans.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import able.cmm.trans.service.TransactionSampleService;
import able.cmm.trans.service.dao.TransactionSampleMDAO;
import able.com.service.HService;
import able.com.vo.HMap;

/**
 * @ClassName   : TransactionSampleServiceImpl.java
 * @Description : TransactionSampleService 구현 클래스
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
@Service("transactionSampleService")
public class TransactionSampleServiceImpl extends HService implements TransactionSampleService{

	/**
	 * MyBatis 샘플을 위한 db 연동 처리
	 */
	@Resource(name="transactionSampleMDAO")
	private TransactionSampleMDAO transactionSampleMDAO;

	/**
	 * myBatis 데이터를 등록한다.
	 */
	@Override
	public void insertTransaction(HMap hmap) throws Exception {
		
		//여러건을 등록한다.
		String[] ids = (String[]) hmap.get("transIds");
		String[] names = (String[]) hmap.get("transNames");
		String[] descs = (String[]) hmap.get("transDescs");
		
		for(int i=0;i<ids.length;i++){
			HMap map = new HMap();
			map.put("transId", ids[i]);
			map.put("transName", names[i]);
			map.put("transDesc", descs[i]);
			
			transactionSampleMDAO.insertTransaction(map);
		}
		
		
	}

	/**
	 * mybatis 데이터 목록을 조회한다.
	 */
	@Override
	public List<?> selectTransactionList(HMap hmap) throws Exception {		
		return transactionSampleMDAO.selectTransactionList(hmap);
	}

	/**
	 * 롤백처리를 위한 트랜잭션
	 */
	@Override
	public void insertTransRollback(HMap hmap) throws Exception {
		//여러건을 등록한다.
		String[] ids = (String[]) hmap.get("transIds");
		String[] names = (String[]) hmap.get("transNames");
		String[] descs = (String[]) hmap.get("transDescs");
		
		for(int i=0;i<ids.length;i++){
			HMap map = new HMap();
			map.put("transId", ids[i]);
			map.put("transName", names[i]);
			map.put("transDesc", descs[i]);
			
			if(i==2)
				throw processException("fail.common.msg");
			
			transactionSampleMDAO.insertTransaction(map);
						
		}
		
	}
	
	/**
	 * 롤백처리 하지않는 트랜잭션
	 */
	@Override
	public void noRollbackInsertTrans(HMap hmap) throws Exception {
		//여러건을 등록한다.
		String[] ids = (String[]) hmap.get("transIds");
		String[] names = (String[]) hmap.get("transNames");
		String[] descs = (String[]) hmap.get("transDescs");
		
		for(int i=0;i<ids.length;i++){
			HMap map = new HMap();
			map.put("transId", ids[i]);
			map.put("transName", names[i]);
			map.put("transDesc", descs[i]);
			
			if(i==2)
				throw processException("fail.common.msg");
			
			transactionSampleMDAO.insertTransaction(map);
			
		}
		
	}


}
