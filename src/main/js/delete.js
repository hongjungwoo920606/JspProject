 var wStatus = true;

$(function(){
	//글삭제 폼의 삭제버튼을 클릭하면 자동실행
	$("#delete").click(function(){
		formCheckIt();
		if(wStatus){
			//삭제버튼의 값으로 지정된 현재페이지의 번호를 얻어냄
			var pageNum = $("#delete").val();
			var query = {passwd:$("#passwd").val(),
						num:$("#num").val()	
						};
						
						$.ajax({
							type:"POST",
							url: "deletePro.jsp",
							data:query,
							success: function(data){
								if(data==1){
									//글삭제처리에 성공한경우
									alert("글이 삭제되었습니다");
									var query = "list.jsp?pageNum="+pageNum;
									$("#main_board").load(query);
								}else{
									//글삭제처리에 실패한경우
									alert("비밀번호가 틀립니다");
									$("#passwd").val("");
									$("#passwd").focus();
									
								}
							}
						});
		}
	});
	
	//글삭제폼의 삭제버튼을 클릭하면 자동실행 list.jsp페이지를 표시
	$("#cancel").click(function(){
		var pageNum = $("#cancel").val();
		var query = "list.jsp?pageNum="+pageNum;
		$("#main_board").load(query);
	});
	
	//글삭제폼의 비밀번호 입력유무를 확인
	function formCheckIt(){
		wStatus = true;
		if(!$.trim($("#passwd").val())){
			alert("비밀번호를 입력하세요");
			$("#passwd").focus();
			wStatus = false;
			return false;
		}
	}





	
});