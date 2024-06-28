<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Basic Sample</title>

<!-- Bootstrap core CSS -->
<link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>"
	rel="stylesheet">
<link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">

<!-- jQuery -->
<script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>"></script>

<script>
	function goList() {
		document.detailForm.action = "<c:url value='/cmm/basic/selectItemList.do'/>";
		document.detailForm.submit();

	}

	function goDelete(seq) {
		if (confirm('삭제 하시겠습니까?')) {
			document.detailForm.selectedSeq.value = seq;
			document.detailForm.action = "<c:url value='/cmm/basic/deleteItem.do'/>";
			document.detailForm.submit();
		} else {
			return false;
		}
	}

	function goModify(seq) {
		document.detailForm.selectedSeq.value = seq;
		document.detailForm.action = "<c:url value='/cmm/basic/updateItemForm.do'/>";
		document.detailForm.submit();
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
			<c:set var="menuId" value="2" />
			<%@include file="../include/menu.jsp"%>
			<!--  왼쪽 메뉴 끝 -->
			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
				<h1 class="page-header">조회</h1>
				<form:form commandName="basicSampleVO" id="detailForm"
					name="detailForm">
					<input type="hidden" name="selectedSeq" />
					<div class="table-responsive">
						<table class="table table-bordered" align="center">
							<tr>
								<th>분류</th>
								<td>${result.category}</td>
								<th>작성자</th>
								<td>${result.regUser}</td>
								<th>작성일</th>
								<td>${result.regDate}</td>
							</tr>
							<tr>
								<th>제목</th>
								<td colspan="5">${result.title}</td>
							</tr>
							<tr>
								<th>내용</th>
								<%-- <td colspan="5">${result.description}</td> --%>
								<td colspan="5"><textarea name="content" rows="10" cols="100" style="border:0;" readOnly>${result.description}</textarea></td> 
							</tr>
						</table>
					</div>
				</form:form>
				<div class="panel-group">
					<div class="panel-body" align="right">
						<a href="javascript:goList();" class="btn btn-primary"> 목록 <span
							class="glyphicon glyphicon-home"> </span>
						</a> <a href="javascript:goModify('<c:out value="${result.seq}"/>')"
							class="btn btn-success"> 수정 <span
							class="glyphicon glyphicon-transfer"></span>
						</a> <a href="javascript:goDelete('<c:out value="${result.seq}"/>')"
							class="btn btn-danger"> 삭제 <span
							class="glyphicon glyphicon-trash"></span>
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>