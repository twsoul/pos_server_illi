<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <title>Transaction Sample</title>
    
    <!-- Bootstrap core CSS -->
    <link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">
    
    <!-- jQuery -->
    <script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>" ></script>
    
    <script>    
    $(document).ready(function(){
        $("#goInsertSuccess").click(function(){
        	$( "#inputForm" ).attr({action:"<c:url value='/cmm/trans/insertTrans.do'/>"}).submit();
        });
        
		$("#goInsertRollback").click(function(){
			$( "#inputForm" ).attr({action:"<c:url value='/cmm/trans/insertTransRollback.do'/>"}).submit();
        });
		$("#goInsertNoRollback").click(function(){
			$( "#inputForm" ).attr({action:"<c:url value='/cmm/trans/insertTransNoRollback.do'/>"}).submit();
		});
        
    });
    
    </script>
</head>
<body>
    <!-- 상단 메뉴 시작 -->
        <c:out value="${menuId }"/>
    <%@include file="../include/nav.jsp" %>
    <!-- 상단 메뉴 끝 -->
    
    <div class="container-fluid">
      <div class="row">
      
        <!--  왼쪽 메뉴 시작 -->
        <c:set var="menuId" value="16"/>
        <%@include file="../include/menu.jsp" %>
        <!--  왼쪽 메뉴 끝 -->
        
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
          <h1 class="page-header">Transaction Sample</h1>               

<form id="inputForm" name="inputForm" method="post">
          <div class="panel-group">
            <div class="panel panel-info">
              <div class="panel-heading"> TRANS 테이블에 여러건의 데이터를 INSERT 하는 예제</div>
              
              <div class="panel-body">
                  <div class="input-group">
					  <span class="input-group-addon" id="addon1">ID</span>
					  <input id="ids1" name="transIds" type="text" class="form-control" placeholder="ID" aria-describedby="addon1">
					  <span class="input-group-addon" id="names">NAME</span>
					  <input id="names1" name="transNames" type="text" class="form-control" placeholder="NAME" aria-describedby="addon2">
					  <span class="input-group-addon" id="descs">Desc.</span>
					  <input id="descs1" name="transDescs" type="text" class="form-control" placeholder="Description" aria-describedby="addon3">					  
				  </div>
              </div>
              <div class="panel-body">
                  <div class="input-group">
					  <span class="input-group-addon" id="addon4">ID</span>
					  <input id="ids2" name="transIds" type="text" class="form-control" placeholder="ID" aria-describedby="addon4">
					  <span class="input-group-addon" id="addon5">NAME</span>
					  <input id="names2" name="transNames" type="text" class="form-control" placeholder="NAME" aria-describedby="addon5">
					  <span class="input-group-addon" id="addon6">Desc.</span>
					  <input id="descs2" name="transDescs" type="text" class="form-control" placeholder="Description" aria-describedby="addon6">					  
				  </div>
              </div>
              <div class="panel-body">
                  <div class="input-group">
					  <span class="input-group-addon" id="addon7">ID</span>
					  <input id="ids3" name="transIds" type="text" class="form-control" placeholder="ID" aria-describedby="addon7">
					  <span class="input-group-addon" id="addon8">NAME</span>
					  <input id="names3" name="transNames" type="text" class="form-control" placeholder="NAME" aria-describedby="addon8">
					  <span class="input-group-addon" id="addon9">Desc.</span>
					  <input id="descs3" name="transDescs" type="text" class="form-control" placeholder="Description" aria-describedby="addon9">					  
				  </div>
              </div>
              <div class="panel-body" align="left">
                  <button id="goInsertSuccess" type="button" class="btn btn-primary">정상등록</button>
                  <button id="goInsertRollback" type="button" class="btn btn-primary" onClick="javascript:goInsertRollback();">두건 정상등록 후 에러 발생 (rollback 처리)</button>
                  <button id="goInsertNoRollback" type="button" class="btn btn-primary" onClick="javascript:goInsertNoRollback();">두건 정상등록 후 에러 발생 (rollback 미처리)</button>
              </div>
            </div>
            ※ Multi Database 연결 샘플은 able-sample-transaction project 참조.
          </div>
</form>  
			
			<div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>         
                  <th>ID</th>
                  <th>NAME</th>
                  <th>DESCRIPTION</th>
                </tr>
              </thead>
              <tbody>
              <c:forEach var="result" items="${resultList}" varStatus="status">
                <tr>
                  <td><c:out value="${result.transId }"/></td>
                  <td><c:out value="${result.transName }"/></td>
                  <td><c:out value="${result.transDesc }"/></td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
          
        </div>
      </div>
    </div>

</body>
</html>