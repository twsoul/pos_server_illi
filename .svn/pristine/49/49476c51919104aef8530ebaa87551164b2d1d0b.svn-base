package able.common.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import able.basic.web.BasicSampleController;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

//import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import net.sf.json.JSONObject;

@SuppressWarnings("unchecked")
public class AbleRequestData {
	
	private static final Logger logger = LoggerFactory.getLogger(BasicSampleController.class);
	
	//private Map<String,Object> uriPathVal;		//rest로 넘어온 URI Path VARIABLES ATTRIBUTE 맵정보
	//private Map<String,Object> reqMap;			//클라이언트에서 넘어온 request(HEAD+BODY) 모든정보
	private Map<String,Object> reqHeadMap;		//클라이언트에서 넘어온 공통 헤더 맵정보
	private Map<String,Object> reqBodyMap;		//클라이언트에서 넘긴 파라미터 맵정보

	//클라이언트에 넘길 Response 맵 세팅
	private Map<String,Object> responseMap = new HashMap<String, Object>();
	private Map<String,Object> responseBodyMap= new HashMap<String, Object>();
	
	private boolean isMultipart = false;
	
	public AbleRequestData(HttpServletRequest req) {
	    Map pathVariables = (Map)req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Map map = new HashMap();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
          map = (Map)objectMapper.readValue(req.getInputStream(), new TypeReference<Map<String,Object>>() { } );
        } catch (Exception e) {
            logger.info("#####  : "+e.getMessage());
        }
        JSONObject mspData = new JSONObject();
        mspData.putAll(map);
        req.setAttribute("http-body", map);
        req.setAttribute("head", map.get("head"));
        req.setAttribute("body", map.get("body"));
        req.setAttribute("rest_uri_path_att", pathVariables);
        
		//uriPathVal	= (Map<String,Object>)req.getAttribute(Const.REST_URI_PATH_VAL);
		//reqMap		= (Map<String,Object>)req.getAttribute(Const.HTTP_BODY);
		reqHeadMap	= (Map<String,Object>)req.getAttribute("head");
		reqBodyMap  = (Map<String,Object>)req.getAttribute("body");		
		if(reqHeadMap==null){
			reqHeadMap = new HashMap<String, Object>();
		}
		reqHeadMap.put("result_code", "200");
		reqHeadMap.put("result_msg", "Success");
		logger.info("ReQuestMap : " + map.toString());
	}
	
	public AbleRequestData(HttpServletRequest req, String UPLOAD_ROOT_PATH) {
		reqHeadMap	= (Map<String,Object>)req.getAttribute("head");
		reqBodyMap	= (Map<String,Object>)req.getAttribute("body");
		
		if(reqHeadMap==null){
			reqHeadMap = new HashMap<String, Object>();
		}
		
		isMultipart = ServletFileUpload.isMultipartContent(req);
		if (isMultipart){
			try {
				List<Map<String,Object>> imgInfoList = new ArrayList<Map<String,Object>>();
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)req;
				final Map<String, MultipartFile> files = multipartRequest.getFileMap();
				Iterator<Map.Entry<String, MultipartFile>> itr = files.entrySet().iterator();
				MultipartFile file;
				String filePath = "";
				String uploadFileName = "";
				while(itr.hasNext()) {
					Map.Entry<String, MultipartFile> entry = itr.next();
					//System.out.println("[" + entry.getKey() + "]" + entry.getValue());
					file = entry.getValue();
					if (!"".equals(file.getOriginalFilename())){
						uploadFileName = file.getOriginalFilename();
						String fileExtention = uploadFileName.substring(uploadFileName.lastIndexOf(".")+1,uploadFileName.length()).toLowerCase();
	                    if(!fileExtention.equals("jpg") && !fileExtention.equals("gif") && !fileExtention.equals("png")){
	                        throw new Exception("올바르지 않은 확장자 입니다");
	                    }
						filePath = UPLOAD_ROOT_PATH + File.separator + file.getOriginalFilename();
						file.transferTo(new File(filePath));
					}
					Map<String,Object> imgInfoMap = new HashMap<String, Object>();
					imgInfoMap.put("httpurl", getServerHostURL(req) + "/" + filePath + "/" + uploadFileName);
					imgInfoMap.put("uploadFileName", uploadFileName);
					imgInfoMap.put("absPath", filePath);
					imgInfoList.add(imgInfoMap);
				}
				responseBodyMap.put("attachFiles", imgInfoList);
				reqHeadMap.put("result_code", "200");
				reqHeadMap.put("result_msg", "Success");
			} catch (Exception ex) {
				reqHeadMap.put("result_code", "500");
				reqHeadMap.put("result_msg", ex.getMessage());
				logger.info("File Upload Exception!! : " + ex.getMessage());
				
			}
		} else {
			reqHeadMap.put("result_code", "200");
			reqHeadMap.put("result_msg", "Success");
		}
	}
	
	private String getServerHostURL(HttpServletRequest request) {
        String sHostUrl = request.getScheme() + "://" + request.getServerName() +
                (request.getServerPort() > 0 ? ":" + request.getServerPort() : "") + request.getContextPath();
        sHostUrl = sHostUrl.endsWith("/") ? sHostUrl : sHostUrl + "/";
        return sHostUrl;
    }
	
	public boolean getIsMultipart() {
		return isMultipart;
	}
	
	public Map<String,Object> getRequestBodyMap() {
		return reqBodyMap;
	}
	
	public Map<String,Object> getBodyMap() {
		return responseBodyMap;
	}
	
	/*
	 * (로그) 클라이언트에서 넘어온 공통 헤더 맵정보 출력
	 */
	public String getLogHeader() {
		String rtnLog;
		Set<Map.Entry<String,Object>> HeadMapSet = reqHeadMap.entrySet();
		rtnLog = "#############################################################\n";
		rtnLog += "################## 클라이언트가 보낸 헤더정보 ###############\n";
		rtnLog += "#############################################################\n";
		for(Map.Entry<String,Object> me : HeadMapSet){
			rtnLog += "# Key:"+me.getKey() + ", Value : " + me.getValue() + "\n";
		}
		rtnLog += "#############################################################\n";
		
		//JSON 데이타로 출력해보기
		JSONObject headMapJson = new JSONObject();
		headMapJson.putAll(reqHeadMap);
		rtnLog += "#############################################################\n";
		rtnLog += "############### 클라이언트가 보낸 헤더 JSON변환 #############\n";
		rtnLog += "#############################################################\n";
		rtnLog += "# Head Json DATA:"+headMapJson + "\n";
		rtnLog += "#############################################################";
		return rtnLog;
	}
	
	/*
	 * (로그) 클라이언트에서 넘어온 파라미터 맵정보 출력
	 */
	public String getLogBody() {
		String rtnLog;
		Set<Map.Entry<String,Object>> BodyMapSet = reqBodyMap.entrySet();
		rtnLog = "#############################################################\n";
		rtnLog += "############## 클라이언트가 보낸 파라미터 정보 ##############\n";
		rtnLog += "#############################################################\n";
		for(Map.Entry<String,Object> me : BodyMapSet){
			rtnLog += "# Key:"+me.getKey() + ", Value : " + me.getValue() +"\n";
		}
		rtnLog += "#############################################################\n";

		//JSON 데이타로 출력해보기
		JSONObject bodyMapJson = new JSONObject();
		bodyMapJson.putAll(reqHeadMap);
		rtnLog = "#############################################################\n";
		rtnLog += "############ 클라이언트가 보낸 파라미터 JSON변환   ##########\n";
		rtnLog += "#############################################################\n";
		rtnLog += "# Parameters Json DATA:"+bodyMapJson + "\n";
		rtnLog += "#############################################################";
		return rtnLog;
	}
	
	/*
	 * 리턴용 Body 데이터 삽입
	 */
	public void setBodyMap(String name, List<Map<String,Object>> listData) {
		responseBodyMap.put(name, listData);
	}
	public void setBodyMap(String name, Map<String,Object> Data) {
		responseBodyMap.put(name, Data);
	}
	public void setBodyMap(String name, String Data) {
		responseBodyMap.put(name, Data);
	}
	
	/*
	 * Exception 데이터 등록
	 */
	public void setError(Exception e) {
		reqHeadMap.put("result_code", "500");
		if (e.getMessage() != null){
			if(e.getMessage() == "Session_err1"){
				reqHeadMap.put("result_code", "999");
				reqHeadMap.put("result_msg", "Session Expired. Please sign in again");
				logger.info("AbleRequestData Exception!! : " + e.getMessage());
			}
			else if(e.getMessage() == "Session_err2"){
				reqHeadMap.put("result_code", "999");
				reqHeadMap.put("result_msg", "No session matched. Please sign in again");
				logger.info("AbleRequestData Exception!! : " + e.getMessage());
			}
			else{
				reqHeadMap.put("result_msg", "A server error occurred");
				logger.info("AbleRequestData Exception!! : " + e.getMessage());
			}
		}
		else
			reqHeadMap.put("result_msg", "An unknown error occurred");
	}
	
	/*
	 * Json 데이터로 리턴
	 */
	public Object getReturnJson() {
		responseMap.put("head", reqHeadMap);
		responseMap.put("body", responseBodyMap);
		logger.info("ReSponseMap : "+responseMap.toString());
		return responseMap;
	}
}
