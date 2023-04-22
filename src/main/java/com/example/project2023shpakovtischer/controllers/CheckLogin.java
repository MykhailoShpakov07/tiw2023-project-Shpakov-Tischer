package com.example.project2023shpakovtischer.controllers;

import com.example.project2023shpakovtischer.dao.UserDAO;
import com.example.project2023shpakovtischer.javaBeans.UserBean;
import com.example.project2023shpakovtischer.utils.ConnectionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static com.example.project2023shpakovtischer.utils.Paths.*;


@WebServlet(name = "CheckLogin", value = CHECK_LOGIN_SERVLET)
public class CheckLogin extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public void init(){
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");

        try{
            connection = ConnectionHandler.getConnection(servletContext);
        }
        catch (UnavailableException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext servletContext = getServletContext();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        UserDAO usr = new UserDAO(connection);
        UserBean user = null;
        try {
            user = usr.getUserByEmail(email);
        } catch (UnavailableException e) {
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
            System.out.println(e.getMessage());
            return;
        }

        String path = getServletContext().getContextPath();
        String message = null;

        if (user == null) {
            //user not found
            message = "User not found ! Please try again";

        }
        else if(!user.getPassword().equals(password)){
            //password does not coincide
            message = "Wrong password ! Please try again";
        }
        else {
            //login successful
            //set password = null for security reasons
            user.setPassword(null);
            request.getSession().setAttribute("user", user);
            response.sendRedirect(path + GET_COURSES_SERVLET);
        }

        //render login page with error message
        path = path + LOGIN_PAGE;
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("message", message);
        templateEngine.process(path, ctx, response.getWriter());
    }

    public void destroy(){
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
