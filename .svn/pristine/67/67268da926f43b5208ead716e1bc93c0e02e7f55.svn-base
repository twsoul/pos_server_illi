package able.cmm.mybatis.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import able.cmm.mybatis.service.MyBatisSampleService;
import able.com.exception.BizException;
import able.com.vo.HMap;
import able.com.web.HController;
import able.com.web.view.PagingInfo;

/**
 * @ClassName : MyBatisSampleController.java
 * @Description : Sample 컨트롤러 Class
 * @author ADM기술팀
 * @since 2016. 7. 1.
 * @version 1.0
 * @see
 * @Modification Information
 * 
 *               <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.       ADM기술팀                                      최초 생성
 * </pre>
 */

@Controller
public class MyBatisSampleController extends HController {

    /** MessageMngService */
    @Resource(name = "myBatisSampleService")
    private MyBatisSampleService myBatisSampleService;

    @RequestMapping(value = "/cmm/mybatis/myBatisSample.do")
    public String myBatisSample(ModelMap model) throws Exception {
        return "able/mybatis/myBatisSample";
    }

    @RequestMapping(value = "/cmm/mybatis/selectMyBatisList.do")
    public String selectMyBatisList(HMap hmap, ModelMap model) throws Exception {

        if (null == hmap.get("currPage") || hmap.get("currPage").equals("")) {
            hmap.put("currPage", "1");
        }
        int currPage = Integer.parseInt(hmap.get("currPage") + "");

        hmap.put("limit", 10 * currPage);
        hmap.put("offset", 10 * (currPage - 1));

        List<?> resultList = myBatisSampleService.selectMyBatisList(hmap);

        // 페이징 정보
        PagingInfo bbsMasterPage = new PagingInfo();

        // 총 게시물 건수는 별도로 조회해야 한다.
        bbsMasterPage.setTotalRecordCount(myBatisSampleService.selectMyBatisListTotCnt(hmap));
        // 페이지수 표시할 건수와 한페이지당 뿌릴 건수는 property에서 조회한다.
        int pageUnit = propertiesService.getInt("pageUnit");
        // bbsMasterPage.setRecordCountPerPage(5);
        bbsMasterPage.setRecordCountPerPage(pageUnit);
        int pageSize = propertiesService.getInt("pageSize");
        bbsMasterPage.setPageSize(pageSize);
        // bbsMasterPage.setPageSize(20);

        bbsMasterPage.setCurrentPageNo(Integer.parseInt("" + hmap.get("currPage")));

        model.addAttribute("resultList", resultList);
        model.addAttribute("page", bbsMasterPage);

        return "able/mybatis/myBatisSample";
    }

    @RequestMapping(value = "/cmm/mybatis/selectMyBatis.do")
    public String selectMyBatis(@RequestParam("id") String msgKey, Model model) throws Exception {

        HMap map = new HMap();
        map.put("id", msgKey);
        HMap resultMap = myBatisSampleService.selectMyBatis(map);

        model.addAttribute("result", resultMap);
        return "jsonView";
    }

    @RequestMapping(value = "/cmm/mybatis/updateMyBatis.do")
    public String updateMyBatis(HMap hmap, Model model) throws Exception {

        myBatisSampleService.updateMyBatis(hmap);
        model.addAttribute("message", getMessage("info.success.update"));
        return "jsonView";
    }

    @RequestMapping(value = "/cmm/mybatis/deleteMyBatis.do")
    public String deleteMyBatis(@RequestParam("id") String id, Model model) throws Exception {

        HMap param = new HMap();
        param.put("id", id);

        int result = myBatisSampleService.deleteMyBatisByKey(param);
        if (result > 0)
            model.addAttribute("message", getMessage("info.success.delete"));
        else
            model.addAttribute("message", getMessage("fail.common.msg"));

        return "jsonView";
    }

    @RequestMapping(value = "/cmm/mybatis/insertMyBatis.do")
    public String insertMyBatis(HMap hmap, Model model) throws Exception {

        hmap.put("regUser", "admin");

        // 동일한 메세지 코드가 있는지 점검 후 등록
        myBatisSampleService.insertMyBatis(hmap);
        model.addAttribute("message", getMessage("info.success.insert"));

        return "jsonView";
    }

}
