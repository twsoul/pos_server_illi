<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<%
  /**
  * @Class Name : ableSampleList.jsp
  * @Description : Sample List 화면
  * @Modification Information
  *
  *   수정일         수정자                   수정내용
  *  -------    --------    ---------------------------
  *  2015.11.18             최초 생성
  *
  * author sunny
  *
  */
%>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<c:set var="registerFlag" value="${empty sampleVO.id ? 'create' : 'modify'}"/>
<title>ABLE_Frame Sample List</title>
<link type="text/css" rel="stylesheet" href="<c:url value='/css/able/sample.css'/>"/>
<script type="text/javascript" src="<c:url value='/cmmn/validator.do'/>"></script>
<validator:javascript formName="sampleVO" staticJavascript="false" xhtml="true" cdata="false"/>

<script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>" ></script>

	<script>

        /* 글 수정 화면 function */
        function fn_egov_select(id) {
        	document.listForm.selectedId.value = id;
           	//document.listForm.action = "<c:url value='/updateSampleView.do'/>";
           	//document.listForm.submit();
           	searchAjax(id);
           	
        }
        
        /* pagination 페이지 링크 function */
        function fn_egov_link_page(pageNo){
        	document.listForm.pageIndex.value = pageNo;
        	document.listForm.action = "<c:url value='/test/ableSampleList.do'/>";
           	document.listForm.submit();
        }
        
        /* 글 등록 function */
        function fn_egov_save() {
        	frm = document.detailForm;
        	//if(!validateSampleVO(frm)){
            //    return;
            //}else{
            	frm.action = "<c:url value='/test/addSample.do'/>";
                frm.submit();
            //}
        }
        
        function searchAjax(id) {
            //var data = {}
            //data["query"] = $("#query").val();
            
            $.ajax({
                type : "POST",
                contentType : "application/json",
                url : "<c:url value='/test/selectSampleView.do'/>?selectedId="+id,
                //data : JSON.stringify(data),
                dataType : 'json',
                timeout : 100000,
                success : function(data) {
                    console.log("SUCCESS: ", data);
                    display(data);
                },
                error : function(e) {
                    console.log("ERROR: ", e);
                    display(e);
                },
                done : function(e) {
                    console.log("DONE");
                }
            });
        }
        
        function display(e){        	
        	var json = JSON.stringify(e, null, 4);        	
        	$('#name').val(e.view.name);
        	$('#description').val(e.view.description);
        	$('#useYn').val(e.view.useYn);
        	$('#regUser').val(e.view.regUser);
        }
        
        function fn_update_locale(lc){
        	location.href="<c:url value='/test/ableSampleList.do'/>?ABLE_LANGUAGE_SELECTION_PARAM="+lc;       	
        	
        }

    </script>
</head>
<body style="text-align:center; margin:0 auto; display:inline; padding-top:100px;">
<span class="btn_blue_l">
<a href="javascript:fn_update_locale('ko_KR');"><spring:message code="button.ko" /></a>
<img src="<c:url value='/images/egovframework/example/btn_bg_r.gif'/>" style="margin-left:6px;" alt=""/>
</span>
<span class="btn_blue_l">
<a href="javascript:fn_update_locale('en_US');"><spring:message code="button.en" /></a>
<img src="<c:url value='/images/egovframework/example/btn_bg_r.gif'/>" style="margin-left:6px;" alt=""/>
</span>


    <form:form commandName="searchVO" id="listForm" name="listForm" method="post">
        <input type="hidden" name="selectedId" />
        <div id="content_pop">
        	<!-- 타이틀 -->
        	<div id="title">
        		<ul>
        			<li><img src="<c:url value='/images/egovframework/example/title_dot.gif'/>" alt=""/><spring:message code="list.sample" /></li>
        		</ul>
        	</div>
        	<!-- // 타이틀 -->
        	<div id="search">
        		<ul>
        			<li>
        			    <label for="searchCondition" style="visibility:hidden;"><spring:message code="search.choose" /></label>
        				<form:select path="searchCondition" cssClass="use">
        					<form:option value="1" label="Name" />
        					<form:option value="0" label="ID" />
        				</form:select>
        			</li>
        			<li><label for="searchKeyword" style="visibility:hidden;display:none;"><spring:message code="search.keyword" /></label>
                        <form:input path="searchKeyword" cssClass="txt"/>
                    </li>
        			<li>
        	            <span class="btn_blue_l">
        	                <a href="javascript:fn_egov_selectList();"><spring:message code="button.search" /></a>
        	                <img src="<c:url value='/images/egovframework/example/btn_bg_r.gif'/>" style="margin-left:6px;" alt=""/>
        	            </span>
        	        </li>
                </ul>
        	</div>
        	<!-- List -->
        	<div id="table">
        		<table width="100%" border="0" cellpadding="0" cellspacing="0" summary="카테고리ID, 케테고리명, 사용여부, Description, 등록자 표시하는 테이블">
        			<caption style="visibility:hidden">카테고리ID, 케테고리명, 사용여부, Description, 등록자 표시하는 테이블</caption>
        			<colgroup>
        				<col width="40"/>
        				<col width="100"/>
        				<col width="150"/>
        				<col width="80"/>
        				<col width="?"/>
        				<col width="60"/>
        			</colgroup>
        			<tr>
        				<th align="center">No</th>
        				<th align="center"><spring:message code="title.sample.id" /></th>
        				<th align="center"><spring:message code="title.sample.name" /></th>
        				<th align="center"><spring:message code="title.sample.useYn" /></th>
        				<th align="center"><spring:message code="title.sample.description" /></th>
        				<th align="center"><spring:message code="title.sample.regUser" /></th>
        			</tr>
        			<c:forEach var="result" items="${resultList}" varStatus="status">
            			<tr>
            				<td align="center" class="listtd"><c:out value="${paginationInfo.totalRecordCount+1 - ((searchVO.pageIndex-1) * searchVO.pageSize + status.count)}"/></td>
            				<td align="center" class="listtd"><a href="javascript:fn_egov_select('<c:out value="${result.id}"/>')"><c:out value="${result.id}"/></a></td>
            				<td align="left" class="listtd">${result.name}<c:out value="${result.name}"/>&nbsp;</td>
            				<td align="center" class="listtd"><c:out value="${result.useYn}"/>&nbsp;</td>
            				<td align="center" class="listtd"><c:out value="${result.description}"/>&nbsp;</td>
            				<td align="center" class="listtd"><c:out value="${result.regUser}"/>&nbsp;</td>
            			</tr>
        			</c:forEach>
        		</table>
        	</div>
        	<!-- /List -->
        	<div id="paging">
        		<ui:pagination paginationInfo = "${paginationInfo}" type="image" jsFunction="fn_egov_link_page" />
        		<form:hidden path="pageIndex" />
        	</div>
        	<div id="sysbtn">
        	  <ul>
        	      <li>
        	          <span class="btn_blue_l">
        	              <a href="javascript:fn_egov_addView();"><spring:message code="button.create" /></a>
                          <img src="<c:url value='/images/egovframework/example/btn_bg_r.gif'/>" style="margin-left:6px;" alt=""/>
                      </span>
                  </li>
              </ul>
        	</div>
        </div>
    </form:form>
    
    <form:form commandName="sampleVO" id="detailForm" name="detailForm">
	    <div id="content_pop">
	    	<!-- 타이틀 -->
	    	<div id="title">
	    		<ul>
	    			<li><img src="<c:url value='/images/egovframework/example/title_dot.gif'/>" alt=""/>
	                    <c:if test="${registerFlag == 'create'}"><spring:message code="button.create" /></c:if>
	                    <c:if test="${registerFlag == 'modify'}"><spring:message code="button.modify" /></c:if>
	                </li>
	    		</ul>
	    	</div>
	    	<!-- // 타이틀 -->
	    	<div id="table">
	    	<table width="100%" border="1" cellpadding="0" cellspacing="0" style="bordercolor:#D3E2EC; bordercolordark:#FFFFFF; BORDER-TOP:#C2D0DB 2px solid; BORDER-LEFT:#ffffff 1px solid; BORDER-RIGHT:#ffffff 1px solid; BORDER-BOTTOM:#C2D0DB 1px solid; border-collapse: collapse;">
	    		<colgroup>
	    			<col width="150"/>
	    			<col width="?"/>
	    		</colgroup>
	    		<c:if test="${registerFlag == 'modify'}">
	        		<tr>
	        			<td class="tbtd_caption"><label for="id"><spring:message code="title.sample.id" /></label></td>
	        			<td class="tbtd_content">
	        			
	        				<form:input path="id" cssClass="essentiality" maxlength="10" readonly="true" />
	        			</td>
	        		</tr>
	    		</c:if>
	    		<tr>
	    			<td class="tbtd_caption"><label for="name"><spring:message code="title.sample.name" /></label></td>
	    			<td class="tbtd_content">
	    				<form:input path="name" maxlength="30" cssClass="txt"/>
	    				&nbsp;<form:errors path="name" />
	    			</td>
	    		</tr>
	    		<tr>
	    			<td class="tbtd_caption"><label for="useYn"><spring:message code="title.sample.useYn" /></label></td>
	    			<td class="tbtd_content">
	    				<form:select path="useYn" cssClass="use">
	    					<form:option value="Y" label="Yes" />
	    					<form:option value="N" label="No" />
	    				</form:select>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td class="tbtd_caption"><label for="description"><spring:message code="title.sample.description" /></label></td>
	    			<td class="tbtd_content">
	    				<form:textarea path="description" rows="5" cols="58" />&nbsp;<form:errors path="description" />
	                </td>
	    		</tr>
	    		<tr>
	    			<td class="tbtd_caption"><label for="regUser"><spring:message code="title.sample.regUser" /></label></td>
	    			<td class="tbtd_content">
	                    <c:if test="${registerFlag == 'modify'}">
	        				<form:input path="regUser" maxlength="10" cssClass="essentiality" readonly="true" />
	        				&nbsp;<form:errors path="regUser" /></td>
	                    </c:if>
	                    <c:if test="${registerFlag != 'modify'}">
	        				<form:input path="regUser" maxlength="10" cssClass="txt"  />
	        				&nbsp;<form:errors path="regUser" /></td>
	                    </c:if>
	    		</tr>
	    	</table>
	      </div>
	    	<div id="sysbtn">
	    		<ul>
	    			<li>
	                    <span class="btn_blue_l">
	                        <a href="javascript:fn_egov_selectList();"><spring:message code="button.list" /></a>
	                        <img src="<c:url value='/images/egovframework/example/btn_bg_r.gif'/>" style="margin-left:6px;" alt=""/>
	                    </span>
	                </li>
	    			<li>
	                    <span class="btn_blue_l">
	                        <a href="javascript:fn_egov_save();">
	                            <c:if test="${registerFlag == 'create'}"><spring:message code="button.create" /></c:if>
	                            <c:if test="${registerFlag == 'modify'}"><spring:message code="button.modify" /></c:if>
	                        </a>
	                        <img src="<c:url value='/images/egovframework/example/btn_bg_r.gif'/>" style="margin-left:6px;" alt=""/>
	                    </span>
	                </li>
	    			<c:if test="${registerFlag == 'modify'}">
	                    <li>
	                        <span class="btn_blue_l">
	                            <a href="javascript:fn_egov_delete();"><spring:message code="button.delete" /></a>
	                            <img src="<c:url value='/images/egovframework/example/btn_bg_r.gif'/>" style="margin-left:6px;" alt=""/>
	                        </span>
	                    </li>
	    			</c:if>
	    			<li>
	                    <span class="btn_blue_l">
	                        <a href="javascript:document.detailForm.reset();"><spring:message code="button.reset" /></a>
	                        <img src="<c:url value='/images/egovframework/example/btn_bg_r.gif'/>" style="margin-left:6px;" alt=""/>
	                    </span>
	                </li>
	            </ul>
	    	</div>
	    </div>
	    <!-- 검색조건 유지 -->
	    <input type="hidden" name="searchCondition" value="<c:out value='${searchVO.searchCondition}'/>"/>
	    <input type="hidden" name="searchKeyword" value="<c:out value='${searchVO.searchKeyword}'/>"/>
	    <input type="hidden" name="pageIndex" value="<c:out value='${searchVO.pageIndex}'/>"/>
	</form:form>

</body>
</html>