/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_bateau.infra.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import mvc_bateau.infra.exception.DBException;

/**
 *
 * @author macbook
 */
public class JdbcHelper {

    private final static String nomHote = "localhost";
    private final static String port = "5432";
    private final static String nomBD = "maritime";
    private final static String userName = "adm_maritime"; 
    private final static String password = "maritime@2025";
    
    private Connection conn;
    
    private static JdbcHelper INSTANCE;

    private JdbcHelper() {
    }

    public static JdbcHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JdbcHelper();
        }
        return INSTANCE;
    }

    public Connection getConn() {
        if (conn == null) {
            conn = getPGConnection();
        }
        return conn;
    }
    
    private Connection getPGConnection() {
        
        String connectionUrl = String.format(
                "jdbc:postgresql://%s:%s/%s", 
                nomHote, 
                port, 
                nomBD);
        try {
            return DriverManager.getConnection(connectionUrl, userName, password);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            throw new DBException("Echec de connexion à la base de données");
        }
    }

    public void refreshConn() {
        closeConn();
        conn = getPGConnection();
    }

    public boolean testConn() {
        refreshConn();
        return conn != null;
    }

    public void closeConn() {
        if (conn != null) {
            System.out.println("Closing db connection");
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
