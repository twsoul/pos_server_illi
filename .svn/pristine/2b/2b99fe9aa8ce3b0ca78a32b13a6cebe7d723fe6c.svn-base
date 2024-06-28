package able.cmm.aop.annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : AopSampleAnnotation.java
 * @Description : AOP 규칙 정의(Aspect, PointCut, Advice)
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
@Aspect
public class AopSampleAnnotation {

	@Pointcut("execution(public * able.cmm.aop.service..*Impl.get*(..))")
	public void getAopAnno() {
	}

	@Before("getAopAnno()")
	public void beforeExecuteGetMethod(JoinPoint thisJointPoint) {
		setSession(thisJointPoint, "AOP Msg : @Before-Execute\n");
	}

	@AfterReturning("getAopAnno()")
	public void afterReturningExecuteGetMethod(JoinPoint thisJointPoint) {
		setSession(thisJointPoint, "AOP Msg : @AfterReturning-Execute\n");
	}

	@After("getAopAnno()")
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
