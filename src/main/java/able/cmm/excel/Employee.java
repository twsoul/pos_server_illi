package able.cmm.excel;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : Employee.java
 * @Description : Employee VO
 * @author "ADM Technology Team"
 * @since 2016. 7. 1
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.     "ADM Technology Team"     	최초 생성
 * </pre>
 */
public class Employee {
    private String name;
    private Date birthDate;
    private BigDecimal payment;
    private BigDecimal bonus;

    public Employee(String name, Date birthDate, BigDecimal payment, BigDecimal bonus) {
        this.name = name;
        this.birthDate = birthDate;
        this.payment = payment;
        this.bonus = bonus;
    }

    public Employee(String name, Date birthDate, double payment, double bonus) {
        this(name, birthDate, new BigDecimal(payment), new BigDecimal(bonus));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }
}
