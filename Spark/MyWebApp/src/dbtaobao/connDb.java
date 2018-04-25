package dbtaobao;

import java.sql.*;
import java.util.ArrayList;

public class connDb {
	private static Connection con = null;
	private static Statement stmt = null;
	private static ResultSet rs = null;

	// 连接数据库方法
	public static void startConn() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// 连接数据库中间件
			try {
				con = DriverManager.getConnection("jdbc:MySQL://localhost:3306/dbtaobao", "root", "root");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 关闭连接数据库方法
	public static void endConn() throws SQLException {
		if (con != null) {
			con.close();
			con = null;
		}
		if (rs != null) {
			rs.close();
			rs = null;
		}
		if (stmt != null) {
			stmt.close();
			stmt = null;
		}
	}

	/**
	 * 数据库双11 所有买家消费行为比例
	 * 
	 * @return ArrayList,[Action, Numb]
	 * @throws SQLException
	 */
	public static ArrayList<String[]> index() throws SQLException {
		ArrayList<String[]> list = new ArrayList<String[]>();
		startConn();
		stmt = con.createStatement();
		rs = stmt.executeQuery("select action,count(*) num from user_log group by action desc");
		while (rs.next()) {
			String[] temp = { rs.getString("action"), rs.getString("num") };
			list.add(temp);
		}
		endConn();
		return list;
	}

	/**
	 * 男女买家交易对比
	 * 
	 * @return ArrayList,[gender,num]
	 * @throws SQLException
	 */
	public static ArrayList<String[]> index_1() throws SQLException {
		ArrayList<String[]> list = new ArrayList<String[]>();
		startConn();
		stmt = con.createStatement();
		rs = stmt.executeQuery("select gender,count(*) num from user_log group by gender desc");
		while (rs.next()) {
			String[] temp = { rs.getString("gender"), rs.getString("num") };
			list.add(temp);
		}
		endConn();
		return list;
	}

	/**
	 * 男女买家各个年龄段交易对比
	 * 
	 * @return ArrayList,[gender,age_range,num]
	 * @throws SQLException
	 */
	public static ArrayList<String[]> index_2() throws SQLException {
		ArrayList<String[]> list = new ArrayList<String[]>();
		startConn();
		stmt = con.createStatement();
		rs = stmt.executeQuery("select gender,age_range,count(*) num from user_log group by gender,age_range desc");
		while (rs.next()) {
			String[] temp = { rs.getString("gender"), rs.getString("age_range"), rs.getString("num") };
			list.add(temp);
		}
		endConn();
		return list;
	}

	/**
	 * 获取销量前五的商品类别
	 * 
	 * @return ArrayList,[cat_id,num]
	 * @throws SQLException
	 */
	public static ArrayList<String[]> index_3() throws SQLException {
		ArrayList<String[]> list = new ArrayList<String[]>();
		startConn();
		stmt = con.createStatement();
		rs = stmt.executeQuery(
				"select cat_id,count(*) num from user_log group by cat_id order by count(*) desc limit 5");
		while (rs.next()) {
			String[] temp = { rs.getString("cat_id"), rs.getString("num") };
			list.add(temp);
		}
		endConn();
		return list;
	}

	/**
	 * 各个省份的的总成交量对比
	 * 
	 * @return ArrayList,[province,num]
	 * @throws SQLException
	 */
	public static ArrayList<String[]> index_4() throws SQLException {
		ArrayList<String[]> list = new ArrayList<String[]>();
		startConn();
		stmt = con.createStatement();
		rs = stmt.executeQuery("select province,count(*) num from user_log group by province order by count(*) desc");
		while (rs.next()) {
			String[] temp = { rs.getString("province"), rs.getString("num") };
			list.add(temp);
		}
		endConn();
		return list;
	}

	/**
	 * 测试函数
	 * 
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		ArrayList<String[]> test = index();
		ArrayList<String[]> test1 = index_1();
		ArrayList<String[]> test2 = index_2();
		ArrayList<String[]> test3 = index_3();
		ArrayList<String[]> test4 = index_4();

		for (String[] strings : test) {
			System.out.println("Action: " + strings[0] + " num: " + strings[1]);
		}

		for (String[] strings : test1) {
			System.out.println("gender: " + strings[0] + " num: " + strings[1]);
		}

		for (String[] strings : test2) {
			System.out.println("gender: " + strings[0] + " age_range: " + strings[1] + " num: " + strings[2]);
		}

		for (String[] strings : test3) {
			System.out.println("cat_id: " + strings[0] + " num: " + strings[1]);
		}

		for (String[] strings : test4) {
			System.out.println("province: " + strings[0] + " num: " + strings[1]);
		}
	}
}
