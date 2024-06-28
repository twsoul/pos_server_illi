package able.cmm.aop.vo;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : AopSampleVO.java
 * @Description : AOP 메세지 처리 VO
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
public class AopSampleVO {

	// 입력메세지
	private String title;

	// 출력메세지
	private String description;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
