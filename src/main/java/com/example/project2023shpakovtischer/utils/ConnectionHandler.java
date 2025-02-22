package com.example.project2023shpakovtischer.utils;



import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**Class that handles the connection to the database*/
public class ConnectionHandler {

    /**Returns a connection to the database created using the parameters in the web.xml file*/
    public static Connection getConnection(ServletContext context) throws UnavailableException {
        Connection connection = null;
        try {
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Can't load database driver");
        } catch (SQLException e) {
            throw new UnavailableException("Couldn't get db connection");
        }
        return connection;
    }

    /**Closes the connection to the database*/
    public static void closeConnection(Connection connection) throws SQLException {
        if(connection!=null){
            connection.close();
        }
    }
}
