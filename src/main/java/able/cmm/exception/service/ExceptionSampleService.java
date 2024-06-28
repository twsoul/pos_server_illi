package able.cmm.exception.service;

import java.util.List;

import able.com.vo.HMap;


/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ExceptionSampleService.java
 * @Description : 예외처리 목록 조회 인터페이스
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
public interface ExceptionSampleService {

	//ExceptionSampleVO selectCategory(ExceptionSampleVO vo) throws Exception;
	List<HMap> selectCategory(HMap map) throws Exception;
	
	List<HMap> selectCategory2(HMap map) throws Exception;
	
	List<HMap> selectCategory3(HMap map) throws Exception;
}
