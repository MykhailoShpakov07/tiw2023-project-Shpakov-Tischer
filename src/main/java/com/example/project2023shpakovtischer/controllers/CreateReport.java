package com.example.project2023shpakovtischer.controllers;

import com.example.project2023shpakovtischer.dao.ReportDAO;
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

import static com.example.project2023shpakovtischer.utils.Paths.CREATE_REPORT_SERVLET;
import static com.example.project2023shpakovtischer.utils.Paths.REPORT_PAGE;

@WebServlet(name = "CreateReport", value = CREATE_REPORT_SERVLET)
public class CreateReport extends HttpServlet {
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
        try {
            roundId = Integer.parseInt(request.getParameter("roundId"));
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid roundId parameter");
        }
        ReportDAO reportDAO = new ReportDAO(connection);
        try {
            reportDAO.createReport(roundId);

        } catch (SQLException | UnavailableException e) {
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in database while creating report");
        }
        ctx.setVariable("message", "Report created successfully");
        templateEngine.process(REPORT_PAGE, ctx, response.getWriter());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }

    public void destroy() {
        try{
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}