<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE HTML>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <title>SlickGrid example 1: Basic grid</title>
  <link rel="stylesheet" href="<c:url value='/css/slick/slick.grid.css'/>"/>
  <link rel="stylesheet" href="<c:url value='/css/slick/smoothness/jquery-ui-1.8.16.custom.css'/>"/>
  <link rel="stylesheet" href="<c:url value='/css/slick/examples.css'/>"/>
</head>
<body>
<table width="100%">
  <tr>
    <td valign="top" width="50%">
      <div id="myGrid" style="width:600px;height:500px;"></div>
    </td>
  </tr>
</table>

<script src="<c:url value='/js/jquery/jquery-2.1.4.min.js'/>"></script>
<script src="<c:url value='/js/jquery/jquery.event.drag/jquery.event.drag.min.js'/>"></script>

<script src="<c:url value='/js/slick/slick.core.js'/>"></script>
<script src="<c:url value='/js/slick/slick.grid.js'/>"></script>

<script>
  var grid;
  var columns = [
    {id: "title", name: "Title", field: "title"},
    {id: "duration", name: "Duration", field: "duration"},
    {id: "%", name: "% Complete", field: "complete"},
    {id: "start", name: "Start", field: "start"},
    {id: "finish", name: "Finish", field: "finish"},
    {id: "effort-driven", name: "Effort Driven", field: "effort"}
  ];
  var options = {
    enableCellNavigation: true,
    enableColumnReorder: false
  };
  
  $(function () {
	  $.ajax({
          type : "GET",
          contentType : "application/json",
          url : "<c:url value='/test/grid/selectSlickGrid.do'/>",
          //data : JSON.stringify(data),
          dataType : 'json',
          timeout : 100000,
          success : function(data) {
              console.log("SUCCESS: ", data);
              grid = new Slick.Grid("#myGrid", data.result, columns, options);
          },
          error : function(e) {
              console.log("ERROR: ", e);
          },
          done : function(e) {
              console.log("DONE");
          }
      });	  
  })
</script>
</body>
</html>