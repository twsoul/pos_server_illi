package able.cmm.aop.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import able.cmm.aop.vo.AopSampleVO;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : AopSampleService.java
 * @Description : AOP 메세지 처리 서비스 인터페이스
 * @author "ADM Technology Team"
 * @since 2016. 7. 1.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.     "ADM Technology Team"       최초 생성
 * </pre>
 */
public interface AopSampleService {

	/** Logger object. */
	Log LOGGER = LogFactory.getLog(AopSampleService.class);
	
	
	/**
	 * 입력 메세지 처리 / AOP
	 * 
	 */
	public void getService(HttpServletRequest request, AopSampleVO aopSampleVO) throws Exception ;
	

	/**
	 * 출력 메세지 처리
	 * 
	 */
	public AopSampleVO prtService(HttpServletRequest request, AopSampleVO aopSampleVO) throws Exception ;

}
