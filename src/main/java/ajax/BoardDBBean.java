package ajax;

/*
 getInstance: 전역 BoardDBBean 객체의 레퍼런스를 리턴
 getConnection: 쿼리작업에 사용할 Connection 객체를 커넥션 풀로 얻어냄
 
 insertArticle(BoardDatabean article)
 새로운 글을 board테이블에 추가한다 글입력처리
 
 getArticleCount(): 
 board테이블의 전체레코드수를 받아온다, 글목록에서 글번호 및 전체레코드수를 표시할때 사용
 
 getArticle(int start, int end):
 시작부터 끝까지의 개수만큼 레코드를 board테이블에서 검색한다
 글목록 보기에서 사용함
 
 updateGetArticle(int num):
 id에 해당하는 레코드를 board테이블에서 검색한다 글 수정폼에서 사용
 
 updateArticle(BoardDataBean article):
 수정된 글의 내용을 갱신할때 사용한다 글 수정처리에서 사용
 
 deleteArticle(int num, String passwd):
 id에 해당하는 레코드를 board테이블에서 삭제한다. 글삭제처리에 사용한다
 
 */

/*sql작업을 수행하기때문에 java.sql패키지에 있는 아래의 클래스들을 임포트한다*/
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/*java.naming패키지는 커넥션풀에 네이밍 작업에 필요한 InitialContext클래스와
 Context인터페이스 javax.sql패키지는 커넥션풀에 네이밍작업 및 커넥션객체를 얻어내는데
 필요한 DataSource인터페이스를 사용하기 위해 import 받았다*/
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDBBean {
	private static BoardDBBean instance = new BoardDBBean();
	//클래스는 기본 뼈대이고 instance는 구체적인 "상태(변수)"와 행위(기능)의 집합이라고 생각하면된다.
	//서로다른상태를 가지게 하기위해서 
	
	public static BoardDBBean getInstance() {//이메소드를 호출한곳으로 LogonDBBean객체를 리턴
		return instance;
		
	}
	//jsp페이지에서 db연동빈인 BoardDBBean 클래스의 메소드에 접근시 필요
	private BoardDBBean() {}
	
	//커넥션풀로부터 커넥션 객체를 얻어냄 DB연동빈의 쿼리문을 수행하는 메서드에서 사용
	private Connection getConnection() throws Exception{
		Context initCtx = new InitialContext();
		Context envCtx = (Context)initCtx.lookup("java:comp/env");
		DataSource ds = (DataSource)envCtx.lookup("jdbc/jsptest");
		return ds.getConnection();
		/*dbcp api커넥션 풀을 사용해서 커넥션객체를 할당받는다
		 getConnection()리턴타입은 Connection
		  */
	}
	//board테이블에 글을추가(insert문) => writePro.jsp에서 사용
	public int insertArticle(BoardDataBean article) {//한개의 파라미터, article
		//article은 writeForm에서 입력한 글의 내용을 가지고 있다
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int x = 0;
		int number = 0; //board테이블에 들어갈 글 번호
		String sql = "";
		
		//아래 코드는 댓글이 가진정보
		int num = article.getNum(); //제목글의 번호
		/*원래 글번호는 board테이블에서 새로운글이 추가될 경우 자동으로 증가해서 생성되고
		 article.getNum과 같이 jsp페이지에서 넘어온 객체로부터 얻어내는 글번호는 댓글을 갖는
		 제목글의 글번호일 경우 발생한다
		  */
		int ref = article.getRef(); //제목글의 그룹화 아이디
		/*board테이블과 같이 관계형데이터베이스에서는 제목글과 댓글형태의 계층형을 
		 표현할수없다 다만 화면에 글을 표시할때 계층형태를 보여주기 위해서
		 그룹화라는 걸 사용한다 보통 그룹화 아이디값은 제목글의 글번호를 사용한다
		 */
		//댓글이 최신글이더라도 제목글 위에 올수없기때문에 이런 순서를 지정하기위해서 필요
		int re_step = article.getRe_step(); //그룹내의 글의 순서
		
		int re_level = article.getRe_level(); //글 제목의 들여쓰기
		//제목글은 들여쓰기가 0이고 댓글은 1이다
		
		try {
			conn = getConnection();
			//현재 board테이블에 레코드의 유무판단과 글 번호 결정
			pstmt = conn.prepareStatement("select max(num)from board");
			rs = pstmt.executeQuery();
			
			if(rs.next())//기존에 레코드가 존재
				number = rs.getInt(1) + 1; //다음 글번호는 가장 큰 글번호 + 1
			else
				number= 1;
			
			//제목글과 댓글간의 순서를 결정
			if(num != 0) { //댓글 - 제목글의 글번호 가짐
				sql = "update board set re_step=re_step+1 where ref=? and re_step>?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, ref);
				pstmt.setInt(2, re_step);
				pstmt.executeUpdate();
				re_step = re_step + 1;
				re_level = re_level + 1;
				
			}else {
				//제목글 - 글번호 없음
				ref = number;
				re_step = 0;
				re_level = 0;				
			}
			//쿼리를 작성: board테이블에 새로운 레코드 추가
			sql = "insert into board(writer,subject,content,passwd,reg_date,";
			sql +="ip,ref,re_step,re_level) values(?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, article.getWriter());
			pstmt.setString(2, article.getSubject());
			pstmt.setString(3, article.getContent());
			pstmt.setString(4, article.getPasswd());
			pstmt.setTimestamp(5, article.getReg_date());
			pstmt.setString(6, article.getIp());
			pstmt.setInt(7, ref);
			pstmt.setInt(8, re_step);
			pstmt.setInt(9, re_level);
			pstmt.executeUpdate();
			x=1;//레코드추가성공
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			if(rs!=null)try {rs.close();}catch(SQLException ex) {}
			if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {}
			if(conn!=null)try {conn.close();}catch(SQLException ex) {}
		}
		return x;
		
		
	}
	//board테이블에 저장된 전체글의 수를 얻어냄 => list.jsp에서 사용
	public int getArticleCount() {
		Connection conn =null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int x=0;		
		try {
			conn = getConnection();
			pstmt= conn.prepareStatement("select count(*)from board");
			rs = pstmt.executeQuery();
			
			if(rs.next())
				x = rs.getInt(1);
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			if(rs!=null)try {rs.close();}catch(SQLException ex) {}
			if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {}
			if(conn!=null)try {conn.close();}catch(SQLException ex) {}
		}
		return x;
	}
	//글의 목록을 가져옴 list.jsp
	public List<BoardDataBean> getArticles(int start, int end){
		//시작 글번호[start]부터 추출한 레코드 수 만큼 [end]의 레코드를 검색하는 메소드
		//이 메소드는 게시판의 글목록을 보는 부분에서 사용되며 list.jsp페이지에서 호출해 사용
		//이게시판시스템에서 list.jsp는 글목록과 글 내용보기가 결합된것이다
		Connection conn =null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardDataBean> articleList = null; //글목록을 저장하는 객체
		/*글목록을 저장하는 컬렉션이며 List객체 타입으로 레퍼런스 변수 articleList선언
		 List는 인터페이스로 객체를 직접생성할수 없다. 따라서 
		 articleList = new ArrayList<BoardDataBean>(end); 과 같이 
		 하위클래스인 ArrayList타입의 객체를 생성해서 사용한다
		 해당컬렉션이 저장할 객체의 타입과 같은 형태로 기술해주는것이 제너릭이라고 한다
		 예를들어 List<BoardDataBean>, ArrayList<BoardDataBean>과 같이
		 컬렉션에 지정할 객체타입을 지정하면 나중에 객체를 꺼내서 사용할때 형변환을 할 필요가없어 편해진다
		  */
		try {
			conn= getConnection();
			pstmt = conn.prepareStatement("select * from board order by desc, re_step asc limit?,?");
			//limit?,? 는 검색하는 레코드의 수를 제한하기위해 사용하는 것으로 리미트에서 파라미터가 둘인 경우 사용법은 다음과 같다
			/*limit 시작레코드 번호, 검색할 레코드의 개수
			 mysql에서 레코드의 번호는 0부터 시작하는데 jsp페이지에서 글은 1부터 시작한다
			 따라서 list.jsp에서 사용할때보다 시작번호가 하나가 적어야한다  
			 */
			pstmt.setInt(1, start-1);
			pstmt.setInt(2, end);
			rs= pstmt.executeQuery();
			
			if(rs.next()) {
				articleList = new ArrayList<BoardDataBean>(end);
				/*end변수의 값만큼 객체를 저장할 수 있는 ArrayList객체를 생성한다*/
				do {
					BoardDataBean article = new BoardDataBean();
					article.setNum(rs.getInt("num"));
					article.setWriter(rs.getString("writer"));
					article.setSubject(rs.getString("subject"));
					article.setSubject(rs.getString("content"));
					article.setPasswd(rs.getString("passwd"));
					article.setReg_date(rs.getTimestamp("reg_date"));
					article.setReadcount(rs.getInt("readcount"));
					article.setRef(rs.getInt("ref"));
					article.setRe_step(rs.getInt("re_step"));
					article.setRe_level(rs.getInt("re_level"));
					article.setContent(rs.getString("content"));
					article.setIp(rs.getString("ip"));
					articleList.add(article);
				}while(rs.next());
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			
		}finally {
			if(rs!=null)try {rs.close();}catch(SQLException ex) {}
			if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {}
			if(conn!=null)try {conn.close();}catch(SQLException ex) {}
		}
		return articleList; //List객체의 레퍼런스를 리턴
		
	}
	//글수정 폼에서 사용할 글의 내용(1개의 글) => updateFrom.jsp에서 사용
	public BoardDataBean updateGetArticle(int num) {
		/*updateGetArticle메소드는 num에 헤당하는 메소드만 검색하는 메소드이다
		 게시판에서 그 내용을 수정하는 폼부분에서 사용되는 것으로 updateForm.jsp에서 호출되어 사용한다
		  */
		Connection conn =null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardDataBean article = null;
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("select * from board where num=?");
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				article = new BoardDataBean();
				article.setNum(rs.getInt("num"));
				article.setWriter(rs.getString("writer"));
				article.setSubject(rs.getString("subject"));
				article.setSubject(rs.getString("content"));
				article.setPasswd(rs.getString("passwd"));
				article.setReg_date(rs.getTimestamp("reg_date"));
				article.setReadcount(rs.getInt("readcount"));
				article.setRef(rs.getInt("ref"));
				article.setRe_step(rs.getInt("re_step"));
				article.setRe_level(rs.getInt("re_level"));
				article.setContent(rs.getString("content"));
				article.setIp(rs.getString("ip"));
				
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			if(rs!=null)try {rs.close();}catch(SQLException ex) {}
			if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {}
			if(conn!=null)try {conn.close();}catch(SQLException ex) {}
		}
		return article;
	}
	
	//글수정처리에 사용=> updatePro.jsp에서 사용
	public int updateArticle(BoardDataBean article) {
		
		Connection conn =null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int x = -1;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("select passwd from board where num =?");
			pstmt.setInt(1, article.getNum());
			rs = pstmt.executeQuery();
			
			if(rs.next()) {//글수정을 위해 패스워드를 가져옴
				String dbpasswd = rs.getString("passwd");
				if(dbpasswd.equals(article.getPasswd())) {
					String sql = "update board set subject =?,";
					sql +="content=? where num=?";
					pstmt.setString(1, article.getSubject());
					pstmt.setString(2, article.getContent());
					pstmt.setInt(3, article.getNum());
					pstmt.executeUpdate();
					x= 1;
					/*패스워드 일치여부에 따라 맞으면 수정하고 1, 틀리면 0*/
				}else
					x=0;
			}
		}catch(Exception ex) {
				ex.printStackTrace();
			}finally {
				if(rs!=null)try {rs.close();}catch(SQLException ex) {}
				if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {}
				if(conn!=null)try {conn.close();}catch(SQLException ex) {}
			}
			return x;
		}
		//글삭제 처리시 사용(delete문) => deletePro.jsp에서 사용
		public int deleteArticle(int num, String passwd) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			int x = -1;
			try {
				conn= getConnection();
				pstmt = conn.prepareStatement("select passwd from board where num =?");
				pstmt.setInt(1, num);
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					String dbpasswd = rs.getString("passwd");
					if(dbpasswd.equals(passwd)) {
						pstmt = conn.prepareStatement("delete from board where num=?");
						pstmt.setInt(1, num);
						pstmt.executeUpdate();
						x =1; //글삭제성공
					}else//비밀번호틀림
						x=0;
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}finally {
				if(rs!=null)try {rs.close();}catch(SQLException ex) {}
				if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {}
				if(conn!=null)try {conn.close();}catch(SQLException ex) {}
			}
			return x;
		}
		
		
		
	}
	
	
	
	
	
	













