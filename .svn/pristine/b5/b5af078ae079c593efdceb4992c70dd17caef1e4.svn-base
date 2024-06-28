package able.cmm.aopxml.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import able.cmm.aopxml.service.AopSampleXMLService;
import able.cmm.aopxml.vo.AopSampleXMLVO;
/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : AopSampleXMLServiceImpl.java
 * @Description : AOP 메세지 처리 서비스
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
@Service("AopSampleXMLService")
public class AopSampleXMLServiceImpl  implements AopSampleXMLService{
	
	/**
	 * 입력 메세지 처리 / AOP
	 * 
	 */
	public void getService(HttpServletRequest request, AopSampleXMLVO aopSampleXMLVO) throws Exception {
		
		String msg="Input Msg : " + aopSampleXMLVO.getTitle()+"\n";
		
		HttpSession session = request.getSession();
		session.setAttribute("description", session.getAttribute("description")+msg);
		
	}

	
	/**
	 * 출력 메세지 처리
	 * 
	 */
	public AopSampleXMLVO prtService(HttpServletRequest request, AopSampleXMLVO aopSampleXMLVO) throws Exception {
		
		HttpSession session = request.getSession();

		aopSampleXMLVO.setDescription((String)session.getAttribute("description"));
		System.out.println(session.getAttribute("description"));
		session.setAttribute("description", "");
		
		return aopSampleXMLVO;
		
	}
}