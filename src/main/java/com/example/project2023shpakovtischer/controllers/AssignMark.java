package com.example.project2023shpakovtischer.controllers;

import com.example.project2023shpakovtischer.dao.AttendanceDAO;
import com.example.project2023shpakovtischer.utils.ConnectionHandler;

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

@WebServlet(name = "AssignMark", value = ASSIGN_MARK_SERVLET)
public class AssignMark extends HttpServlet {
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

        int roundId = 0;
        int studentId = 0;
        int mark = 0;
        try {
            roundId = Integer.parseInt((String) ctx.getVariable("roundId"));
            studentId = Integer.parseInt((String) ctx.getVariable("studentId"));
            mark = Integer.parseInt((String) ctx.getVariable("mark"));
        } catch (NumberFormatException e) {
            ctx.setVariable("message", "Invalid student id or round id or mark");
            templateEngine.process(ATTENDEES_PAGE, ctx, response.getWriter());
        }
        AttendanceDAO attendanceDAO = new AttendanceDAO(connection);
        try {
            attendanceDAO.assignMark(studentId, roundId, mark);
        } catch (UnavailableException e) {
            System.out.println("UnavailableException:" + e.getMessage());
            templateEngine.process(ATTENDEES_PAGE, ctx, response.getWriter());
        }
        templateEngine.process(ATTENDEES_PAGE, ctx, response.getWriter());
    }

    public void destroy() {
        try{
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}