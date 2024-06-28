<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sample Dashboard</title>
    <!-- Bootstrap core CSS -->
    <link href="<c:url value='/css/bootstrap/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/able/dashboard.css'/>" rel="stylesheet">
    
    <style>
        /* Base structure */
		
		/* Move down content because we have a fixed navbar that is 50px tall */
		body {
		  padding-top: 50px;
		}		
		
		/* Global add-ons */		
		.sub-header {
		  padding-bottom: 10px;
		  border-bottom: 1px solid #eee;
		}
		
		/*
		 * Top navigation
		 * Hide default border to remove 1px line.
		 */
		.navbar-fixed-top {
		  border: 0;
		}
		
		/* Sidebar */
		
		/* Hide for mobile, show later */
		.sidebar {
		  display: none;
		}
		@media (min-width: 768px) {
		  .sidebar {
		    position: fixed;
		    top: 51px;
		    bottom: 0;
		    left: 0;
		    z-index: 1000;
		    display: block;
		    padding: 20px;
		    overflow-x: hidden;
		    overflow-y: auto; /* Scrollable contents if viewport is shorter than content. */
		    background-color: #f5f5f5;
		    border-right: 1px solid #eee;
		  }
		}
		
		/* Sidebar navigation */
		.nav-sidebar {
		  margin-right: -21px; /* 20px padding + 1px border */
		  margin-bottom: 20px;
		  margin-left: -20px;
		}
		.nav-sidebar > li > a {
		  padding-right: 20px;
		  padding-left: 20px;
		}
		.nav-sidebar > .active > a,
		.nav-sidebar > .active > a:hover,
		.nav-sidebar > .active > a:focus {
		  color: #fff;
		  background-color: #428bca;
		}		
		
		/* Main content */		
		.main {
		  padding: 20px;
		}
		@media (min-width: 768px) {
		  .main {
		    padding-right: 40px;
		    padding-left: 40px;
		  }
		}
		.main .page-header {
		  margin-top: 0;
		}		
		
		/* Placeholder dashboard ideas  */		
		.placeholders {
		  margin-bottom: 30px;
		  text-align: center;
		}
		.placeholders h4 {
		  margin-bottom: 0;
		}
		.placeholder {
		  margin-bottom: 20px;
		}
		.placeholder img {
		  display: inline-block;
		  border-radius: 50%;
		}
    
    </style>
</head>
<body>
    <!-- 상단 메뉴 시작 -->
    <%@include file="include/nav.jsp" %>
    <!-- 상단 메뉴 끝 -->

    <div class="container-fluid">
      <div class="row">
        <!--  왼쪽 메뉴 시작 -->
        <c:set var="menuId" value="1"/>
        <%@include file="include/menu.jsp" %>
        <!--  왼쪽 메뉴 끝 -->
        
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
          <h2 class="sub-header">Sample List</h2>
          <div class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Sample Title</th>
                  <th>Description</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>1</td>
                  <td>Basic Sample</td>
                  <td>기본 CRUD 샘플 (SAMPLE 테이블의 목록 조회/등록/수정/삭제)</td>
                </tr>
                <tr>
                  <td>2</td>
                  <td>Board</td>
                  <td>게시판 샘플 (SAMPLE 테이블을 데이타로 목록조회/등록/수정/삭제 및 페이징, 파일업/다운로드)</td>
                </tr>
                <tr>
                  <td>3</td>
                  <td>MessageSource</td>
                  <td>resource>message>*.properties 파일로 관리하는 메시지와 DB로 관리하는 메시지에 대해 변경 적용 샘플로 화면의 데이터는 ABLE_MSG 테이블의 데이타를 조회함.
                     화면에서 데이터 변경 시 ABLE_MSG 테이블의 데이타가 변경되며 context-common.xml 파일의 설정에 의해 refresh 됨.</td>
                </tr>
                <tr>
                  <td>4</td>
                  <td>Validation</td>
                  <td>사용자가 입력한 값에 대한 유효성을 체크하기 위해 JSR-303 Validator을 이용한 샘플. PhoneNumber은 Custom Validator로 구성하여 적용.</td>
                </tr>
                <tr>
                  <td>5</td>
                  <td>MyBatis</td>
                  <td>SAMPLE 테이블 데이타에 대한 목록조회/상세조회/등록/수정/삭제 기능이 있으며, 목록을 제외하고 나머지 기능들은 AJAX 콜을 통한 JSONVIEW 를 사용하였음</td>
                </tr>
                <tr>
                  <td>6</td>
                  <td>Excel</td>
                  <td>EXCEL_TEST 테이블의 데이타를 기준으로 엑셀파일로 다운로드 받거나 엑셀파일을 업로드해서 데이타를 입력한다. 엑셀 다운로드(MAP)은 테이블의 데이터를 MAP에 담아 파일로 만듦.(파일다운로드 모듈 연동)
                  엑셀 다운로드(VO)은 데이터를 VO에 담아 엑셀파일로 만듦.(파일다운로드 모듈 미연동)</td>
                </tr>
                <tr>
                  <td>7</td>
                  <td>File Up/Down</td>
                  <td>파일 Upload/Download의 기능이며, Multi 파일 처리가 가능한 기능 샘플임</td>
                </tr>
                <tr>
                  <td>8</td>
                  <td>File Up/Down (Min)</td>
                  <td>단순히 하나의 파일에 대해 Upload/Download의 기능임</td>
                </tr>
                <tr>
                  <td>9</td>
                  <td>File Handling</td>
                  <td>서버 내의 파일을 복사하거나 삭제하거나 하는 기능 샘플임</td>
                </tr>
                <tr>
                  <td>10</td>
                  <td>Web Security</td>
                  <td>XSS, SQL Injection 처리의 실습자료이며, 공통영역에서 처리하기 때문에 테스트 용으로 사용한 샘플임</td>
                </tr>
                <tr>
                  <td>11</td>
                  <td>Session</td>
                  <td>Session 처리에 대한 샘플임</td>
                </tr>
                <tr>
                  <td>12</td>
                  <td>Encryption&Decryption</td>
                  <td>암호화/복호화 처리에 대한 심플임</td>
                </tr>                
                <tr>
                  <td>13</td>
                  <td>Property</td>
                  <td>context-properties.xml 파일의 entry 정보를 조회한다. 외부파일(resource>properties>resource.properties)을 사용하는 방식 추가</td>
                </tr>
                <tr>
                  <td>14</td>
                  <td>Compress&Decompress</td>
                  <td>파일업로드 모듈을 사용하여 파일을 업로드하고 업로드한 파일을 압축함. 압축 풀 파일을 업로드하여 서버 내에 파일을 품.</td>
                </tr>
                <tr>
                  <td>15</td>
                  <td>Transaction</td>
                  <td>TRANS 테이블을 기준으로 Transaction 처리 샘플 구현. 세건의 데이타를 등록하는 로직이며, 두건 등록 후 에러를 발생 시켜 롤백처리하는 샘플 구현</td>
                </tr>
                <tr>
                  <td>16</td>
                  <td>Exception</td>
                  <td>에러 처리에 대한 표준을 적용한 샘플임</td>
                </tr>
                <tr>
                  <td>17</td>
                  <td>String Util</td>
                  <td></td>
                </tr>
                <tr>
                  <td>18</td>
                  <td>AOP(Annotation)</td>
                  <td>@AspectJ 어노테이션 기반의 AOP를 구성하는 로직이며, 메세지 값을 입력 후 실행하면 정의된 Advice와 입력메세지 순차표시.</td>
                </tr>
                <tr>
                  <td>19</td>
                  <td>AOP(XML)</td>
                  <td>XML 스키마 기반의 AOP를 구성하는 로직이며, 메세지 값을 입력 후 실행하면 정의된 Advice와 입력메세지 순차표시.</td>
                </tr>
                <tr>
                  <td>20</td>
                  <td>Cache</td>
                  <td>Cache 기능으로 응답속도를 개선하는 로직이며, 최초 실행 시 데이터가 Cache 처리됨.</td>
                </tr>
                <tr>
                  <td>21</td>
                  <td>Spring Scheduling</td>
                  <td></td>
                </tr>
                <tr>
                  <td>22</td>
                  <td>Quartz</td>
                  <td></td>
                </tr>
                <tr>
                  <td>23</td>
                  <td>REST</td>
                  <td></td>
                </tr>                
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/sample/js/jquery/jquery-2.1.4.min.js"></script>
    <script src="/sample/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>