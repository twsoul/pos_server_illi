package able.cmm.valid.service.validator;

import java.util.regex.Matcher;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @ClassName : PhoneNumberValidator.java
 * @Description : Custom Validator 정의
 * @author ADM기술팀
 * @since 2016. 7. 1.
 * @version 1.0
 * @see
 * @Modification Information
 * 
 *               <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.       ADM기술팀                                     최초 생성
 * </pre>
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    private java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[0-9]\\d{2}-(\\d{3}|\\d{4})-\\d{4}$");

    @Override
    public void initialize(PhoneNumber annotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.length() == 0) {
            return true;
        }

        // 전화번호 검증(Pattern)
        Matcher m = pattern.matcher(value);
        return m.matches();
    }
}
