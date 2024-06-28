package able.cmm.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import able.com.web.HController;

/**
 * @ClassName   : IncludeCommonController.java
 * @Description : 메인페이지 호출 컨트롤러
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
public class IncludeCommonController extends HController {

	@RequestMapping(value = "/cmm/dashboard.do")
	public String selectSampleList(ModelMap model) throws Exception {
		return "login";
	}
	
}
