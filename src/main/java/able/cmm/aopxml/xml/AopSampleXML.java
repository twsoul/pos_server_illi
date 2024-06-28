package able.cmm.aopxml.xml;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : AopSampleXML.java
 * @Description : AOP Advice 정의
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
public class AopSampleXML {
	
	public void getAopAnno() {
	}

	public void beforeExecuteGetMethod(JoinPoint thisJointPoint) {
		setSession(thisJointPoint, "AOP Msg : @Before-Execute\n");
	}

	public void afterReturningExecuteGetMethod(JoinPoint thisJointPoint) {
		setSession(thisJointPoint, "AOP Msg : @AfterReturning-Execute\n");
	}

	public void afterExecuteGetMethod(JoinPoint thisJointPoint) {
		setSession(thisJointPoint, "AOP Msg : @After-Execute\n");
	}
	
	
	/**
	 * 세션 처리
	 * 
	 */	
	public void setSession(JoinPoint thisJointPoint, String msg) {
		
		HttpServletRequest request = null;
		
		for (Object o : thisJointPoint.getArgs()) {
			if (o instanceof HttpServletRequest) {
				request = (HttpServletRequest) o;
			}
		}
		
		HttpSession session = request.getSession();
		
		String strDes = (String)session.getAttribute("description");
		
		if(strDes == null || strDes.length() == 0) {
			strDes = "";
		}
		
		session.setAttribute("description", strDes+msg);
		
	}
	
}
