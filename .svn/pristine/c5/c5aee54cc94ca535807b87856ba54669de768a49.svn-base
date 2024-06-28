package able.cmm.file.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import able.cmm.file.service.FileSampleService;
import able.cmm.file.service.dao.FileSampleMDAO;
import able.com.service.file.FileVO;
import able.com.vo.HMap;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : FileSampleServiceImpl.java
 * @Description : 파일 처리 서비스
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
@Service("fileSampleService")
public class FileSampleServiceImpl implements FileSampleService {

	@Resource(name="fileSampleMDAO")
	FileSampleMDAO fileSampleMDAO;
	
	@Override
	public List<FileVO> selectFileVOList(HMap hMap) {
		return fileSampleMDAO.selectFileVOList(hMap);
	}

	@Override
	public FileVO selectFileVOByKey(HMap hMap) {
		return fileSampleMDAO.selectFileVOByKey(hMap);
	}
	
	@Override
	public int deleteFileVOByKey(HMap hMap) {
		return fileSampleMDAO.deleteFileVOByKey(hMap);
	}

	@Override
	public void insertFileVOList(List<FileVO> fvoList) {
		for(int i=0;i<fvoList.size();i++){
			FileVO fvo = fvoList.get(i);
			insertFileVO(fvo);
		}
		//일부 db 에서 에러남
		//fileSampleMDAO.insertFileVOList(fvoList);
	}

	@Override
	public void insertFileVO(FileVO fvo) {
		fileSampleMDAO.insertFileVO(fvo);
	}
	

}
