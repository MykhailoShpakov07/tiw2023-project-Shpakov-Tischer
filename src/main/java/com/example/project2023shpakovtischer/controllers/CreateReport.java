package com.example.project2023shpakovtischer.controllers;

import com.example.project2023shpakovtischer.beans.AttendanceBean;
import com.example.project2023shpakovtischer.beans.ReportBean;
import com.example.project2023shpakovtischer.dao.AttendanceDAO;
import com.example.project2023shpakovtischer.dao.ReportDAO;
import com.example.project2023shpakovtischer.enums.AttendeesColumn;
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
import java.util.List;

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
            return;
        }


        ReportDAO reportDAO = new ReportDAO(connection);
        AttendanceDAO attendanceDAO = new AttendanceDAO(connection);
        List<AttendanceBean> attendances = null;
        ReportBean report = reportDAO.getReportByRoundId(roundId);

        try {
            attendances = attendanceDAO.getOrderedAttendances(roundId, AttendeesColumn.STUDENT_ID.getName(), false);

            //do set of controls to allow user to create report
            if(reportDAO.canBeReported(roundId) && !attendances.isEmpty()){
                try {
                    reportDAO.createReport(roundId);
                    attendances = attendanceDAO.getOrderedAttendances(roundId, AttendeesColumn.STUDENT_ID.getName(), false);
                    showReportPage(response, ctx, attendances, reportDAO.getReportByRoundId(roundId));

                } catch (SQLException | UnavailableException e) {
                    System.out.println(e.getMessage());
                    response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in database while creating report");
                    return;
                }
            }
            else if (report != null) {
                showReportPage(response, ctx, attendances, report);
            }
            else{
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Report cannot be created at this time");
                return;
            }
        }
        catch (UnavailableException e){
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database access");
            return;
        }
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

    private void showReportPage(HttpServletResponse response, WebContext ctx, List<AttendanceBean> attendances, ReportBean report) throws IOException {
        ArrayList<AttendeesColumn> columnNames = new ArrayList<>();
        columnNames.add(AttendeesColumn.STUDENT_ID);
        columnNames.add(AttendeesColumn.NAME);
        columnNames.add(AttendeesColumn.SURNAME);
        columnNames.add(AttendeesColumn.MARK);
        ctx.setVariable("attendances", attendances);
        ctx.setVariable("columnNames", columnNames);
        ctx.setVariable("report", report);
        templateEngine.process(REPORT_PAGE, ctx, response.getWriter());
    }
}