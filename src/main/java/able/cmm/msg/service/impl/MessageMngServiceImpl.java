package able.cmm.msg.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import able.cmm.msg.service.MessageMngService;
import able.cmm.msg.service.dao.MessageMngMDAO;
import able.cmm.msg.vo.MessageMngVO;
import able.com.service.HService;
import able.com.vo.HMap;

/**
 * @ClassName   : MessageMngServiceImpl.java
 * @Description : MessageMngService 구현 Class
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
@Service("messageMngService")
public class MessageMngServiceImpl extends HService implements MessageMngService{

	@Resource(name="messageMngMDAO")
	private MessageMngMDAO messageMngMDAO;

	@Override
	public List<?> selectMessageList(HMap map) throws Exception {
		
		return messageMngMDAO.selectMessageList(map);
	}

	@Override
	public void insertMessage(MessageMngVO vo) throws Exception {
		//동일한 코드가 있는지 점검
		HMap map = new HMap();
		map.put("msgKey", vo.getMsgKey());
		List keys = messageMngMDAO.selectMessageByKey(map);
		if(null!=keys && keys.size()>0)
			throw processException("error.duplicate.key", new String[]{(String) map.get("msgKey")});
		//메세지 등록
		//INSERT ABLE_MSG
        long timestamp = System.currentTimeMillis();
        
        HMap kmap = new HMap();
        kmap.put("msgKey", vo.getMsgKey());
        kmap.put("msgText", vo.getMsgKo());
        kmap.put("msgLanguage", "ko_KR");
        kmap.put("msgTimestamp", timestamp+"");
        
        HMap emap = new HMap();
        emap.put("msgKey", vo.getMsgKey());
        emap.put("msgText", vo.getMsgEn());
        emap.put("msgLanguage", "en_US");
        emap.put("msgTimestamp", timestamp+"");
        
		messageMngMDAO.insertMessage(kmap);
		messageMngMDAO.insertMessage(emap);
		
	}

	@Override
	public void updateMessage(MessageMngVO vo) throws Exception {
			
		long timestamp = System.currentTimeMillis();
	
		//변경된 데이터 만 수정
		if(!vo.getMsgKoOrg().equals(vo.getMsgKo())){
			HMap kmap = new HMap();
	        kmap.put("msgKey", vo.getMsgKey());
	        kmap.put("msgText", vo.getMsgKo());
	        kmap.put("msgLanguage", "ko_KR");
	        kmap.put("msgTimestamp", timestamp+"");
	        messageMngMDAO.updateMessage(kmap);
		}				
        if(!vo.getMsgEnOrg().equals(vo.getMsgEn())){
        	HMap emap = new HMap();
            emap.put("msgKey", vo.getMsgKey());
            emap.put("msgText", vo.getMsgEn());
            emap.put("msgLanguage", "en_US");
            emap.put("msgTimestamp", timestamp+"");
    		messageMngMDAO.updateMessage(emap);
        }
       	
	}

	@Override
	public int deleteMessageByKey(HMap map) throws Exception {
		int result = messageMngMDAO.deleteMessageByKey(map);
		return result;		
	}

	@Override
	public List selectMessage(HMap map) throws Exception {
		// TODO Auto-generated method stub
		return messageMngMDAO.selectMessageByKey(map);
	}


}
