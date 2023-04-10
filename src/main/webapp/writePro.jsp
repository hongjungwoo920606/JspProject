<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import = "ajax.BoardDBBean" %>    
<%@ page import = "ajax.BoardDataBean" %>
<%@ page import = "java.sql.Timestamp" %>

<% request.setCharacterEncoding("utf-8"); %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
<title></title>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="col-md-12">
		<!-- 글쓰기폼에 입력한 값을 가지고 BoardDataBean클래스 객체 article 생성 -->
		<jsp:useBean id="article" scope="page" class="ajax.BoardDataBean">
			<jsp:setProperty name="article" property="*"/>						
		</jsp:useBean>
		<%
		String id= "";
		try{
			id=(String)session.getAttribute("id");//세션에서 얻어낸 사용자 아이디
		}catch(Exception e){e.printStackTrace();}
		
		//폼으로 부터 넘어오지 않는 값을 데이터 저장빈 BoardDataBean객체 article에 직접저장
		article.setWriter("id");
		article.setReg_date(new Timestamp(System.currentTimeMillis()));
		article.setIp(request.getRemoteAddr());
		
		//db처리빈의 객체를 얻어냄
		BoardDBBean dbPro = BoardDBBean.getInstance();
		//db처리빈인 BoardDBBean클래스의 insertArticle()메소드를 호출해서 레코드추가
		//이때 추가될 레코드내용 article을 매개변수로 가짐
		//이메소드의 처리결과는 check변수에 저장
		int check = dbPro.insertArticle(article);
		
		//이페이지를 호출한 write.js로 처리결과값 check를 리턴
		out.println(check);				
		%>
								
		</div>
	</div>
</div>

</body>
</html>