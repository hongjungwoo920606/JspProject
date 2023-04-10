<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.3/jquery.min.js"></script>
<title>main</title>
</head>
<body>

<div class="container">
	<div class="row">
		<div class="col-md-6">
			<img class="img-thumbnail img-fluid" src="img/main_img.png" >
		</div>
		
		<div class="col-md-6">
			<jsp:include page="loginForm.jsp"/>
		</div>
	</div>
	
	<div class="row">
		<div class="col-md-12">
			<jsp:include page="main_board.jsp"/>
		</div>
	</div>
</div>

</body>
</html>