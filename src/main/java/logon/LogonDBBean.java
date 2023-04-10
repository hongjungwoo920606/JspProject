package logon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/*
LogonDBBeanŬ�������� SQL�۾��� �����ϱ� ������ java.sql��Ű���� �ִ� Connection,
PreparedStatement,ResultSet �������̽��� 
SQLExceptionŬ������ ����Ʈ �Ѵ�
*/

import javax.naming.Context;
import javax.naming.InitialContext;//���̹� ������ �����ϱ� ���� ���� ���ؽ�Ʈ
import javax.sql.DataSource;
/*
javax.naming.��Ű���� Ŀ�ؼ�Ǯ�� ���̹��۾����� �ʿ��ؼ� InitialContextŬ������ 
Context�������̽��� import�޴´�
*/

import work.crypt.SHA256;
//KISA�� SHA256 ��ȣȭ�� ����
import work.crypt.BCrypt;
//BCrypt��ȣȭ�� ����ϱ� ���� import�� ���� �������� ȸ�������� ��й�ȣ�� ��ȣȭ �Ѵ�

public class LogonDBBean {
	
	//LogonDBBean������ü ����
	
	private static LogonDBBean instance = new LogonDBBean();
	//��ó�� ��ü�� �����ϸ� �̴� ��ü���� ������ü�� �ȴ� �ѹ��� �����ǰ� ��ü�鰣�� ������ �ȴ�
	
	//LogonDBBean��ü�� �����ϴ� �޼ҵ�
	public static LogonDBBean getInstance() {
		return instance;
	}
	private LogonDBBean() {}
	
	//Ŀ�ؼ� Ǯ���� Ŀ�ؼ� ��ü�� ���� �żҵ�
	private Connection getConnection() throws Exception{
		Context initCtx = new InitialContext();
		Context envCtx = (Context)initCtx.lookup("java:comp/env");
		/*���ҽ� �̸��տ� �ٴ� ���λ��� ���ø� �˴ϴ�
		��� ������ ��Ʈ���� �ڿ��� JNDI ���ӽ����̽��� java:comp/env ��
		���̰� �ǰ� �ڿ��� ���� �����Ϸ��� lookup(java:comp/env)�� ���� 
		���ø��ҽ��� �����մϴ�
		*/
		DataSource ds = (DataSource)envCtx.lookup("jdbc/jsptest");
		return ds.getConnection();
	}
	
	//ȸ������ ó��(registerPro.jsp)���� ����ϴ� �����ڵ� �߰� �޼ҵ�
	public void insertMember(LogonDataBean member) {
		Connection conn = null;
		PreparedStatement pstmt=null;
		
		SHA256 sha = SHA256.getInstance();//��ȣȭ�� ����ϱ� ���ؼ� Ŭ������ ��ü�� ��´�
		try {
		conn = getConnection();
		//�޼ҵ带 ȣ���ؼ� Ŀ�ؼ� ��ü�� conn���۷����� �Ѱ��ش�
		String orgPass = member.getPasswd();
		String shaPass = sha.getSha256(orgPass.getBytes());
		//����ڰ� �Է��� ��й�ȣ ���ڿ��� ����Ʈ�迭 byte[]�� ��ȯ
		
		String bcPass = BCrypt.hashpw(shaPass, BCrypt.gensalt());
		//����Ʈ�� �迭�� ��ȣȭ�� ��й�ȣ�� shaPass������ ����ȴ�
		pstmt = conn.prepareStatement(
		//�ɹ����̺� �߰��ϴ� �κ����� insert�� ���
		"insert into member values(?,?,?,?,?,?)");
		//getXXX�޼ҵ忡 �����ؼ� ������ ������Ƽ���� �����´�
		//pstmt.�� ���ڵ带 �����ϱ� ���� ��ġȦ��(?)�� ���������� ��ġ�ϴ� �κ��Դϴ�
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
			//Ŀ�ؼ� Ǯ�� ����� Ŀ�ؼ� ��ü�� ��ȯ�Ѵ�
		}
	}
	
	
	//�α��� ��ó��(loginPro.jsp)�������� ����� ����ó�� �� ȸ������ ���� / Ż�� 
	//����� ����(memberCheck.jsp)���� ����ϴ� �޼ҵ�
	//�ΰ��� �Ķ����(����ڰ� �Է���  id�� , ����ڰ� �Է��� ��й�ȣ��)
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
		    
		    if(rs.next()) {//�ش� ���̵� ������ ����
		    	String dbpasswd= rs.getString("passwd");
		    	if(BCrypt.checkpw(shaPass,dbpasswd))
		    	x=1;//���� ����
		    	else
		    	x=0;//��й�ȣ Ʋ��
		    	
		    }else//�ش� ���̵� ������ ����
		    	x=-1;//���̵����
		    	
		    }catch(Exception ex) {
		    	ex.printStackTrace();
		
		}finally {
			if(rs !=null) try {rs.close();}catch(SQLException ex) {}
			if(pstmt !=null) try {pstmt.close();}catch(SQLException ex) {}
			if(conn !=null) try {conn.close();}catch(SQLException ex) {}
	}
		return x;
	}
	
	/*���̵� �ߺ�Ȯ��(confirmId.jsp)�� �ϴ� �޼ҵ�*/
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
			
			if(rs.next())//���̵� ����
				x=1; //���� ���̵� ����
			else
				x=-1;//���� ���̵� ����
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			if(rs !=null) try {rs.close();}catch(SQLException ex) {}
			if(pstmt !=null) try {pstmt.close();}catch(SQLException ex) {}
			if(conn !=null) try {conn.close();}catch(SQLException ex) {}
		}
		return x;
	}
	
	/*ȸ������ ������(modifyForm.jsp)�� ���� ���� ���������� �������� �޼ҵ�*/
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
			
			if(rs.next()) {//�ش���̵� ���� ���ڵ尡 ����
				String dbpasswd = rs.getString("passwd");
				//����ڰ� �Է��� ��й�ȣ�� ���̺��� ��й�ȣ�� ������ ����
				if(BCrypt.checkpw(shaPass,dbpasswd)) {
					member = new LogonDataBean();//������ ����� ��ü ����
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
		return member;//������ ����� ��ü member����
	}
	
	//ȸ������ ����ó������(modifyPro.jsp)���� ȸ������ ������ ó���ϴ� �޼ҵ�
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
			
			if(rs.next()) {//�ش� ���̵� ������ ����
				String dbpasswd=rs.getString("passwd");
				if(BCrypt.checkpw(shaPass,dbpasswd)) {
					pstmt = conn.prepareStatement("update member set name=?,address=?,tel=?"+"where id=?");
					pstmt.setString(1, member.getName());
					pstmt.setString(2, member.getAddress());
					pstmt.setString(3, member.getTel());
					pstmt.setString(4, member.getId());
					pstmt.executeUpdate();
					x=1;//ȸ������ ����ó�� ����
				}else
				x=0;//ȸ������ ����ó�� ����
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

	//ȸ��Ż�� ó��( deletePro.jsp)���� ȸ�������� �����ϴ� �޼ҵ�
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
					x=1;//ȸ��Ż�� ó�� ����
				}else
					x=0;//ȸ��Ż�� ó�� ����
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
