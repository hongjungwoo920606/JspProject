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
  <script src="js/login.js"></script>
<title>login</title>
</head>
<body>
<%
String id="";
try{
	//id세션속성의 값을 얻어내어 id변수에 저장 인증된 사용자의 경우 id세션의 속성값이 null또는 공백이 아님
	id= (String)session.getAttribute("id");
%>
<%
if(id == null || id.equals("")){
	//인증되지 않은 사용자의 영역
	
%>
<div class="container">
	<div class="row">
		<div class="col-md-12">
			<div class="input-group my-3">
				<label class="input-group-text" for="id">아이디</label>	
				<input type="email" id="id" name="id" maxlength="50" placeholder="enter your email"
				class="form-control">
			</div>
			
			<div class="input-group my-3">
				<label class="input-group-text" for="passwd">패스워드</label>	
				<input type="password" id="passwd" name="passwd" maxlength="16" placeholder="6~16자리 숫자/문자"
				class="form-control">
			</div>
			
			<div class="d-flex justify-content-end my-3">
				<button class="btn btn-primary" id="login">로그인</button>				
			</div>
		</div>
		<%
		}else{//인증된 사용자영역
		%>
		<div class="col-md-12 bg-primary text-white rounded">
			<h1><%=id %><small>님이 로그인 하셨습니다.</small></h1>
			<div class="d-flex justify-content-end my-3">
				<button class="btn btn-primary" id="logout">로그아웃</button>				
			</div>
		</div>
	</div>
</div>

<%
}}catch(Exception e){e.printStackTrace();}
%>
</body>
</html>