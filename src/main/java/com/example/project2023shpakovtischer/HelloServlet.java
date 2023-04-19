package com.example.project2023shpakovtischer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

   public void init() throws ServletException {
        try{
            ServletContext context = getServletContext();
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
            throw new UnavailableException("Can`t load db driver");
        }
        catch (SQLException e){
            throw new UnavailableException("Can`t connect to db");
        }
   }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        System.out.println("Hello Servlet");
        if(connection == null){
            request.setAttribute("message", "Can`t connect to db");
        }
        else{
            request.setAttribute("message", "Connection to db is successful");
        }
        request.getRequestDispatcher("/WEB-INF/test.jsp").forward(request, response);

    }

    public void destroy() {
        if(connection != null){
            try{
                connection.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}