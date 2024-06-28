package able.cmm.file.web;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import able.cmm.file.service.FileSampleService;
import able.com.service.file.FileDownloadService;
import able.com.service.file.FileUploadService;
import able.com.service.file.FileVO;
import able.com.util.sim.FileTool;
import able.com.vo.HMap;
import able.com.web.HController;


/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : FileSampleController.java
 * @Description : 파일 처리 컨트롤러
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
public class FileSampleController extends HController {
	
	/**
	 * 파일 업로드 서비스
	 */
	@Resource(name="fileUploadService")
	FileUploadService fileUploadService;
	
	/**
	 * DB 사용을 위한 서비스
	 */
	@Resource(name="fileSampleService")
	FileSampleService fileSampleService;
	
	/**
	 * 업로드 컨트롤러 예제
	 * @param request - HttpServletRequest
	 * @return String - viewName
	 * @throws Exception
	 */
	@RequestMapping(path="/cmm/file/upload.do", method = RequestMethod.POST)
	public String upload(HttpServletRequest request) throws Exception {
		//파일 업로드
		List<FileVO> uploadFileList = fileUploadService.upload(request);
		//DB 저장
		if(uploadFileList.size() > 0){
			fileSampleService.insertFileVOList(uploadFileList);
		}
		//목록 보여주기 redirect
		return "redirect:selectFileList.do";
	}
	
	/**
	 * 다운로드 컨트롤러 예제
	 * @param hMap
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(path="/cmm/file/download.do")
	public void download(HMap hMap, HttpServletRequest request, HttpServletResponse response) throws IOException{
		FileVO fvo = fileSampleService.selectFileVOByKey(hMap);
		
		//다운받을 이름
		String downloadFileName = fvo.getOriginalFileName();
		//저장된 경로
		String filePath = fvo.getFolderPath() + File.separator + fvo.getStoredFileName();
		//다운로드
		FileDownloadService.fileDown(filePath, downloadFileName, request, response);
	}
	
	/**
	 * 파일 목록 컨트롤러 예제
	 * @param hMap
	 * @return
	 */
	@RequestMapping(path="/cmm/file/selectFileList.do")
	public String selectFileList(HMap hMap, ModelMap model){
		//DB 읽기
		List<FileVO> fvoList = fileSampleService.selectFileVOList(hMap);
		model.put("fileVOList", fvoList);
		return "able/file/fileSample";
	}
	
	/**
	 * 파일 삭제 컨트롤러 예제
	 * @param hMap
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(path="/cmm/file/delete.do")
	public String delete(HMap hMap, ModelMap model) throws IOException{
		FileVO fvo = fileSampleService.selectFileVOByKey(hMap);
		String filePath = fvo.getFolderPath() + File.separator + fvo.getStoredFileName();
		//파일 삭제
		FileTool.deleteFile(filePath);
		//DB 삭제
		fileSampleService.deleteFileVOByKey(hMap);
		//목록 보여주기 redirect
		return "redirect:selectFileList.do";
	}

}
