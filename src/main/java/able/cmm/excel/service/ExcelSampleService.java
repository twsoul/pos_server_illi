package able.cmm.excel.service;

import java.util.List;

import able.cmm.excel.vo.ExcelTestVO;
import able.com.vo.HMap;
/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ExcelSampleService.java
 * @Description : 엑셀 데이터 조회, 생성 인터페이스
 * @author "ADM Technology Team"
 * @since 2016. 7. 1.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.     "ADM Technology Team"      최초 생성
 * </pre>
 */
public interface ExcelSampleService {

	List<HMap> selectExcelList(HMap map) throws Exception;
	
	void insertExcelList(List list) throws Exception;
	
	List<ExcelTestVO> selectExcelList() throws Exception;
}
