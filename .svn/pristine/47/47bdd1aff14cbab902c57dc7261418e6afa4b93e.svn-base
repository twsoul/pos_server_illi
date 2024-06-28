package able.cmm.websecurity.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import able.com.vo.HMap;
import able.com.web.HController;

/**
 * @ClassName   : WebSecuritySampleController.java
 * @Description : WebSecurity 샘플 컨트롤러
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
public class WebSecuritySampleController extends HController {
	
	@RequestMapping(path="/cmm/websecurity/websecuritySample.do", method = RequestMethod.GET)
	public String list(HMap hmap, ModelMap model) throws Exception {
		
		if(hmap.containsKey("inputString")){
			model.put("inputString", hmap.get("inputString"));
		}
		
		if(hmap.containsKey("downloadPath")){
			model.put("downloadPath", hmap.get("downloadPath"));
		}
		return "able/websecurity/websecuritySample";
	}
	
	@RequestMapping(path="/cmm/websecurity/uploadtest.do", method = RequestMethod.POST)
	public String list(HttpServletRequest request) throws Exception {
		return "able/websecurity/websecuritySample";
	}
	
}
