package able.cmm.exception.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import able.cmm.exception.service.ExceptionSampleService;
import able.cmm.exception.service.dao.ExceptionSampleMDAO;
import able.com.exception.BizException;
import able.com.service.HService;
import able.com.vo.HMap;


/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ExceptionSampleServiceImpl.java
 * @Description : 예외처리 목록 조회
 * @author "ADM Technology Team"
 * @since 2016. 7. 1.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.     "ADM Technology Team"     	최초 생성
 * </pre>
 */
@Service("exceptionSampleService")
public class ExceptionSampleServiceImpl extends HService implements
		ExceptionSampleService {

	@Resource(name = "exceptionSampleMDAO")
	private ExceptionSampleMDAO exceptionSampleMDAO;

	@Override
	public List<HMap> selectCategory(HMap map) throws Exception {

		List<HMap> result = exceptionSampleMDAO.selectCategory(null);

		if (result.size() == 0) {
			//able.com.exception.BizException 매핑
			//지정된 Message Code Mapping
			throw new BizException(messageSource, "info.nodata.msg");
		} else {
			return result;
		}
	}

	@Override
	public List<HMap> selectCategory2(HMap map) throws Exception {

		List<HMap> result = exceptionSampleMDAO.selectCategory(null);

		try {
			//강제로 발생한 ArithmeticException
			int i = 1 / 0;
			System.out.println(i);
		} catch (ArithmeticException athex) {
			//Exception 을 발생하지 않고 후처리 로직 실행.
			leaveaTrace("fail.common.arithmetic");
		}
		return result;

	}

	@Override
	public List<HMap> selectCategory3(HMap map) throws Exception {
		List<HMap> result = exceptionSampleMDAO.selectCategory(null);

		if (result.size() == 0) {
			//Message Code 대신 직접 Message 입력
			//ProcessException과 유사하지만 Message처리 방식의 차이
			//throw new BizException("error!");
			throw processException("info.nodata.msg");
		} else {
			return result;
		}
	}
}
