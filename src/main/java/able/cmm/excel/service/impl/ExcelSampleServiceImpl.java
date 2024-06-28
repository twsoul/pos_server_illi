package able.cmm.excel.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import able.cmm.excel.service.ExcelSampleService;
import able.cmm.excel.service.dao.ExcelSampleMDAO;
import able.cmm.excel.vo.ExcelTestVO;
import able.com.vo.HMap;
/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ExcelSampleServiceImpl.java
 * @Description : 엑셀 데이터 조회, 생성
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
@Service("excelSampleService")
public class ExcelSampleServiceImpl implements ExcelSampleService{

	@Resource(name="excelSampleMDAO")
	ExcelSampleMDAO excelSampleMDAO;
	
	@Override
	public List<HMap> selectExcelList(HMap map) throws Exception {
		return excelSampleMDAO.selectExcelList(map);
	}

	@Override
	public void insertExcelList(List list) throws Exception {
		HMap hmap = new HMap();
		for(int i=0;i<list.size();i++){
			hmap = (HMap) list.get(i);
			if(null!=hmap.get("id"))
				excelSampleMDAO.insertExcel(hmap);
		}		
	}

	@Override
	public List<ExcelTestVO> selectExcelList() throws Exception {
		return excelSampleMDAO.selectExcelVOList();
	}

}
