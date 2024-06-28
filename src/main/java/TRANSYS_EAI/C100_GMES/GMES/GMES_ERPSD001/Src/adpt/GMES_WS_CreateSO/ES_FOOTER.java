/**
 * ES_FOOTER.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO;

public class ES_FOOTER  implements java.io.Serializable {
    private java.lang.String TOT_CNT;

    private java.lang.String EAI;

    private java.lang.String MESSAGE;

    public ES_FOOTER() {
    }

    public ES_FOOTER(
           java.lang.String TOT_CNT,
           java.lang.String EAI,
           java.lang.String MESSAGE) {
           this.TOT_CNT = TOT_CNT;
           this.EAI = EAI;
           this.MESSAGE = MESSAGE;
    }


    /**
     * Gets the TOT_CNT value for this ES_FOOTER.
     * 
     * @return TOT_CNT
     */
    public java.lang.String getTOT_CNT() {
        return TOT_CNT;
    }


    /**
     * Sets the TOT_CNT value for this ES_FOOTER.
     * 
     * @param TOT_CNT
     */
    public void setTOT_CNT(java.lang.String TOT_CNT) {
        this.TOT_CNT = TOT_CNT;
    }


    /**
     * Gets the EAI value for this ES_FOOTER.
     * 
     * @return EAI
     */
    public java.lang.String getEAI() {
        return EAI;
    }


    /**
     * Sets the EAI value for this ES_FOOTER.
     * 
     * @param EAI
     */
    public void setEAI(java.lang.String EAI) {
        this.EAI = EAI;
    }


    /**
     * Gets the MESSAGE value for this ES_FOOTER.
     * 
     * @return MESSAGE
     */
    public java.lang.String getMESSAGE() {
        return MESSAGE;
    }


    /**
     * Sets the MESSAGE value for this ES_FOOTER.
     * 
     * @param MESSAGE
     */
    public void setMESSAGE(java.lang.String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ES_FOOTER)) return false;
        ES_FOOTER other = (ES_FOOTER) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.TOT_CNT==null && other.getTOT_CNT()==null) || 
             (this.TOT_CNT!=null &&
              this.TOT_CNT.equals(other.getTOT_CNT()))) &&
            ((this.EAI==null && other.getEAI()==null) || 
             (this.EAI!=null &&
              this.EAI.equals(other.getEAI()))) &&
            ((this.MESSAGE==null && other.getMESSAGE()==null) || 
             (this.MESSAGE!=null &&
              this.MESSAGE.equals(other.getMESSAGE())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getTOT_CNT() != null) {
            _hashCode += getTOT_CNT().hashCode();
        }
        if (getEAI() != null) {
            _hashCode += getEAI().hashCode();
        }
        if (getMESSAGE() != null) {
            _hashCode += getMESSAGE().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ES_FOOTER.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://TRANSYS_EAI/C100_GMES/GMES/GMES_ERPSD001/Src/adpt/GMES_WS_CreateSO", "ES_FOOTER"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("TOT_CNT");
        elemField.setXmlName(new javax.xml.namespace.QName("", "TOT_CNT"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EAI");
        elemField.setXmlName(new javax.xml.namespace.QName("", "EAI"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MESSAGE");
        elemField.setXmlName(new javax.xml.namespace.QName("", "MESSAGE"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
