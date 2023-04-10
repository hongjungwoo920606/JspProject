<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import ="ajax.*" %>  
<%request.setCharacterEncoding("utf-8"); %>  
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
  
<title>updatePro</title>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="col-12">
		<!-- BoardDataBean 클래스의 객체 아티클 생성: 향후 이객체에 접근시 -->
		<jsp:useBean id="article" scope="page" class=ajax.BoardDataBean>
			<jsp:setProperty name="article" property="*"/>
		
		</jsp:useBean>
		
		<%
		BoardDBBean dbPro = BoardDBBean.getInstance();
		//글수정처리후 결과를 check변수에 저장
		int check = dbPro.updateArticle(article);
		
		//이페이지를 호출한 update.js로 처리결과값 check를 리턴
		out.println(check);
		%>
		</div>
	</div>
</div>

</body>
</html>