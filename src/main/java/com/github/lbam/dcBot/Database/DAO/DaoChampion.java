package com.github.lbam.dcBot.Database.DAO;

import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.github.lbam.dcBot.Database.Factory.ConFactory;
import com.github.lbam.dcBot.Database.Models.Champion;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class DaoChampion {
	
	private String url, username, password;
	private Properties prop;
	
	private Connection con;
	private Statement cmd;
	
	public DaoChampion() {
		prop = new Properties();
		username = System.getenv("DBUSER");
		password = System.getenv("DBPASS");
		url = System.getenv("DBSERVER");
//    	try {
//			prop.load(new FileInputStream("config.properties"));
//			url = prop.getProperty("database");
//			username = prop.getProperty("dbuser");
//			password = prop.getProperty("dbpassword");
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public Champion getRandomChampion(String id) {
		connect();
		try {
			ResultSet rs =  cmd.executeQuery("SELECT * "
							+ "FROM champions c "
							+ "WHERE c.id NOT IN"
							+ "(SELECT p.idChampion "
							+ "FROM progresso p "
							+ "WHERE p.status = 1 "
							+ "AND p.idPlayer = " + id 
							+ ") ORDER BY RAND() LIMIT 1");
			rs.next();
			
			Champion c = new Champion(
					rs.getInt("id"), rs.getString("name"), 
					rs.getString("representation"), 
					rs.getString("hint"));
			
			return c;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}finally {
			close();
		}
	}
	
	public int getProgress(String id) {
		connect();
		try {
			ResultSet rs = cmd.executeQuery("SELECT COUNT(*) AS total "
					+ "FROM progresso p "
					+ "WHERE p.idPlayer = " + id + " AND p.status = 1");
			rs.next();
			return rs.getInt("total");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		return 0;
	}
	
	public int getTries(String id) {
		connect();
		try {
			ResultSet rs = cmd.executeQuery("SELECT COUNT(*) AS total "
					+ "FROM progresso p "
					+ "WHERE p.idPlayer = " + id);
			rs.next();
			return rs.getInt("total");
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}finally {
			close();
		}
	}
	
	public int getMaxChampionId() {
		connect();
		try {
			ResultSet rs = cmd.executeQuery("SELECT "
					+ "MAX(id) AS total "
					+ "FROM champions");
			rs.next();
			return rs.getInt("total");
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}finally {
			close();
		}
	}
	
	public void registerIncorrectGuess(String idPlayer, int idChampion, int usedHint) {
		connect();
		try {
			cmd.executeUpdate("INSERT INTO progresso(idPlayer, idChampion, status, hint) "
							+ "VALUES("+idPlayer+","+idChampion+","+0+","+usedHint+")");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
	}
	
	public void registerCorrectAnswer(String idPlayer, int idChampion, int usedHint) {
		connect();
		try {
			cmd.executeUpdate("INSERT INTO progresso(idPlayer, idChampion, status, hint) "
					+ "VALUES("+idPlayer+","+idChampion+","+1+","+usedHint+")");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
	}
	
	public int getUsedHints(String playerId) {
		connect();
		try {
			ResultSet rs = cmd.executeQuery("SELECT COUNT(DISTINCT p.idChampion) as total "
					+ "FROM progresso p "
					+ "WHERE p.idPlayer = "+playerId+" AND p.hint = 1");
			rs.next();
			return rs.getInt("total");
		}catch(SQLException e) {
			e.printStackTrace();
			return 0;
		}finally {
			close();
		}
	}
	
	public void getNoHint() {
		connect();
		ResultSet rs;
		try {
			rs = cmd.executeQuery("SELECT *"
					+ "FROM champions c");
			while(rs.next()) {
				System.out.println(rs.getString("name")+ " " + rs.getString("hint"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Champion getChampionById(int id){
		connect();
		ResultSet rs;
		try {
			rs = cmd.executeQuery("SELECT * FROM champions c WHERE c.id = "+id);
			rs.next();
			return new Champion(rs.getInt("id"), rs.getString("name"), 
					rs.getString("representation"), 
					rs.getString("hint"));
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		finally{
			close();
		}
		
	}
	
	public void connect() {
		try {
			con = (Connection) ConFactory.getConnection(url, username, password);
			cmd = (Statement) con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			cmd.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
