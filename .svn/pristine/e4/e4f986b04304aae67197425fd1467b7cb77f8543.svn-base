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
    	$(document).ready(function(){
    		//btn-file 
    		$('#btn-file').click(function(){
    			$('#input-file').click()
    			});
    		//업로드 파일 리스트
    		$('#input-file').change(function(){
    			if(this.files){
    				$('#file-list').empty();
    				var ul = $('#file-list').get(0);
    				
    				var length = this.files.length;
    				for(var i = 0; i<length ; i++){
    					//console.log(i);
    					var li = document.createElement('li');
    					li.appendChild(document.createTextNode(this.files[i].name));
	    				ul.appendChild(li)
    				}
    			}
    		});
    		//업로드 파일 개수 뱃지
    		$('#input-file').change(function(){
    			if(this.files){
    				 var badge = $('#file-badge').get(0)
    				 if(badge){
    					 badge.innerHTML = this.files.length; 
    				 }
    			}
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
        <c:set var="menuId" value="8"/>
        <%@include file="../include/menu.jsp" %>
        <!--  왼쪽 메뉴 끝 -->
        
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		
		<!-- 파일 업로드 예제 시작 -->
		
		<h3>파일 업로드</h3>
		<form method="POST" action='<c:url value="/cmm/file/upload.do"/>' enctype="multipart/form-data">
 			<span id="btn-file" class="btn btn-success">
				<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
			Select Files
				<span class="badge" id="file-badge"></span>
			</span>
			<input type="file" name="files" id="input-file" multiple style="display:none"/>
			
			<!-- <input type="submit" /> -->
			<button type="submit" class="btn btn-info">
				<span class="glyphicon glyphicon-upload" aria-hidden="true"></span>
				Upload
			</button>
			
			
		</form>
			<ul id="file-list"></ul>
		
		
		<h3>파일 다운로드</h3>
		<table class="table table-condensed">
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
						<td><c:out value="${fileVO.originalFileName}" /></td>
						<td><c:out value="${fileVO.fileSize}" /></td>
						<td>
							<c:out value="${fileVO.regDate}"/>
							<%-- <fmt:formatDate	type="date" value="${fileVO.regDate}" pattern="yyyy-MM-dd" /> --%>
						</td>
						<td>
							<a class="btn btn-warning" href='<c:url value="/cmm/file/download.do?fileId=${fileVO.fileId}"/>'>
								<span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span>
								Download
							</a>
						</td>
						<td>
							<a class="btn btn-danger" href='<c:url value="/cmm/file/delete.do?fileId=${fileVO.fileId}"/>'>
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
		<!-- 파일 업로드 예제 끝 -->
		  
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