package able.cmm.trans.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import able.cmm.trans.service.TransactionSampleService;
import able.com.exception.BizException;
import able.com.vo.HMap;
import able.com.web.HController;

/**
 * @ClassName   : TransactionSampleController.java
 * @Description : 멀티 트랜잭션 샘플 컨트롤러
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
public class TransactionSampleController extends HController {

	/** MessageMngService */
	@Resource(name = "transactionSampleService")
	private TransactionSampleService transactionSampleService;
	
	@RequestMapping(value = "/cmm/trans/selectTransactionList.do")
	public String selectTransactionList(ModelMap model) throws Exception {
		try{
			List<?> resultList = transactionSampleService.selectTransactionList(null);
			model.addAttribute("resultList", resultList);
		}catch(Exception e){
			//Exception 처리
			model.addAttribute("message", getMessage("fail.common.msg"));
		}
		return "able/trans/transactionSample";
	}
	
	@RequestMapping(value="/cmm/trans/insertTrans.do")
	public String insertTrans(HMap hmap, Model model) throws Exception{
		try{
			//동일한 메세지 코드가 있는지 점검 후 등록
			transactionSampleService.insertTransaction(hmap);
			model.addAttribute("message", getMessage("info.success.insert"));
			
		}catch(BizException e){
			model.addAttribute("message", e.getMessage());
		}catch(Exception e){
			model.addAttribute("message", getMessage("fail.common.msg"));
		}
		return "redirect:/cmm/trans/selectTransactionList.do";
	}
	
	@RequestMapping(value="/cmm/trans/insertTransRollback.do")
	public String insertTransRollback(HMap hmap, Model model) throws Exception{
		try{
			//동일한 메세지 코드가 있는지 점검 후 등록
			transactionSampleService.insertTransRollback(hmap);
			model.addAttribute("message", getMessage("info.success.insert"));
		}catch(BizException e){
			model.addAttribute("message", e.getMessage());
		}catch(Exception e){
			model.addAttribute("message", getMessage("fail.common.msg"));
		}
				
		return "redirect:/cmm/trans/selectTransactionList.do";
	}
	
	@RequestMapping(value="/cmm/trans/insertTransNoRollback.do")
	public String insertTransNoRollback(HMap hmap, Model model) throws Exception{
		try{
			//동일한 메세지 코드가 있는지 점검 후 등록
			transactionSampleService.noRollbackInsertTrans(hmap);
			model.addAttribute("message", getMessage("info.success.insert"));
			
		}catch(BizException e){
			model.addAttribute("message", e.getMessage());
		}catch(Exception e){
			model.addAttribute("message", getMessage("fail.common.msg"));
		}
		return "redirect:/cmm/trans/selectTransactionList.do";
	}
	
}
