/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kowelka.server;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author peter
 */
public class KowelkaServer {
    
    static final int MAX_PROD = 1024;

    public static void main(String[] args) {
        KowelkaServer kow = new KowelkaServer();
        kow.start(args);
    }
    
    void start(String[] args) {

        Connection connection = null;
        Prod[] Products;
        Products = new Prod[MAX_PROD];
        
	try {

            Class.forName("org.postgresql.Driver");

	} catch (ClassNotFoundException e) {

            System.out.println("Cannot load PostgreSQL JDBC Driver");
            e.printStackTrace();
            return;
	}

        try {

            connection = DriverManager.getConnection(
                                "jdbc:postgresql://192.168.211.231:5432/kowelka", 
                                "root", "123456");

        } catch (SQLException e) {

            System.out.println("Connection Failed!");
            e.printStackTrace();
            return;

        }

        int i=0;    
    
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(   "SELECT public.prod.id as prod_id, public.prod.name as prod_name, public.cat.name as cat_name" +
                                                " FROM public.prod, public.cat" +
                                                " WHERE public.prod.id_cat=public.cat.id");
            i=0;
            while ( rs.next() && i<MAX_PROD ) {
                Prod prod = new Prod();
                prod.setId(rs.getLong("prod_id"));
                prod.setName(rs.getString ("prod_name"));
                prod.setCatname(rs.getString ("cat_name"));
                Products[i]=prod;
                i++;
            }
            rs.close();
            st.close();
        } catch (SQLException se) {
            System.err.println("SQLException creating the list of products");
            System.err.println(se.getMessage());
        }
        Integer imax=i;
        System.out.println("\nid\tProduct\tCategory");
        for(i=0;i<imax;i++) {
            System.out.println("\n"+Products[i].getId().toString()+"\t"+Products[i].getName()+"\t"+Products[i].getCatname());
        }
        System.out.println("\n"+"Total:"+imax.toString()+"\n");
    }
    
}
