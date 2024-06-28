package able.cmm.filehandling.web;

import java.io.File;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import able.com.util.sim.FileTool;
import able.com.vo.HMap;
import able.com.web.HController;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : FileHandlingController.java
 * @Description : 파일 핸들링 컨트롤러
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
public class FileHandlingController extends HController {
	
	//작업 디렉토리 설정
	private String workDir = System.getProperty("user.dir");
	//private String workDir = "/wasapp/test/file";
	
	ArrayList<String> filenames = new ArrayList<String>();
	
	/**
	 * 파일핸들링 유틸 예제 - 리스트
	 * @param hmap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path="/cmm/filehandling/list.do", method = RequestMethod.GET)
	public String list(HMap hmap, ModelMap model) throws Exception {
		
		model.put("workDir", workDir);
		model.put("filenames", filenames);
		return "able/filehandling/filehandlingSample";
	}
	
	/**
	 * 파일핸들링 유틸 예제 - 파일생성
	 * @param hmap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path="/cmm/filehandling/create.do", method = RequestMethod.GET)
	public String create(HMap hmap, ModelMap model) throws Exception {
		
		String basename = "testfile";
		String extension = ".tmp";
		String filename;
		
		int i=0;
		do {
			filename = basename + i + extension;
			i++;
		}while(FileTool.checkFileExstByName(workDir, filename));
		
		String filePath = workDir + File.separator + filename;
		//파일 생성
		FileTool.createNewFile(filePath);
		//파일 목록에 추가
		filenames.add(filename);
		
		return "redirect:list.do";
	}
	
	/**
	 * 파일핸들링 유틸 예제 - 파일 삭제
	 * @param hmap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path="/cmm/filehandling/delete.do", method = RequestMethod.GET)
	public String delete(HMap hmap, ModelMap model) throws Exception {
		
		String filename = hmap.getString("filename");
		String filePath = workDir + File.separator + filename;
		
		//파일 삭제
		FileTool.deleteFile(filePath);
		//파일 목록에서 제거
		filenames.remove(filename);
		
		return "redirect:list.do";
	}
	
	/**
	 * 파일핸들링 유틸 예제 - 파일 복사
	 * @param hmap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path="/cmm/filehandling/copy.do", method = RequestMethod.GET)
	public String copy(HMap hmap, ModelMap model) throws Exception {
		
		String filename = hmap.getString("filename");
		String sourcePath = workDir + File.separator + filename;
		
		String copyFilename;
		int i=1;
		do {
			copyFilename = "Copy of " + filename + " " + i;
			i++;
		} while(FileTool.checkFileExstByName(workDir, copyFilename));
		
		String targetPath = workDir + File.separator + copyFilename;
		//파일 복사
		FileTool.copyFile(sourcePath, targetPath);
		//파일 목록에 추가
		filenames.add(copyFilename);
		
		return "redirect:list.do";
	}
}
