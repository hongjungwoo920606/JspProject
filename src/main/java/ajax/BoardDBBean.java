package ajax;

/*
 getInstance: ���� BoardDBBean ��ü�� ���۷����� ����
 getConnection: �����۾��� ����� Connection ��ü�� Ŀ�ؼ� Ǯ�� ��
 
 insertArticle(BoardDatabean article)
 ���ο� ���� board���̺� �߰��Ѵ� ���Է�ó��
 
 getArticleCount(): 
 board���̺��� ��ü���ڵ���� �޾ƿ´�, �۸�Ͽ��� �۹�ȣ �� ��ü���ڵ���� ǥ���Ҷ� ���
 
 getArticle(int start, int end):
 ���ۺ��� �������� ������ŭ ���ڵ带 board���̺��� �˻��Ѵ�
 �۸�� ���⿡�� �����
 
 updateGetArticle(int num):
 id�� �ش��ϴ� ���ڵ带 board���̺��� �˻��Ѵ� �� ���������� ���
 
 updateArticle(BoardDataBean article):
 ������ ���� ������ �����Ҷ� ����Ѵ� �� ����ó������ ���
 
 deleteArticle(int num, String passwd):
 id�� �ش��ϴ� ���ڵ带 board���̺��� �����Ѵ�. �ۻ���ó���� ����Ѵ�
 
 */

/*sql�۾��� �����ϱ⶧���� java.sql��Ű���� �ִ� �Ʒ��� Ŭ�������� ����Ʈ�Ѵ�*/
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/*java.naming��Ű���� Ŀ�ؼ�Ǯ�� ���̹� �۾��� �ʿ��� InitialContextŬ������
 Context�������̽� javax.sql��Ű���� Ŀ�ؼ�Ǯ�� ���̹��۾� �� Ŀ�ؼǰ�ü�� ���µ�
 �ʿ��� DataSource�������̽��� ����ϱ� ���� import �޾Ҵ�*/
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDBBean {
	private static BoardDBBean instance = new BoardDBBean();
	//Ŭ������ �⺻ �����̰� instance�� ��ü���� "����(����)"�� ����(���)�� �����̶�� �����ϸ�ȴ�.
	//���δٸ����¸� ������ �ϱ����ؼ� 
	
	public static BoardDBBean getInstance() {//�̸޼ҵ带 ȣ���Ѱ����� LogonDBBean��ü�� ����
		return instance;
		
	}
	//jsp���������� db�������� BoardDBBean Ŭ������ �޼ҵ忡 ���ٽ� �ʿ�
	private BoardDBBean() {}
	
	//Ŀ�ؼ�Ǯ�κ��� Ŀ�ؼ� ��ü�� �� DB�������� �������� �����ϴ� �޼��忡�� ���
	private Connection getConnection() throws Exception{
		Context initCtx = new InitialContext();
		Context envCtx = (Context)initCtx.lookup("java:comp/env");
		DataSource ds = (DataSource)envCtx.lookup("jdbc/jsptest");
		return ds.getConnection();
		/*dbcp apiĿ�ؼ� Ǯ�� ����ؼ� Ŀ�ؼǰ�ü�� �Ҵ�޴´�
		 getConnection()����Ÿ���� Connection
		  */
	}
	//board���̺� �����߰�(insert��) => writePro.jsp���� ���
	public int insertArticle(BoardDataBean article) {//�Ѱ��� �Ķ����, article
		//article�� writeForm���� �Է��� ���� ������ ������ �ִ�
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int x = 0;
		int number = 0; //board���̺� �� �� ��ȣ
		String sql = "";
		
		//�Ʒ� �ڵ�� ����� ��������
		int num = article.getNum(); //������� ��ȣ
		/*���� �۹�ȣ�� board���̺��� ���ο���� �߰��� ��� �ڵ����� �����ؼ� �����ǰ�
		 article.getNum�� ���� jsp���������� �Ѿ�� ��ü�κ��� ���� �۹�ȣ�� ����� ����
		 ������� �۹�ȣ�� ��� �߻��Ѵ�
		  */
		int ref = article.getRef(); //������� �׷�ȭ ���̵�
		/*board���̺�� ���� �����������ͺ��̽������� ����۰� ��������� �������� 
		 ǥ���Ҽ����� �ٸ� ȭ�鿡 ���� ǥ���Ҷ� �������¸� �����ֱ� ���ؼ�
		 �׷�ȭ��� �� ����Ѵ� ���� �׷�ȭ ���̵��� ������� �۹�ȣ�� ����Ѵ�
		 */
		//����� �ֽű��̴��� ����� ���� �ü����⶧���� �̷� ������ �����ϱ����ؼ� �ʿ�
		int re_step = article.getRe_step(); //�׷쳻�� ���� ����
		
		int re_level = article.getRe_level(); //�� ������ �鿩����
		//������� �鿩���Ⱑ 0�̰� ����� 1�̴�
		
		try {
			conn = getConnection();
			//���� board���̺� ���ڵ��� �����Ǵܰ� �� ��ȣ ����
			pstmt = conn.prepareStatement("select max(num)from board");
			rs = pstmt.executeQuery();
			
			if(rs.next())//������ ���ڵ尡 ����
				number = rs.getInt(1) + 1; //���� �۹�ȣ�� ���� ū �۹�ȣ + 1
			else
				number= 1;
			
			//����۰� ��۰��� ������ ����
			if(num != 0) { //��� - ������� �۹�ȣ ����
				sql = "update board set re_step=re_step+1 where ref=? and re_step>?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, ref);
				pstmt.setInt(2, re_step);
				pstmt.executeUpdate();
				re_step = re_step + 1;
				re_level = re_level + 1;
				
			}else {
				//����� - �۹�ȣ ����
				ref = number;
				re_step = 0;
				re_level = 0;				
			}
			//������ �ۼ�: board���̺� ���ο� ���ڵ� �߰�
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
			x=1;//���ڵ��߰�����
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			if(rs!=null)try {rs.close();}catch(SQLException ex) {}
			if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {}
			if(conn!=null)try {conn.close();}catch(SQLException ex) {}
		}
		return x;
		
		
	}
	//board���̺� ����� ��ü���� ���� �� => list.jsp���� ���
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
	//���� ����� ������ list.jsp
	public List<BoardDataBean> getArticles(int start, int end){
		//���� �۹�ȣ[start]���� ������ ���ڵ� �� ��ŭ [end]�� ���ڵ带 �˻��ϴ� �޼ҵ�
		//�� �޼ҵ�� �Խ����� �۸���� ���� �κп��� ���Ǹ� list.jsp���������� ȣ���� ���
		//�̰Խ��ǽý��ۿ��� list.jsp�� �۸�ϰ� �� ���뺸�Ⱑ ���յȰ��̴�
		Connection conn =null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardDataBean> articleList = null; //�۸���� �����ϴ� ��ü
		/*�۸���� �����ϴ� �÷����̸� List��ü Ÿ������ ���۷��� ���� articleList����
		 List�� �������̽��� ��ü�� ���������Ҽ� ����. ���� 
		 articleList = new ArrayList<BoardDataBean>(end); �� ���� 
		 ����Ŭ������ ArrayListŸ���� ��ü�� �����ؼ� ����Ѵ�
		 �ش��÷����� ������ ��ü�� Ÿ�԰� ���� ���·� ������ִ°��� ���ʸ��̶�� �Ѵ�
		 ������� List<BoardDataBean>, ArrayList<BoardDataBean>�� ����
		 �÷��ǿ� ������ ��üŸ���� �����ϸ� ���߿� ��ü�� ������ ����Ҷ� ����ȯ�� �� �ʿ䰡���� ��������
		  */
		try {
			conn= getConnection();
			pstmt = conn.prepareStatement("select * from board order by desc, re_step asc limit?,?");
			//limit?,? �� �˻��ϴ� ���ڵ��� ���� �����ϱ����� ����ϴ� ������ ����Ʈ���� �Ķ���Ͱ� ���� ��� ������ ������ ����
			/*limit ���۷��ڵ� ��ȣ, �˻��� ���ڵ��� ����
			 mysql���� ���ڵ��� ��ȣ�� 0���� �����ϴµ� jsp���������� ���� 1���� �����Ѵ�
			 ���� list.jsp���� ����Ҷ����� ���۹�ȣ�� �ϳ��� ������Ѵ�  
			 */
			pstmt.setInt(1, start-1);
			pstmt.setInt(2, end);
			rs= pstmt.executeQuery();
			
			if(rs.next()) {
				articleList = new ArrayList<BoardDataBean>(end);
				/*end������ ����ŭ ��ü�� ������ �� �ִ� ArrayList��ü�� �����Ѵ�*/
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
		return articleList; //List��ü�� ���۷����� ����
		
	}
	//�ۼ��� ������ ����� ���� ����(1���� ��) => updateFrom.jsp���� ���
	public BoardDataBean updateGetArticle(int num) {
		/*updateGetArticle�޼ҵ�� num�� ����ϴ� �޼ҵ常 �˻��ϴ� �޼ҵ��̴�
		 �Խ��ǿ��� �� ������ �����ϴ� ���κп��� ���Ǵ� ������ updateForm.jsp���� ȣ��Ǿ� ����Ѵ�
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
	
	//�ۼ���ó���� ���=> updatePro.jsp���� ���
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
			
			if(rs.next()) {//�ۼ����� ���� �н����带 ������
				String dbpasswd = rs.getString("passwd");
				if(dbpasswd.equals(article.getPasswd())) {
					String sql = "update board set subject =?,";
					sql +="content=? where num=?";
					pstmt.setString(1, article.getSubject());
					pstmt.setString(2, article.getContent());
					pstmt.setInt(3, article.getNum());
					pstmt.executeUpdate();
					x= 1;
					/*�н����� ��ġ���ο� ���� ������ �����ϰ� 1, Ʋ���� 0*/
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
		//�ۻ��� ó���� ���(delete��) => deletePro.jsp���� ���
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
						x =1; //�ۻ�������
					}else//��й�ȣƲ��
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
	
	
	
	
	
	













