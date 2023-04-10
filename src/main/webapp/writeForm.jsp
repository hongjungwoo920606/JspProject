<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="js/write.js"></script>
<title>writeForm</title>
</head>
<body>
<% request.setCharacterEncoding("utf-8"); %>
<%
//제목글의 경우 갖는 값
int num = 0, ref=1, re_step=0, re_level=0;
int pageNum = 1;

try{
	//댓글의 경우 갖는 값
	if(request.getParameter("num") !=null){//댓글
		//제목글의 글번호, 그룹화 번호, 그룹화 내의 순서, 들여쓰기 정도가 list.jsp에서 넘어옴
		num = Integer.parseInt(request.getParameter("num"));
		ref = Integer.parseInt(request.getParameter("ref"));
		re_step = Integer.parseInt(request.getParameter("re_step"));
		re_level = Integer.parseInt(request.getParameter("re_level"));
		pageNum = Integer.parseInt(request.getParameter("pageNum")); 
		
	}


%>
<input type="hidden" id="num" value="<%=num%>">
<input type="hidden" id="ref" value="<%=ref%>">
<input type="hidden" id="re_step" value="<%=re_step%>">
<input type="hidden" id="re_level" value="<%=re_level%>">
<div class="container">
	<div class="row">
		<div class="col-12" id="writeForm">
			<div class="input-group">
				<label class="input-group-text" for="subject">
					제목
				</label>
				<%if(num != 0){//댓글 %>
					<img src="">
				<%}%>
				<input id="subject" name="subject" type="text" placeholder="제목"
				maxlength="50" class="form-control">
			</div>
			
			<div class="input-group my-3">
				<label class="input-group-text" for="content">
					내용
				</label>			
				<textarea id="content" name="content" rows="13" cols="" class="form-control" placeholder="내용" maxlength="50"></textarea>
			</div>
		</div>
	</div>
</div>
<%}catch(Exception e){} %>
</body>
</html>