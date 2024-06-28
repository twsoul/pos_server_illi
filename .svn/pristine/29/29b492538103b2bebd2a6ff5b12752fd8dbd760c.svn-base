package able.board.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import able.board.service.BoardSampleService;
import able.board.vo.BoardSampleFileVO;
import able.board.vo.BoardSampleVO;
import able.com.service.file.FileDownloadService;
import able.com.service.file.FileUploadService;
import able.com.service.file.FileVO;
import able.com.util.sim.FileTool;
import able.com.web.HController;
import able.com.web.view.PagingInfo;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : BoardSampleController.java
 * @Description : 게시판 관련 업무 컨트롤러 클래스
 * @author ADM기술팀
 * @since 2016. 7. 1.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.       ADM기술팀                                      최초 생성
 * </pre>
 */

@Controller
public class BoardSampleController extends HController{
	
	@Resource(name = "boardSampleService")
	private BoardSampleService boardSampleService;
	
	/**
	 * 파일 업로드 서비스
	 */
	@Resource(name="fileUploadService")
	FileUploadService fileUploadService;
	
	/**
	 * 게시글의 목록을 조회한다.
	 * @param boardSampleVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/cmm/board/selectItemList.do")
	public String selectItemList(@ModelAttribute("boardSampleVO") BoardSampleVO boardSampleVO, Model model) throws Exception {
			
			if(null==boardSampleVO.getCurrPage() || boardSampleVO.getCurrPage().equals("")){
	            boardSampleVO.setCurrPage("1");
	        }			
			int currPage = Integer.parseInt(boardSampleVO.getCurrPage());
			
			int pageSize = propertiesService.getInt("pageSize");
			int pageUnit = propertiesService.getInt("pageUnit");
			
			//hsqldb
			//boardSampleVO.setLimit("10");
			//boardSampleVO.setOffset(String.valueOf(10*(currPage-1)));
			
			//oracle
			boardSampleVO.setLimit(String.valueOf(pageUnit*currPage));
			boardSampleVO.setOffset(String.valueOf(pageUnit*(currPage-1)));

			List<BoardSampleVO> sampleList = boardSampleService.selectSampleList(boardSampleVO);
			
			// 페이징 정보
	        PagingInfo pagingInfo = new PagingInfo();
	        int listCount = boardSampleService.selectSampleListCount(boardSampleVO);
	        pagingInfo.setTotalRecordCount(listCount);	        
	        pagingInfo.setRecordCountPerPage(pageUnit);	        
	        pagingInfo.setPageSize(pageSize);
	        pagingInfo.setCurrentPageNo(Integer.parseInt(boardSampleVO.getCurrPage()));
			
			model.addAttribute("resultList", sampleList);
			model.addAttribute("page", pagingInfo);
		
		return "able/board/boardSampleList";
	}
	
	/**
	 * 게시글 등록 화면
	 * @param boardSampleVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/cmm/board/insertItemForm.do", method = RequestMethod.GET)
	public String insertItemForm(@ModelAttribute BoardSampleVO boardSampleVO, Model model) throws Exception {
		return "able/board/boardSampleRegisterForm";
	}
	
	/**
	 * 게시글 등록
	 * 유효성 검사 후 조건에 맞지 않으면 form으로 이동하고, 조건에 맞으면 INSERT를 하고 게시글 목록 화면으로 이동한다.
	 * 첨부 파일이 있는지 확인 하고 첨부파일이 있는 경우 첨부파일 업로드를 한다.
	 * @param boardSampleVO
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/cmm/board/insertItem.do", method = RequestMethod.POST)
	public String insertItem(@ModelAttribute @Valid BoardSampleVO boardSampleVO, BindingResult result, Model model,
			HttpServletRequest request) throws Exception {
		
		//form 유효성 검사
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError e : list) {
				logger.debug("ObjectError : " + e);
			}
			return "able/board/boardSampleRegisterForm";
		}
		
		//첨부파일 여부 확인, 첨부파일이 존재하면 업로드
		checkFileUpload(request, boardSampleVO);
		
		//게시글 DB 저장
		boardSampleService.insertSample(boardSampleVO);
		
		return "forward:/cmm/board/selectItemList.do";
	}
	
	/**
	 * 게시글 상세 조회 화면
	 * @param boardSampleVO
	 * @param id
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/cmm/board/selectItemById.do")
	public String selectItemById(@ModelAttribute BoardSampleVO boardSampleVO, @RequestParam("selectedId") String id, Model model) throws Exception {
		//첨부파일 리스트
		model.addAttribute("fileList", selectFileList(id));
		//글 상세내용
		model.addAttribute("boardSampleVO", selectItem(id));
		return "able/board/boardSampleDetailView";
	}
	
	/**
	 * 게시글 수정 화면
	 * @param boardSampleVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/cmm/board/updateItemForm.do")
	public String updateItemForm(@ModelAttribute BoardSampleVO boardSampleVO, Model model) throws Exception {
		String id = boardSampleVO.getArtId();
		model.addAttribute("boardSampleVO", selectItem(id));
		model.addAttribute("fileList", selectFileList(id));
		return "able/board/boardSampleModifyForm";
	}
	
	/**
	 * 게시글 수정
	 * 유효성 검사 후 조건에 맞지 않으면 form으로 이동하고, 조건에 맞으면 UPDATE를 하고 게시글 수정 화면으로 이동한다.
	 * 첨부 파일이 있는지 확인 하고 첨부파일이 있는 경우 첨부파일 업로드를 한다. 
	 * @param request
	 * @param boardSampleVO
	 * @param result
	 * @param model
	 * @return
	 * @throws Exception
	 */
	/*@RequestMapping("/cmm/board/updateItem.do")*/
	@RequestMapping(value = "/cmm/board/updateItem.do", method = RequestMethod.POST)
	public String updateItem(HttpServletRequest request, @ModelAttribute @Valid BoardSampleVO boardSampleVO, BindingResult result,Model model) throws Exception {
		
		//form 유효성 검사
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError e : list) {
				logger.debug("ObjectError : " + e);
			}
			return "able/board/boardSampleModifyForm";
		}
		
		//파일 업로드
		checkFileUpload(request, boardSampleVO);
		
		//게시글 업데이트
		boardSampleService.updateSample(boardSampleVO);
		
		String id = boardSampleVO.getArtId();
		model.addAttribute("result", selectItem(id));
		model.addAttribute("fileList", selectFileList(id));
		return "able/board/boardSampleDetailView";
	}
	
	/**
	 * 첨부파일 다운로드
	 * @param fileId
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(path="/cmm/board/fileDownload.do")
	public void fileDownload(String fileId, HttpServletRequest request, HttpServletResponse response) throws Exception{
		BoardSampleFileVO fvo = boardSampleService.selectFileVOByKey(fileId) ;
		//다운받을 이름
		String downloadFileName = fvo.getOriginalFileName();
		//저장된 경로
		String filePath = fvo.getFolderPath() + File.separator + fvo.getStoredFileName();
		//다운로드
		FileDownloadService.fileDown(filePath, downloadFileName, request, response);
	}
	
	/**
	 * 첨부파일 삭제 (수정화면)
	 * @param fileId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path="/cmm/board/fileDelete.do", method = RequestMethod.GET)
/*	@ResponseBody*/
	public String fileDelete(@RequestParam("selectedFileId") String fileId, Model model) throws Exception{
		BoardSampleFileVO fvo = boardSampleService.selectFileVOByKey(fileId);
		String filePath = fvo.getFolderPath() + File.separator + fvo.getStoredFileName();
		//파일 삭제
		FileTool.deleteFile(filePath);
		//DB 삭제
		boardSampleService.deleteFileVOByKey(fileId);
		
		model.addAttribute("fileId", fileId);
		
		return "jsonView";
	}
	
	/**
	 * 게시글 삭제
	 * @param boardSampleVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path="/cmm/board/deleteItem.do")
	public String deleteItem(@ModelAttribute BoardSampleVO boardSampleVO) throws Exception {
		boardSampleService.deleteSample(boardSampleVO.getArtId());
		return "forward:/cmm/board/selectItemList.do";
	}
	
	/**
	 * (공통함수) 게시글 상세 내용 가져오기
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BoardSampleVO selectItem(String id) throws Exception {
		return boardSampleService.selectSample(id); 
	}
	
	/**
	 * (공통함수) 첨부파일 리스트 가져오기
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<BoardSampleFileVO> selectFileList(String id) throws Exception {
		return boardSampleService.selectFileVOList(id);
	}
	
	/**
	 * 첨부파일 여부 체크
	 * @param request
	 * @param boardSampleVO
	 * @throws Exception
	 */
	public void checkFileUpload(HttpServletRequest request, BoardSampleVO boardSampleVO) throws Exception{
		//파일 업로드
		List<FileVO> uploadFileList = fileUploadService.upload(request);
		
		if (uploadFileList != null){
			
			List<BoardSampleFileVO> bsfvList = new ArrayList<BoardSampleFileVO>();
			int fileList = uploadFileList.size();
			for(int i=0; i<fileList; i++){
				BoardSampleFileVO fv = new BoardSampleFileVO();
				fv.setFileId(uploadFileList.get(i).getFileId());
				fv.setFileSize(uploadFileList.get(i).getFileSize());
				fv.setFolderPath(uploadFileList.get(i).getFolderPath());
				fv.setRegDate(uploadFileList.get(i).getRegDate());
				fv.setOriginalFileName(uploadFileList.get(i).getOriginalFileName());
				fv.setStoredFileName(uploadFileList.get(i).getStoredFileName());
				System.out.println("1."+uploadFileList.get(i).getFileId());
				System.out.println("2."+uploadFileList.get(i).getFileSize());
				System.out.println("3."+uploadFileList.get(i).getFolderPath());
				System.out.println("4."+uploadFileList.get(i).getRegDate());
				System.out.println("5."+uploadFileList.get(i).getOriginalFileName());
				System.out.println("6."+uploadFileList.get(i).getStoredFileName());
				
				bsfvList.add(fv);
				
			}
			boardSampleVO.setFileList(bsfvList);
			logger.debug("checkFileUpload1");
		}
		logger.debug("checkFileUpload2");
	}
	
}
