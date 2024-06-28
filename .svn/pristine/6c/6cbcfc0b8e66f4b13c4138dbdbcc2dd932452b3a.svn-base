package able.cmm.grid.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import able.com.vo.HMap;
import able.com.web.HController;


/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : SlickGridController.java
 * @Description : 그리드 컨트롤러
 * @author "ADM Technology Team"
 * @since 2016. 7. 1.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.     "ADM Technology Team"     	최초 생성
 * </pre>
 */
@Controller
public class SlickGridController extends HController {

	@RequestMapping(value = "/test/grid/slickGridSimple.do")
	public String slickGridSimple(ModelMap model) throws Exception {

		return "able/grid/slickGridSimple";
		//return "egovframework/example/sample/egovSampleList";
	}
	
	@RequestMapping(value = "/test/grid/selectSlickGrid.do")
	public String selectSlickGrid(ModelMap model) throws Exception {

		List result = new ArrayList();		
		
		/*
		 {id: "title", name: "Title", field: "title"},
    {id: "duration", name: "Duration", field: "duration"},
    {id: "%", name: "% Complete", field: "percentComplete"},
    {id: "start", name: "Start", field: "start"},
    {id: "finish", name: "Finish", field: "finish"},
    {id: "effort-driven", name: "Effort Driven", field: "effortDriven"}
		 */
		for(int i=0;i<10000;i++){
			HMap map = new HMap();
			map.put("id", i);
			map.put("title", "title"+i);
			map.put("duration", "duration"+i);
			map.put("complete", "complete"+i);
			map.put("start", "start"+i);
			map.put("finish", "finish"+i);
			map.put("effort", "effort"+i);		
			result.add(map);
		}		
		
		model.addAttribute("result", result);		
		
		return "jsonView";
		//return "egovframework/example/sample/egovSampleList";
	}
	
}