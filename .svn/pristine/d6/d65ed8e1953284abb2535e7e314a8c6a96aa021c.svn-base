package able.basic.web;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.holders.StringHolder;

import able.com.web.HController;
import able.common.utility.AbleRequestData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Eai extends HController implements Serializable{
	private static final Logger logger = LoggerFactory.getLogger(Eai.class);
	
	private static final long serialVersionUID = 2868210232929931052L;

	@Autowired
	ServletContext context;

/*	@Value("${common.eai.l4}")
	private String EAI_L4;
	@Value("${common.eai.ap1}")
	private String EAI_AP1;
	@Value("${common.eai.ap2}")
	private String EAI_AP2;
	@Value("${common.eai.port}")
	private String EAI_PORT;
	@Value("${common.eai.qname}")
	private String EAI_QNAME;
	@Value("${common.eai.qname2}")
	private String EAI_QNAME2;
	@Value("${common.eai.id}")
	private String EAI_ID;
	@Value("${common.eai.pwd}")
	private String EAI_PWD;*/
	
	private String EAI_L4 = "10.135.100.217"; //운영
	private String EAI_AP1 = "10.135.106.184"; //개발
	private String EAI_PORT = "6110";
	private String EAI_QNAME = "http://eaidev.dev.net";
	private String EAI_QNAME2 = "http://TRANSYS_EAI";
	private String EAI_ID = "if_user";
	private String EAI_PWD = "eaiUser1!";
	
	private String getUsrSessionId(HttpSession session) {
	  String strTmp = session.getId();
	  if(strTmp.lastIndexOf(".") > -1) {
	    return strTmp.substring(0, strTmp.lastIndexOf("."));
	  } else {
	    return strTmp;
	  }
	}
	
	/**
     * 출하 구분정보
     */
	@RequestMapping(value = "/api/LocationMoveCode.do", method = {RequestMethod.POST })
    @ResponseBody
    public Object LocationMoveCode(Model model, HttpServletRequest req, HttpSession session) {
        AbleRequestData reqData = new AbleRequestData(req);
        try {
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                System.out.println("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                System.out.println("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            System.out.println("#################### 출하 구분정보 인터페이스 수행결과 ####################");
            // Business Logic
            Map<String, Object> reqMap = reqData.getRequestBodyMap();

            // Fail Over
            URL wsdlURL = null;
            if(isNull(reqMap.get("SERVER")).equals("0")){
                wsdlURL = new URL("http://" + EAI_L4 + ":" + "6120"
                        + "/ws/C100_CPMSPDA.CPMSPDA.CPMSPDA_JMES4003.Src.adpt.CPMSPDA_WS_LocationMoveCode/C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4003_Src_adpt_CPMSPDA_WS_LocationMoveCode_Port");
            }else{
                wsdlURL = new URL("http://" + EAI_AP1 + ":" + "5120"
                        + "/ws/C100_CPMSPDA.CPMSPDA.CPMSPDA_JMES4003.Src.adpt.CPMSPDA_WS_LocationMoveCode/C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4003_Src_adpt_CPMSPDA_WS_LocationMoveCode_Port");
            }
            try{
                URLConnection con = wsdlURL.openConnection();
                HttpURLConnection exitCode = (HttpURLConnection) con;
                exitCode.getResponseCode();
            } catch (IOException e) {
                return null;
            }
            
            net.dev.eaidev.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4003_Src_adpt_CPMSPDA_WS_LocationMoveCode.OUT_LOAD_DATA[] OutLoadData = null;
            
            net.dev.eaidev.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4003_Src_adpt_CPMSPDA_WS_LocationMoveCode.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4003_Src_adpt_CPMSPDA_WS_LocationMoveCode_BinderStub stub = null;
            stub = new net.dev.eaidev.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4003_Src_adpt_CPMSPDA_WS_LocationMoveCode.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4003_Src_adpt_CPMSPDA_WS_LocationMoveCode_BinderStub(
                    wsdlURL, null);

            stub.setUsername(EAI_ID);
            stub.setPassword(EAI_PWD);
            
            System.out.println("Invoking LocationMoveCode...");
            
            OutLoadData = stub.CPMSPDA_LocationMoveCode(isNull(reqMap.get("PLANT_CD")),
                                                        isNull(reqMap.get("LINE_CD")),
                                                        isNull(reqMap.get("GROUP_ID")));

            System.out.println("##################################################################");
            
            if (OutLoadData.length != 0) {
                List<Map<String, Object>> LocationMoveCodeList = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < OutLoadData.length; i++) {
                    Map<String, Object> LocationMoveCodeListMap = new HashMap<String, Object>();
                    LocationMoveCodeListMap.put("LocationMoveCode", OutLoadData[i].getNAME());
                    LocationMoveCodeList.add(LocationMoveCodeListMap);
                }
                reqData.setBodyMap("LocationMoveCodeList", LocationMoveCodeList);
            } else {
                System.out.println(reqMap.get("event").toString() + " 결과 없음");
                System.out.println("##################################################################");
            }

            reqData.setBodyMap("LocationMoveCodeReturn", "S");
        } catch (Exception e) {
            reqData.setBodyMap("LocationMoveCodeReturn", "E");
            e.printStackTrace();
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
	
	/**
     * 출하 정보수신
     */
	@RequestMapping(value = "/api/PDAShipIF.do", method = {RequestMethod.POST })
    @ResponseBody
    public Object PDAShipIF(Model model, HttpServletRequest req, HttpSession session) {
        AbleRequestData reqData = new AbleRequestData(req);
        try {
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                System.out.println("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                System.out.println("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            System.out.println("#################### 출하 구분정보 인터페이스 수행결과 ####################");
            // Business Logic
            Map<String, Object> reqMap = reqData.getRequestBodyMap();

            // Fail Over
            URL wsdlURL = null;
            if(isNull(reqMap.get("SERVER")).equals("0")){
                wsdlURL = new URL("http://" + EAI_L4 + ":" + "6120"
                        + "/ws/C100_CPMSPDA.CPMSPDA.CPMSPDA_JMES4001.Src.adpt.CPMSPDA_WS_PDAShipIF/C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4001_Src_adpt_CPMSPDA_WS_PDAShipIF_Port");
            }else{
                wsdlURL = new URL("http://" + EAI_AP1 + ":" + "5120"
                        + "/ws/C100_CPMSPDA.CPMSPDA.CPMSPDA_JMES4001.Src.adpt.CPMSPDA_WS_PDAShipIF/C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4001_Src_adpt_CPMSPDA_WS_PDAShipIF_Port");
            }
            try{
                URLConnection con = wsdlURL.openConnection();
                HttpURLConnection exitCode = (HttpURLConnection) con;
                exitCode.getResponseCode();
            } catch (IOException e) {
                return null;
            }
            
            net.dev.eaidev.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4001_Src_adpt_CPMSPDA_WS_PDAShipIF.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4001_Src_adpt_CPMSPDA_WS_PDAShipIF_BinderStub stub = null;
            stub = new net.dev.eaidev.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4001_Src_adpt_CPMSPDA_WS_PDAShipIF.C100_CPMSPDA_CPMSPDA_CPMSPDA_JMES4001_Src_adpt_CPMSPDA_WS_PDAShipIF_BinderStub(
                    wsdlURL, null);

            stub.setUsername(EAI_ID);
            stub.setPassword(EAI_PWD);
            
            System.out.println("Invoking PDAShipIF...");
            
            String result = null;

            result = stub.CPMSPDA_PDAShipIF(isNull(reqMap.get("PLANT_CD")),
                                            isNull(reqMap.get("LINE_CD")),
                                            isNull(reqMap.get("GUBUN")),
                                            isNull(reqMap.get("BARCODE")),
                                            isNull(reqMap.get("PROCESS_DATE")),
                                            isNull(reqMap.get("AREA")),
                                            isNull(reqMap.get("UID")));

            System.out.println("##################################################################");
            
            if (result != null) {
                reqData.setBodyMap("PDAShipIFResult", result);
            } else {
                System.out.println(reqMap.get("event").toString() + " 결과 없음");
                System.out.println("##################################################################");
            }
            reqData.setBodyMap("PDAShipIFReturn", "S");
        } catch (Exception e) {
            reqData.setBodyMap("PDAShipIFReturn", "E");
            e.printStackTrace();
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
	
	/**
     * KD유상사급 판매처리
     */
    @RequestMapping(value = "/api/TLS_CreateSO.do", method = {RequestMethod.POST })
    @ResponseBody
    public Object TLS_CreateSO(Model model, HttpServletRequest req, HttpSession session) {
        AbleRequestData reqData = new AbleRequestData(req);
        try {
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                System.out.println("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                System.out.println("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            System.out.println("#################### KD유상사급 판매처리 데이터 수신 인터페이스 수행결과 ####################");
            // Business Logic
            Map<String, Object> reqMap = reqData.getRequestBodyMap();

            // Fail Over
            URL wsdlURL = null;
            if(isNull(reqMap.get("SERVER")).equals("0")){
                wsdlURL = new URL("http://" + EAI_L4 + ":" + EAI_PORT
                        + "/ws/C100_GMES.GMES.GMES_ERPSD001.Src.adpt:GMES_WS_CreateSO/C100_GMES_GMES_GMES_ERPSD001_Src_adpt_GMES_WS_CreateSO_Port");
            }else{
                wsdlURL = new URL("http://" + EAI_AP1 + ":" + EAI_PORT
                        + "/ws/C100_GMES.GMES.GMES_ERPSD001.Src.adpt:GMES_WS_CreateSO/C100_GMES_GMES_GMES_ERPSD001_Src_adpt_GMES_WS_CreateSO_Port");
            }
            try{
                URLConnection con = wsdlURL.openConnection();
                HttpURLConnection exitCode = (HttpURLConnection) con;
                exitCode.getResponseCode();
            } catch (IOException e) {
                return null;
            }
                      
            TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.holders.ES_FOOTERHolder esFooter = null;
            esFooter = new TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.holders.ES_FOOTERHolder();            

            TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.C100_GMES_GMES_GMES_ERPSD001_Src_adpt_GMES_WS_CreateSO_BinderStub stub = null;
            stub = new TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.C100_GMES_GMES_GMES_ERPSD001_Src_adpt_GMES_WS_CreateSO_BinderStub(
                    wsdlURL, null);

            stub.setUsername(EAI_ID);
            stub.setPassword(EAI_PWD);

            System.out.println("Invoking TLS_CreateSO...");

            String tbody = isNull(reqMap.get("tbody"));
            System.out.println("## tbody ---> " + tbody);
            String[] strArr = null;
            String kvStr = null;
            String[] kvArr = null;
            String[] kvArr2 = null;
            Map<String, String> bodyMap = new HashMap<String, String>();

            TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.IT_ITEM[] tBodyArray = null;
            TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.IT_ITEM tBody = null;

            if (tbody.indexOf('[') >= 0) {
                strArr = tbody.split("\\{");
                tBodyArray = new TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.IT_ITEM[strArr.length
                        - 1];
                for (int i = 1; i < strArr.length; i++) {
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    tBody = new TRANSYS_EAI.C100_GMES.GMES.GMES_ERPSD001.Src.adpt.GMES_WS_CreateSO.IT_ITEM();
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        else bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                    }
                    tBody.setPOSNR(isNull(bodyMap.get("posnr")));
                    tBody.setMATNR(isNull(bodyMap.get("matnr")));
                    tBody.setMENGE(isNull(bodyMap.get("menge")));
                    tBody.setMEINS(isNull(bodyMap.get("meins")));
                    tBody.setWERKS(isNull(bodyMap.get("werks")));
                    tBody.setLGORT(isNull(bodyMap.get("lgort")));
                    
                    tBodyArray[i - 1] = tBody;
                }
            }
            
            StringHolder EV_VBELN = new StringHolder();
            StringHolder EV_VBELN_VL = new StringHolder();

            stub.GMES_CreateSO("03",
                               isNull(reqMap.get("kunnr")),
                               "",
                               "",
                               isNull(reqMap.get("budat")),
                               "I",
                               "",
                               tBodyArray,
                               esFooter,
                               EV_VBELN,
                               EV_VBELN_VL);

            logger.info("- KD유상사급 판매처리 데이터 수신\n" + "ㆍEAI 성공여부= " + esFooter.value.getEAI() + "\n" + "ㆍEAI 메세지= "
                    + esFooter.value.getMESSAGE() + "\n" + "ㆍEAI 카운터= " + esFooter.value.getTOT_CNT() + "\n");

            if (esFooter.value.getEAI().toString().equals("S")) {
                System.out.println(reqMap.get("event").toString() + " 인터페이스 성공");
            } else {
                System.out.println(reqMap.get("event").toString() + " 인터페이스 실패");
            }
            System.out.println("##################################################################");
                
            reqData.setBodyMap("TLS_CreateSOReturn", esFooter.value.getEAI());
            reqData.setBodyMap("TLS_CreateSOMsg", esFooter.value.getMESSAGE());
            reqData.setBodyMap("TLS_CreateSOCount", esFooter.value.getTOT_CNT());
            reqData.setBodyMap("TLS_CreateSOEV_VBELN", EV_VBELN.value.toString());
            reqData.setBodyMap("TLS_CreateSOEV_VBELN_VL", EV_VBELN_VL.value.toString());
        } catch (Exception e) {
            reqData.setBodyMap("TLS_CreateSOReturn", "E");
            e.printStackTrace();
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }

	// Ping Check
	public boolean getPingCheck(String url) {
		boolean pingChk = false;

		try {
			InetAddress iaddr = InetAddress.getByName(url);
			boolean reachable = iaddr.isReachable(2000);

			if (reachable) {
				pingChk = true;
				System.out.println("EAI IP(" + url + ") alive!");
			} else {
				pingChk = false;
				System.out.println("EAI IP(" + url + ") dead..");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return pingChk;
	}

	/**
	 * NULL을 "" 변환
	 */
	public static String isNull(Object s) {
		if (s == null || s.toString().equals("null")) {
			return "";
		}

		return s.toString();
	}
}
