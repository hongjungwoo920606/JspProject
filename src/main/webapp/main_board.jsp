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
<title>main_board</title>
</head>
<body>
<%
String id = "";
try{
	id=(String)session.getAttribute("id");
%>
<%if(id==null || id.equals("")){ %>


<div class="container">
	<div class="row">
		<div class="col-md-12 text-center">
			<h3>로그인 하세요! 게시판은 회원만 볼 수 있습니다.</h3>
		</div>
		<%}else{ %>
		<div class="col-md-12">
			<jsp:include page="list.jsp"/>
		</div>
		<%}}catch(Exception e){e.printStackTrace();} %>
	</div>
</div>


</body>
</html>