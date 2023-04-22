package com.example.project2023shpakovtischer.controllers;

import com.example.project2023shpakovtischer.dao.CourseDAO;
import com.example.project2023shpakovtischer.enums.UserRole;
import com.example.project2023shpakovtischer.javaBeans.CourseBean;
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
import java.util.ArrayList;

import static com.example.project2023shpakovtischer.utils.Paths.GET_COURSES_SERVLET;
import static com.example.project2023shpakovtischer.utils.Paths.HOME_PAGE;

@WebServlet(name = "GetCourses", value = GET_COURSES_SERVLET)
public class GetCourses extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
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

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();

        UserBean user = (UserBean) request.getSession().getAttribute("user");
        String path = getServletContext().getContextPath();

        String message = null;
        ArrayList<CourseBean> courses = null;

        CourseDAO courseDAO = new CourseDAO(connection);

        if (user.getRole().equals(UserRole.STUDENT)) {
            courses = courseDAO.getCoursesByStudentId(user.getId());
        }
        else if (user.getRole().equals(UserRole.PROFESSOR)) {
            courses = courseDAO.getCoursesByProfessorId(user.getId());
        }

        message = "Welcome " + user.getName() + " " + user.getSurname() + "!";
        path = path + HOME_PAGE;

        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("message", message);
        ctx.setVariable("courses", courses);
        templateEngine.process(path, ctx, response.getWriter());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
