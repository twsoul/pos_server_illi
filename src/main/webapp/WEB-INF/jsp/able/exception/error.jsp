<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Exception Sample</title>
<!-- Bootstrap core CSS -->
<link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>"
	rel="stylesheet">
<link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">

<!-- jQuery -->
<script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>"></script>

<script type="text/javascript">
	function goExceptionSample() {
	    location.href =  "<c:url value='/cmm/exception/exceptionSample.do'/>";
	}
</script>
</head>
<body>
	<!-- 상단 메뉴 시작 -->
	<c:out value="${menuId }" />
	<%@include file="../include/nav.jsp"%>
	<!-- 상단 메뉴 끝 -->

	<div class="container-fluid">
		<div class="row">

			<!--  왼쪽 메뉴 시작 -->
			<c:set var="menuId" value="17" />
			<%@include file="../include/menu.jsp"%>
			<!--  왼쪽 메뉴 끝 -->
			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
				<h1>에러가 발생하였습니다.</h1>
				<!-- <a href="javascript:goExceptionSample();" class="btn btn-primary">돌아가기</a> -->
				<a href="javascript:goExceptionSample();" class="btn btn-primary">돌아가기</a>
			</div>
		</div>
	</div>

	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<!-- <script src="/sample/js/jquery/jquery-2.1.4.min.js"></script> -->
	<script src="<c:url value='/js/bootstrap/bootstrap.min.js'/>"></script>
</body>
</html>