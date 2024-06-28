package able.cmm.cache.web;

import able.cmm.cache.service.CacheSampleService;
import able.cmm.cache.vo.CacheSampleVO;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import able.com.web.HController;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : CacheSampleController.java
 * @Description : 캐시 메세지 처리 컨트롤러
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
public class CacheSampleController extends HController {
	
	Log log = LogFactory.getLog(CacheSampleController.class);

	
	@Resource(name = "CacheSampleService")
	private CacheSampleService cacheSampleService;
	
	
	/**
	 * 화면 로드
	 * 
	 */
	@RequestMapping(value = "/cmm/cache/CacheSampleForm.do")
	public String selectItem(@ModelAttribute CacheSampleVO cacheSampleVO, Model model) throws Exception {
		
		return "able/cache/CacheSampleForm";

	}
	
	/**
	 *  메세지 Cache 생성
	 *  
	 */
	@RequestMapping(value = "/cmm/cache/CacheSampleInsert.do")
	public String insertItem(@ModelAttribute CacheSampleVO cacheSampleVO, Model model) throws Exception {
		
		long start = System.currentTimeMillis();
		
		log.info(cacheSampleService.prtService("Step 1"));
		log.info(cacheSampleService.prtService("Step 2"));
		log.info(cacheSampleService.prtService("Step 3"));

		long end = System.currentTimeMillis();

		
		cacheSampleVO.setDescription(
				   cacheSampleService.prtService("Step 1").getDescription() + "\n"
				+ cacheSampleService.prtService("Step 2").getDescription() + "\n"
				+ cacheSampleService.prtService("Step 3").getDescription() + "\n"
				+ "Execution Time(s) : " + (end - start )/1000.0);
		
		model.addAttribute("result", cacheSampleVO);
				
		return "able/cache/CacheSampleForm";

	}
	
	
	/**
	 *  메세지 Cache 삭제
	 *  
	 */
	@RequestMapping(value = "/cmm/cache/CacheSampleDelete.do")
	public String deleteItem(@ModelAttribute CacheSampleVO cacheSampleVO, Model model) throws Exception {
		
		cacheSampleService.delService();
				
		return "able/cache/CacheSampleForm";

	}
}
