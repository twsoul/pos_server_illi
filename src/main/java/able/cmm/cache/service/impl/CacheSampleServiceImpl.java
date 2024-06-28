package able.cmm.cache.service.impl;

import able.cmm.cache.service.CacheSampleService;
import able.cmm.cache.vo.CacheSampleVO;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : CacheSampleService.java
 * @Description : 캐시 메세지 생성, 삭제
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
@Service("CacheSampleService")
public class CacheSampleServiceImpl  implements CacheSampleService{
	
	/**
	 *  메세지 Cache 생성
	 * 
	 */
	@Cacheable(value="ehcacheCache", key="#str")
	public CacheSampleVO prtService(String str) 
			throws Exception {
		
		simulateSlowService();
		return  new CacheSampleVO(str);
		
	}
	
	/**
	 *  메세지 Cache 삭제
	 * 
	 */
	@CacheEvict(value = "ehcacheCache", allEntries=true)
	public void delService() throws Exception {
		
	}


	private void simulateSlowService(){
        try {
            long time = 1000L;
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
	}

}