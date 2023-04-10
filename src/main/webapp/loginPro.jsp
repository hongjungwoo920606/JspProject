<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import = "logon.LogonDBBean" %>
<% request.setCharacterEncoding("utf-8"); %>    
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.3/jquery.min.js"></script>
<title></title>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="col-md-12">
		<%
		//사용자가 입력한 아이디,비밀번호
		String id = request.getParameter("id");
		String passwd = request.getParameter("passwd");
		LogonDBBean manager = LogonDBBean.getInstance();
		int check = manager.userCheck(id,passwd);
		//사용자 인증에 성공시 세션속성을 설정
		if(check == 1)
			session.setAttribute("id", id);
		out.println(check);
		%>
		</div>
	</div>
</div>

</body>
</html>