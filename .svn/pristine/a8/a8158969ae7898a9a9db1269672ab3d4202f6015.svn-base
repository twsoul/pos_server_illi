<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <title>Property Service Sample</title>
    
    <!-- Bootstrap core CSS -->
    <link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">
    
    <script>
    function goSubmit(){
        location.href="<c:url value='/cmm/prop/refreshProperty.do'/>";
    }    
    </script>
</head>
<body>
    <!-- 상단 메뉴 시작 -->
    <%@include file="../include/nav.jsp" %>
    <!-- 상단 메뉴 끝 -->
    
    <div class="container-fluid">
      <div class="row">
      
        <!--  왼쪽 메뉴 시작 -->
        <c:set var="menuId" value="14"/>
        <%@include file="../include/menu.jsp" %>
        <!--  왼쪽 메뉴 끝 -->
        
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
        	<h1 class="page-header">Property Service Sample</h1>
  			<button id="goRefresh" type="button" class="btn btn-primary" onClick="javascript:goSubmit();">Refresh Properties</button></BR>
  			(context-properties.xml 파일 내에 extFileName 속성에 설정되어 있는 .property 파일의 value를 변경 후 Refresh Properties 버튼을 클릭해보세요.)
  			<h4>pageUnit = ${pageUnit }</h4>
  			<h4>pageSize = ${pageSize }</h4>
  			<h4>AAAA = ${AAAA }</h4>
  			<h4>BBBB = ${BBBB }</h4>
  			<h4>CCCC = ${CCCC }</h4>
        </div>
      </div>
    </div>

</body>
</html>