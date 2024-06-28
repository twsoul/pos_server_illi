package able.cmm.valid.vo;

import java.util.Date;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import able.cmm.valid.service.validator.PhoneNumber;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ValidationSampleVO.java
 * @Description : Validation 샘플 vo 클래스
 * @author ADM기술팀
 * @since 2016. 7. 1.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.       ADM기술팀                                     최초 생성
 * </pre>
 */
public class ValidationSampleVO {

	//코드
	@NumberFormat(pattern = "######")
    @Digits(integer=6, fraction=0)	
	private int seq=0;
	
	//제목
	@NotEmpty(message="{errors.required}")
	private String title;
	
	//내용
	@NotEmpty(message="{errors.required}")
	@Size(min=10, max=400, message="{errors.range}")
	private String description;

	//작성자
	@NotEmpty(message="{errors.required}")
	@Size(max=10, message="{errors.maxlength}")
	private String regUser;
	
	//이메일 아이디
	@NotBlank(message="{errors.required}")
	private String emailAddr;
	
	//이메일 도메인
	@NotNull(message="{errors.required}")
	private String emailDomain;
	
	//나이
	@Range(min=1,max=150)
	private int age=0;
	
	//생년월일
	@NotEmpty
	@Pattern(regexp="^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$", message= "{errors.date}")
	private String birthDate;
	
	//전화번호
	@PhoneNumber
	private String phoneNumber;

	//등록일
	@Past
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date regDate = new Date();
	
	//선택동의
	@AssertTrue
	private Boolean approvalYN;

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

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

	public String getRegUser() {
		return regUser;
	}

	public void setRegUser(String regUser) {
		this.regUser = regUser;
	}

	public String getEmailAddr() {
		return emailAddr;
	}

	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	public String getEmailDomain() {
		return emailDomain;
	}

	public void setEmailDomain(String emailDomain) {
		this.emailDomain = emailDomain;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public Boolean getApprovalYN() {
		return approvalYN;
	}

	public void setApprovalYN(Boolean approvalYN) {
		this.approvalYN = approvalYN;
	}


}
