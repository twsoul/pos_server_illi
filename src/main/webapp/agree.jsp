<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
if(session.getAttribute("USER_SESSION")==null){
	response.sendRedirect("login.jsp");
	return;
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta name="description" content="">
<meta name="author" content="">
<title>H·able</title>
<!-- Bootstrap core CSS -->
<link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<!-- Custom fonts for this template -->
<link href="vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
<link rel="stylesheet"
	href="vendor/simple-line-icons/css/simple-line-icons.css">
<link href="https://fonts.googleapis.com/css?family=Lato"
	rel="stylesheet">
<link
	href="https://fonts.googleapis.com/css?family=Catamaran:100,200,300,400,500,600,700,800,900"
	rel="stylesheet">
<link href="https://fonts.googleapis.com/css?family=Muli"
	rel="stylesheet">

<!-- Plugin CSS -->
<link rel="stylesheet" href="device-mockups/device-mockups.min.css">

<!-- Custom styles for this template -->
<link href="css/new-age.min.css" rel="stylesheet">

<style type="text/css">
.secure_info {
	text-align: left;
	box-sizing: border-box;
	border: 2px solid #fff;
	background: rgba(0, 0, 0, 0.3);
	border-radius: 5px;
	font-size: 1rem;
	padding: 5px;
}

.agree_div {
	position: relative;
	display: block;
}

.agree_div button {
	min-width: 150px;
	position: relative;
	display: inline-block;
	transition: all 0.3s ease;
	box-sizing: border-box;
	border: 1px solid #fff;
	background: rgba(0, 0, 0, 0.5);
	border-radius: 7px;
	padding: 10px 0;
	font-size: 1.5rem;
	color: #fff;
}

.agree_div button:first-child {
	margin-right: 20px;
}

.masthead {
	padding-top: 2rem !important;
	padding-bottom: 30px  !important;
}
</style>
</head>

<body id="page-top">
	<header class="masthead">
		<div class="container h-100">
			<div class="row h-100">
				<div class="col-lg-7 my-auto">
					<div class="header-content mx-auto" style="margin-bottom:0;">
						<h1 class="mb-3">H·able</h1>
						<p class="secure_info mb-5">
						</p>
						<div class="agree_div">
							<button type="submit" id="btn_yes"></button>
							<button type="submit" id="btn_no"></button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</header>

	<!-- Bootstrap core JavaScript -->
	<script src="vendor/jquery/jquery.min.js"></script>
	<script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

	<!-- Plugin JavaScript -->
	<script src="vendor/jquery-easing/jquery.easing.min.js"></script>

	<!-- Custom scripts for this template -->
	<script src="js/new-age.min.js"></script>
	<script src="js/js.storage.min.js"></script>	
    <script src="js/mcore.min.js"></script>	
	<script src="js/agree.js"></script>
</body>

</html>
