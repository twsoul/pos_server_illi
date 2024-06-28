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
        $("#goDownMap").click(function(){
            location.href="<c:url value='/cmm/excel/excelDownloadMapP.do'/>";

        });
        
        $("#goDownVO").click(function(){
            $.post("<c:url value='/cmm/excel/excelDownloadVO.do'/>",
            function(data, status){
                alert("Data: " + data.message + "\nStatus: " + status);
            });
        });
        
        $("#goUp").click(function(){
            $.post("<c:url value='/cmm/excel/excelUpload.do'/>",
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

      //btn-file 
		$('#btn-file').click(function(){
			$('#input-file').click()
			});
    });
    
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
        <c:set var="menuId" value="7"/>
        <%@include file="../include/menu.jsp" %>
        <!--  왼쪽 메뉴 끝 -->
        
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
          <h1 class="page-header">Excel Sample</h1>       
          <div class="panel-group">
              <div class="panel-body" align="left">
                  <button id="goDownMap" type="button" class="btn btn-primary">엑셀 다운로드(MAP)</button>
                  <button id="goDownVO" type="button" class="btn btn-primary">엑셀 다운로드(VO)</button>
                  <form method="POST" action='<c:url value="/cmm/excel/excelUpload.do"/>' enctype="multipart/form-data">
		 			<span id="btn-file" class="btn btn-success">
						<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
					엑셀 업로드 파일 선택
						<span class="badge" id="file-badge"></span>
					</span>
					<input type="file" name="files" id="input-file" multiple style="display:none"/>
					<button type="submit" class="btn btn-info">
						<span class="glyphicon glyphicon-upload" aria-hidden="true"></span>
						엑셀 파일 업로드
					</button>			
				</form>
              </div>
          </div>
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
        </div>
      </div>
    </div>
    
    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/sample/js/jquery/jquery-2.1.4.min.js"></script>
    <script src="/sample/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>