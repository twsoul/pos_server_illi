package able.cmm.aopxml.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import able.cmm.aopxml.service.AopSampleXMLService;
import able.cmm.aopxml.vo.AopSampleXMLVO;
import able.com.web.HController;
/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : AopSampleXMLController.java
 * @Description : XML 방식의 AOP 컨트롤러
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
@Controller
public class AopSampleXMLController extends HController {

	@Resource(name = "AopSampleXMLService")
	AopSampleXMLService aopSampleXMLService;

	/**
	 * 화면 로드
	 * 
	 */
	@RequestMapping(value = "/cmm/aopxml/AopSampleRegisterForm.do")
	public String insertItemForm(@ModelAttribute AopSampleXMLVO aopSampleXMLVO,
			BindingResult result, Model model, HttpServletRequest request)
			throws Exception {

		return "able/aopxml/AopSampleRegisterForm";
	}

	/**
	 * 메세지 실행(AOP)
	 * 
	 */
	@RequestMapping(value = "/cmm/aopxml/AopSampleRegister.do")
	public String insertItem(@ModelAttribute AopSampleXMLVO aopSampleXMLVO,
			BindingResult result, Model model, HttpServletRequest request)
			throws Exception {

		aopSampleXMLService.getService(request,aopSampleXMLVO);

		model.addAttribute("result",aopSampleXMLService.prtService(request, aopSampleXMLVO));
		
		return "able/aopxml/AopSampleRegisterForm";
	}

}
