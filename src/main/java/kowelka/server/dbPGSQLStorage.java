/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kowelka.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author peter
 */
public interface dbPGSQLStorage {

    default Connection Init(String driver, String connString, String user, String password) {
        
	try {
            Class.forName(driver);
	} catch (ClassNotFoundException e) {
            System.out.println("Cannot load PostgreSQL JDBC Driver");
            return null;
	}

        try {
            Connection connection = DriverManager.getConnection(connString, user, password);
            return connection;
        } catch (SQLException e) {
            System.out.println("Connection to "+connString+" Failed!");
            return null;
        }
        
    }    
    
}
