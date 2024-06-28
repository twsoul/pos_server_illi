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
	function goView(seq) {
		document.listForm.selectedSeq.value = seq;
		document.listForm.action = "<c:url value='/cmm/basic/selectItemById.do'/>";
		document.listForm.submit();
	}

	function goRegister() {
		document.listForm.action = "<c:url value='/cmm/basic/insertItemForm.do'/>";
		document.listForm.method = "get";
		document.listForm.submit();
	}

	function goSearch() {
		document.listForm.action = "<c:url value='/cmm/basic/selectItemList.do'/>";
		document.listForm.submit();
	}
</script>
</head>
<body>
	<!-- 상단 메뉴 시작
	<c:out value="${menuId }" /> -->
	<%@include file="../include/nav.jsp"%>
	<!-- 상단 메뉴 끝 -->

	<div class="container-fluid">
		<div class="row">
			<!--  왼쪽 메뉴 시작 -->
			<c:set var="menuId" value="2" />
			<%@include file="../include/menu.jsp"%>
			<!--  왼쪽 메뉴 끝 -->
			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
				<h1 class="page-header">Basic Sample</h1>
				<form:form commandName="basicSampleVO" id="listForm" name="listForm"
					method="post">
					<div class="panel-group">
						<div class="panel-body" align="right">
							<form:select path="searchCondition">
								<form:option value="2" label="작성자" />
								<form:option value="1" label="제목" />
								<form:option value="0" label="분류" />
							</form:select>
							<form:input path="searchKeyword" />
							<a href="javascript:goSearch();" class="btn btn-success"> 검색
								<span class="glyphicon glyphicon-search"> </span>
							</a> <a href="javascript:goRegister();" class="btn btn-primary">
								등록 <span class="glyphicon glyphicon-pencil"> </span>
							</a>
						</div>
					</div>
					<input type="hidden" name="selectedSeq" />
					<div class="table-responsive">
						<table class="table table-hover" align="center">
							<thead>
								<tr>
									<th>번호</th>
									<th>분류</th>
									<th>제목</th>
									<th>작성자</th>
									<th>작성일</th>
									<th>조회수</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${empty resultList}">
										<tr>
											<td colspan="6" align="center">검색된 결과가 없습니다.</td>
										</tr>
									</c:when>
									<c:otherwise>
										<c:forEach var="result" items="${resultList}"
											varStatus="status">
											<tr onclick="javascript:goView('${result.seq }')">
												<td><c:out value="${result.seq }" /></td>
												<td><c:out value="${result.category }" /></td>
												<td><c:out value="${result.title }" /></td>
												<td><c:out value="${result.regUser }" /></td>
												<td><c:out value="${result.regDate }" /></td>
												<td><c:out value="${result.viewCount }" /></td>
											</tr>
										</c:forEach>

									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
				</form:form>
			</div>
		</div>
	</div>
</body>
</html>