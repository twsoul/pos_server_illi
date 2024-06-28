/*
 * Copyright 2008-2014 MOPAS(Ministry of Public Administration and Security).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package able.cmm.excel;

import org.apache.poi.ss.usermodel.Row;

// CHECKSTYLE:OFF
/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ExcelMapping.java
 * @Description : 엑셀파일의 DB업로드용 VO매핑 클래스
 * @author "ADM Technology Team"
 * @since 2016. 7. 1
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.     "ADM Technology Team"     	최초 생성
 * </pre>
 */
public abstract class ExcelMapping {
// CHECKSTYLE:ON
	
    /**
     * 엑셀파일의 DB 업로드를 위한 사용자 VO 매핑 메소드
     * @param row
     * @return
     * @throws Exception
     */
    public abstract Object mappingColumn(Row row) throws Exception;
}
