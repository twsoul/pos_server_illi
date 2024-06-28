package able.board.vo;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : BoardSampleVO.java
 * @Description : 게시판 관련 정보 vo 클래스
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

public class BoardSampleVO {
    
    /** 게시글 ID */
    private String artId;
    
    /** 게시글 분류 */
    private String artCategory;
    
    /** 게시글 제목 */
    @NotEmpty(message="{errors.required}")
    private String artSubject;
    
    /** 게시글 내용 */
    @NotEmpty(message="{errors.required}")
    private String artContent;
    
    /** 게시자 */
    @NotEmpty(message="{errors.required}")
    private String regUser;
    
    /** 게시일 */
    private String regDate;
    
    /** 첨부파일 유무 */
    private String attachYn;
    
    /** 검색Keyword */
    private String searchKeyword;
    
    /** 검색조건 */
    private String searchCondition;
    
    /** offset */
    private String offset;
    
    /** limit */
    private String limit;
    
    /** 현재 페이지 */
    private String currPage;
    
    public String getCurrPage() {
        return currPage;
    }

    public void setCurrPage(String currPage) {
        this.currPage = currPage;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    private List<BoardSampleFileVO> fileList;
    
    public List<BoardSampleFileVO> getFileList() {
        return fileList;
    }

    public void setFileList(List<BoardSampleFileVO> fileList) {
        this.fileList = fileList;
    }

    public String getArtId() {
        return artId;
    }

    public void setArtId(String artId) {
        this.artId = artId;
    }

    public String getArtCategory() {
        return artCategory;
    }

    public void setArtCategory(String artCategory) {
        this.artCategory = artCategory;
    }

    public String getArtSubject() {
        return artSubject;
    }

    public void setArtSubject(String artSubject) {
        this.artSubject = artSubject;
    }

    public String getArtContent() {
        return artContent;
    }

    public void setArtContent(String artContent) {
        this.artContent = artContent;
    }

    public String getRegUser() {
        return regUser;
    }

    public void setRegUser(String regUser) {
        this.regUser = regUser;
    }
    
    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getSearchCondition() {
        return searchCondition;
    }

    public void setSearchCondition(String searchCondition) {
        this.searchCondition = searchCondition;
    }

    public String getAttachYn() {
        return attachYn;
    }

    public void setAttachYn(String attachYn) {
        this.attachYn = attachYn;
    }    
}

