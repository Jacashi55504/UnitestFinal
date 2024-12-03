package com.mayab.quality.logginunittest;

import java.sql.*;

import com.mayab.quality.logginunittest.dao.UserMysqlDAO;
import com.mayab.quality.logginunittest.model.User;

class App {
    public static void main(String args[]) throws SQLException {
        /*Connection con = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String dbURL = "jdbc:mysql://localhost:3307/dbunit";
            System.out.println("jdbcurl=" + dbURL);
            String strUserID = "root";
            String strPassword = "123456";
            con=DriverManager.getConnection(dbURL,strUserID,strPassword);
            System.out.println("Connected to the database.");
            Statement stmt=con.createStatement();
            System.out.println("Executing query");
            ResultSet rs=stmt.executeQuery("SELECT * FROM usuarios");
            while(rs.next())
                System.out.println(rs.getInt("1"));
            con.close();
        }catch(Exception e){ System.out.println(e);}
        finally {
            con.close();
        }*/
    	
    	UserMysqlDAO dao = new UserMysqlDAO();
    	dao.save(new User("name", "example@example.com", "123456"));
    }
    	
}