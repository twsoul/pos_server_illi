package able.board.service;

import java.util.List;

import able.board.vo.BoardSampleFileVO;
import able.board.vo.BoardSampleVO;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName : BoardSampleService.java
 * @Description : 게시판의 기능(조회,등록,수정,삭제 등)을 위한 서비스 인터페이스 클래스
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

public interface BoardSampleService {

	/**
	 * 게시글 등록
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	void insertSample(BoardSampleVO vo) throws Exception;
	
	/**
	 * 게시글 수정
	 * @param vo
	 * @throws Exception
	 */
	void updateSample(BoardSampleVO vo) throws Exception;
	
	/**
	 * 게시글 삭제
	 * @param seq
	 * @throws Exception
	 */
	void deleteSample(String id) throws Exception;
	
	/**
	 * 게시글 상세 조회
	 * @param seq
	 * @return
	 * @throws Exception
	 */
	BoardSampleVO selectSample(String id) throws Exception;
	
	/**
	 * 목록 조회
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	List<BoardSampleVO> selectSampleList(BoardSampleVO vo) throws Exception;
	
	/**
	 * 첨부파일 리스트
	 * @param artId
	 * @return
	 * @throws Exception
	 */
	List<BoardSampleFileVO> selectFileVOList(String artId) throws Exception;
	
	/**
	 * 첨부파일 다운로드 (선택된 첨부파일에 대한 정보 가져오기)
	 * @param id
	 * @return
	 * @throws Exception
	 */
	BoardSampleFileVO selectFileVOByKey(String id) throws Exception;
	
	/**
	 * 첨부파일 삭제 (선택된 첨부파일 삭제)
	 * @param fileId
	 * @return
	 */
	int deleteFileVOByKey(String fileId);
	
	/**
	 * 첨부파일 등록
	 * @param vo
	 * @throws Exception
	 */
	void insertFile(BoardSampleFileVO vo) throws Exception;
	
	/**
	 * 게시글 ID 생성 (ART_ID)
	 * @return
	 * @throws Exception
	 */
	String selectMaxArticleId() throws Exception;
	
	/**
	 * Pagination을 위한 게시글 총 갯수
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	int selectSampleListCount(BoardSampleVO vo) throws Exception; 
	
}