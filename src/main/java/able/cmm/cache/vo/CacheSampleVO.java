package able.cmm.cache.vo;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : CacheSampleVO.java
 * @Description : 캐시 메세지 처리 VO
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
public class CacheSampleVO {

	// 출력메세지
	private String description;
    
	CacheSampleVO() {}
	
	public CacheSampleVO(String description) {
		setDescription(description);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description =  description;
	}

	public String toString() {
		return "CacheSampleVO [description=" + description + "]";
	}
}
