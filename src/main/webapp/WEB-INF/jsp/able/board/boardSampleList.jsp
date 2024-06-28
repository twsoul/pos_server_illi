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
	function goView(id) {
		document.listForm.selectedId.value = id;
		document.listForm.action = "<c:url value='/cmm/board/selectItemById.do'/>";
		document.listForm.submit();
	}

	function goRegister() {
		document.listForm.action = "<c:url value='/cmm/board/insertItemForm.do'/>";
		document.listForm.method = "get";
		document.listForm.submit();
	}

	function goSearch() {
		document.listForm.action = "<c:url value='/cmm/board/selectItemList.do'/>";
		document.listForm.submit();
	}
	
	function jsPage(page) {
        document.listForm.currPage.value = page;
        document.listForm.action =  "<c:url value='/cmm/board/selectItemList.do'/>";
        document.listForm.submit();
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
				<form:form commandName="boardSampleVO" id="listForm" name="listForm"
					method="post">
					<h1 class="page-header">Board Sample</h1>
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
					<input type="hidden" name="selectedId" />
					<input type="hidden" name="currPage" value=""/>  
					<div class="table-responsive">
						<table class="table table-bordered table-hover" align="center">
							<thead>
								<tr class="info">
									<th class="col-md-1 text-center">번호</th>
									<th class="col-md-1 text-center">분류</th>
									<th class="col-md-7 text-center">제목</th>
									<th class="col-md-1 text-center">첨부파일</th>
									<th class="col-md-1 text-center">작성자</th>
									<th class="col-md-1 text-center">작성일</th>
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
											<%-- <tr onclick="javascript:goView('${result.artId}')"> --%>
											<tr onclick="javascript:goView('${result.artId}')">
												<td class="text-center"><c:out value="${result.artId}" /></td>
												<td class="text-center"><c:out value="${result.artCategory}" /></td>
												<td class="text-left"><c:out value="${result.artSubject}" /></td>
												<td class="text-center">
													<c:choose>
														<c:when test="${result.attachYn == 'Y'}">
															<span class="glyphicon glyphicon-paperclip" />												
														</c:when>
													</c:choose>
												</td>
												<td class="text-center"><c:out value="${result.regUser}" /></td>
												<td class="text-center"><c:out value="${result.regDate}" /></td>
											</tr>
										</c:forEach>

									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
					</div>
				</form:form>
				<div align="center">
					<able:pagination paginationInfo="${page}" type="image" jsFunction="jsPage"/>
		        </div>
			</div>
		</div>
	</div>
</body>
</html>