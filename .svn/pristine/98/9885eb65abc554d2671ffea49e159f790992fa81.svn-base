package able.cmm.excel.service.dao;

import java.util.List;

import able.cmm.excel.vo.ExcelTestVO;
import able.com.mybatis.Mapper;
import able.com.vo.HMap;
/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ExcelSampleMDAO.java
 * @Description : 엑셀 데이터 처리 DAO
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
@Mapper("excelSampleMDAO")
public interface ExcelSampleMDAO {
	public List<HMap> selectExcelList(HMap hmap) throws Exception;
	
	public HMap selectExcelByKey(HMap hmap) throws Exception;
	
	public void insertExcel(HMap hmap) throws Exception;
	
	public int updateExcel(HMap hmap) throws Exception;
	
	public int deleteExcelByKey(HMap hmap) throws Exception;
	
	public List<ExcelTestVO> selectExcelVOList() throws Exception;
}
