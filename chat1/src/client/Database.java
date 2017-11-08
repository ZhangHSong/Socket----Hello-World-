package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * ���ݿ�
 */
public class Database {

	boolean result = false;
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;

	/*
	 * ���ݿ����Ӳ���
	 */
	public void getConnection() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://127.0.0.1:3306/user";
			conn = DriverManager.getConnection(url, "root", "");
		} catch (ClassNotFoundException e) {
			System.err.println("����ʧ��:" + e.getMessage());
		} catch (SQLException e) {
			System.err.println("����ʧ��:" + e.getMessage());
		}
	}

	/*
	 * �رս����
	 */
	public void closeRs() {
		try {
			if (null != rs)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * �ر�������
	 */
	public void closePstmt() {
		try {
			if (null != pstmt)
				pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * �ر������ݿ�����
	 */
	public void closeConn() {
		try {
			if (null != conn)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * �������ݲ���
	 */
	public void insertDatabase(String username, String pword) {
		getConnection();
		try {
			pstmt = conn.prepareStatement("INSERT INTO register values(?,?)");
			pstmt.setString(1, username);
			pstmt.setString(2, pword);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closePstmt();
			closeConn();
		}
	}

	/*
	 * ���ݿ��ѯ����
	 */
	public void selectDatabase(String username) {
		getConnection();
		try {
			pstmt = conn.prepareStatement("SELECT username FROM register WHERE username = ?");
			pstmt.setString(1, username);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeRs();
			closePstmt();
			closeConn();
		}
	}

}
