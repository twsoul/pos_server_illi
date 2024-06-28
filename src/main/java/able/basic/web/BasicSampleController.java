package able.basic.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import able.basic.service.BasicSampleService;
import able.com.web.HController;
import able.common.utility.AbleRequestData;
import able.common.utility.SessionBindingListener;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

@Controller
public class BasicSampleController extends HController implements Serializable{
    private static final Logger logger = LoggerFactory.getLogger(BasicSampleController.class);
    
    private static final long serialVersionUID = 2868210232929931052L;
    
    @Value("${common.server.info}")
    private String CONNECT_SERVER;
    
    private String UPLOAD_ROOT_PATH = "/tgpsnas/tglo/fileAttach/";
    
    @Resource(name = "basicSampleService")
    private BasicSampleService basicSampleService;
    
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    ServletContext context;
    
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public @ResponseBody String home(Model model) {
        logger.info("============ H·able Gateway is Running! ==============");
        return "H·able Gateway is Running! - Connect for (" + CONNECT_SERVER + ") / [NEW H·able!!]";
    }
    
    @RequestMapping(value = "/loginLanding.do", method = { RequestMethod.POST })
    public ModelAndView loginLanding(HttpSession session, HttpServletRequest req) {
        logger.info("Controller : /loginLanding.do");
        String redirectUrl = "login.jsp";
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:" + redirectUrl);
        String resultSTR ="";
        try {
            // Business Logic
            String userid = isNull(req.getParameter("id").toString());
            String password = isNull(req.getParameter("pw").toString());
            String comp = isNull(req.getParameter("complist_"));
            if (isEmpty(comp))
                comp = "HPT";
            Map<String, Object> result = null;
            result = basicSampleService.getAuthCheckNInfo(userid, password);
            if (result == null){
                resultSTR = "E";
            } else {            
                 /*SESSION 등록*/
                session.setAttribute("USER_SESSION", getUsrSessionId(session));
                resultSTR = "S";
            }
        } catch (Exception e) {
            resultSTR = "E";
            e.printStackTrace();
        }
        if(resultSTR.equals("S")) {
            mav.setViewName("redirect:agree.jsp");
        }else{
            mav = new ModelAndView("redirect:" + redirectUrl, "message", "login failed");
        }
        return mav;
    }
    
    @RequestMapping(value = "/api/versionCheck.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object versionCheck(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/versionCheck.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            // Business Logic
            Map<String,Object> reqMap = reqData.getRequestBodyMap();
            String version = reqMap.get("version").toString();
            Map<String, Object> result = basicSampleService.versionCheck();
            if (result == null){
                reqData.setBodyMap("RESULT", "ERROR");
            } else {
                if(result.get("OS_VERSION").equals(version)){
                    reqData.setBodyMap("RESULT", "OK");
                }else{
                    reqData.setBodyMap("RESULT", "NG");
                }
            }
            
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/login.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object login(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/login.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            // Business Logic
            SessionBindingListener loginManager = SessionBindingListener.getInstance();
            Map<String,Object> reqMap = reqData.getRequestBodyMap();
            String userid = reqMap.get("user_id").toString();
            String password = reqMap.get("user_pw").toString();
            Map<String, Object> result = basicSampleService.getAuthCheckNInfo(userid, password);
            if (result == null){
                reqData.setBodyMap("RESULT", "ERROR");
            } else {
                loginManager.printloginUsers();
                if(loginManager.isUsing(userid)){
                    reqData.setBodyMap("SESSION", "ERROR");
                }else{
                    loginManager.setSession(session, userid);
                    reqData.setBodyMap("SESSION", "OK");
                }
                if(isNull(result.get("MOBL_AUTH_CD")).equals("C") || isNull(result.get("MOBL_AUTH_CD")).equals("D")){
                    if(isNull(result.get("FIRST_LOGIN_DT")).equals("")){
                        reqData.setBodyMap("RESULT", "PW");
                    }else{
                        reqData.setBodyMap("RESULT", "OK");
                        reqData.setBodyMap("AUTH_INFO", result);
                        session.setAttribute("USER_SESSION", getUsrSessionId(session));
                    }
                }else{
                    reqData.setBodyMap("RESULT", "OK");
                    reqData.setBodyMap("AUTH_INFO", result);
                    session.setAttribute("USER_SESSION", getUsrSessionId(session));
                }
            }
            
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/changePW.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object changePW(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/changePW.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            if(kvArr[j].split("=")[0].trim().equals("PW")){
                                bodyMap.put(kvArr[j].split("=")[0].trim(), passwordEncoder.encode(kvArr[j].split("=")[1].trim()));
                                bodyMap.put("PW_TMS", kvArr[j].split("=")[1].trim());
                            }else{
                                bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                            }
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }

            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.changePW(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/disconnect.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object disconnect(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/disconnect.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            // Business Logic
            /* SESSION DISCONNECT */
            SessionBindingListener loginManager = SessionBindingListener.getInstance();
            Map<String,Object> reqMap = reqData.getRequestBodyMap();
            String userid = reqMap.get("user_id").toString();
            loginManager.removeSession(userid);
            reqData.setBodyMap("RESULT", "OK");
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/logout.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object logout(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/logout.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            // Business Logic
            /* SESSION REMOVE */
            session.invalidate();
            reqData.setBodyMap("RESULT", "OK");
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }

    @RequestMapping(value = "/api/LanguageList.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object languagelist(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/LanguageList.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.get_pda_LangList(param);          
            reqData.setBodyMap("RESULTCount", String.valueOf(result.size()));
            reqData.setBodyMap("RESULT", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    private String getUsrSessionId(HttpSession session) {
      String strTmp = session.getId();
      if(strTmp.lastIndexOf(".") > -1) {
        return strTmp.substring(0, strTmp.lastIndexOf("."));
      } else {
        return strTmp;
      }
    }
    
    @RequestMapping(value = "/api/MenuList.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object menulist(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/MenuList.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.get_pda_MenuList(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PlantCodeList.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object plantcodelist(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PlantCodeList.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectComboPlantCode(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/LocCodeList.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object LocCodeList(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/LocCodeList.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectComboLocCode(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/LocCodeListLP.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object LocCodeListLP(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/LocCodeListLP.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectComboLocCodeLP(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/LocCodeListKD.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object LocCodeListKD(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/LocCodeListKD.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectComboLocCodeKD(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/LocCodeListEtc.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object LocCodeListEtc(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/LocCodeListEtc.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectComboLocCodeEtc(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }    
    
    @RequestMapping(value = "/api/selectVwCaseNo.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectVwCaseNo(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectVwCaseNo.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectVwCaseNo(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectVwCaseOnly.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectVwCaseOnly(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectVwCaseOnly.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectVwCaseOnly(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectVwBoxNo.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectVwBoxNo(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectVwBoxNo.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectVwBoxNo(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectBoxNoIn.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectBoxNoIn(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectBoxNoIn.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectBoxNoIn(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectBoxNoInTemp.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectBoxNoInTemp(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectBoxNoInTemp.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectBoxNoInTemp(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectBoxNo.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectBoxNo(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectBoxNo.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectBoxNo(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectBoxNoOut.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectBoxNoOut(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectBoxNoOut.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectBoxNoOut(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectBoxNoOutTemp.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectBoxNoOutTemp(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectBoxNoOutTemp.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectBoxNoOutTemp(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/AreaCodeList.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object AreaCodeList(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/AreaCodeList.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.AreaCodeList(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/LineCodeList.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object LineCodeList(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/LineCodeList.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.LineCodeList(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/AreaCodeList2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object AreaCodeList2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/AreaCodeList2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.AreaCodeList2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/LineCodeList2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object LineCodeList2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/LineCodeList2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.LineCodeList2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectWHCode.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectWHCode(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectWHCode.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectWHCode(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectLocCode.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectLocCode(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectLocCode.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectLocCode(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectRackCode.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectRackCode(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectRackCode.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectRackCode(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectVwRackCode.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectVwRackCode(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectVwRackCode.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectVwRackCode(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectComboMoveCauseCode.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectComboMoveCauseCode(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectComboMoveCauseCode.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectComboMoveCauseCode(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectComboProcCode.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectComboProcCode(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectComboProcCode.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectComboProcCode(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }

    @RequestMapping(value = "/api/selectComboProcCodeField.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectComboProcCodeField(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectComboProcCodeField.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectComboProcCodeField(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }    
    
    @RequestMapping(value = "/api/selectDeliNoO.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectDeliNoO(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectDeliNoO.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectDeliNoO(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectKDBoxNo.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectKDBoxNo(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectKDBoxNo.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectKDBoxNo(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectKDBoxNoTemp.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectKDBoxNoTemp(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectKDBoxNoTemp.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectKDBoxNoTemp(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectPlalletNoSCM.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectPlalletNoSCM(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectPlalletNoSCM.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectPlalletNoSCM(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectOtTmNo.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectOtTmNo(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectOtTmNo.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectOtTmNo(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectTmNo.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectTmNo(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectTmNo.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectTmNo(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectInvDump.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectInvDump(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectInvDump.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectInvDump(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectInvDumpExists.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectInvDumpExists(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectInvDumpExists.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectInvDumpExists(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectRealInvExists.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectRealInvExists(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectRealInvExists.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectRealInvExists(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectTmPlant.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectTmPlant(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectTmPlant.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectTmPlant(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectOtTmLen.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectOtTmLen(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectOtTmLen.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectOtTmLen(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectOtLogi.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectOtLogi(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectOtLogi.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectOtLogi(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectOtCkdLogi.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectOtCkdLogi(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectOtCkdLogi.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectOtCkdLogi(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectOtProcCd.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectOtProcCd(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectOtProcCd.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectOtProcCd(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectVwPallet.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectVwPallet(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectVwPallet.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectVwPallet(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectVwPalletTemp.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectVwPalletTemp(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectVwPalletTemp.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectVwPalletTemp(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/selectVwTm.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object selectVwTm(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/selectVwTm.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.selectVwTm(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/IN_010_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object IN_010_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/IN_010_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.IN_010_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/IN_010_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object IN_010_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/IN_010_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.IN_010_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
        
    @RequestMapping(value = "/api/PR_PDA_IN_020_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_IN_020_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_IN_020_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            if(kvArr[j].split("=")[0].trim().equals("PART_NM")){
                                bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim().replace("^&", "[").replace("&^", "]").replace("^*", ",").replace("^%", "="));
                            }else{
                                bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                            }
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }

            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_020_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_IN_020_C2");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_020_C2(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_IN_020_C3.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_IN_020_C3(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_IN_020_C3.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            if(kvArr[j].split("=")[0].trim().equals("DELI_BAR_NO")){
                                bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim().replace("^&", "[").replace("&^", "]").replace("^*", ",").replace("^%", "="));
                            }else{
                                bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                            }
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }

            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_020_C3(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_IN_020_C4");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_020_C4(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/IN_030_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object IN_030_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/IN_030_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.IN_030_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/IN_030_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object IN_030_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/IN_030_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.IN_030_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_IN_030_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_IN_030_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_IN_030_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_030_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_IN_040_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_IN_040_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_IN_040_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }

            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_040_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_IN_040_C2");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_040_C2(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/IN_050_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object IN_050_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/IN_050_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.IN_050_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/IN_051_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object IN_051_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/IN_051_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.IN_051_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/IN_051_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object IN_051_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/IN_051_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.IN_051_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/IN_051_S3.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object IN_051_S3(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/IN_051_S3.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.IN_051_S3(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/IN_051_S4.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object IN_051_S4(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/IN_051_S4.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.IN_051_S4(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/IN_051_S5.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object IN_051_S5(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/IN_051_S5.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.IN_051_S5(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_IN_051_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_IN_051_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_IN_051_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_051_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }

    @RequestMapping(value = "/api/IN_060_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object IN_060_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/IN_060_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.IN_060_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_IN_060_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_IN_060_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_IN_060_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            if(kvArr[j].split("=")[0].trim().equals("PART_NM")){
                                bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim().replace("^&", "[").replace("&^", "]").replace("^*", ",").replace("^%", "="));
                            }else{
                                bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                            }
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }

            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_060_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_IN_060_C2");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_060_C2(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/IN_070_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object IN_070_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/IN_070_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.IN_070_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_IN_070_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_IN_070_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_IN_070_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_070_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_IN_080_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_IN_080_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_IN_080_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }

            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_080_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_IN_080_C2");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_080_C2(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }

    @RequestMapping(value = "/api/IN_090_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object IN_090_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/IN_090_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.IN_090_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_IN_090_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_IN_090_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_IN_090_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_IN_090_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_010_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_010_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_010_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_010_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_015_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_015_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_015_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            String resultLotNo = "";
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_015_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                if(param1listMap.get(i).get("RTN_LOT") != null){
                    resultLotNo = param1listMap.get(i).get("RTN_LOT").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultLotNo", resultLotNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_015_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_015_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_015_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_015_C2(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_020_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_020_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_020_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_020_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_025_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_025_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_025_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_025_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_030_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_030_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_030_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_030_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_035_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_035_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_035_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_035_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_035_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_035_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_035_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }

            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_035_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_OT_035_C2");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_035_C2(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_040_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_040_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_040_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_040_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_040_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_040_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_040_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_040_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_040_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_040_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_040_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_040_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_041_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_041_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_041_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_041_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_041_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_041_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_041_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_041_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_050_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_050_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_050_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_050_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_060_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_060_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_060_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_060_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_070_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_070_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_070_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_070_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_080_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_080_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_080_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_080_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_080_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_080_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_080_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_080_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_080_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_080_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_080_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_080_C1(param1listMap.get(i));

                String result = "";
                String resultMoveNo = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_MOVE") != null){
                    resultMoveNo = param1listMap.get(i).get("RTN_MOVE").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultMoveNo", resultMoveNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_080_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_080_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_080_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }

            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_080_C2(param1listMap.get(i));

                String result = "";
                String resultMoveDt = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_MOVE_DT") != null){
                    resultMoveDt = param1listMap.get(i).get("RTN_MOVE_DT").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultMoveDt", resultMoveDt);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_080_C3.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_080_C3(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_080_C3.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_080_C3(param1listMap.get(i));

                String result = "";
                String resultBoxNo = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBoxNo = param1listMap.get(i).get("RTN_BOX").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultBoxNo", resultBoxNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_085_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_085_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_085_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_085_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_085_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_085_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_085_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_085_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_085_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_085_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_085_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_085_C1(param1listMap.get(i));

                String result = "";
                String resultMoveNo = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_MOVE") != null){
                    resultMoveNo = param1listMap.get(i).get("RTN_MOVE").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultMoveNo", resultMoveNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_085_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_085_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_085_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }

            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_085_C2(param1listMap.get(i));

                String result = "";
                String resultMoveDt = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_MOVE_DT") != null){
                    resultMoveDt = param1listMap.get(i).get("RTN_MOVE_DT").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultMoveDt", resultMoveDt);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_086_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_086_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_086_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_086_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_086_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_086_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_086_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }

            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_086_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_087_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_087_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_087_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_087_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_087_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_087_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_087_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }

            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_087_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_090_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_090_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_090_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_090_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_090_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_090_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_090_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_090_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_100_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_100_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_100_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_100_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_101_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_101_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_101_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_101_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_101_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_101_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_101_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_101_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_101_I1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_101_I1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_101_I1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            basicSampleService.OT_101_I1(param);          
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_110_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_110_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_110_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_110_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_110_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_110_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_110_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_110_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_110_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_110_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_110_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_110_C2(param1listMap.get(i));

                String result = "";
                String resultBoxNo = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBoxNo = param1listMap.get(i).get("RTN_BOX").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultBoxNo", resultBoxNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_111_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_111_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_111_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_111_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_115_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_115_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_115_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_115_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_115_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_115_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_115_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_115_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_118_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_118_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_118_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_118_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_118_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_118_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_118_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_118_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_118_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_118_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_118_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_118_C1(param1listMap.get(i));

                String result = "";
                String resultBoxNo = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBoxNo = param1listMap.get(i).get("RTN_BOX").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultBoxNo", resultBoxNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_120_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_120_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_120_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_120_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_120_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_120_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_120_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_120_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_120_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_120_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_120_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_120_C1(param1listMap.get(i));

                String result = "";
                String resultBoxNo = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBoxNo = param1listMap.get(i).get("RTN_BOX").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultBoxNo", resultBoxNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_140_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_140_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_140_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_140_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_140_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_140_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_140_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    bodyMap.put("RTN_ORD_NO", "");
                    bodyMap.put("RTN_ORD_SEQ", "");
                    param1listMap.add(bodyMap);
                }
            }
            String ORD_NO = null;
            String ORD_SEQ = null;
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_140_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                if(param1listMap.get(i).get("RTN_ORD_NO") != null){
                    ORD_NO = param1listMap.get(i).get("RTN_ORD_NO").toString();
                }
                if(param1listMap.get(i).get("RTN_ORD_SEQ") != null){
                    ORD_SEQ = param1listMap.get(i).get("RTN_ORD_SEQ").toString();
                }
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            logger.info("Controller : /api/PR_PDA_OT_140_C2.do");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param21listMap = new ArrayList<Map<String, Object>>();
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    bodyMap.put("ORD_NO", ORD_NO);
                    bodyMap.put("ORD_SEQ", ORD_SEQ);
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param21listMap.add(bodyMap);
                }
            }
            for (int i = 0; i < param21listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_140_C2(param21listMap.get(i));

                String result = "";

                if(param21listMap.get(i).get("RTN_MSG") != null){
                    result = param21listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_145_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_145_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_145_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_145_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_145_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_145_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_145_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_145_C1(param1listMap.get(i));

                String result = "";
                String resultBoxNo = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBoxNo = param1listMap.get(i).get("RTN_BOX").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultBoxNo", resultBoxNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/OT_150_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object OT_150_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/OT_150_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.OT_150_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_OT_150_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_OT_150_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_OT_150_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_OT_150_C1(param1listMap.get(i));

                String result = "";
                String resultBoxNo = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBoxNo = param1listMap.get(i).get("RTN_BOX").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultBoxNo", resultBoxNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/ST_010_S1_TEMP1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object ST_010_S1_TEMP1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/ST_010_S1_TEMP1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.ST_010_S1_TEMP1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/ST_010_S1_TEMP2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object ST_010_S1_TEMP2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/ST_010_S1_TEMP2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.ST_010_S1_TEMP2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/ST_010_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object ST_010_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/ST_010_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.ST_010_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_010_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_010_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_010_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            if(kvArr[j].split("=")[0].trim().equals("PART_NM")){
                                bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim().replace("^&", "[").replace("&^", "]").replace("^*", ",").replace("^%", "="));
                            }else{
                                bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                            }
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_010_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }

                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_020_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_020_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_020_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_020_C1(param1listMap.get(i));

                String result = "";
                String resultBoxNo = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBoxNo = param1listMap.get(i).get("RTN_BOX").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultBoxNo", resultBoxNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_020_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_020_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_020_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_020_C2(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_025_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_025_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_025_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_025_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_030_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_030_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_030_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_030_C1(param1listMap.get(i));

                String result = "";
                String resultBoxNo = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBoxNo = param1listMap.get(i).get("RTN_BOX").toString();
                }

                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultBoxNo", resultBoxNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_030_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_030_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_030_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_030_C2(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_ST_030_C3.do");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_030_C3(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_040_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_040_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_040_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_040_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/ST_050_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object ST_050_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/ST_050_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.ST_050_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_050_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_050_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_050_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_050_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_060_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_060_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_060_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_060_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_065_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_065_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_065_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_065_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_070_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_070_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_070_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_070_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/ST_080_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object ST_080_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/ST_080_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.ST_080_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/ST_080_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object ST_080_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/ST_080_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.ST_080_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_080_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_080_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_080_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_080_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_100_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_100_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_100_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_100_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_110_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_110_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_110_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    bodyMap.put("RTN_BAR", "");
                    param1listMap.add(bodyMap);
                }
            }
            String CASE_NO = null;
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_110_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                if(param1listMap.get(i).get("RTN_BAR") != null){
                    CASE_NO = param1listMap.get(i).get("RTN_BAR").toString();
                }
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            logger.info("Controller : /api/PR_PDA_ST_110_C2.do");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param21listMap = new ArrayList<Map<String, Object>>();
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    bodyMap.put("CASE_NO", CASE_NO);
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param21listMap.add(bodyMap);
                }
            }
            for (int i = 0; i < param21listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_110_C2(param21listMap.get(i));

                String result = "";

                if(param21listMap.get(i).get("RTN_MSG") != null){
                    result = param21listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_120_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_120_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_120_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_120_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_130_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_130_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_130_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_130_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_ST_140_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_140_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_140_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_140_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_010_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_010_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_010_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_010_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_010_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_010_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_010_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_010_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_010_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_010_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_010_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_010_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_020_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_020_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_020_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_020_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_030_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_030_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_030_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_030_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_040_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_040_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_040_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_040_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_050_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_050_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_050_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_050_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_060_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_060_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_060_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_060_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_060_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_060_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_060_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_060_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_070_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_070_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_070_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_070_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_070_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_070_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_070_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_070_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_070_S3.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_070_S3(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_070_S3.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_070_S3(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_070_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_070_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_070_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_070_C1(param1listMap.get(i));

                String result = "";
                String resultBUDAT = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_BUDAT") != null){
                    resultBUDAT = param1listMap.get(i).get("RTN_BUDAT").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultBUDAT", resultBUDAT);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_070_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_070_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_070_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_070_C2(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_075_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_075_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_075_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_075_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_075_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_075_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_075_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_075_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_080_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_080_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_080_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_080_C1(param1listMap.get(i));

                String result = "";
                String resultBoxNo = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBoxNo = param1listMap.get(i).get("RTN_BOX").toString();
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultBoxNo", resultBoxNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_080_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_080_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_080_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_080_C2(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_090_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_090_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_090_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_090_C1(param1listMap.get(i));

                String result = "";
                String resultBoxNo = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBoxNo = param1listMap.get(i).get("RTN_BOX").toString();
                }

                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultBoxNo", resultBoxNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_090_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_090_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_090_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_090_C2(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_KD_090_C3");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_090_C3(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_100_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_100_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_100_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_100_C1(param1listMap.get(i));

                String result = "";
                String resultBoxNo = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBoxNo = param1listMap.get(i).get("RTN_BOX").toString();
                }

                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultBoxNo", resultBoxNo);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_100_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_100_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_100_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_100_C2(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_KD_100_C3");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_100_C3(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_110_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_110_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_110_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_110_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_110_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_110_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_110_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_110_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_120_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_120_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_120_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_120_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_120_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_120_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_120_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_120_C1(param1listMap.get(i));

                String result = "";
                String resultDT = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_DT") != null){
                    resultDT = param1listMap.get(i).get("RTN_DT").toString();
                }

                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultDT", resultDT);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_120_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_120_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_120_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_120_C2(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_130_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_130_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_130_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_130_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_130_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_130_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_130_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_130_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_130_S3.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_130_S3(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_130_S3.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_130_S3(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_130_S4.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_130_S4(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_130_S4.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_130_S4(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_130_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_130_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_130_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_130_C1(param1listMap.get(i));

                String result = "";
                String resultCase = "";
                String resultPart = "";
                String resultBox = "";
                String resultCQty = "";
                String resultQty = "";
                String resultDone = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_CASE") != null){
                    resultCase = param1listMap.get(i).get("RTN_CASE").toString();
                }
                if(param1listMap.get(i).get("RTN_PART") != null){
                    resultPart = param1listMap.get(i).get("RTN_PART").toString();
                }
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBox = param1listMap.get(i).get("RTN_BOX").toString();
                }
                if(param1listMap.get(i).get("RTN_CQTY") != null){
                    resultCQty = param1listMap.get(i).get("RTN_CQTY").toString();
                }
                if(param1listMap.get(i).get("RTN_QTY") != null){
                    resultQty = param1listMap.get(i).get("RTN_QTY").toString();
                }
                if(param1listMap.get(i).get("RTN_DONE") != null){
                    resultDone = param1listMap.get(i).get("RTN_DONE").toString();
                }
                if("S".equals(result.substring(0,1)))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultCase", resultCase);
                    reqData.setBodyMap("resultPart", resultPart);
                    reqData.setBodyMap("resultBox", resultBox);
                    reqData.setBodyMap("resultCQty", resultCQty);
                    reqData.setBodyMap("resultQty", resultQty);
                    reqData.setBodyMap("resultDone", resultDone);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_130_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_130_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_130_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_130_C2(param1listMap.get(i));

                String result = "";
                String resultCase = "";
                String resultPart = "";
                String resultBox = "";
                String resultCQty = "";
                String resultQty = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_CASE") != null){
                    resultCase = param1listMap.get(i).get("RTN_CASE").toString();
                }
                if(param1listMap.get(i).get("RTN_PART") != null){
                    resultPart = param1listMap.get(i).get("RTN_PART").toString();
                }
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBox = param1listMap.get(i).get("RTN_BOX").toString();
                }
                if(param1listMap.get(i).get("RTN_CQTY") != null){
                    resultCQty = param1listMap.get(i).get("RTN_CQTY").toString();
                }
                if(param1listMap.get(i).get("RTN_QTY") != null){
                    resultQty = param1listMap.get(i).get("RTN_QTY").toString();
                }

                if("S".equals(result.substring(0,1)))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultCase", resultCase);
                    reqData.setBodyMap("resultPart", resultPart);
                    reqData.setBodyMap("resultBox", resultBox);
                    reqData.setBodyMap("resultCQty", resultCQty);
                    reqData.setBodyMap("resultQty", resultQty);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_130_C3.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_130_C3(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_130_C3.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_130_C3(param1listMap.get(i));

                String result = "";
                String resultCase = "";
                String resultPart = "";
                String resultBox = "";
                String resultCQty = "";
                String resultQty = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if(param1listMap.get(i).get("RTN_CASE") != null){
                    resultCase = param1listMap.get(i).get("RTN_CASE").toString();
                }
                if(param1listMap.get(i).get("RTN_PART") != null){
                    resultPart = param1listMap.get(i).get("RTN_PART").toString();
                }
                if(param1listMap.get(i).get("RTN_BOX") != null){
                    resultBox = param1listMap.get(i).get("RTN_BOX").toString();
                }
                if(param1listMap.get(i).get("RTN_CQTY") != null){
                    resultCQty = param1listMap.get(i).get("RTN_CQTY").toString();
                }
                if(param1listMap.get(i).get("RTN_QTY") != null){
                    resultQty = param1listMap.get(i).get("RTN_QTY").toString();
                }

                if("S".equals(result.substring(0,1)))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                    reqData.setBodyMap("resultCase", resultCase);
                    reqData.setBodyMap("resultPart", resultPart);
                    reqData.setBodyMap("resultBox", resultBox);
                    reqData.setBodyMap("resultCQty", resultCQty);
                    reqData.setBodyMap("resultQty", resultQty);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_140_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_140_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_140_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_140_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_140_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_140_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_140_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_140_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_145_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_145_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_145_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_145_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_145_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_145_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_145_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_145_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_150_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_150_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_150_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_150_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_160_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_160_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_160_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    bodyMap.put("RTN_BAR", "");
                    param1listMap.add(bodyMap);
                }
            }
            String CASE_NO = null;
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_160_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                if(param1listMap.get(i).get("RTN_BAR") != null){
                    CASE_NO = param1listMap.get(i).get("RTN_BAR").toString();
                }
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            logger.info("Controller : /api/PR_PDA_KD_160_C2.do");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param21listMap = new ArrayList<Map<String, Object>>();
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    bodyMap.put("CASE_NO", CASE_NO);
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param21listMap.add(bodyMap);
                }
            }
            for (int i = 0; i < param21listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_160_C2(param21listMap.get(i));

                String result = "";

                if(param21listMap.get(i).get("RTN_MSG") != null){
                    result = param21listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
   
    @RequestMapping(value = "/api/KD_170_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_170_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_170_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_170_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_170_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_170_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_170_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_170_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_170_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_170_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_170_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_170_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_180_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_180_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_180_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_180_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_190_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_190_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_190_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_190_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_200_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_200_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_200_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_200_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_200_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_200_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_200_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_200_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_200_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_200_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_200_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_200_C2(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_205_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_205_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_205_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_205_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_210_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_210_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_210_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_210_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_220_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_220_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_220_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_220_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_229_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_229_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_229_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_229_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_229_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_229_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_229_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_229_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_229_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_229_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_229_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_229_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_230_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_230_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_230_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_230_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_230_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_230_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_230_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_230_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_230_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_230_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_230_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_230_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_240_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_240_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_240_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_240_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_240_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_240_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_240_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_240_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_240_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_240_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_240_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_240_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_250_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_250_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_250_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_250_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_253_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_253_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_253_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_253_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_253_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_253_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_253_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_253_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_255_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_255_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_255_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_255_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_255_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_255_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_255_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_255_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
        
    @RequestMapping(value = "/api/PR_PDA_KD_257_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_257_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_257_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_257_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_260_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_260_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_260_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_260_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_260_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_260_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_260_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    bodyMap.put("RTN_SEQ", "");
                    param1listMap.add(bodyMap);
                }
            }

            String PLT_SEQ = null;
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_260_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                if(param1listMap.get(i).get("RTN_SEQ") != null){
                    PLT_SEQ = param1listMap.get(i).get("RTN_SEQ").toString();
                }
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_KD_260_C2");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    bodyMap.put("PLT_SEQ", PLT_SEQ);
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_260_C2(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_265_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_265_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_265_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_265_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_265_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_265_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_265_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    bodyMap.put("RTN_SEQ", "");
                    param1listMap.add(bodyMap);
                }
            }

            String PLT_SEQ = null;
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_265_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                if(param1listMap.get(i).get("RTN_SEQ") != null){
                    PLT_SEQ = param1listMap.get(i).get("RTN_SEQ").toString();
                }
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_KD_265_C2");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    bodyMap.put("PLT_SEQ", PLT_SEQ);
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_265_C2(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/KD_270_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object KD_270_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/KD_270_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.KD_270_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_KD_270_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_KD_270_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_KD_270_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_KD_270_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_045_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_045_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_045_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_045_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_045_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_045_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_045_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_045_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_046_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_046_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_046_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_046_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_046_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_046_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_046_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_046_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_046_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_046_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_046_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_046_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_048_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_048_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_048_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_048_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_048_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_048_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_048_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_048_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_048_S3.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_048_S3(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_048_S3.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_048_S3(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_048_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_048_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_048_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_048_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_065_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_065_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_065_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_065_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_065_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_065_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_065_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_065_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_065_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_065_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_065_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_065_C2(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_066_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_066_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_066_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_066_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_066_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_066_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_066_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_066_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_066_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_066_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_066_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_066_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_067_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_067_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_067_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_067_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_072_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_072_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_072_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_072_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_072_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_072_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_072_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_072_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_072_S3.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_072_S3(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_072_S3.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_072_S3(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_072_S4.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_072_S4(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_072_S4.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_072_S4(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_072_S5.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_072_S5(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_072_S5.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_072_S5(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_072_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_072_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_072_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_072_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
        
    @RequestMapping(value = "/api/PR_PDA_PD_073_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_073_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_073_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_073_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_074_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_074_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_074_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_074_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_075_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_075_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_075_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_075_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_076_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_076_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_076_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_076_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_076_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_076_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_076_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_076_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_077_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_077_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_077_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_077_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_090_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_090_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_090_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_090_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
        
    @RequestMapping(value = "/api/PR_PDA_PD_090_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_090_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_090_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_090_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_094_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_094_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_094_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_094_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_094_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_094_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_094_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_094_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_094_C2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_094_C2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_094_C2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    bodyMap.put("RTN_BAR", "");
                    param1listMap.add(bodyMap);
                }
            }

            String BAR_NO = null;
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_094_C2(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                if(param1listMap.get(i).get("RTN_BAR") != null){
                    BAR_NO = param1listMap.get(i).get("RTN_BAR").toString();
                }
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_PD_094_C3");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    bodyMap.put("BAR_NO", BAR_NO);
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_094_C3(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }

    @RequestMapping(value = "/api/PD_095_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_095_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_095_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_095_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_095_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_095_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_095_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    bodyMap.put("RTN_SEQ", "");
                    param1listMap.add(bodyMap);
                }
            }

            String PLT_SEQ = null;
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_095_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                if(param1listMap.get(i).get("RTN_SEQ") != null){
                    PLT_SEQ = param1listMap.get(i).get("RTN_SEQ").toString();
                }
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_PD_095_C2");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    bodyMap.put("PLT_SEQ", PLT_SEQ);
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_095_C2(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_096_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_096_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_096_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_096_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_096_S2.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_096_S2(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_096_S2.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_096_S2(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_096_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_096_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_096_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    bodyMap.put("RTN_SEQ", "");
                    bodyMap.put("RTN_BAR", "");
                    param1listMap.add(bodyMap);
                }
            }

            String PLT_SEQ = null;
            String BAR_NO = null;
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_096_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                if(param1listMap.get(i).get("RTN_SEQ") != null){
                    PLT_SEQ = param1listMap.get(i).get("RTN_SEQ").toString();
                }
                if(param1listMap.get(i).get("RTN_BAR") != null){
                    BAR_NO = param1listMap.get(i).get("RTN_BAR").toString();
                }
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_PD_096_C2");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    bodyMap.put("PLT_SEQ", PLT_SEQ);
                    bodyMap.put("BAR_NO", BAR_NO);
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_096_C2(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_100_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_100_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_100_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_100_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_105_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_105_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_105_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_105_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_105_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_105_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_105_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_105_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_107_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_107_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_107_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_107_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_107_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_107_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_107_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_107_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
        
    @RequestMapping(value = "/api/PR_PDA_PD_110_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_110_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_110_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_110_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_120_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_120_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_120_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_120_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_120_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_120_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_120_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    bodyMap.put("RTN_SEQ", "");
                    param1listMap.add(bodyMap);
                }
            }

            String PLT_SEQ = null;
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_120_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                if(param1listMap.get(i).get("RTN_SEQ") != null){
                    PLT_SEQ = param1listMap.get(i).get("RTN_SEQ").toString();
                }
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }
            
            logger.info("PR_PDA_PD_120_C2");
            String param2 = isNull(param.get("param2"));
            List<Map<String, Object>> param2listMap = new ArrayList<Map<String, Object>>();
            
            if (param2.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param2.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    bodyMap.put("PLT_SEQ", PLT_SEQ);
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2){
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param2listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param2listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_120_C2(param2listMap.get(i));

                String result = "";

                if(param2listMap.get(i).get("RTN_MSG") != null){
                    result = param2listMap.get(i).get("RTN_MSG").toString();
                }
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_130_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_130_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_130_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_130_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_130_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_130_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_130_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_130_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PR_PDA_PD_140_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_PD_140_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_PD_140_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_PD_140_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/PD_150_S1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PD_150_S1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PD_150_S1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            List<Map<String,Object>> result = basicSampleService.PD_150_S1(param);          
            reqData.setBodyMap("ListCount", String.valueOf(result.size()));
            reqData.setBodyMap("ListData", result);
        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }

    @RequestMapping(value = "/api/PR_PDA_ST_990_C1.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object PR_PDA_ST_990_C1(Model model, HttpServletRequest req, HttpSession session) {
        logger.info("Controller : /api/PR_PDA_ST_990_C1.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            if((String)session.getAttribute("USER_SESSION")== null)
            {
                logger.info("Null Session!!");
                String e2 = "Session_err1";
                throw new Exception(e2);
            }
            else if(!session.getAttribute("USER_SESSION").equals(getUsrSessionId(session)))
            {
                logger.info("Diffrent Session!!");
                String e2 = "Session_err2";
                throw new Exception(e2);
            }
            Map<String,Object> param = reqData.getRequestBodyMap();
            
            String param1 = isNull(param.get("param1"));
            List<Map<String, Object>> param1listMap = new ArrayList<Map<String, Object>>();
            if (param1.indexOf('[') >= 0) {
                String[] strArr = null;
                String kvStr = null;
                String[] kvArr = null;
                String[] kvArr2 = null;
                strArr = param1.split("\\{");
                for (int i = 1; i < strArr.length; i++) {
                    Map<String,Object> bodyMap = new HashMap<String, Object>();
                    kvStr = strArr[i].replaceAll("},", "").replaceAll("}]", "");
                    kvArr = kvStr.split(",");
                    for (int j = 0; j < kvArr.length; j++) {
                        kvArr2 = kvArr[j].split("=");
                        if (kvArr2.length == 2) {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), kvArr[j].split("=")[1].trim());
                        }
                        else {
                            bodyMap.put(kvArr[j].split("=")[0].trim(), "");
                        }
                    }
                    param1listMap.add(bodyMap);
                }
            }
            
            for (int i = 0; i < param1listMap.size(); i++) {
                basicSampleService.PR_PDA_ST_990_C1(param1listMap.get(i));

                String result = "";

                if(param1listMap.get(i).get("RTN_MSG") != null){
                    result = param1listMap.get(i).get("RTN_MSG").toString();
                }
                
                else
                {
                    result = "E : ERROR, DB NOT RETURNED";
                }
                
                if("S".equals(result))
                {
                    reqData.setBodyMap("resultMsg", "");
                    reqData.setBodyMap("result", result);
                }
                else
                {
                    reqData.setBodyMap("resultMsg", result);
                    reqData.setBodyMap("result", result);
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            reqData.setError(e);
        }
        return reqData.getReturnJson();
    }    
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value="/api/ImageFileUpload.do", method = {RequestMethod.POST})
    public @ResponseBody Object ImageFileUpload(HttpServletRequest req) {
        logger.info("Controller : /api/ImageFileUpload.do");
        AbleRequestData reqData = new AbleRequestData(req, UPLOAD_ROOT_PATH);
        if (reqData.getBodyMap().get("result_code") != "500"){
            try{
                if (reqData.getIsMultipart()){
                    List<Map<String,Object>> list = (List<Map<String,Object>>)reqData.getBodyMap().get("attachFiles");
                    for(Map<String,Object> m : list){
                        logger.info("UPLOAD FILE - httpurl : " + m.get("httpurl"));
                        logger.info("UPLOAD FILE - uploadFileName: " + m.get("uploadFileName"));
                        logger.info("UPLOAD FILE - absPath : " + m.get("absPath"));
                        
                        // rotate
                        Thumbnails.of(UPLOAD_ROOT_PATH + m.get("uploadFileName").toString()).scale(1).toFiles(Rename.NO_CHANGE);
                    }
                } else {
                    reqData.setError(new Exception("첨부파일이 없습니다."));
                }
            } catch (Exception ex){
                reqData.setError(ex);
            }
        }
        return reqData.getReturnJson();
    }
    
    @RequestMapping(value = "/api/image/{imageName}")
    @ResponseBody
    public byte[] getImage(@PathVariable(value = "imageName") String imageName, HttpServletResponse resp) {
        logger.info("Controller : /api/image/{imageName}");
        try{
            File serverFile = new File(UPLOAD_ROOT_PATH + imageName + ".jpg");
            return Files.readAllBytes(serverFile.toPath()); 
        } catch (IOException ex){
            try{
                InputStream in = context.getResourceAsStream("/resources/images/noimagefound.gif");
                return IOUtils.toByteArray(in);
            } catch (Exception ex2){
                return null;
            }
        }
    }
        
    @RequestMapping(value="/api/ImageFileDelete.do", method = {RequestMethod.POST})
    public @ResponseBody Object ImageFileDelete(HttpServletRequest req){
        logger.info("Controller : /api/ImageFileDelete.do");
        AbleRequestData reqData = new AbleRequestData(req);
        try{
            Map<String,Object> param = reqData.getRequestBodyMap();
            String fileList = param.get("FILE_LIST").toString();
            String[] arrFileList = fileList.split(";");
            for(int i = 0; i < arrFileList.length; i++){
                File f = new File(UPLOAD_ROOT_PATH + arrFileList[i]);
                if (f.exists()){
                    f.delete();
                }
            }
        } catch (Exception ex){
            reqData.setError(ex);
        }
        
        return reqData.getReturnJson();
    }
    
    @SuppressWarnings("unchecked")
    public static boolean isEmpty(Object s) {
        if (s == null) {
            return true;
        }
        if ((s instanceof String) && (((String) s).trim().length() == 0)) {
            return true;
        }
        if (s instanceof Map) {
            return ((Map<String, Object>) s).isEmpty();
        }
        if (s instanceof List) {
            return ((List<Map<String, Object>>) s).isEmpty();
        }
        if (s instanceof Object[]) {
            return (((Object[]) s).length == 0);
        }

        return false;
    }
    
    public static String isNull(Object s) {
        if (s == null || s.toString().equals("null")) {
            return "";
        }

        return s.toString();
    }
}
