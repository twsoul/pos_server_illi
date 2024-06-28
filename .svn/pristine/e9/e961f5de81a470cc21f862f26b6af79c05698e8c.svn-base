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
				<li class="active"><a href="<c:url value='/cmm/crypto/sampleSHA256.do'/>">SHA-256</a></li>
				<li><a href="<c:url value='/cmm/crypto/sampleAES256.do'/>">AES-256</a></li>
				<li><a href="<c:url value='/cmm/crypto/sampleTripleDES.do'/>">TripleDES</a></li>
				<li><a href="<c:url value='/cmm/crypto/sampleARIA.do'/>">ARIA</a></li>
			</ul>
			<!-- 암호화 탭 끝 -->
		
			<!-- 암호화 예제 시작 -->
			<h3>SHA-256 Encryption (Hash)</h3>
			
			<div class="panel panel-default">
				<div class="panel-body">
					<form action="<c:url value='/cmm/crypto/encryptSHA256.do'/>" method="POST">
						<div class="form-group">
							<label>Plain Text:</label>
							<input type="text" class="form-control" name="msg" value='<c:out value="${msg}" default="ableframe"/>' />
							<label>Salt:</label>
							<input type="text" class="form-control" name="salt" value='<c:out value="${salt}" default="ThisIsSalt"/>' />
							<label>Iteration:</label>
							<input type="text" class="form-control" name="iterations" value='<c:out value="${iterations}" default="100"/>' />
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