package able.cmm.cache.service;

import able.cmm.cache.vo.CacheSampleVO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : CacheSampleService.java
 * @Description : 캐시 메세지 생성, 삭제 인터페이스
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
public interface CacheSampleService {

	/**
	 *  메세지 Cache 생성
	 * 
	 */
	public CacheSampleVO prtService(String str) throws Exception;
	
	/**
	 *  메세지 Cache 삭제
	 * 
	 */
	public void delService() throws Exception;
}
