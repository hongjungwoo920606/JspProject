package logon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/*
LogonDBBean클래스에서 SQL작업을 수행하기 때문에 java.sql패키지에 있는 Connection,
PreparedStatement,ResultSet 인터페이스와 
SQLException클래스를 임포트 한다
*/

import javax.naming.Context;
import javax.naming.InitialContext;//네이밍 조작을 실행하기 위한 개시 컨텍스트
import javax.sql.DataSource;
/*
javax.naming.패키지는 커넥션풀의 네이밍작업에서 필요해서 InitialContext클래스와 
Context인터페이스를 import받는다
*/

import work.crypt.SHA256;
//KISA의 SHA256 암호화를 위해
import work.crypt.BCrypt;
//BCrypt암호화를 사용하기 위해 import를 받음 위에둘은 회원계정의 비밀번호를 암호화 한다

public class LogonDBBean {
	
	//LogonDBBean전역객체 생성
	
	private static LogonDBBean instance = new LogonDBBean();
	//얘처럼 객체를 생성하면 이는 객체간의 전역객체가 된다 한번만 생성되고 객체들간에 공유가 된다
	
	//LogonDBBean객체를 리턴하는 메소드
	public static LogonDBBean getInstance() {
		return instance;
	}
	private LogonDBBean() {}
	
	//커넥션 풀에서 커넥션 객체를 얻어내는 매소드
	private Connection getConnection() throws Exception{
		Context initCtx = new InitialContext();
		Context envCtx = (Context)initCtx.lookup("java:comp/env");
		/*리소스 이름앞에 붙는 접두사라고 보시면 됩니다
		모든 설정된 엔트리와 자원은 JNDI 네임스페이스에 java:comp/env 에
		놓이게 되고 자원에 대해 접근하려면 lookup(java:comp/env)를 통해 
		로컬리소스에 접근합니다
		*/
		DataSource ds = (DataSource)envCtx.lookup("jdbc/jsptest");
		return ds.getConnection();
	}
	
	//회원가입 처리(registerPro.jsp)에서 사용하는 새레코드 추가 메소드
	public void insertMember(LogonDataBean member) {
		Connection conn = null;
		PreparedStatement pstmt=null;
		
		SHA256 sha = SHA256.getInstance();//암호화를 사용하기 위해서 클래스의 객체를 얻는다
		try {
		conn = getConnection();
		//메소드를 호출해서 커넥션 객체를 conn레퍼런스에 넘겨준다
		String orgPass = member.getPasswd();
		String shaPass = sha.getSha256(orgPass.getBytes());
		//사용자가 입력한 비밀번호 문자열을 바이트배열 byte[]로 변환
		
		String bcPass = BCrypt.hashpw(shaPass, BCrypt.gensalt());
		//바이트된 배열로 암호화된 비밀번호가 shaPass변수에 저장된다
		pstmt = conn.prepareStatement(
		//맴버테이블 추가하는 부분으로 insert문 사용
		"insert into member values(?,?,?,?,?,?)");
		//getXXX메소드에 접근해서 보관된 프로퍼티값을 가져온다
		//pstmt.는 레코드를 삽입하기 위해 위치홀더(?)를 실제값으로 대치하는 부분입니다
		pstmt.setString(1, member.getId());
		pstmt.setString(2, bcPass);
		pstmt.setString(3, member.getName());
		pstmt.setTimestamp(4, member.getReg_date());
		pstmt.setString(5, member.getAddress());
		pstmt.setString(6, member.getTel());
		pstmt.executeUpdate();
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			if(pstmt !=null) try {pstmt.close();}catch(SQLException ex) {}
			if(conn !=null) try {conn.close();}catch(SQLException ex) {}
			//커넥션 풀에 사용한 커넥션 객체를 반환한다
		}
	}
	
	
	//로그인 폼처리(loginPro.jsp)페이지의 사용자 인증처리 및 회원정보 수정 / 탈퇴를 
	//사용자 인증(memberCheck.jsp)에서 사용하는 메소드
	//두개의 파라미터(사용자가 입력한  id를 , 사용자가 입력한 비밀번호를)
	public int userCheck(String id, String passwd) {
		Connection conn = null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		int x= -1;
		
		SHA256 sha = SHA256.getInstance();
		try {
			conn = getConnection();
			String orgPass=passwd;
			String shaPass=sha.getSha256(orgPass.getBytes());
			
			pstmt=conn.prepareStatement("select passwd from member where id=?");
		    pstmt.setString(1, id);
		    rs=pstmt.executeQuery();
		    
		    if(rs.next()) {//해당 아이디가 있으면 수행
		    	String dbpasswd= rs.getString("passwd");
		    	if(BCrypt.checkpw(shaPass,dbpasswd))
		    	x=1;//인증 성공
		    	else
		    	x=0;//비밀번호 틀림
		    	
		    }else//해당 아이디 없으면 수행
		    	x=-1;//아이디없음
		    	
		    }catch(Exception ex) {
		    	ex.printStackTrace();
		
		}finally {
			if(rs !=null) try {rs.close();}catch(SQLException ex) {}
			if(pstmt !=null) try {pstmt.close();}catch(SQLException ex) {}
			if(conn !=null) try {conn.close();}catch(SQLException ex) {}
	}
		return x;
	}
	
	/*아이디 중복확인(confirmId.jsp)을 하는 메소드*/
	public int confirmId(String id) {
		Connection conn = null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		int x=-1;
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("select id from member where id =?");
			pstmt.setString(1, id);
			rs=pstmt.executeQuery();
			
			if(rs.next())//아이디 존재
				x=1; //같은 아이디 있음
			else
				x=-1;//같은 아이디 없음
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			if(rs !=null) try {rs.close();}catch(SQLException ex) {}
			if(pstmt !=null) try {pstmt.close();}catch(SQLException ex) {}
			if(conn !=null) try {conn.close();}catch(SQLException ex) {}
		}
		return x;
	}
	
	/*회원정보 수정폼(modifyForm.jsp)을 위한 기존 가입정보를 가져오는 메소드*/
	public LogonDataBean getMember(String id, String passwd) {
		Connection conn = null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		LogonDataBean member =null;
		
		SHA256 sha = SHA256.getInstance();
		try {
			conn = getConnection();
			String orgPass = passwd;
			String shaPass = sha.getSha256(orgPass.getBytes());
			
			pstmt = conn.prepareStatement("select * from member where id = ?");
			pstmt.setString(1, id);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {//해당아이디에 대한 레코드가 존재
				String dbpasswd = rs.getString("passwd");
				//사용자가 입력한 비밀번호와 테이블의 비밀번호가 같으면 수행
				if(BCrypt.checkpw(shaPass,dbpasswd)) {
					member = new LogonDataBean();//데이터 저장빈 객체 생성
					member.setId(rs.getString("id"));
					member.setName(rs.getString("name"));
					member.setReg_date(rs.getTimestamp("reg_date"));
					member.setAddress(rs.getString("addr"));
					member.setTel(rs.getString("tel"));
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			if(rs !=null) try {rs.close();}catch(SQLException ex) {}
			if(pstmt !=null) try {pstmt.close();}catch(SQLException ex) {}
			if(conn !=null) try {conn.close();}catch(SQLException ex) {}
		}
		return member;//데이터 저장빈 객체 member리턴
	}
	
	//회원정보 수정처리에서(modifyPro.jsp)에서 회원정보 수정을 처리하는 메소드
	public int updateMember(LogonDataBean member) {
		Connection conn = null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		int x=-1;
		SHA256 sha = SHA256.getInstance();
		try {
			conn = getConnection();
			String orgPass = member.getPasswd();
			String shaPass = sha.getSha256(orgPass.getBytes());
			
			pstmt = conn.prepareStatement("select passwd from member where id = ?");
			pstmt.setString(1, member.getId());
			rs=pstmt.executeQuery();
			
			if(rs.next()) {//해당 아이디가 있으면 수행
				String dbpasswd=rs.getString("passwd");
				if(BCrypt.checkpw(shaPass,dbpasswd)) {
					pstmt = conn.prepareStatement("update member set name=?,address=?,tel=?"+"where id=?");
					pstmt.setString(1, member.getName());
					pstmt.setString(2, member.getAddress());
					pstmt.setString(3, member.getTel());
					pstmt.setString(4, member.getId());
					pstmt.executeUpdate();
					x=1;//회원정보 수정처리 성공
				}else
				x=0;//회원정보 수정처리 실패
			}
			}catch(Exception ex) {
				ex.printStackTrace();
			}finally {
				if(rs !=null) try {rs.close();}catch(SQLException ex) {}
				if(pstmt !=null) try {pstmt.close();}catch(SQLException ex) {}
				if(conn !=null) try {conn.close();}catch(SQLException ex) {}
			}
			return x;
		}

	//회원탈퇴 처리( deletePro.jsp)에서 회원정보를 삭제하는 메소드
	public int deleteMember(String id, String passwd) {
		Connection conn = null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		int x=-1;
		
		SHA256 sha = SHA256.getInstance();
		try {
			conn = getConnection();
			
			String orgPass=passwd;
			String shaPass = sha.getSha256(orgPass.getBytes());
			
			pstmt = conn.prepareStatement("select passwd from member where id=?");
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				String dbpasswd = rs.getString("passwd");
				if(BCrypt.checkpw(shaPass.dbpasswd)) {
					pstmt = conn.prepareStatement("delete from member where id=?");
					pstmt.setString(1, id);
					pstmt.executeUpdate();
					x=1;//회원탈퇴 처리 성공
				}else
					x=0;//회원탈퇴 처리 실패
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			if(rs !=null) try {rs.close();}catch(SQLException ex) {}
			if(pstmt !=null) try {pstmt.close();}catch(SQLException ex) {}
			if(conn !=null) try {conn.close();}catch(SQLException ex) {}
		}
		return x;
	}
}
