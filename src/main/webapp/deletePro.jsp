<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="ajax.*" %>
<% request.setCharacterEncoding("utf-8"); %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
<title>deletePro</title>
</head>
<body>
<div class="container">
<div class="row">
<div class="col-md-12">
<%
int num = Integer.parseInt(request.getParameter("num"));
String passwd = request.getParameter("passwd");

BoardDBBean dbPro = BoardDBBean.getInstance();
//글삭제 처리후 결과를 check변수에 저장
int check = dbPro.deleteArticle(num, passwd);
//이 페이지를 호출한 delete.js로 처리결과값 check리턴
out.println(check);

%>
</div>
</div>
</div>

</body>
</html>