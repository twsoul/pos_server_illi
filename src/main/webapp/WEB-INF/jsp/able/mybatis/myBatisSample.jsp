<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <title>MyBatis Sample</title>
    
    <!-- Bootstrap core CSS -->
    <link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">
    
    <!-- jQuery -->
    <script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>" ></script>
    
    <script>    
    $(document).ready(function(){
        $("#goModify").click(function(){
            $.post("<c:url value='/cmm/mybatis/updateMyBatis.do'/>",
            {
                id: $('#ids').val(),
                name: $('#name').val(),
                description: $('#description').val(),
                useYn: $('#useYn').val()                
            },
            function(data, status){
                alert("Data: " + data.message + "\nStatus: " + status);
            });
        });
        
        $("#goInsert").click(function(){
            $.post("<c:url value='/cmm/mybatis/insertMyBatis.do'/>",
            {
                id: $('#ids').val(),
                name: $('#name').val(),
                description: $('#description').val(),
                useYn: $('#useYn').val()              
            },
            function(data, status){
                alert("Data: " + data.message + "\nStatus: " + status);
            });
        });
        
        $("#goDelete").click(function(){
            $.post("<c:url value='/cmm/mybatis/deleteMyBatis.do'/>",
            {
                id: $('#ids').val()              
            },
            function(data, status){
                alert("Data: " + data.message + "\nStatus: " + status);
            });
        });
    });
    
	    function goView(key) {
	        //var data = {}
	        //data["query"] = $("#query").val();
	        
	        $.ajax({
	            type : "GET",
	            contentType : "application/json",
	            url : "<c:url value='/cmm/mybatis/selectMyBatis.do'/>?id="+key,
	            //data : JSON.stringify(data),
	            dataType : 'json',
	            timeout : 100000,
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
	        //alert(e.result.msgKey);
	        $('#ids').val(e.result.id);
	        $('#name').val(e.result.name);
	        $('#description').val(e.result.description);
	        $('#useYn').val(e.result.useYn);
	        $('#regUser').val(e.result.regUser);	        
	    }
	    
	    function jsPage( page ) {
	        var frm = document.pageForm;
	        frm.currPage.value = page;
	        frm.action = "<c:url value='/cmm/mybatis/selectMyBatisList.do'/>";
	        frm.submit();
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
        <c:set var="menuId" value="6"/>
        <%@include file="../include/menu.jsp" %>
        <!--  왼쪽 메뉴 끝 -->
        
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
          <h1 class="page-header">MyBatis Sample</h1>       
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>         
                  <th>ID</th>
                  <th>NAME</th>
                  <th>DESCRIPTION</th>
                  <th>USE_YN</th>
                  <th>REG_USER</th>
                </tr>
              </thead>
              <tbody>
              <c:forEach var="result" items="${resultList}" varStatus="status">
                <tr onclick="javascript:goView('${result.id }')">
                  <td><c:out value="${result.id }"/></td>
                  <td><c:out value="${result.name }"/></td>
                  <td><c:out value="${result.description }"/></td>
                  <td><c:out value="${result.useYn }"/></td>
                  <td><c:out value="${result.regUser }"/></td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
<form id="pageForm" name="pageForm" method="post">          
<input type="hidden" name="currPage" value=""/>          
</form>
          <div>
	           <able:pagination paginationInfo="${page}" type="image" jsFunction="jsPage"/>
	      </div>
<form id="listForm" name="listForm" method="post">
<input type="hidden" name="currPage" value=""/>
          <div class="panel-group">
            <div class="panel panel-info">
              <div class="panel-heading">Message Info</div>
              <div class="panel-body">
                  <label for="id">ID</label>
                  <input type="text" class="form-control" id="ids">
              </div>
              <div class="panel-body">
                  <label for="name">NAME</label>
                  <input type="text" class="form-control" id="name">
              </div>
              <div class="panel-body">
                  <label for="description">DESCRIPTION</label>
                  <input type="text" class="form-control" id="description">
              </div>
              <div class="panel-body">
                  <label for="useYn">USE_YN</label>
                  <input type="text" class="form-control" id="useYn">
              </div>
              <div class="panel-body">
                  <label for="regUser">REG_USER</label>
                  <input type="text" class="form-control" id="regUser">
              </div>
              <div class="panel-body" align="right">
                  <button id="goModify" type="button" class="btn btn-primary">수정</button>
                  <button id="goDelete" type="button" class="btn btn-primary">삭제</button>
                  <button id="goInsert" type="button" class="btn btn-primary">등록</button>
              </div>
            </div>
          </div>
</form>  
        </div>
      </div>
    </div>

</body>
</html>