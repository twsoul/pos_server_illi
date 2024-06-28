<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Board Sample</title>

<!-- Bootstrap core CSS -->
<link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>"
	rel="stylesheet">
<link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">

<!-- jQuery -->
<script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>"></script>

<script>
	$(document).ready(function() {
		$('#cmtRegUser').val('');
		$('#cmtContent').val('');
	});
	
	function goList() {
		document.detailForm.action = "<c:url value='/cmm/board/selectItemList.do'/>";
		document.detailForm.submit();

	}

	function goModify() {
		document.detailForm.action = "<c:url value='/cmm/board/updateItemForm.do'/>";
		document.detailForm.submit();
	}
	
	function goDelete() {
		if (confirm('삭제 하시겠습니까?')) {
			document.detailForm.action = "<c:url value='/cmm/board/deleteItem.do'/>";
			document.detailForm.submit();
		} else {
			return false;
		}
	}
	
</script>
</head>
<body>
	<!-- 상단 메뉴 시작 
	<c:out value="${menuId }" />-->
	<%@include file="../include/nav.jsp"%>
	<!-- 상단 메뉴 끝 -->

	<div class="container-fluid">
		<div class="row">

			<!--  왼쪽 메뉴 시작 -->
			<c:set var="menuId" value="3" />
			<%@include file="../include/menu.jsp"%>
			<!--  왼쪽 메뉴 끝 -->

			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
				<h1 class="page-header">조회</h1>
				<form:form commandName="boardSampleVO" id="detailForm"
					name="detailForm">
					<form:input type="hidden" path="artId" name="artId"/>
					<input type="hidden" name="selectedCmtId" />
					<div class="table-responsive">
						<table class="table table-bordered" align="center">
							<tr>
								<td class="col-md-1 text-center active">분류</td>
								<td class="col-md-11">
									<form:input path="artCategory" class="form-control" type="text" readonly="true"/>
								</td>
							</tr>
							<tr>
								<td class="col-md-1 text-center active">제목</td>
								<td class="col-md-11">
									<form:input path="artSubject" class="form-control" type="text" readonly="true"/>
								</td>
							</tr>
							<tr>
								<td class="col-md-1 text-center active">내용</td>
								<td class="col-md-11">
									<form:textarea path="artContent" class="form-control" type="text" rows="10" readonly="true"/>
								</td>
							</tr>
							<tr>
								<td class="col-md-1 text-center active">작성자</td>
								<td class="col-md-11">
									<form:input path="regUser" class="form-control" type="text" readonly="true"/>
								</td>
							</tr>
							<tr>
								<td class="col-md-1 text-center active">첨부파일</td>
								<td class="col-md-11">
									<table>
										<c:forEach var="fileVO" items="${fileList}">
											<tr>
												<td>${fileVO.originalFileName}&nbsp;</td>
												<td><a
													href='<c:url value="/cmm/board/fileDownload.do?fileId=${fileVO.fileId}"/>'>
														<span class="glyphicon glyphicon-download-alt"></span>
												</a></td>
											</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
						</table>
					</div>
					<div class="panel-group">
						<div class="panel-body" align="right">
							<a href="javascript:goList();" class="btn btn-primary"> 목록 <span
								class="glyphicon glyphicon-home"> </span>
							</a> <a
								href="javascript:goModify('<c:out value="${result.artId}"/>');"
								class="btn btn-success"> 수정 <span
								class="glyphicon glyphicon-floppy-saved"> </span>
							</a>
							<a href="javascript:goDelete('<c:out value="${result.artId}"/>')" class="btn btn-danger">
								삭제 <span class="glyphicon glyphicon-trash"></span>
							</a>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</body>
</html>