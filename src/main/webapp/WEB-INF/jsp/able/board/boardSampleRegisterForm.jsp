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

<script type="text/javascript">
	$(document).ready(function(){
		//btn-file 
		$('#btn-file').click(function(){
			$('#input-file').click()
			});
		//업로드 파일 리스트
		$('#input-file').change(function(){
			if(this.files){
				$('#file-list').empty();
				var ul = $('#file-list').get(0);
				
				var length = this.files.length;
				for(var i = 0; i<length ; i++){
					//console.log(i);
					//$("#file-list").text(this.files[i].name);
					var li = document.createElement('li');
   					li.appendChild(document.createTextNode(this.files[i].name));
    				ul.appendChild(li);
				}
			}
		});
		//업로드 파일 개수 뱃지
		$('#input-file').change(function(){
			if(this.files){
				 var badge = $('#file-badge').get(0)
				 if(badge){
					 badge.innerHTML = this.files.length; 
				 }
			}
		});

	});
	function goList() {
		document.detailForm.action = "<c:url value='/cmm/board/selectItemList.do'/>";
		document.detailForm.submit();

	}

	function goRegister() {
		document.detailForm.action = "<c:url value='/cmm/board/insertItem.do'/>";
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
			<c:set var="menuId" value="3" />
			<%@include file="../include/menu.jsp"%>
			<!--  왼쪽 메뉴 끝 -->

			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
				<h1 class="page-header">등록</h1>
				<form:form commandName="boardSampleVO" id="detailForm"
					name="detailForm" class="form-horizontal" role="form" enctype="multipart/form-data">
					<div class="table-responsive">
						<table class="table table-bordered" align="center">
							<tr>
								<td class="col-md-1 text-center active"
									style="vertical-align: middle">분류</td>
								<td class="col-md-11"><form:select path="artCategory"
										class="form-control">
										<form:option value="전체공지" label="전체공지" />
										<form:option value="부분공지" label="부분공지" />
									</form:select></td>
							</tr>
							<tr>
								<td class="col-md-1 text-center active"
									style="vertical-align: middle">제목</td>
								<td class="col-md-11"><form:input path="artSubject" 
										class="form-control" type="text" placeholder="제목을 입력하세요." />
									<form:errors path="artSubject" /></td>
							</tr>
							<tr>
								<td class="col-md-1 text-center active"
									style="vertical-align: middle">내용</td>
								<td class="col-md-11"><form:textarea path="artContent"
										rows="10" class="form-control" type="text"
										placeholder="내용을 입력하세요." /> <form:errors path="artContent" />
								</td>
							</tr>
							<tr> 
								<td class="col-md-1 text-center active"
									style="vertical-align: middle">작성자</td>
								<td class="col-md-11"><form:input path="regUser"
										class="form-control" type="text" placeholder="작성자를 입력하세요." />
									<form:errors path="regUser" /></td>
							</tr>
							<tr>
								<td class="col-md-1 text-center active"
									style="vertical-align: middle">첨부파일</td>
								<td class="col-md-11">
									<span id="btn-file" class="btn btn-success"> 
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										찾기
									<span class="badge" id="file-badge"></span>
									</span>
									<input type="file" name="files" id="input-file" multiple style="display:none"/>
									<ul id="file-list"></ul>
								</td>
							</tr>
						</table>
					</div>
				</form:form>
				<div class="panel-group">
					<div class="panel-body" align="right">
						<a href="javascript:goList();" class="btn btn-primary"> 목록 <span
							class="glyphicon glyphicon-home"> </span>
						</a> <a href="javascript:goRegister();" class="btn btn-success">
							등록 <span class="glyphicon glyphicon-floppy-saved"> </span>
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>