<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import ="ajax.*" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.text.SimpleDateFormat" %>
<% request.setCharacterEncoding("utf-8"); %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="js/list.js"></script>
<title></title>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="col-md-12">
			<%
			String id="";
			int pageSize = 3; //한페이지에 표시할 글 수 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//날짜 데이터 표시형식지정
			
			String pageNum = request.getParameter("pageNum");//화면에 표시할 페이지 번호
			if(pageNum == null)//페이지 번호가 없으면 1페이지의 내용이 화면에 표시
			pageNum = "1";
			
			int count =0; //전체글의 수
			int currentPage = Integer.parseInt(pageNum);//숫자로 파싱
			
			List<BoardDataBean> articleList = null; //글목록 저장
			BoardDBBean dbPro = BoardDBBean.getInstance();
			count= dbPro.getArticleCount();//전체글수를 얻어냄
			
			if(count == (currentPage-1) * pageSize)
				currentPage -=1;
			int startRow = (currentPage - 1) * pageSize + 1;
			
			try{
				if(count > 0)//테이블에 저장된 글이 있으면 테이블에서 글 목록을 가져옴
				articleList = dbPro.getArticles(startRow, pageSize);
				
				if(articleList.isEmpty())//테이블에 저장된 글이 없으면 전체글수 0
				count =0;
			}catch(Exception e){}
			%>
			<div id="list_head">
				<h3>글목록(전체 글: <%= count %>)</h3>
				<button class="btn btn-primary" id="new">글쓰기</button>
			</div>
			
			<%if(count==0){//게시판에 글이 없는경우 %>
			<div id="list_article">
			<h3>게시판에 저장된 글이 없습니다</h3>
			</div>
			<%}else{ %>
			<div id="list_article">
			<%
			//글목록을 반복처리
			for(int i=0; i< articleList.size(); i++){
				BoardDataBean article = articleList.get(i);
			
			%>
			<ul>
				<li class="layout_f">
					<%
					String writer = article.getWriter();
					out.println(writer.substring(0,4)+"****");
					//이 문자열의 하위 문자열인 새문자열을 리턴, 하위문자열은 지정된 인덱스에 잇는 문자로 시작하여
					//이 문자열의 끝까지 확장된다 원래문자열은 변경되지 않은 상태로 유지된다
					%>
				</li>
				<li class="layout_f">
					<%
					int wid=0;
					if(article.getRe_level()>0){
						wid=5*(article.getRe_level());
					
					%>
					<img src="" width="<%=wid %>">
					<%}else{ %>
					<img src="" width="<%=wid %>">
					<%}%>
					<%
					int num = article.getNum();
					int ref = article.getRef();
					int re_step = article.getRe_step();
					int re_level = article.getRe_level();
					%>
					<%=article.getSubject()%>
					<p class="date">
						<%=sdf.format(article.getReg_date()) %><br>
						<pre><%=article.getContent() %></pre><br>
						<%try{
							id=(String)session.getAttribute("id");	
						}catch(Exception e){}%>
						<%if(id.equals(writer)){ %>
						<button id="edit" name="<%=num+","+pageNum%>" onclick="edit(this)"
						class="btn btn-success">수정</button>
						<button id="delete" name="<%=num+","+pageNum%>" onclick="del(this)"
						class="btn btn-warning">삭제</button>
						<%}else{%>
						<button id="reply" name="<%=num+","+ref+","+re_step+","+re_level+","+pageNum%>" onclick="reply(this)"
						class="btn btn-success">댓글쓰기</button>
						<%}%>
					</p>
				</li>	
			</ul>
			<%} %>
			</div>
			<%}%>
			<div id="jump">
			<%
			if(count > 0){
				int pageCount = count/pageSize + (count % pageSize ==0? 0: 1);
				//if(count % pageSize ==0 )return 0; else return 1
				int startPage = 1;
				
				if(currentPage % pageSize !=0)
					startPage = (int)(currentPage/pageSize) * pageSize +1;
				else
					startPage = ((int)(currentPage/pageSize)-1) * pageSize +1;
				
				int pageBlock = 3; //페이지들의 블럭단위 지정
				int endPage = startPage + pageBlock-1;
				
				if(endPage > pageCount) endPage = pageCount;
				
				if(startPage > pageBlock){%>
				<button id="jup" name="<%=startPage - pageBlock%>" onclick="p(this)" class="">
				이전
				</button>
			<% }
			for(int i= startPage; i<=endPage; i++){
				if(currentPage ==i){%>
				<button id="ju" name="<%=i%>" onclick="p(this)" class="">
				<%=i%>
				</button>
			 <%}else{%>
			 	<button id="ju" name="<%=i%>" onclick="p(this)" class="">
				<%=i%>
				</button>
			<%}%>
			<%}if(endPage < pageCount){ %>
				<button id="juN" name="<%=startPage + pageBlock%>" onclick="p(this)" class="">
				다음
				</button>
			<%
			}
			}
			%>
			</div>
			
		</div>
	</div>
</div>

</body>
</html>
<!-- 
페이징 처리
총 데이터가 37개의 글이 있다
한페이지당 리스트 갯수: 4 
37/4 = 9.25 10페이지가 나온다

페이지 그룹은 몇개로 묶을건지 정한다
[1][2][3] [4][5][6] [7][8][9] [10]
페이지 그룹 넘버
-1 -2 -3 -4

페이지그룹 넘버값으로 페이지바의 첫번째 페이지 계산
numPageGroup = 올림(currentPage/pageGroupSize)
5페이지가 호출된다고 가정하면
numPageGroup = 올림(5/3) = 2 

startPage = (numPageGroup -1) * pageGroupSize + 1;
=(2-1)*3+1 = 4

마지막 페이지는
endPage = startPage + pageGroupSize-1 => 4+3-1=6

그런데 마지막 페이지그룹을 위공식대로 계산하면 endpage 12가 나옴

if( 마지막페이지>총페이지)
마지막페이지 = 총페이지 => endpage =10

 -->




