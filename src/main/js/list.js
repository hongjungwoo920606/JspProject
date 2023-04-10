$(function(){
	//글쓰기버튼을 클릭하면 자동실행 : 제목글쓰기
	//main.jsp의 main_board영역에 writeForm.jsp표시
	$("#new").click(function(){
		$("#main_board").load("writeForm.jsp");	
	});
});

//글수정 버튼을 클릭하면 자동실행
function edit(editBtn){
	//수정할 글의 정보가 [글수정] 버튼인 editBtn에 name속성에 지정
	var rStr = editBtn.name;
	//이벤트가 발생한 버튼의 속성값을 얻어내 rStr변수에 저장하고
	var arr = rStr.split(",");
	//여기서 rStr변수에 수정할 글번호와 수정할글이 위치한 페이지 번호가 쉼표로 연결되어있다
	/*
	rStr.split(","): "2,1"과 같은 형태의 값을 가진 rStr변수에 split메소드를 사용하여 분할하는
	것으로 이때 중심으로 분할해  arr에 저장한다
	첫번째 값은 arr[0] 두번째 값은 arr[1]에 저장된다
	arr[0]은 수정할 글번호를 arr[1]은 수정할 글이 위치한 페이지번호가 들어간다
	*/
	//글번호와 페이지 번호를 갖고 updateForm.jsp페이지 로드
	var query = "updateForm.jsp?num="+arr[0];
	query += "&pageNum="+arr[1];
	$("main_board").load(query);
}
//글 삭제 버튼을 클릭하면 자동실행 main.jsp의 main_board영역에 글 삭제 폼 표시
function del(delBtn){
	var rStr = delBtn.name;
	var arr = rStr.split(",");
	//글번호와 페이지 번호를 갖고 deleteForm.jsp페이지 로드
	var query = "deleteForm.jsp?num="+arr[0];
	query += "&pageNum="+arr[1];
	$("main_board").load(query);
}
//댓글쓰기 버튼을 클릭하면 자동실행 main.jsp의 main_board영역에 글쓰기 폼 표시
/* 
rStr에 "3,3,0,0,1"과 같이 제목글의 글번호, 그룹화아이디, 그룹내 순서,들여쓰기 값, 제목글의
페이지 번호를 저장한다 따라서
arr[0]: 제목글의 글번호
arr[1]: 그룹화아이디
arr[2]: 그룹내 순서
arr[3]: 들여쓰기
arr[4]: 제목글의 페이지번호
*/


function reply(replyBtn){
	var rStr = replyBtn.name;
	var arr = rStr.split(",");
	//댓글쓰기에 필요한 정보를 갖고 writeForm.jsp페이지 로드
	var query = "writeForm.jsp?num="+arr[0]+"&ref="+arr[1];
	query += "&re_step="+arr[2]+"&re_level="+arr[3]+"&pageNum="+arr[4];
	$("main_board").load(query);
}
//페이지 이동버튼을 누르면 자동으로 실행 main.jsp의 main_board영역에 해당페이지의 글목록 표시
function p(jumpBtn){
	var rStr = jumpBtn.name;
	var query = "list.jsp?pageNum="+rStr;
	$("#main_board").load(query);
}

