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
	function callExceptionResolver() {
		location.href = "<c:url value='/cmm/exception/exceptionResolver.do'/>";
	}
	function callLeaveTrace() {
		location.href = "<c:url value='/cmm/exception/leaveTrace.do'/>";
	}
	function callBizException() {
		location.href = "<c:url value='/cmm/exception/bizException.do'/>";
	}

	function callAjaxException() {
		$.ajax({
			type : "POST",
			dataType : "json",
			url : '<c:url value="/cmm/exception/ajaxTryCatchException.do"/>',
			success:function(data){
				alert("Success");
				if(data[0].result == "fail"){
					//Exception처리
					alert("HTTP STATUS:200OK 이지만 result:"+data[0].result);
				}
			},
			error:function(xhr, status, error){
				alert("Error");
				console.log(xhr);
				console.log(status);
				console.log(error);
			}
		});
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

				<!-- 예제 시작 -->
				<h3>Exception Sample</h3>


				<div class="panel panel-default">
					<div class="panel-heading">Exception Resolver</div>
					<div class="panel-body">
						<div class="form-group">
							<a href="javascript:callExceptionResolver();"
								class="btn btn-info">Call</a>
						</div>
					</div>
				</div>

				<div class="panel panel-default">
					<div class="panel-heading">Ajax Exception - try ~ catch</div>
					<div class="panel-body">
						<div class="form-group">
							<a href="javascript:callAjaxException();" class="btn btn-info">Call</a>
						</div>
					</div>
				</div>

				<div class="panel panel-default">
					<div class="panel-heading">LeaveTrace</div>
					<div class="panel-body">
						<div class="form-group">
							<div class="form-group">
								<a href="javascript:callLeaveTrace();" class="btn btn-info">Call</a>
							</div>
							${ltMessage}
						</div>
					</div>
				</div>
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
	<script src="<c:url value='/js/bootstrap/bootstrap.min.js'/>"></script>
</body>
</html>