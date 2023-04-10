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
<title>table</title>
</head>
<body>
<div class="container">
<div class="row">
<div class="col-12">
<pre>
create table board(
<!-- 
num : 글번호를 저장하는 필드, 기본키이고 자동으로 글번호를 증가시킨다
writer: 글쓴이 저장필드
subject : 글제목을 저장하는필드
content: 글내용을 저장하는 필드
ip 글쓴이의 ip를 저장하는 필드
readcount 글의 조회수를 저장하는 필드
ref: 글을 그룹화 하기 위한 필드
re_step: 제목글과 답변의 순서를 정리하기위한 필드
re_level: 글의 레벨을 저장하는 필드

 -->
num int not null primary key auto_increment,
writer varchar(50)not null,
subject varchar(50)not null,
content text not null,
passwd varchar(60)not null,
reg_date datetime not null,
ip varchar(30)not null,
readcount int default 0,
ref int not null,
re_step smallint not null,
re_level smallint not null
);
</pre>
</div>
</div>
</div>

</body>
</html>