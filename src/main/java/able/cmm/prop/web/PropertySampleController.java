package able.cmm.prop.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import able.com.web.HController;

/**
 * @ClassName   : PropertySampleController.java
 * @Description : 프로퍼티 샘플 컨트롤러
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
public class PropertySampleController extends HController{

	@RequestMapping(value = "/cmm/prop/selectProperty.do")
	public String selectProperty(ModelMap model) throws Exception {

//		logger.debug(propertiesService.getString("pageUnit"));
//		logger.debug(propertiesService.getString("pageSize"));
//		logger.debug(propertiesService.getString("AAAA"));
//		logger.debug(propertiesService.getString("BBBB"));
//		logger.debug(propertiesService.getString("CCCC"));
		
		model.addAttribute("pageUnit", propertiesService.getInt("pageUnit"));
		model.addAttribute("pageSize", propertiesService.getInt("pageSize"));
		model.addAttribute("AAAA", propertiesService.getString("AAAA"));
		model.addAttribute("BBBB", propertiesService.getString("BBBB"));
		model.addAttribute("CCCC", propertiesService.getString("CCCC"));
		
		
		return "able/prop/propertySample";
	}
	
	@RequestMapping(value = "/cmm/prop/refreshProperty.do")
	public String refreshProperty(ModelMap model) throws Exception {

		propertiesService.refreshPropertyFiles();
		
//		logger.debug(propertiesService.getString("pageUnit"));
//		logger.debug(propertiesService.getString("pageSize"));
//		logger.debug(propertiesService.getString("AAAA"));
//		logger.debug(propertiesService.getString("BBBB"));
//		logger.debug(propertiesService.getString("CCCC"));
		
		model.addAttribute("pageUnit", propertiesService.getInt("pageUnit"));
		model.addAttribute("pageSize", propertiesService.getInt("pageSize"));
		model.addAttribute("AAAA", propertiesService.getString("AAAA"));
		model.addAttribute("BBBB", propertiesService.getString("BBBB"));
		model.addAttribute("CCCC", propertiesService.getString("CCCC"));
		
		return "able/prop/propertySample";
	}
	
}
