package able.cmm.excel.web;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import able.cmm.excel.service.ExcelSampleService;
import able.com.service.file.FileDownloadService;
import able.com.service.file.FileUploadService;
import able.com.service.file.FileVO;
import able.com.util.excel.Excel;
import able.com.vo.HMap;
import able.com.web.HController;
/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ExcelSampleController.java
 * @Description : 엑셀 데이터 처리 컨트롤러
 * @author "ADM Technology Team"
 * @since 2016. 7. 1.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.     "ADM Technology Team"       최초 생성
 * </pre>
 */
@Controller
public class ExcelSampleController extends HController{

	@Resource(name="excelSampleService")
	ExcelSampleService excelSampleService;
	
	@Resource(name="fileUploadService")
	FileUploadService fileUploadService;
	
	@RequestMapping(value = "/cmm/excel/selectExcelList.do")
	public String selectExcelList(HMap hmap, ModelMap model) throws Exception {

		/*int currPage = 1;
		if(null!=hmap && null!=hmap.get("currPage"))
			currPage = (int)hmap.get("currPage");
		
		hmap.put("limit", 10);
		hmap.put("offset", 10*currPage);*/
		
		List<?> resultList = excelSampleService.selectExcelList(null);
		model.addAttribute("resultList", resultList);
		
        /*// 페이징 정보
        PagingInfo bbsMasterPage = new PagingInfo();
        bbsMasterPage.setTotalRecordCount(100);
        bbsMasterPage.setRecordCountPerPage(10);
        bbsMasterPage.setPageSize(20);
        bbsMasterPage.setCurrentPageNo(currPage);*/
		
		return "able/excel/excelSample";
	}
	
	@RequestMapping(value = "/cmm/excel/excelDownload.do")
	public String excelDownload(ModelMap model) throws Exception {
		
		try{
			
			List<HMap> resultList = excelSampleService.selectExcelList(null);
			
			String fileStore = propertiesService.getString("fileStore");
			
			String fileLocation = "/wasapp/test/file"; //엑셀이 저장될 위치(서버위치)
			String filename = "excelDownloadSample01"; //저장하는 엑셀 파일명
			
			//excel.write(fileLocation, filename+excel.getExtension());
			Excel excel	= Excel.make(Excel.EXCEL_TYPE_XSSF);
			
			excel.addSheet();			
			
			//TODO
			
			excel.write(fileLocation, filename);
			
			model.addAttribute("message", getMessage("info.success.msg"));
			
		}catch(Exception e){
			//Exception 처리
			model.addAttribute("message", getMessage("fail.common.msg"));
		}
		
		return "jsonView";
	}
	
	@RequestMapping(value = "/cmm/excel/excelDownloadMapP.do")
	public void excelDownloadMapP(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
			
		List<HMap> resultList = excelSampleService.selectExcelList(null);
		
		String fileLocation = "/wasapp/test/file"; //엑셀이 저장될 위치(서버위치)
		String fileName = "excelDownloadSample01"; //저장하는 엑셀 파일명
		
		//excel.write(fileLocation, filename+excel.getExtension());
		Excel excel	= Excel.make(Excel.EXCEL_TYPE_XSSF);
		
		File file = excel.writeForHMap(excel, fileLocation, fileName, resultList);
		excel.getSheet(0).defaultHF("샘플타이틀입니다.");
		excel.write(file);
		//excel.write(fileLocation, fileName);
		
		FileDownloadService.fileDown(fileLocation+"\\"+fileName+".xlsx", fileName+".xlsx", request, response);

	}
	
	@RequestMapping(value = "/cmm/excel/excelDownloadMap.do")
	public String excelDownloadMap(ModelMap model) throws Exception {
		
		try{
			
			List<HMap> resultList = excelSampleService.selectExcelList(null);
			
			String fileLocation = "/wasapp/test/file"; //엑셀이 저장될 위치(서버위치)
			String filename = "excelDownloadSample01"; //저장하는 엑셀 파일명
			
			//excel.write(fileLocation, filename+excel.getExtension());
			Excel excel	= Excel.make(Excel.EXCEL_TYPE_XSSF);
			excel.writeForHMap(excel, fileLocation, filename, resultList);
			
			model.addAttribute("message", getMessage("info.success.msg"));
			
		}catch(Exception e){
			//Exception 처리
			model.addAttribute("message", getMessage("fail.common.msg"));
		}
		
		return "jsonView";
	}
	
	@RequestMapping(value = "/cmm/excel/excelDownloadVO.do")
	public String excelDownloadVO(ModelMap model) throws Exception{
		try{
			
			/*SampleVO vo = new SampleVO();
			vo.setId("id1");
			vo.setName("name1");
			vo.setDescription("description1");
			vo.setUseYn("useYn1");
			vo.setRegUser("regUser1");
			
			SampleVO vo2 = new SampleVO();
			vo2.setId("id2");
			vo2.setName("name2");
			vo2.setDescription("description2");
			vo2.setUseYn("useYn2");
			vo2.setRegUser("regUser2");
			
			List resultList = new ArrayList();
			resultList.add(vo);
			resultList.add(vo2);*/
			
			List resultList = excelSampleService.selectExcelList();
			
			String fileLocation = "/wasapp/test/file"; //엑셀이 저장될 위치(서버위치)
			String filename = "excelDownloadSample01"; //저장하는 엑셀 파일명
			
			//excel.write(fileLocation, filename+excel.getExtension());
			Excel excel	= Excel.make(Excel.EXCEL_TYPE_XSSF);
			File file = excel.writeForVO(excel, fileLocation, filename, resultList);
			excel.write(file);
		
			model.addAttribute("message", getMessage("info.success.msg"));
		
			
		}catch(Exception e){
			//Exception 처리
			model.addAttribute("message", getMessage("fail.common.msg"));
		}
		
		return "jsonView";
	}
	
	@RequestMapping(value = "/cmm/excel/excelUpload.do")
	public String excelUpload(ModelMap model, HttpServletRequest request) throws Exception{

		//파일 업로드
		List<FileVO> uploadFileList = fileUploadService.upload(request);
			
		//파일은 하나만 올리도록 한다.
		FileVO fvo = new FileVO();
		if(uploadFileList.size()>0){
			fvo = uploadFileList.get(0);
		}else{
			model.addAttribute("message", getMessage("fail.common.msg"));
			return "redirect:/cmm/excel/selectExcelList.do";
		}
					
		//HSSF, XSSF, CSV, TSV
		//Excel excel1	= Excel.make(new File("C:/eGovFrameDev-3.5.1-32bit/temp/real/excelDownloadSample01.xlsx"));
		
		Excel excel1	= Excel.makeSec(new File(fvo.getFolderPath()+"/"+fvo.getStoredFileName()), fvo.getOriginalFileName());
		List<List<HMap>> dataList = excel1.toHMapList();
		
		for(int i=0;i<dataList.size();i++){
			List<HMap> sheet = dataList.get(i);
			excelSampleService.insertExcelList(sheet);
		}		
		
		return "redirect:/cmm/excel/selectExcelList.do";
	}
	
}
