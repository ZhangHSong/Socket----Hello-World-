package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * 数据库
 */
public class Database {

	boolean result = false;
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;

	/*
	 * 数据库连接操作
	 */
	public void getConnection() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://127.0.0.1:3306/user";
			conn = DriverManager.getConnection(url, "root", "");
		} catch (ClassNotFoundException e) {
			System.err.println("连接失败:" + e.getMessage());
		} catch (SQLException e) {
			System.err.println("连接失败:" + e.getMessage());
		}
	}

	/*
	 * 关闭结果集
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
	 * 关闭语句对象
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
	 * 关闭与数据库连接
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
	 * 插入数据操作
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
	 * 数据库查询操作
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
