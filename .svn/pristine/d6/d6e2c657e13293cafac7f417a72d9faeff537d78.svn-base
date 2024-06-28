<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

	<title>File Upload/Download Sample (Min)</title>
	
	<!-- Bootstrap core CSS -->
    <link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">
    
	<!-- jQuery -->
    <script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>" ></script>
    
    <style>
    	table, th, td {
    	   border: 1px solid black;
    	}
    </style>
</head>
<body>
    <!-- 상단 메뉴 시작 
        <c:out value="${menuId }"/>-->
    <%@include file="../include/nav.jsp" %>
    <!-- 상단 메뉴 끝 -->
    
    <div class="container-fluid">
      <div class="row">
      
        <!--  왼쪽 메뉴 시작 -->
        <c:set var="menuId" value="9"/>
        <%@include file="../include/menu.jsp" %>
        <!--  왼쪽 메뉴 끝 -->
        
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
        
		<!--  파일 업로드/다운로드 예제 시작 -->
				<h3>파일 업로드</h3>
		<form method="POST" action="<c:url value='/cmm/file/uploadMin.do'/>"
			enctype="multipart/form-data">
			<input type="file" name="files" multiple/>
			<input type="submit" />
		</form>
		
		<h3>파일 다운로드</h3>
		<table>
			<thead>
				<tr>
					<th>File Name</th>
					<th>File Size</th>
					<th>Registration Date</th>
					<th>Download</th>
					<th>Delete</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="fileVO" items="${fileVOList}">
					<tr>
						<td>
							${fileVO.originalFileName}
						</td>
						<td>
							${fileVO.fileSize}
						</td>
						<td>
							${fileVO.regDate}
						</td>
						<td>
							<a href="<c:url value='/cmm/file/downloadMin.do?fileId=${fileVO.fileId}'/>">
								Download
							</a>
						</td>
						<td>
							<a href="<c:url value='/cmm/file/deleteMin.do?fileId=${fileVO.fileId}'/>">
								Delete
							</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<!--  파일 업로드/다운로드 예제 끝 -->
		
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