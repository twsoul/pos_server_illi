/**
 * C100_GMES_GMES_GMES_ERPSD001_Src_adpt_GMES_WS_CreateSO_BinderStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO;

public class C100_GMES_GMES_GMES_ERPSD001_Src_adpt_GMES_WS_CreateSO_BinderStub extends org.apache.axis.client.Stub implements TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.GMES_WS_CreateSO_PortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[1];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GMES_CreateSO");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "IV_TYPE"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "IV_KUNNR"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "IV_PURCH"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "IV_VBELN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "IV_WADAT"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "IV_PROCESS"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "IV_VBELN_VL"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "IT_ITEM"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://TRANSYS_EAI/C100_GMES/GMES/GMES_ERPSD001/Src/adpt/GMES_WS_CreateSO", "IT_ITEM"), TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.IT_ITEM[].class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ES_FOOTER"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://TRANSYS_EAI/C100_GMES/GMES/GMES_ERPSD001/Src/adpt/GMES_WS_CreateSO", "ES_FOOTER"), TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.ES_FOOTER.class, false, false);
        param.setOmittable(true);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "EV_VBELN"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "EV_VBELN_VL"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        param.setNillable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

    }

    public C100_GMES_GMES_GMES_ERPSD001_Src_adpt_GMES_WS_CreateSO_BinderStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public C100_GMES_GMES_GMES_ERPSD001_Src_adpt_GMES_WS_CreateSO_BinderStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public C100_GMES_GMES_GMES_ERPSD001_Src_adpt_GMES_WS_CreateSO_BinderStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://TRANSYS_EAI/C100_GMES/GMES/GMES_ERPSD001/Src/adpt/GMES_WS_CreateSO", "ES_FOOTER");
            cachedSerQNames.add(qName);
            cls = TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.ES_FOOTER.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://TRANSYS_EAI/C100_GMES/GMES/GMES_ERPSD001/Src/adpt/GMES_WS_CreateSO", "IT_ITEM");
            cachedSerQNames.add(qName);
            cls = TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.IT_ITEM.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public void GMES_CreateSO(java.lang.String IV_TYPE, java.lang.String IV_KUNNR, java.lang.String IV_PURCH, java.lang.String IV_VBELN, java.lang.String IV_WADAT, java.lang.String IV_PROCESS, java.lang.String IV_VBELN_VL, TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.IT_ITEM[] IT_ITEM, TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.holders.ES_FOOTERHolder ES_FOOTER, javax.xml.rpc.holders.StringHolder EV_VBELN, javax.xml.rpc.holders.StringHolder EV_VBELN_VL) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("C100_GMES_GMES_GMES_ERPSD001_Src_adpt_GMES_WS_CreateSO_Binder_GMES_CreateSO");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://TRANSYS_EAI/C100_GMES/GMES/GMES_ERPSD001/Src/adpt/GMES_WS_CreateSO", "GMES_CreateSO"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {IV_TYPE, IV_KUNNR, IV_PURCH, IV_VBELN, IV_WADAT, IV_PROCESS, IV_VBELN_VL, IT_ITEM});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                ES_FOOTER.value = (TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.ES_FOOTER) _output.get(new javax.xml.namespace.QName("", "ES_FOOTER"));
            } catch (java.lang.Exception _exception) {
                ES_FOOTER.value = (TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.ES_FOOTER) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("", "ES_FOOTER")), TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.ES_FOOTER.class);
            }
            try {
                EV_VBELN.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("", "EV_VBELN"));
            } catch (java.lang.Exception _exception) {
                EV_VBELN.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("", "EV_VBELN")), java.lang.String.class);
            }
            try {
                EV_VBELN_VL.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("", "EV_VBELN_VL"));
            } catch (java.lang.Exception _exception) {
                EV_VBELN_VL.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("", "EV_VBELN_VL")), java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
