<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<title>Encryption & Decryption Sample</title>
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
        <c:set var="menuId" value="13"/>
        <%@include file="../include/menu.jsp" %>
        <!--  왼쪽 메뉴 끝 -->
        
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		
			<!-- 암호화 탭 시작 -->
			<ul class="nav nav-tabs">
				<li><a href="<c:url value='/cmm/crypto/sampleSHA256.do'/>">SHA-256</a></li>
				<li><a href="<c:url value='/cmm/crypto/sampleAES256.do'/>">AES-256</a></li>
				<li class="active"><a href="<c:url value='/cmm/crypto/sampleTripleDES.do'/>">TripleDES</a></li>
				<li><a href="<c:url value='/cmm/crypto/sampleARIA.do'/>">ARIA</a></li>
			</ul>
			<!-- 암호화 탭 끝 -->
		
			<!-- 암호화 예제 시작 -->
			<h3>TripleDES Encryption</h3>

			<div class="panel panel-default">
				<div class="panel-body">
					<form action="<c:url value='/cmm/crypto/encryptTripleDES.do'/>" method="POST">
						<div class="form-group">
							<label>Plain Text:</label>
							<input type="text" class="form-control" name="msg" value='<c:out value="${msg}" default="ableframe"/>' />
							<label>Key (Hex, 192bits):</label>
							<input type="text" class="form-control" name="key" value='<c:out value="${keyTDES}" default="6e7ae0e33251ec91a7fb62c4b97c1a646d70ab016ba846b6"/>' />
							<label>Initialization Vector (Hex, 64bits):</label>
							<input type="text" class="form-control" name="iv" value='<c:out value="${iv}" default="0001020304050607"/>' />
						</div>
						<div class="form-group">
							<button type="submit" class="btn btn-warning">
								<span class="glyphicon glyphicon-lock" aria-hidden="true"></span>
								Encrypt
							</button>
						</div>
					</form>
					
					<label>Encrypted Text (Base64):</label>
					<input type="text" class="form-control" value='<c:out value="${encrypted}"/>' />
				</div>
			</div>
			
			<h3>TripleDES Decryption</h3>
			
			<div class="panel panel-default">
				<div class="panel-body">
					<form action="<c:url value='/cmm/crypto/decryptTripleDES.do'/>" method="POST">
						<div class="form-group">
							<label>Encrypted Text (Base64):</label>
							<input type="text" class="form-control" name="encMsg" value='<c:out value="${encMsg}" default="Cp6qGvtTTbnt3Bc07bAptg=="/>' />
							<label>Key (Hex, 192bits):</label>
							<input type="text" class="form-control" name="key" value='<c:out value="${keyTDES}" default="6e7ae0e33251ec91a7fb62c4b97c1a646d70ab016ba846b6"/>' />
							<label>Initialization Vector (Hex, 64bits):</label>
							<input type="text" class="form-control" name="iv" value='<c:out value="${iv}" default="0001020304050607"/>' />
						</div>
						<div class="form-group">
							<button type="submit" class="btn btn-success">
								<span class="glyphicon glyphicon-sunglasses" aria-hidden="true"></span>
								Decrypt
							</button>
						</div>
					</form>
					
					<label>Decrypted Text</label>
					<input type="text" class="form-control" value='<c:out value="${decrypted}"/>' />
				</div>
			</div>
			
			<!-- 암호화 예제 끝 -->
		  
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