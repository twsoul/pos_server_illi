<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Validation Form</title>
<!-- Bootstrap core CSS -->
<link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>" rel="stylesheet">
<link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">

<!-- jQuery -->
<script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>"></script>

<script>
	function goUpdate() {
		document.detailForm.action = "<c:url value='/cmm/valid/insertItem.do'/>";
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
			<c:set var="menuId" value="5" />
			<%@include file="../include/menu.jsp"%>
			<!--  왼쪽 메뉴 끝 -->
			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
				<h1 class="page-header">Validation</h1>
				<form:form commandName="validationSampleVO" id="detailForm"
					name="detailForm" class="form-horizontal" role="form" >
					<div class="form-group">
						<label class="col-sm-1 control-label">코드<font
							class="text-danger">*</font></label>
						<div class="col-sm-5">
							<form:input path="seq" class="form-control" type="text" />
							<form:errors path="seq" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-1 control-label">제목<font
							class="text-danger">*</font></label>
						<div class="col-sm-5">
							<form:input path="title" class="form-control" type="text" />
							<form:errors path="title" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-1 control-label">내용<font
							class="text-danger">*</font></label>
						<div class="col-sm-5">
							<form:textarea path="description" rows="5" cols="60"
								class="form-control" type="text" />
							<form:errors path="description" />	
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-1 control-label">작성자<font
							class="text-danger">*</font></label>
						<div class="col-sm-5">
							<form:input path="regUser" class="form-control" type="text"  />
							<form:errors path="regUser" />	
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-1 control-label">이메일<font
							class="text-danger"></font></label>
						<div class="col-sm-2">
							<form:input path="emailAddr" class="form-control"  type="text"  />
							<form:errors path="emailAddr" />
						</div>
						<font class="pull-left">@</font>
						<div class="form-horizontal .checkbox-inline col-sm-2">
							<form:select path="emailDomain" class="form-control"  type="text" >
							    <form:option value="" label="선택" />
								<form:option value="naver.com" label="naver.com" />
								<form:option value="google.com" label="google.com" />
							</form:select>
							<form:errors path="emailDomain" />	
						</div>
					</div>		
					<div class="form-group">
						<label class="col-sm-1 control-label">나이<font
							class="text-danger"></font></label>
						<div class="col-sm-5">
							<form:input path="age" class="form-control" type="text" />
							<form:errors path="age" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-1 control-label">생년월일<font
							class="text-danger"></font></label>
						<div class="col-sm-5">
							<form:input path="birthDate" class="form-control" type="text" />
							<form:errors path="birthDate" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-1 control-label">휴대전화<font
							class="text-danger"></font></label>
						<div class="col-sm-5">
							<form:input path="phoneNumber" class="form-control" type="text" />
							<form:errors path="phoneNumber" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-1 control-label">등록일<font
							class="text-danger"></font></label>
						<div class="col-sm-5">
							<form:input path="regDate" class="form-control" type="text" />
							<form:errors path="regDate" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-1 control-label">선택동의<font
							class="text-danger"></font></label>
						<div class="col-sm-5">
							수신동의 <form:radiobutton path="approvalYN" name="approvalYN" value="1"/>
							수신거부 <form:radiobutton path="approvalYN" name="approvalYN" value="0"/>							
							<form:errors  path="approvalYN" />
						</div>
					</div>
				</form:form>
				<div class="panel-group">
					<div class="panel-body" align="right">
						<a href="javascript:goUpdate();" class="btn btn-success"> 저장
							 <span class="glyphicon glyphicon-floppy-saved"> </span>
						</a> 
					</div>
				</div>
			</div>
		</div>
	</div>
	</div>
</body>
</html>