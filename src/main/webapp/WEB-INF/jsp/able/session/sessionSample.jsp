<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<title>Session Sample</title>
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
        <c:set var="menuId" value="12"/>
        <%@include file="../include/menu.jsp" %>
        <!--  왼쪽 메뉴 끝 -->
        
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		
		<!-- 예제 시작 -->
		<h3>Session Attributes Sample</h3>
		<form class="form-inline" method="POST" action='<c:url value="/cmm/session/addAttribute.do"/>' enctype="multipart/form-data">
			<div class="form-group">
				<label>Key:</label>
				<input type="text" name="attributeKey" class="form-control"/>
				<label>Value:</label>
				<input type="text" name="attributeValue" class="form-control"/>
			</div>
			<button type="submit" class="btn btn-primary">
				<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
				Add Attribute
			</button>
			<a class="btn btn-danger" href='<c:url value="/cmm/session/destroySession.do"/>'>
				<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
				Destroy Session
			</a>
			<a class="btn btn-success" href='<c:url value="/cmm/session/sessionSample.do"/>'>
				<span class="glyphicon glyphicon-repeat" aria-hidden="true"></span>
				Refresh
			</a>
		</form>
		<table class="table table-condensed">
			<thead>
				<tr>
					<th>Attribute Key</th>
					<th>Attribute Value</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="session" items="${sessionScope}">
					<tr>
						<td><c:out value="${session.key}" /></td>
						<td><c:out value="${session.value}" /></td>
						<td>
							<form method="POST" action='<c:url value="/cmm/session/delAttribute.do"/>' enctype="multipart/form-data">
								<input name="attributeKey" type="hidden" value="${session.key}"/>
								<button type="submit" class="btn btn-warning">
								<span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
								Delete Attribute
								</button>
							</form>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<!-- 예제 끝 -->
		  
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