package able.cmm.aop.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import able.cmm.aop.service.AopSampleService;
import able.cmm.aop.vo.AopSampleVO;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : AopSampleServiceImpl.java
 * @Description : AOP 메세지 처리 서비스
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
@Service("AopSampleService")
public class AopSampleServiceImpl  implements AopSampleService{
	
	/**
	 * 입력 메세지 처리 / AOP
	 * 
	 */
	public void getService(HttpServletRequest request, AopSampleVO aopSampleVO) throws Exception {
		
		String msg="Input Msg : " + aopSampleVO.getTitle()+"\n";
		
		HttpSession session = request.getSession();
		session.setAttribute("description", session.getAttribute("description")+msg);
		
	}

	/**
	 * 출력 메세지 처리
	 * 
	 */
	public AopSampleVO prtService(HttpServletRequest request, AopSampleVO aopSampleVO) throws Exception {
		
		HttpSession session = request.getSession();

		aopSampleVO.setDescription((String)session.getAttribute("description"));
		System.out.println(session.getAttribute("description"));
		session.setAttribute("description", "");
		
		return aopSampleVO;
		
	}
}