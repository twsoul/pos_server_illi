<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <title>Compress/Decompress Service Sample</title>
    
    <!-- Bootstrap core CSS -->
    <link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">
    <!--  Javascript -->
    <script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>" ></script>
	<script src="<c:url value='/js/bootstrap/bootstrap.min.js'/>" ></script>   
    
    <script>
    function goSubmit(){
        location.href="<c:url value='/cmm/prop/refreshProperty.do'/>";
    }    
    function goType(type){
    	document.compressForm.ctype.value=type;
    	document.getElementById("typelabel").innerHTML = "Compress Type = "+type;
    }
    function goDeType(type){
    	document.decompressForm.dtype.value=type;
    	document.getElementById("dtypelabel").innerHTML = "Decompress Type = "+type;
    }
    
    $(document).ready(function(){        
      //btn-file 
		$('#btn-file').click(function(){
			$('#input-file').click()
			});
		$('#dbtn-file').click(function(){
			$('#dinput-file').click()
			});
    });
    </script>
</head>
<body>
    <!-- 상단 메뉴 시작 -->
    <%@include file="../include/nav.jsp" %>
    <!-- 상단 메뉴 끝 -->
    
    <div class="container-fluid">
      <div class="row">
      
        <!--  왼쪽 메뉴 시작 -->
        <c:set var="menuId" value="15"/>
        <%@include file="../include/menu.jsp" %>
        <!--  왼쪽 메뉴 끝 -->
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
	        <h1 class="page-header">Compress/Decompress Sample</h1>
	        
	        <p>Compress (압축할 파일 선택 -> 압축형태 선택 -> 압축 -> 파일 다운로드)</p> 
			<form id="compressForm" name="compressForm" method="POST" action='<c:url value="/cmm/cmprs/compressSampleByType.do"/>' enctype="multipart/form-data">
			<input type="hidden" id="ctype" name="ctype" value=""/>
	 			<span id="btn-file" class="btn btn-success">
					<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
				Compress 파일 선택
					<span class="badge" id="file-badge"></span>
				</span>
				<input type="file" name="files" id="input-file" multiple style="display:none"/>
					<div class="dropdown">
					    <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
					    Compress Type
					    <span class="caret"></span></button>
					    <ul class="dropdown-menu">
					      <li><a href="javascript:goType('ZIP');">ZIP</a></li>
					      <li><a href="javascript:goType('JAR');">JAR</a></li>
					      <li><a href="javascript:goType('TAR');">TAR</a></li>
					      <li><a href="javascript:goType('CPIO');">CPIO</a></li>
					      <li><a href="javascript:goType('AR');">AR</a></li>
					      <li><a href="javascript:goType('TAR.GZ');">TAR.GZ</a></li>
					    </ul>
					    <span id="typelabel"></span>
					</div>
					
					<button type="submit" class="btn btn-info">
						<span class="glyphicon glyphicon-upload" aria-hidden="true"></span>
					 Compress
					</button>
			</form>
			<p> </p>
			<p>Decompress (압축 풀 파일 선택 -> 압축 푸는 방식 선택 -> 압축 풀기 -> 서버에 저장)</p> 
			<form id="decompressForm" name="decompressForm" method="POST" action='<c:url value="/cmm/cmprs/decompressSampleByType.do"/>' enctype="multipart/form-data">
			<input type="hidden" id="dtype" name="dtype" value=""/>
	 			<span id="dbtn-file" class="btn btn-success">
					<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
				Decompress 파일 선택
					<span class="badge" id="file-badge"></span>
				</span>
				<input type="file" name="dfiles" id="dinput-file" multiple style="display:none"/>
					<div class="dropdown">
					    <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
					    Decompress Type
					    <span class="caret"></span></button>
					    <ul class="dropdown-menu">
					      <li><a href="javascript:goDeType('ZIP');">ZIP</a></li>
					      <li><a href="javascript:goDeType('GZIP');">GZIP</a></li>
					      <li><a href="javascript:goDeType('ARCHIVE');">ARCHIVE</a></li>
					      <li><a href="javascript:goDeType('JAVA ZIP');">JAVA ZIP</a></li>
					    </ul>
					    <span id="dtypelabel"></span>
					</div>
					
					<button type="submit" class="btn btn-info">
						<span class="glyphicon glyphicon-upload" aria-hidden="true"></span>
					 Decompress
					</button>
			</form>
		</div>
      </div>
    </div>
</body>

</html>