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

	function goUpdate() {
		document.detailForm.action = "<c:url value='/cmm/board/updateItem.do'/>";
		document.detailForm.submit();
	}
	
	function fileDelete(fileId) {
		if (confirm('삭제 하시겠습니까?')) {
			$.ajax({
				type: "GET",
				dataType: "json",
				url: '<c:url value="/cmm/board/fileDelete.do?selectedFileId='+fileId+'" />', 		 		
				success :function(data) {
					$('#'+data.fileId).empty();
				},
				error : function(xhr, status, error) {
	                alert("에러발생");
	            }
			});	
			
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
				<h1 class="page-header">수정</h1>
				<form:form commandName="boardSampleVO" id="detailForm"
					name="detailForm" class="form-horizontal" role="form"
					enctype="multipart/form-data">
					<form:input type="hidden" path="artId"/>
					<input type="hidden" name="selectedFileId"/>
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
										class="form-control" type="text" /> <form:errors
										path="artSubject" /></td>
							</tr>
							<tr>
								<td class="col-md-1 text-center active"
									style="vertical-align: middle">내용</td>
								<td class="col-md-11"><form:textarea path="artContent"
										rows="10" class="form-control" type="text" /> <form:errors
										path="artContent" /></td>
							</tr>
							<tr>
								<td class="col-md-1 text-center active"
									style="vertical-align: middle">작성자</td>
								<td class="col-md-11"><form:input path="regUser"
										class="form-control" type="text" readonly="true" /> <form:errors
										path="regUser" /></td>
							</tr>
							<tr>
								<td class="col-md-1 text-center active"
									style="vertical-align: middle">첨부파일</td>
								<td class="col-md-11">
									<table>
										<c:forEach var="fileVO" items="${fileList}">
											<tr id="${fileVO.fileId}">
												<td>${fileVO.originalFileName}&nbsp;</td>
												<td><a onclick="fileDelete('${fileVO.fileId}')"><span
														class="glyphicon glyphicon-trash"></span></a></td>
											</tr>
										</c:forEach>
									</table> <span id="btn-file" class="btn btn-success"> <span
										class="glyphicon glyphicon-plus" aria-hidden="true"></span> 찾기
										<span class="badge" id="file-badge"></span>
								</span> <input type="file" name="files" id="input-file" multiple
									style="display: none" />
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
						</a> <a href="javascript:goUpdate();" class="btn btn-success">
							수정 <span class="glyphicon glyphicon-floppy-saved"> </span>
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>