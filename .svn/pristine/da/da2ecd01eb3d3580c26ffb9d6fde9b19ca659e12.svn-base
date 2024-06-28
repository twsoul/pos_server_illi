<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
if(session.getAttribute("AUTH_INFO")==null){
	session.removeAttribute("AUTH_INFO");
}
%>
<!DOCTYPE html>
<html lang="en">

  <head>
<%
	String message = request.getParameter("message");
	if (message != null) {
%>
	<script type="text/javascript">
	<!--
	alert("<%=message%>");
	window.location.replace("login.jsp");
	//-->
	</script>
<%
	}
%>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>H·able</title>
    <!-- Bootstrap core CSS -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom fonts for this template -->
    <link href="vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="vendor/simple-line-icons/css/simple-line-icons.css">
    
    <!-- Plugin CSS -->
    <link rel="stylesheet" href="device-mockups/device-mockups.min.css">
		<link href="css/font.css" rel="stylesheet">
    
    <!-- Custom styles for this template -->
    <link href="css/new-age.min.css" rel="stylesheet">
    <link href="css/jquery.loading.min.css" rel="stylesheet">
    
    
		<style type="text/css">
			*{margin:0; padding:0; line-height:1; vertical-align:top; font-family:"HDharmonyL";}
			ul, ol, li {list-style:none;}
			h1, h2, h3, h4, h5, h6, strong, thead, th {font-weight:normal;}
			form, table, fieldset, legend, thead, tbody, tr, th, td {border:none; border-collapse:collapse; vertical-align:middle;}
			thead, th {font-weight:normal;}em, strong, i, b {font-style:normal; font-weight:normal;}
			a, a:hover {color:#000; text-decoration:none;}
			input, select, button, textarea {width:100%; height:100%; appearance:none; -webkit-appearance:none; -moz-appearance:none; border:none; outline:none; white-space:nowrap;cursor:pointer; background:transparent; overflow:visible; cursor:pointer;}
			textarea {white-space:initial; resize:none;}
			textarea:-ms-input-placeholder {color:#ccc;}
			textarea:-moz-input-placeholder {color:#ccc;}
			textarea::-moz-input-placeholder {color:#ccc;}
			textarea::-webkit-input-placeholder {color:#ccc;}
			textarea:focus:-ms-input-placeholder { color:transparent;}
			textarea:focus:-moz-placeholder { color:transparent;}
			textarea:focus::-moz-placeholder { color:transparent;}
			textarea:focus::-webkit-input-placeholder { color:transparent;}
			input:-ms-input-placeholder {color:#ccc;}
			input:-moz-input-placeholder {color:#ccc;}
			input::-moz-input-placeholder {color:#ccc;}
			input::-webkit-input-placeholder {color:#ccc;}
			input:focus:-ms-input-placeholder { color:transparent;}
			input:focus:-moz-placeholder { color:transparent;}
			input:focus::-moz-placeholder { color:transparent;}
			input:focus::-webkit-input-placeholder { color:transparent;}
			hr {white-space:nowrap; border:none; background:transparent;}
			img {max-width:100%;}

		.blind {width:0; height:0; visibility:hidden; backface-visibility:hidden; white-space:nowrap; position:absolute; z-index:-100; display:none; font-size:0!important; line-height:0;}
		
		.container {max-width:100%; box-sizing:border-box; margin:0 auto; padding:0 4.924vmin;}
		.login_bg {height:100%; min-height:100%;background:url(./img/bg_login.jpg); background-size:cover;}
		section.logIn .container {padding:0 13%;}
		.appTitle > h1, .appTitle > p {margin:auto;}
		.appTitle {text-align:center;}
		.appTitle > p {margin-top:5px;  margin-bottom:20px;}
		.login_area {max-width:100%; box-sizing:border-box; margin:37px  auto 0;}
		.login_area .infotype,
		.login_area .seltype {box-sizing:border-box; border:2px solid #fff; background:rgba(0,0,0,0.3); margin-bottom:1.65vmax;}
		.login_area div {border-radius:7px;}
		.login_area div:nth-child(4) {background:rgba(0,0,0,0.7);text-align:center;margin-top:20px !important;}
		.login_area .infotype span,
		.login_area .seltype span {display:inline-block; vertical-align:middle;}
		.login_area .infotype .apImg_box,
		.login_area .seltype .apImg_box {width:8.95%; text-align:center;  margin:0 0.44% 0.4% 5.5%;}
		.login_area .apImg_box > img {max-width:100%;}
		.login_area .type_box,
		.login_area .sel_box {width:85%; padding:12px 0;}
		.login_area .sel_box select {background:url(./img/bleet_dropDn.png) 90.5% center no-repeat; background-size:1.354vmax;}
		.login_area .type_box input,
		.login_area .sel_box select {font-size:1.25rem; box-sizing:border-box; padding-left:3%; color:#fff;}
		.login_area span.signIn {transition:all 0.3s ease;}
		.login_area span.signIn > button {padding:12px 0; text-transform:uppercase; font-family:"HDharmonyM"; font-size:1.5rem; color:#fff;}
		.login_area span.signIn:hover {}
		</style>
  </head>

  <body id="page-top" class="login_bg">
   <header id="main" class="logIn">
			<section class="logIn">
				<div class="container">
							<div class="appTitle">
						<h1 style="letter-spacing:-0.025rem;text-align:center;font-weight:bold;text-shadow:3px 3px 3px #aaa;"><p style="display:inline-block;vertical-align:baseline;font-size:5rem;font-style:italic;color:#002D72;">H</p><p style="display:inline-block;vertical-align:baseline;font-size:3rem;text-transform:none;font-style:italic;color:#002D72;">·</p><p style="display:inline-block;vertical-align:baseline;font-size:3rem;text-transform:none;color:#0085CA;">able</p></h1>
					</div>
					<form name="frmMain" method="post" onSubmit="return false;">
					<input type="hidden" name="COMMAND" value="" />
					<input type="hidden" name="IS_AGREE" value="" />
					<div class="login_area">
						<div class="seltype" style="display:none !important;">
							<span class="apImg_box"><img class="codeID" id="compIcon" src="./img/HPT.png" alt=""></span><span class="sel_box">
							<select name="complist_" id="comp">
                            <option value="HPT">Korea</option>
                            <option value="PTA">USA</option>
                            <option value="PTM">Mexico</option>
							<option value="PTC">China</option>
						</select></span>
						</div>
						<div class="infotype">
							<span class="apImg_box"><img class="codeID" src="./img/ico_user.png" alt=""></span><!--
							--><span class="type_box"><input class="codeIDKey" type="text" placeholder="Type in ID" id="id" name="id"></span>
						</div>
						<div class="infotype">
							<span class="apImg_box"><img class="codePW" src="./img/ico_password.png" alt=""></span><!--
							--><span class="type_box"><input class="codePWKey" type="password" placeholder="Type in PW" id="pw" value="" name="pw"></span>
						</div>
						<div><span class="signIn blocked"><button type="submit" id="btn_signIn">Sign in</button></span></div>
					</div>
				</div>
				</form>
			</section>
		</header>


    <!-- Bootstrap core JavaScript -->
    <script src="vendor/jquery/jquery.min.js"></script>
    <script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

    <!-- Plugin JavaScript -->
    <script src="vendor/jquery-easing/jquery.easing.min.js"></script>
    <script src="js/jquery.loading.min.js"></script>

    <!-- Custom scripts for this template -->
    <script src="js/new-age.min.js"></script>
    <script src="js/js.storage.min.js"></script>	
    <script src="js/mcore.min.js"></script>	
    <script src="js/login.js"></script>
  </body>

</html>
