<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<title>Web Security Sample</title>
	<!-- Bootstrap core CSS -->
    <link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">
    
    <!-- jQuery -->
    <script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>" ></script>
    
    <script type="text/javascript">
    </script>
</head>
<body>
    <!-- 상단 메뉴 시작 -->
        <c:out value="${menuId }"/>
    <%@include file="../include/nav.jsp" %>
    <!-- 상단 메뉴 끝 -->
    
    <div class="container-fluid">
      <div class="row">
      
        <!--  왼쪽 메뉴 시작 -->
        <c:set var="menuId" value="11"/>
        <%@include file="../include/menu.jsp" %>
        <!--  왼쪽 메뉴 끝 -->

			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">

				<!-- 예제 시작 -->
				<h3>Security Filter</h3>
				<div class="panel panel-default">
					<div class="panel-heading">
						XSS String Converting (<code>' " : ; ( ) < > { } # $ % & ? ! @ * |</code>)
					</div>
					<div class="panel-body">
						<form action="<c:url value='/cmm/websecurity/websecuritySample.do'/>">
							<div class="form-group">
							<label>Input Text:</label>
								<input type="text" name="inputString" class="form-control"
								 value="<c:out value="${inputString}" default="' &quot : ; ( ) < > { } # $ % & ? ! @ * |" escapeXml="false"/>"/>
							</div>
							<div class="form-group">
								<button type="submit" class="btn btn-success">
									<span class="glyphicon glyphicon-transfer" aria-hidden="true"></span>
									Submit
								</button>
							</div>
						</form>
						
						<div class="form-group">
							<%-- <label>Original Value</label>
							<input type="text" class="form-control" value="<c:out value="${inputString}" escapeXml="false"/>"/> --%>
							<label>Escaped Value (Actual Value)</label>
							<input type="text" class="form-control" value="<c:out value="${inputString}" escapeXml="true"/>"/>
						</div>
					</div>
				</div>
				<h3>Security Interceptor</h3>
				<div class="panel panel-default">
					<div class="panel-heading">
					Upload Extension check (<code>.asp .java .php .exe ....</code>)
					-> <u class="text-danger"> Exception</u>
					</div>
					<div class="panel-body">
						<form action="<c:url value='/cmm/websecurity/uploadtest.do'/>" method="POST"
							enctype="multipart/form-data">
							<div class="form-group">
								<input type="file" name="files" multiple>
							</div>
 							<div class="form-group">
								<button type="submit" class="btn btn-info">
									<span class="glyphicon glyphicon-upload" aria-hidden="true"></span>
									Upload
								</button>
							</div>
						</form>
					</div>
				</div>
				<div class="panel panel-default">
					<div class="panel-heading">
					Download param check (<code>.\ ..\ ./ ../ ....</code>)
					-> <u class="text-danger"> Exception</u>
					</div>
					<div class="panel-body">
						<form action="<c:url value='/cmm/websecurity/websecuritySample.do'/>" method="GET"
							enctype="multipart/form-data">
							<div class="form-group">
								<input type="text" name="downloadPath" class="form-control"
								 value="<c:out value="${downloadPath}" default="../a.txt" escapeXml="false"/>"/>
							</div>
 							<div class="form-group">
								<button type="submit" class="btn btn-danger">
									<span class="glyphicon glyphicon-download" aria-hidden="true"></span>
									Download
								</button>
							</div>
						</form>
					</div>
				</div>
				<!-- 예제 끝 -->
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