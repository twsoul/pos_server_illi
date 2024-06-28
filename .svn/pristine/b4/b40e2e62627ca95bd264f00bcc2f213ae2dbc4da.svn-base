<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Cache Form</title>
<!-- Bootstrap core CSS -->
<link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>" rel="stylesheet">
<link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">

<!-- jQuery -->
<script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>"></script>

<script>
	function goUpdate() {
		document.detailForm.description.value = "";
		document.detailForm.action = "<c:url value='/cmm/cache/CacheSampleInsert.do'/>";
		document.detailForm.submit();
	}
	
	function goDelete() {
		document.detailForm.description.value = "";
		document.detailForm.action = "<c:url value='/cmm/cache/CacheSampleDelete.do'/>";
		document.detailForm.submit();
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
			<c:set var="menuId" value="21" />
			<%@include file="../include/menu.jsp"%>
			<!--  왼쪽 메뉴 끝 -->
			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
				<h1 class="page-header">Cache Message</h1>
				<form:form commandName="cacheSampleVO" id="detailForm"
					name="detailForm" class="form-horizontal" role="form" >
					<div class="form-group">
						<label class="col-sm-1 control-label">출력<font
							class="text-danger"></font></label>
						<div class="col-sm-5">
							<form:textarea path="description" rows="7" cols="60"
								class="form-control" type="text" />
							<form:errors path="description" />	
						</div>
					</div>
				</form:form>
				<div class="panel-group">
					<div class="panel-body" align="right">
						<a href="javascript:goUpdate();" class="btn btn-success">실행
							 <span class="glyphicon glyphicon-floppy-saved"> </span>
						</a> 
						<a href="javascript:goDelete();" class="btn btn-danger">삭제
							 <span class="glyphicon glyphicon-trash"> </span>
						</a> 
					</div>
				</div>
			</div>
		</div>
	</div>
	</div>
</body>
</html>