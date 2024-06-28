package able.cmm.file.service.dao;

import java.util.List;

import able.com.mybatis.Mapper;
import able.com.service.file.FileVO;
import able.com.vo.HMap;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : FileSampleMDAO.java
 * @Description : 파일 처리 MDAO
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
@Mapper("fileSampleMDAO")
public interface FileSampleMDAO {
	
	public List<FileVO> selectFileVOList(HMap hMap);
	
	public FileVO selectFileVOByKey(HMap hMap);
	
	public int deleteFileVOByKey(HMap hMap);
	
	public void insertFileVO(FileVO fvo);

	public void insertFileVOList(List<FileVO> fvoList);
	
}
