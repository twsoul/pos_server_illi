<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <title>Sample MessageSource</title>
    
    <!-- Bootstrap core CSS -->
    <link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">
    <!-- jQuery -->
    <script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>" ></script>
    
    <script>
    
    function goView(key) {
        //var data = {}
        //data["query"] = $("#query").val();
        $.ajax({
            type : "GET",
            contentType : "application/json",
            url : "<c:url value='/cmm/msg/selectMessage.do'/>?msgKey="+key,
            //data : JSON.stringify(data),
            dataType : 'json',
            success : function(data) {
                console.log("SUCCESS: ", data);
                display(data);
            },
            error : function(e) {
                console.log("ERROR: ", e);
            },
            done : function(e) {
                console.log("DONE");
            }
        });
    }
    
    function display(e){ 
    	
    	$('#msgKey').val(e.result.msgKey);
        $('#msgKo').val(e.result.msgKoKr);
        $('#msgEn').val(e.result.msgEnUs);
        $('#msgKoDate').html("Last Modify Datetime : "+e.result.msgKoKrDate);
        $('#msgEnDate').html("Last Modify Datetime : "+e.result.msgEnUsDate);
        
        $('#msgKeyOrg').val(e.result.msgKey);
        $('#msgKoOrg').val(e.result.msgKoKr);
        $('#msgEnOrg').val(e.result.msgEnUs);
    }
    
    function goModify(){
    	alert($('#msgKoOrg').val());
    	$.ajax({
            type : "POST",
            url : "<c:url value='/cmm/msg/updateMessage.do'/>",
            data : {msgKey:$('#msgKey').val(),
                msgKo:$('#msgKo').val(),
                msgEn:$('#msgEn').val(),
                msgKoOrg:$('#msgKoOrg').val(),
                msgEnOrg:$('#msgEnOrg').val()},
            success : function(data) {
                console.log("SUCCESS: ", data);
                goSuccess(data.message);
            },
            error : function(e) {
                console.log("ERROR: ", e);
            },
            done : function(e) {
                console.log("DONE");
            }
        });
    }
    
    function goDelete(){
    	var orgkey = $('#msgKeyOrg').val();
    	$.ajax({
            type : "GET",
            contentType : "application/json",
            url : "<c:url value='/cmm/msg/deleteMessage.do'/>?msgKey="+orgkey,
            dataType : 'json',
            timeout : 100000,
            success : function(data) {
                console.log("SUCCESS: ", data);
                goSuccess(data.message);
            },
            error : function(e) {
                console.log("ERROR: ", e);
            },
            done : function(e) {
                console.log("DONE");
            }
        });
    }
    
    function goInsert(){    	
    	$.ajax({
            type : "POST",
            url : "<c:url value='/cmm/msg/insertMessage.do'/>",
            data : {msgKey:$('#msgKey').val(),
                msgKo:$('#msgKo').val(),
                msgEn:$('#msgEn').val(),
                msgKoOrg:$('#msgKoOrg').val(),
                msgEnOrg:$('#msgEnOrg').val()},
            success : function(data) {
                console.log("SUCCESS: ", data);
                goSuccess(data.message);
            },
            error : function(e) {
                console.log("ERROR: ", e);
            },
            done : function(e) {
                console.log("DONE");
            }
        });
    }
    
    function goSuccess(message){
        alert(message);
        window.location.href="<c:url value='/cmm/msg/selectMessageList.do'/>";
    }
    
    </script>
</head>
<body>
    <!-- 상단 메뉴 시작 
        <c:out value="${menuId }"/>-->
    <%@include file="../include/nav.jsp" %>
    <!-- 상단 메뉴 끝 -->
    
    <div class="container-fluid">
      <div class="row">
      
        <!--  왼쪽 메뉴 시작 -->
        <c:set var="menuId" value="4"/>
        <%@include file="../include/menu.jsp" %>
        <!--  왼쪽 메뉴 끝 -->
        
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
          <h1 class="page-header">Message Source Management</h1>
<!-- 
          <div class="dropdown">
		    <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">언어 선택
		    <span class="caret"></span></button>
		    <ul class="dropdown-menu">
		      <li><a href="#"><spring:message code="button.ko"/></a></li>
		      <li><a href="#"><spring:message code="button.en"/></a></li>
		    </ul>
		  </div>
 -->		  
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>                  
                  <th>#</th>
                  <th><spring:message code="msg_code"/></th>
                  <th><spring:message code="msg_value"/></th>
                  <th><spring:message code="msg_language"/></th>
                  <th><spring:message code="msg_lastModify"/></th>
                </tr>
              </thead>
              <tbody>
              <c:forEach var="result" items="${resultList}" varStatus="status">
                <tr onclick="javascript:goView('${result.key }')">
                  <td>#</td>
                  <td><c:out value="${result.key }"/></td>
                  <td><c:out value="${result.value }"/></td>
                  <td><c:out value="${result.language }"/></td>
                  <td><c:out value="${result.lastmodify }"/></td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
<form id="listForm" name="listForm" method="post">
<input type="hidden" id="msgKeyOrg" />
<input type="hidden" id="msgKoOrg" />
<input type="hidden" id="msgEnOrg" />        
          <div class="panel-group">
		    <div class="panel panel-info">
		      <div class="panel-heading">Message Info</div>
		      <div class="panel-body">
		          <label for="msgKey">Message Code</label>
                  <input type="text" class="form-control" id="msgKey">
              </div>
		      <div class="panel-body">
		          <label for="msgKo">Message Text (KO_KR)</label>
                  <input type="text" class="form-control" id="msgKo">
                  <div id="msgKoDate"></div>
              </div>
		      <div class="panel-body">
		          <label for="msgEn">Message Text (EN_US)</label>
                  <input type="text" class="form-control" id="msgEn">
                  <div id="msgEnDate"></div>
              </div>
              <div class="panel-body" align="right">
                  <button type="button" class="btn btn-primary" onclick="javascript:goModify()">수정</button>
                  <button type="button" class="btn btn-primary" onclick="javascript:goDelete()">삭제</button>
                  <button type="button" class="btn btn-primary" onclick="javascript:goInsert()">등록</button>
              </div>
		    </div>
          </div>
</form>  
        </div>
      </div>
    </div>
    
    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster 
    <script src="/sample/js/jquery/jquery-2.1.4.min.js"></script>
    <script src="/sample/bootstrap/js/bootstrap.min.js"></script>-->
</body>
</html>