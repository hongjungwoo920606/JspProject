<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import=ajax.* %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="js/delete.js"></script>
<title>deleteForm</title>
</head>
<body>
<%request.setCharacterEncoding("utf-8"); %>
<%
//수정할 글의 번호와 수정할 글의 위치한 페이지 번호를 얻어냄
int num = Integer.parseInt(request.getParameter("num"));
String pageNum = request.getParameter("pageNum");

%>
<div class="container">
	<div class="row">
		<div class="col-md-12" id="deleteForm">
		<h1>글삭제</h1>
			<div class="input-group">
				<label class="input-group-text" for="passwd">
				비밀번호
				</label>
					
				<input 
				id="passwd" 
				name="passwd" 
				type="password" 
				placeholder="6~16자 숫자/문자"
				maxlength="16" 
				class="form-control"
				
				>
				<input 
				type="hidden"
				id="num"
				value="<%=num %>"
				>
			</div>
			
			
			<div class="d-flex justify-content-end mt-3">
				<div class="btn-group">
				<button id="delete" class="btn btn-success" value="<%=pageNum%>">삭제</button>
				<button id="cancel" class="btn btn-warning" value="<%=pageNum%>">취소</button>
				</div>
			</div>
			
		</div>
	</div>
</div>

</body>
</html>