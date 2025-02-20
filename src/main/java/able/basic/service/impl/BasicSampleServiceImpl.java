package able.basic.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import able.basic.service.BasicSampleService;
import able.basic.service.dao.BasicSampleMDAO;
import able.com.service.HService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName : BasicSampleServiceImpl.java
 * @Description : BasicSampleService의 서비스 구현 클래스
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

@Service("basicSampleService")
public class BasicSampleServiceImpl extends HService implements BasicSampleService {
    protected Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    
    /** MDAO 등록 */
	@Resource(name = "basicSampleMDAO")
	private BasicSampleMDAO basicSampleMDAO;
	
	public Map<String, Object> versionCheck() throws Exception {
        try{
            List<Map<String,Object>> result = null;
            result = basicSampleMDAO.versionCheck();
            if (result.size() > 0){
                Map<String,Object> info = result.get(0);
                    return info;
            } else {
                return null;
            }
        } catch (Exception ex){
            log.error("로그인 오류" + ex.getMessage());
            return null;
        }
    }
	
	public Map<String, Object> getAuthCheckNInfo(String id, String pw) throws Exception {
        try{
            List<Map<String,Object>> result = null;
            result = basicSampleMDAO.getAuthCheckNInfo(id);
            if (result.size() > 0){
                Map<String,Object> info = result.get(0);
                if (passwordEncoder.matches(pw,info.get("USER_PW").toString())){
                    return info;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception ex){
            log.error("로그인 오류" + ex.getMessage());
            return null;
        }
    }
	
	@Override
    public void changePW(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.changePW(reqMap);
    }
	
	public Map<String, Object> changePW(String id, String pw) throws Exception {
        try{
            List<Map<String,Object>> result = null;
            result = basicSampleMDAO.getAuthCheckNInfo(id);
            if (result.size() > 0){
                Map<String,Object> info = result.get(0);
                if (passwordEncoder.matches(pw,info.get("USER_PW").toString())){
                    return info;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception ex){
            log.error("로그인 오류" + ex.getMessage());
            return null;
        }
    }
	
	@Override
	public List<Map<String, Object>> get_pda_LangList(Map<String,Object> reqMap) throws Exception {
		return basicSampleMDAO.get_pda_LangList(reqMap); 
	}

    @Override
    public List<Map<String, Object>> get_pda_MenuList(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.get_pda_MenuList(reqMap); 
    }
    
    @Override
    public List<Map<String, Object>> selectComboPlantCode(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectComboPlantCode(reqMap); 
    }
    
    @Override
    public List<Map<String, Object>> selectComboLocCode(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectComboLocCode(reqMap); 
    }
    
    @Override
    public List<Map<String, Object>> selectComboLocCodeLP(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectComboLocCodeLP(reqMap); 
    }
    
    @Override
    public List<Map<String, Object>> selectComboLocCodeKD(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectComboLocCodeKD(reqMap); 
    }

    @Override
    public List<Map<String, Object>> selectComboLocCodeEtc(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectComboLocCodeEtc(reqMap); 
    }    
    
    @Override
    public List<Map<String, Object>> selectVwCaseNo(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectVwCaseNo(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectVwCaseOnly(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectVwCaseOnly(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectVwBoxNo(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectVwBoxNo(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectBoxNoIn(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectBoxNoIn(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectBoxNoInTemp(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectBoxNoInTemp(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectBoxNo(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectBoxNo(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectBoxNoOut(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectBoxNoOut(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectBoxNoOutTemp(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectBoxNoOutTemp(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> AreaCodeList(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.AreaCodeList(reqMap);
    }

    @Override
    public List<Map<String, Object>> LineCodeList(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.LineCodeList(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> AreaCodeList2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.AreaCodeList2(reqMap);
    }

    @Override
    public List<Map<String, Object>> LineCodeList2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.LineCodeList2(reqMap);
    }

    @Override
    public List<Map<String, Object>> selectWHCode(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectWHCode(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectLocCode(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectLocCode(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectRackCode(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectRackCode(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectVwRackCode(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectVwRackCode(reqMap);
    }
        
    @Override
    public List<Map<String, Object>> selectComboMoveCauseCode(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectComboMoveCauseCode(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectComboProcCode(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectComboProcCode(reqMap);
    }

    @Override
    public List<Map<String, Object>> selectComboProcCodeField(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectComboProcCodeField(reqMap);
    }    
    
    @Override
    public List<Map<String, Object>> selectDeliNoO(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectDeliNoO(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectKDBoxNo(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectKDBoxNo(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectKDBoxNoTemp(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectKDBoxNoTemp(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectPlalletNoSCM(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectPlalletNoSCM(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectOtTmNo(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectOtTmNo(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectTmNo(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectTmNo(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectInvDump(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectInvDump(reqMap);
    }

    @Override
    public List<Map<String, Object>> selectInvDumpExists(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectInvDumpExists(reqMap);
    }    
    
    @Override
    public List<Map<String, Object>> selectRealInvExists(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectRealInvExists(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectTmPlant(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectTmPlant(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectOtTmLen(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectOtTmLen(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectOtLogi(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectOtLogi(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectOtCkdLogi(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectOtCkdLogi(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectOtProcCd(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectOtProcCd(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectVwPallet(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectVwPallet(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectVwPalletTemp(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectVwPalletTemp(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> selectVwTm(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.selectVwTm(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> IN_010_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.IN_010_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> IN_010_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.IN_010_S2(reqMap);
    }

    @Override
    public void PR_PDA_IN_020_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_020_C1(reqMap);
    }

    @Override
    public void PR_PDA_IN_020_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_020_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_IN_020_C3(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_020_C3(reqMap);
    }

    @Override
    public void PR_PDA_IN_020_C4(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_020_C4(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> IN_030_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.IN_030_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> IN_030_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.IN_030_S2(reqMap);
    }
    
    @Override
    public void PR_PDA_IN_030_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_030_C1(reqMap);
    }
        
    @Override
    public void PR_PDA_IN_040_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_040_C1(reqMap);
    }

    @Override
    public void PR_PDA_IN_040_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_040_C2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> IN_050_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.IN_050_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> IN_051_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.IN_051_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> IN_051_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.IN_051_S2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> IN_051_S3(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.IN_051_S3(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> IN_051_S4(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.IN_051_S4(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> IN_051_S5(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.IN_051_S5(reqMap);
    }
    
    @Override
    public void PR_PDA_IN_051_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_051_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> IN_060_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.IN_060_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_IN_060_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_060_C1(reqMap);
    }

    @Override
    public void PR_PDA_IN_060_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_060_C2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> IN_070_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.IN_070_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_IN_070_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_070_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_IN_080_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_080_C1(reqMap);
    }

    @Override
    public void PR_PDA_IN_080_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_080_C2(reqMap);
    }

    @Override
    public List<Map<String, Object>> IN_090_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.IN_090_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_IN_090_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_IN_090_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_010_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_010_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_015_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_015_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_015_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_015_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_020_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_020_C1(reqMap);
    }

    @Override
    public void PR_PDA_OT_025_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_025_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_030_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_030_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_035_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_035_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_035_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_035_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_035_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_035_C2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_040_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_040_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_040_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_040_S2(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_040_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_040_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_041_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_041_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_041_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_041_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_050_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_050_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_060_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_060_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_070_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_070_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_080_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_080_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_080_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_080_S2(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_080_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_080_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_080_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_080_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_080_C3(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_080_C3(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_085_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_085_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_085_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_085_S2(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_085_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_085_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_085_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_085_C2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_086_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_086_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_086_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_086_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_087_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_087_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_087_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_087_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_090_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_090_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_090_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_090_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_100_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_100_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_101_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_101_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_101_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_101_S2(reqMap);
    }
        
    @Override
    public void OT_101_I1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.OT_101_I1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_110_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_110_S1(reqMap);
    }
        
    @Override
    public void PR_PDA_OT_110_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_110_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_110_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_110_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_111_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_111_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_115_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_115_S1(reqMap);
    }
        
    @Override
    public void PR_PDA_OT_115_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_115_C1(reqMap);
    }

    @Override
    public List<Map<String, Object>> OT_118_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_118_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_118_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_118_S2(reqMap);
    }
        
    @Override
    public void PR_PDA_OT_118_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_118_C1(reqMap);
    }
        
    @Override
    public List<Map<String, Object>> OT_120_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_120_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_120_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_120_S2(reqMap);
    }
        
    @Override
    public void PR_PDA_OT_120_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_120_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_140_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_140_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_140_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_140_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_140_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_140_C2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_145_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_145_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_OT_145_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_145_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> OT_150_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.OT_150_S1(reqMap);
    }
        
    @Override
    public void PR_PDA_OT_150_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_OT_150_C1(reqMap);
    }
        
    @Override
    public List<Map<String, Object>> ST_010_S1_TEMP1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.ST_010_S1_TEMP1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> ST_010_S1_TEMP2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.ST_010_S1_TEMP2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> ST_010_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.ST_010_S2(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_010_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_010_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_020_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_020_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_020_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_020_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_025_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_025_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_030_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_030_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_030_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_030_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_030_C3(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_030_C3(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_040_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_040_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> ST_050_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.ST_050_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_050_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_050_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_060_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_060_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_065_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_065_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_070_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_070_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> ST_080_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.ST_080_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> ST_080_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.ST_080_S2(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_080_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_080_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_100_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_100_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_110_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_110_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_110_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_110_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_120_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_120_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_130_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_130_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_140_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_140_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_010_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_010_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_010_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_010_S2(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_010_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_010_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_020_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_020_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_030_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_030_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_040_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_040_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_050_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_050_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_060_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_060_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_060_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_060_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_070_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_070_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_070_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_070_S2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_070_S3(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_070_S3(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_070_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_070_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_070_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_070_C2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_075_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_075_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_075_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_075_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_080_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_080_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_080_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_080_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_090_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_090_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_090_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_090_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_090_C3(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_090_C3(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_100_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_100_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_100_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_100_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_100_C3(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_100_C3(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_110_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_110_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_110_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_110_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_120_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_120_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_120_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_120_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_120_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_120_C2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_130_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_130_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_130_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_130_S2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_130_S3(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_130_S3(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_130_S4(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_130_S4(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_130_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_130_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_130_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_130_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_130_C3(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_130_C3(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_140_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_140_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_140_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_140_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_145_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_145_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_145_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_145_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_150_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_150_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_160_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_160_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_160_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_160_C2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_170_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_170_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_170_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_170_S2(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_170_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_170_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_180_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_180_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_190_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_190_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_200_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_200_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_200_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_200_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_200_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_200_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_205_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_205_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_210_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_210_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_220_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_220_C1(reqMap);
    }

    @Override
    public List<Map<String, Object>> KD_229_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_229_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_229_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_229_S2(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_229_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_229_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_230_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_230_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_230_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_230_S2(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_230_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_230_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_240_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_240_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_240_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_240_S2(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_240_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_240_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_250_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_250_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_253_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_253_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_253_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_253_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_255_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_255_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_255_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_255_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_257_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_257_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_260_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_260_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_260_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_260_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_260_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_260_C2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_265_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_265_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_265_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_265_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_265_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_265_C2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> KD_270_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.KD_270_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_KD_270_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_KD_270_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_045_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_045_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_045_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_045_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_046_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_046_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_046_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_046_S2(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_046_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_046_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_048_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_048_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_048_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_048_S2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_048_S3(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_048_S3(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_048_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_048_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_065_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_065_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_065_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_065_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_065_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_065_C2(reqMap);
    }

    @Override
    public List<Map<String, Object>> PD_066_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_066_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_066_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_066_S2(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_066_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_066_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_067_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_067_C1(reqMap);
    }

    @Override
    public List<Map<String, Object>> PD_072_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_072_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_072_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_072_S2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_072_S3(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_072_S3(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_072_S4(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_072_S4(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_072_S5(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_072_S5(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_072_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_072_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_073_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_073_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_074_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_074_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_075_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_075_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_076_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_076_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_076_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_076_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_077_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_077_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_090_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_090_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_090_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_090_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_094_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_094_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_094_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_094_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_094_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_094_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_094_C3(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_094_C3(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_095_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_095_S1(reqMap);
    }

    @Override
    public void PR_PDA_PD_095_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_095_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_095_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_095_C2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_096_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_096_S1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_096_S2(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_096_S2(reqMap);
    }

    @Override
    public void PR_PDA_PD_096_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_096_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_096_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_096_C2(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_100_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_100_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_105_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_105_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_105_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_105_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_107_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_107_S1(reqMap);
    }
        
    @Override
    public void PR_PDA_PD_107_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_107_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_110_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_110_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_120_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_120_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_120_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_120_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_120_C2(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_120_C2(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_130_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_130_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_130_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_130_C1(reqMap);
    }
    
    @Override
    public void PR_PDA_PD_140_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_PD_140_C1(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> PD_150_S1(Map<String, Object> reqMap) throws Exception {
        return basicSampleMDAO.PD_150_S1(reqMap);
    }
    
    @Override
    public void PR_PDA_ST_990_C1(Map<String, Object> reqMap) throws Exception {
        basicSampleMDAO.PR_PDA_ST_990_C1(reqMap);
    }
    
}
