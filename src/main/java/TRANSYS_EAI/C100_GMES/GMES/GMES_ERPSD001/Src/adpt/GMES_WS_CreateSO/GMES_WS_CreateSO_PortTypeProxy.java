package TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO;

public class GMES_WS_CreateSO_PortTypeProxy implements TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.GMES_WS_CreateSO_PortType {
  private String _endpoint = null;
  private TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.GMES_WS_CreateSO_PortType gMES_WS_CreateSO_PortType = null;
  
  public GMES_WS_CreateSO_PortTypeProxy() {
    _initGMES_WS_CreateSO_PortTypeProxy();
  }
  
  public GMES_WS_CreateSO_PortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initGMES_WS_CreateSO_PortTypeProxy();
  }
  
  private void _initGMES_WS_CreateSO_PortTypeProxy() {
    try {
      gMES_WS_CreateSO_PortType = (new TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.C100_GMESGMESGMES_ERPSD001SrcAdptGMES_WS_CreateSOLocator()).getC100_GMES_GMES_GMES_ERPSD001_Src_adpt_GMES_WS_CreateSO_Port();
      if (gMES_WS_CreateSO_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)gMES_WS_CreateSO_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)gMES_WS_CreateSO_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (gMES_WS_CreateSO_PortType != null)
      ((javax.xml.rpc.Stub)gMES_WS_CreateSO_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.GMES_WS_CreateSO_PortType getGMES_WS_CreateSO_PortType() {
    if (gMES_WS_CreateSO_PortType == null)
      _initGMES_WS_CreateSO_PortTypeProxy();
    return gMES_WS_CreateSO_PortType;
  }
  
  public void GMES_CreateSO(java.lang.String IV_TYPE, java.lang.String IV_KUNNR, java.lang.String IV_PURCH, java.lang.String IV_VBELN, java.lang.String IV_WADAT, java.lang.String IV_PROCESS, java.lang.String IV_VBELN_VL, TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.IT_ITEM[] IT_ITEM, TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.holders.ES_FOOTERHolder ES_FOOTER, javax.xml.rpc.holders.StringHolder EV_VBELN, javax.xml.rpc.holders.StringHolder EV_VBELN_VL) throws java.rmi.RemoteException{
    if (gMES_WS_CreateSO_PortType == null)
      _initGMES_WS_CreateSO_PortTypeProxy();
    gMES_WS_CreateSO_PortType.GMES_CreateSO(IV_TYPE, IV_KUNNR, IV_PURCH, IV_VBELN, IV_WADAT, IV_PROCESS, IV_VBELN_VL, IT_ITEM, ES_FOOTER, EV_VBELN, EV_VBELN_VL);
  }
  
  
}