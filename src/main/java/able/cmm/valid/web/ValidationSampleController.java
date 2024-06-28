package able.cmm.valid.web;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import able.cmm.valid.vo.ValidationSampleVO;
import able.com.web.HController;

/**
 * @ClassName   : ValidationSampleController.java
 * @Description : Validation 샘플 컨트롤러 클래스
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
public class ValidationSampleController extends HController {

	/**
	 * 게시글 등록 화면
	 * 
	 * @param validationSampleVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/cmm/valid/insertItemForm.do")
	public String insertItemForm(@ModelAttribute ValidationSampleVO validationSampleVO, Model model)
			throws Exception {
		
		return "able/valid/validationSampleRegisterForm";
	}

	/**
	 * 게시글 등록 처리(입력값 검증)
	 * 
	 * @param validationSampleVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/cmm/valid/insertItem.do")
	public String insertItem(
			@ModelAttribute @Valid ValidationSampleVO validationSampleVO, BindingResult result, Model model)
			throws Exception {
			
			if(result.hasErrors()){
				//입력 파라미터 오류 출력
				List<ObjectError> list = result.getAllErrors();
				for(ObjectError e:list){
					logger.debug("ObjectError : "+e);
				}
				
				return "able/valid/validationSampleRegisterForm";
		}
		
		//입력 화면 표시
		return "able/valid/validationSampleRegisterForm";
	}
	
}
