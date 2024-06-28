package able.cmm.aopxml.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import able.cmm.aopxml.vo.AopSampleXMLVO;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : AopSampleXMLService.java
 * @Description : AOP 메세지 처리 인터페이스
 * @author "ADM Technology Team"
 * @since 2016. 7. 1.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.     "ADM Technology Team"      최초 생성
 * </pre>
 */
public interface AopSampleXMLService {

	/** Logger object. */
	Log LOGGER = LogFactory.getLog(AopSampleXMLService.class);
	
	
	/**
	 * 입력 메세지 처리 / AOP
	 * 
	 */
	public void getService(HttpServletRequest request, AopSampleXMLVO aopSampleXMLVO) throws Exception ;
	

	/**
	 * 출력 메세지 처리
	 * 
	 */
	public AopSampleXMLVO prtService(HttpServletRequest request, AopSampleXMLVO aopSampleXMLVO) throws Exception ;

}
