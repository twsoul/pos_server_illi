package able.cmm.aop.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import able.cmm.aop.service.AopSampleService;
import able.cmm.aop.vo.AopSampleVO;
import able.com.web.HController;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : AopSampleController.java
 * @Description : Annotation 방식의 AOP 컨트롤러
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
@Controller
public class AopSampleController extends HController {

	@Resource(name = "AopSampleService")
	AopSampleService aopSampleService;

	/**
	 * 화면 로드
	 * 
	 */
	@RequestMapping(value = "/cmm/aop/AopSampleRegisterForm.do")
	public String insertItemForm(@ModelAttribute AopSampleVO aopSampleVO,
			BindingResult result, Model model, HttpServletRequest request)
			throws Exception {

		return "able/aop/AopSampleRegisterForm";
	}

	/**
	 *  메세지 실행(AOP)
	 * 
	 */
	@RequestMapping(value = "/cmm/aop/AopSampleRegister.do")
	public String insertItem(@ModelAttribute AopSampleVO aopSampleVO,
			BindingResult result, Model model, HttpServletRequest request)
			throws Exception {

		aopSampleService.getService(request,aopSampleVO);

		model.addAttribute("result",aopSampleService.prtService(request, aopSampleVO));
		
		return "able/aop/AopSampleRegisterForm";
	}

}
