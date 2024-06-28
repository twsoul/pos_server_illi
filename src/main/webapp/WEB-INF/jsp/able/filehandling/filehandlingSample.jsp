<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<title>File Upload/Download Sample</title>
	<!-- Bootstrap core CSS -->
    <link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">
    
    <!-- jQuery -->
    <script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>" ></script>
    
    <script type="text/javascript">
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
        <c:set var="menuId" value="10"/>
        <%@include file="../include/menu.jsp" %>
        <!--  왼쪽 메뉴 끝 -->
        
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		
		<!-- File Handle 예제 시작 -->
		<h3>File Handling Util 예제</h3>
		<div class="panel panel-info">
			<div class="panel-heading">Working Directory : ${workDir}</div>
		</div>
			
		<!-- File Handle 예제 끝 -->
		
		<a class="btn btn-info" href='<c:url value="/cmm/filehandling/create.do"/>'>
			<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
			Create Test File
		</a>
		<br/>
		<table class="table table-condensed">
			<thead>
				<tr>
					<th>File Name</th>
					<th>Copy</th>
					<th>Delete</th>
					<!-- <th>Registration Date</th>
					<th>Download</th> -->
				</tr>
			</thead>
			<tbody>
				<c:forEach var="filename" items="${filenames}">
					<tr>
						<td><c:out value="${filename}" /></td>
						<td>
							<a class="btn btn-success" href='<c:url value="/cmm/filehandling/copy.do?filename=${filename}"/>'>
								<span class="glyphicon glyphicon-duplicate" aria-hidden="true"></span>
								Copy
							</a>
						</td>
						<td>
							<a class="btn btn-danger" href='<c:url value="/cmm/filehandling/delete.do?filename=${filename}"/>'>
								<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
								Delete
							</a>
							<!--  post delete 예제 -->
							<!-- <form method="#" action="#">
								<button type="submit" class="btn btn-danger">
									<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
									Delete
								</button>
							</form> -->
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<!-- File Handle 예제 시작 -->
		  
        </div>
      </div>
    </div>
		
		
		<btn></btn>
		  
        </div>
      </div>
    </div>
    
    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <!-- <script src="/sample/js/jquery/jquery-2.1.4.min.js"></script> -->
    <script src="<c:url value='/js/bootstrap/bootstrap.min.js'/>" ></script>
</body>
</html>