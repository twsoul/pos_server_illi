package able.cmm.session.web;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import able.com.vo.HMap;
import able.com.web.HController;

/**
 * @ClassName   : SessionSampleController.java
 * @Description : 세션 샘플 컨트롤러
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
@Controller
public class SessionSampleController extends HController {
	
	@RequestMapping(path="/cmm/session/sessionSample.do", method = RequestMethod.GET)
	public String sampleSession(HMap hmap, ModelMap model) {
		return "able/session/sessionSample";
	}
	
	@RequestMapping(path="/cmm/session/addAttribute.do", method = RequestMethod.POST)
	public String addAttribute(HMap hmap, ModelMap model){
		
		String key = hmap.getString("attributeKey");
		String value = hmap.getString("attributeValue");
		//세션 속성 설정
		HController.setSessionAttribute(key, value);
		
		return "able/session/sessionSample";
	}
	
	@RequestMapping(path="/cmm/session/delAttribute.do", method = RequestMethod.POST)
	public String delAttribute(HMap hmap, ModelMap model){
		
		String key = hmap.getString("attributeKey");
		//세션 속성 제거
		HController.removeSessionAttribute(key);
		
		return "able/session/sessionSample";
	}
	
	@RequestMapping(path="/cmm/session/destroySession.do")
	public String destroySession(HttpSession session) throws Exception{
		//세션 삭제
		session.invalidate();
		
		return "able/session/sessionSample";
	}
}
