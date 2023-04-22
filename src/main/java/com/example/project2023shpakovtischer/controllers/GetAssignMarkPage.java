package com.example.project2023shpakovtischer.controllers;

import com.example.project2023shpakovtischer.dao.AttendanceDAO;
import com.example.project2023shpakovtischer.javaBeans.AttendanceBean;
import com.example.project2023shpakovtischer.utils.ConnectionHandler;
import org.thymeleaf.TemplateEngine;
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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import static com.example.project2023shpakovtischer.utils.Paths.*;

@WebServlet(name = "GetAssignMarkPage", value = GET_ASSIGN_MARK_PAGE_SERVLET)
public class GetAssignMarkPage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;

    private Connection connection = null;


    public void init() throws ServletException {
        try{
            ServletContext servletContext = getServletContext();
            ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
            templateResolver.setTemplateMode(TemplateMode.HTML);
            this.templateEngine = new TemplateEngine();
            this.templateEngine.setTemplateResolver(templateResolver);
            templateResolver.setSuffix(".html");
            connection = ConnectionHandler.getConnection(getServletContext());
        }
        catch (UnavailableException e){
            throw new RuntimeException(e);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());

        int studentId = 0;
        int roundId = 0;
        try {
            studentId = Integer.parseInt(request.getParameter("studentId"));
            roundId = Integer.parseInt(request.getParameter("roundId"));
        } catch (NumberFormatException e) {
            ctx.setVariable("message", "Invalid student id or round id");
            templateEngine.process(ATTENDEES_PAGE, ctx, response.getWriter());
        }
        AttendanceDAO attendanceDAO = new AttendanceDAO(connection);
        AttendanceBean attendance = null;
        try {
             attendance = attendanceDAO.getAttendance(studentId, roundId);
        } catch (UnavailableException e) {
            System.out.println("UnavailableException:" + e.getMessage());
            templateEngine.process(ATTENDEES_PAGE, ctx, response.getWriter());
        }
        if (attendance == null){
            ctx.setVariable("message", "No attendance found");
            templateEngine.process(ATTENDEES_PAGE, ctx, response.getWriter());
        } else {
            ctx.setVariable("attendance", attendance);
            templateEngine.process(ASSIGN_MARK_PAGE, ctx, response.getWriter());
        }
        templateEngine.process(ASSIGN_MARK_PAGE, ctx, response.getWriter());
    }

    public void destroy() {
        try{
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}