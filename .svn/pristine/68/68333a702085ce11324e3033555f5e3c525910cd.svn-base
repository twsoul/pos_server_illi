package net.dev.eaidev.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4001_Src_adpt_CPMSPDA_WS_PDAShipIF;

public class CPMSPDA_WS_PDAShipIF_PortTypeProxy implements net.dev.eaidev.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4001_Src_adpt_CPMSPDA_WS_PDAShipIF.CPMSPDA_WS_PDAShipIF_PortType {
  private String _endpoint = null;
  private net.dev.eaidev.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4001_Src_adpt_CPMSPDA_WS_PDAShipIF.CPMSPDA_WS_PDAShipIF_PortType cPMSPDA_WS_PDAShipIF_PortType = null;
  
  public CPMSPDA_WS_PDAShipIF_PortTypeProxy() {
    _initCPMSPDA_WS_PDAShipIF_PortTypeProxy();
  }
  
  public CPMSPDA_WS_PDAShipIF_PortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initCPMSPDA_WS_PDAShipIF_PortTypeProxy();
  }
  
  private void _initCPMSPDA_WS_PDAShipIF_PortTypeProxy() {
    try {
      cPMSPDA_WS_PDAShipIF_PortType = (new net.dev.eaidev.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4001_Src_adpt_CPMSPDA_WS_PDAShipIF.C100_CPMSPDACPMSPDACPMSPDA_JMES4001SrcAdptCPMSPDA_WS_PDAShipIFLocator()).getC100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4001_Src_adpt_CPMSPDA_WS_PDAShipIF_Port();
      if (cPMSPDA_WS_PDAShipIF_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)cPMSPDA_WS_PDAShipIF_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)cPMSPDA_WS_PDAShipIF_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (cPMSPDA_WS_PDAShipIF_PortType != null)
      ((javax.xml.rpc.Stub)cPMSPDA_WS_PDAShipIF_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public net.dev.eaidev.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4001_Src_adpt_CPMSPDA_WS_PDAShipIF.CPMSPDA_WS_PDAShipIF_PortType getCPMSPDA_WS_PDAShipIF_PortType() {
    if (cPMSPDA_WS_PDAShipIF_PortType == null)
      _initCPMSPDA_WS_PDAShipIF_PortTypeProxy();
    return cPMSPDA_WS_PDAShipIF_PortType;
  }
  
  public java.lang.String CPMSPDA_PDAShipIF(java.lang.String PLANT_CD, java.lang.String LINE_CD, java.lang.String GUBUN, java.lang.String BARCODE, java.lang.String PROCESS_DATE, java.lang.String AREA, java.lang.String UID) throws java.rmi.RemoteException{
    if (cPMSPDA_WS_PDAShipIF_PortType == null)
      _initCPMSPDA_WS_PDAShipIF_PortTypeProxy();
    return cPMSPDA_WS_PDAShipIF_PortType.CPMSPDA_PDAShipIF(PLANT_CD, LINE_CD, GUBUN, BARCODE, PROCESS_DATE, AREA, UID);
  }
  
  
}