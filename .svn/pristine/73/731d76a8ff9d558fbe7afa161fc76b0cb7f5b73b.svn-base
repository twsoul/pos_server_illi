package able.cmm.mybatis.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import able.cmm.mybatis.service.MyBatisSampleService;
import able.cmm.mybatis.service.dao.MyBatisSampleMDAO;
import able.com.service.HService;
import able.com.vo.HMap;

/**
 * @ClassName   : MyBatisSampleServiceImpl.java
 * @Description : MyBatisSampleService 구현 Class
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
@Service("myBatisSampleService")
public class MyBatisSampleServiceImpl extends HService implements MyBatisSampleService{

	/**
	 * MyBatis 샘플을 위한 db 연동 처리
	 */
	@Resource(name="myBatisSampleMDAO")
	private MyBatisSampleMDAO myBatisSampleMDAO;

	/**
	 * myBatis 데이터를 등록한다.
	 */
	@Override
	public void insertMyBatis(HMap hmap) throws Exception {
		myBatisSampleMDAO.insertMyBatis(hmap);		
	}

	/**
	 * mybatis 데이터를 수정한다.
	 */
	@Override
	public void updateMyBatis(HMap hmap) throws Exception {
		myBatisSampleMDAO.updateMyBatis(hmap);
	}

	/**
	 * mybatis 데이터를 key를 파라메터로 삭제한다.
	 */
	@Override
	public int deleteMyBatisByKey(HMap hmap) throws Exception {
		myBatisSampleMDAO.deleteMyBatisByKey(hmap);
		return 1;
	}

	/**
	 * mybatis 데이터를 하나 조회한다.
	 */
	@Override
	public HMap selectMyBatis(HMap hmap) throws Exception {		
		HMap result = myBatisSampleMDAO.selectMyBatisByKey(hmap);
		return result;
	}

	/**
	 * mybatis 데이터 목록을 조회한다.
	 */
	@Override
	public List<?> selectMyBatisList(HMap hmap) throws Exception {		
		return myBatisSampleMDAO.selectMyBatisList(hmap);
	}

	@Override
	public int selectMyBatisListTotCnt(HMap map) throws Exception {
		// TODO Auto-generated method stub
		return myBatisSampleMDAO.selectMyBatisListTotCnt(map);
	}

}
