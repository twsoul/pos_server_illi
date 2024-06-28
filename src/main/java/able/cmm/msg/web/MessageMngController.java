package able.cmm.msg.web;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import able.cmm.msg.service.MessageMngService;
import able.cmm.msg.vo.MessageMngVO;
import able.com.exception.BizException;
import able.com.vo.HMap;
import able.com.web.HController;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : MessageMngController.java
 * @Description : 메시지 관리 컨트롤러
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
public class MessageMngController extends HController {

	/** MessageMngService */
	@Resource(name = "messageMngService")
	private MessageMngService messageMngService;
	
	@RequestMapping(value = "/cmm/msg/message.do")
	public String selectSampleList(ModelMap model) throws Exception {
		return "able/msg/messageMng";
	}
	
	@RequestMapping(value = "/cmm/msg/selectMessageList.do")
	public String selectMessageList(ModelMap model) throws Exception {
		
		List<?> resultList = messageMngService.selectMessageList(null);
		model.addAttribute("resultList", resultList);
	
		return "able/msg/messageMng";
	}
	
	@RequestMapping(value="/cmm/msg/selectMessage.do")
	public String selectMessage(@RequestParam("msgKey") String msgKey, Model model) throws Exception{
		
		HMap map = new HMap();
		map.put("msgKey", msgKey);
		List result = messageMngService.selectMessage(map);
		HMap resultMap = new HMap();
		
		if(null!=result && result.size()>0){		
			for(int i=0;i<result.size();i++){
				HMap tmp = (HMap) result.get(i);
				if(i==0)
					resultMap.put("msgKey", tmp.get("key"));
				if(tmp.get("language").equals("ko_KR")){
					resultMap.put("msgKoKr", tmp.get("value"));
					resultMap.put("msgKoKrDate", tmp.get("lastmodify"));
				}else if(tmp.get("language").equals("en_US")){
					resultMap.put("msgEnUs", tmp.get("value"));
					resultMap.put("msgEnUsDate", tmp.get("lastmodify"));
				}
			}			
		}
		
		model.addAttribute("result", resultMap);
		
				
		return "jsonView";
	}
	
	@RequestMapping(value="/cmm/msg/updateMessage.do")
	public String updateMessage(@ModelAttribute MessageMngVO mmVO, Model model) throws Exception{
		
		messageMngService.updateMessage(mmVO);
		model.addAttribute("message", getMessage("info.success.update"));		
			
		return "jsonView";
	}
	
	@RequestMapping(value="/cmm/msg/deleteMessage.do")
	public String deleteMessage(@RequestParam("msgKey") String msgKey, Model model) throws Exception{
		
		HMap param = new HMap();
		param.put("msgKey", msgKey);
		
		int result = messageMngService.deleteMessageByKey(param);
		if(result>0)
			model.addAttribute("message", getMessage("info.success.delete"));
		else
			model.addAttribute("message", getMessage("fail.common.msg"));
			
		return "jsonView";
	}
	
	@RequestMapping(value="/cmm/msg/insertMessage.do")
	public String insertMessage(@Valid MessageMngVO mmVO, BindingResult result, Model model) throws Exception{
	
		if(result.hasErrors()){
			return "jsonView";
		}
		//동일한 메세지 코드가 있는지 점검 후 등록
		messageMngService.insertMessage(mmVO);
		model.addAttribute("message", getMessage("info.success.insert"));
			
		return "jsonView";
	}
	
}
