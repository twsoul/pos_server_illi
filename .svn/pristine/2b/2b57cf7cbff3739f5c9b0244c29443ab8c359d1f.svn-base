package able.cmm.exception.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import able.cmm.exception.service.ExceptionSampleService;
import able.com.exception.BizException;
import able.com.vo.HMap;
import able.com.web.HController;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ExceptionSampleController.java
 * @Description : 예외 처리 컨트롤러
 * @author "ADM Technology Team"
 * @since 2016. 7. 1.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.     "ADM Technology Team"     	최초 생성
 * </pre>
 */
@Controller
public class ExceptionSampleController extends HController{

	@Resource(name="exceptionSampleService")
	ExceptionSampleService exceptionSampleService;
	
	@RequestMapping(value = "/cmm/exception/exceptionSample.do")
	public String exceptionSample(HMap hmap, ModelMap model) throws Exception {
		return "able/exception/exceptionSample";
	}
	
	@RequestMapping(value = "/cmm/exception/exceptionResolver.do")
	public String exceptionResolver(HMap hmap, ModelMap model) throws Exception {
		
		List<HMap> result = exceptionSampleService.selectCategory(null);
		model.addAttribute("result", result);
			
		return "able/exception/exceptionSample";
	}
	
	@RequestMapping(value = "/cmm/exception/leaveTrace.do")
	public String leaveTrace(HMap hmap, ModelMap model) throws Exception {
		
		List<HMap> result = exceptionSampleService.selectCategory2(null);
		model.addAttribute("result", result);
		//impl에서 leaveaTrace message는 서버단 로그를 발생시킴. 클라이언트 로그를 위하여 같은 message code를 모델에 추가
		model.addAttribute("ltMessage", getMessage("fail.common.arithmetic"));
		
		return "able/exception/exceptionSample";
	}
	
	@RequestMapping(value = "/cmm/exception/ajaxTryCatchException.do", method = RequestMethod.POST)
	@ResponseBody
	public List<HMap> ajaxTryCatchException(HMap hmap, ModelMap model) throws Exception {
		
		List<HMap> result = new ArrayList<HMap>();

		try {
			result = exceptionSampleService.selectCategory3(null);
			System.out.println("-----------------------------Try");
		} catch (BizException e) {
			System.out.println("-----------------------------Catch");
			//try문에서 BizException이 발생하므로 Catch문에서 아래 로직이 실행되며
			//hmap에 result 값을 담아 ajax success:function으로 이동
			//(실제로 Exception이 발생하였으나 return은 되므로 httpStatus는 200OK
			hmap.put("result", "fail");
			result.add(hmap);
			
		}
		
		return result;
	}
}

