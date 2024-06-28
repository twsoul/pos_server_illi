package able.cmm.msg.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @ClassName   : MessageMngVO.java
 * @Description : 메시지 관리 vo
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
public class MessageMngVO implements Serializable{

	private static final long serialVersionUID = 1L;

	@NotNull(message="{errors.required}")
	@Size(min=5, max=20, message="5~20 길이 문자 입력")
	private String msgKey;
	
	private String msgKo;
	
	private String msgEn;
	
	private String lastModify;
	
	private String msgKoOrg;
	
	private String msgEnOrg;

	public String getMsgKey() {
		return msgKey;
	}

	public void setMsgKey(String msgKey) {
		this.msgKey = msgKey;
	}

	public String getMsgKo() {
		return msgKo;
	}

	public void setMsgKo(String msgKo) {
		this.msgKo = msgKo;
	}

	public String getMsgEn() {
		return msgEn;
	}

	public void setMsgEn(String msgEn) {
		this.msgEn = msgEn;
	}

	public String getLastModify() {
		return lastModify;
	}

	public void setLastModify(String lastModify) {
		this.lastModify = lastModify;
	}

	public String getMsgKoOrg() {
		return msgKoOrg;
	}

	public void setMsgKoOrg(String msgKoOrg) {
		this.msgKoOrg = msgKoOrg;
	}

	public String getMsgEnOrg() {
		return msgEnOrg;
	}

	public void setMsgEnOrg(String msgEnOrg) {
		this.msgEnOrg = msgEnOrg;
	}
	
	
}
