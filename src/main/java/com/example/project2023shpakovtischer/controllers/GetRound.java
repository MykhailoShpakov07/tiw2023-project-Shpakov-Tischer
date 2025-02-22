package com.example.project2023shpakovtischer.controllers;

import com.example.project2023shpakovtischer.beans.AttendanceBean;
import com.example.project2023shpakovtischer.beans.CourseBean;
import com.example.project2023shpakovtischer.beans.RoundBean;
import com.example.project2023shpakovtischer.beans.UserBean;
import com.example.project2023shpakovtischer.dao.*;
import com.example.project2023shpakovtischer.enums.AttendeesColumn;
import com.example.project2023shpakovtischer.enums.UserRole;
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
import java.util.List;
import java.util.Optional;

import static com.example.project2023shpakovtischer.utils.Paths.*;


@WebServlet(name = "GetRound", value = GET_ROUND_SERVLET)
public class GetRound extends HttpServlet {

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
        templateResolver.setCharacterEncoding("UTF-8");

        try {
            connection = ConnectionHandler.getConnection(servletContext);
        } catch (UnavailableException e) {
            System.out.println(e.getMessage());
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        UserBean user = (UserBean) request.getSession().getAttribute("user");

        int roundId = -1;

        try{
            roundId = Integer.parseInt(request.getParameter("roundId"));
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid roundId parameter");
            return;
        }

        if (user.getRole().equals(UserRole.PROFESSOR)){
            //process request for the professor
            Optional<AttendeesColumn> optOrderLabel = Optional.empty();
            Optional<Boolean> optReverse = Optional.empty();
            AttendeesColumn orderLabel;
            boolean reverse;
            //var that tells whether the marks for this round can be published
            boolean canBePublished = false;
            //var that tells the status of the report, 0 - can`t be published, 1-can be created, 2-already exists
            int reportStatus = 0;

            try {
                optOrderLabel = Optional.ofNullable(request.getParameter("orderLabel"))
                        .map(Integer::parseInt).map(AttendeesColumn::getAttendeesColumnFromInt);
            }
            catch (IllegalArgumentException e){
                System.out.println(e.getMessage());
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid orderLabel parameter");
                return;
            }

            /*contains optional of boolean value of the parameter passed inside request
            this optional is empty if user passes the string value which is not true nor false*/
            optReverse = Optional.ofNullable(request.getParameter("reverse"))
                    .map(String::toLowerCase)
                    .filter(s->s.equals("true")||s.equals("false"))
                    .map(Boolean::parseBoolean);

            //update the attributes in session if the new ordering parameters are contained inside optionals
            //the parameters are contained inside optionals only if they were passed in request
            optOrderLabel.ifPresent(s->request.getSession().setAttribute("orderLabel", s));
            optReverse.ifPresent(s->request.getSession().setAttribute("reverse", s));

            //get ordering parameters from the session
            optOrderLabel = Optional.ofNullable((AttendeesColumn) request.getSession().getAttribute("orderLabel"));
            optReverse = Optional.ofNullable((Boolean) request.getSession().getAttribute("reverse"));

            //update params and set to default some of them in case those are null
            orderLabel = optOrderLabel.orElse(AttendeesColumn.STUDENT_ID);
            reverse = optReverse.orElse(false);


            List<AttendanceBean> attendances = null;
            RoundBean round = null;
            CourseBean course = null;

            AttendanceDAO attendanceDAO = new AttendanceDAO(connection);
            RoundDAO roundDAO = new RoundDAO(connection);
            CourseDAO courseDAO = new CourseDAO(connection);
            ReportDAO reportDAO = new ReportDAO(connection);

            try {
                attendances = attendanceDAO.getOrderedAttendances(roundId, orderLabel.getName(), reverse);
                round = roundDAO.getRoundById(roundId);
                course = courseDAO.getCourseByRoundId(roundId);

                //do set of controls to allow user to publish marks or create report
                if(reportDAO.canBeReported(roundId) && !attendances.isEmpty()){
                    reportStatus = 1;
                    canBePublished = false;
                }
                else if (reportDAO.getReportByRoundId(roundId) != null) {
                    reportStatus = 2;
                    canBePublished = false;
                }
                else{
                    reportStatus = 0;
                    canBePublished = attendanceDAO.canBePublished(roundId);
                }
            }
            catch (UnavailableException e){
                System.out.println(e.getMessage());
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database access");
                return;
            }

            ctx.setVariable("columnNames", List.of(AttendeesColumn.values()) );
            ctx.setVariable("orderLabel", orderLabel.getValue());
            ctx.setVariable("reverse", !reverse);
            ctx.setVariable("attendances", attendances);
            ctx.setVariable("round", round);
            ctx.setVariable("course", course);
            ctx.setVariable("canBePublished", canBePublished);
            ctx.setVariable("reportStatus", reportStatus);
            ctx.setVariable("getRoundServletPath", GET_ROUND_SERVLET);
            ctx.setVariable("getAssignMarkPageServletPath", GET_ASSIGN_MARK_PAGE_SERVLET);
            ctx.setVariable("publishMarksServletPath", PUBLISH_MARKS_SERVLET);
            ctx.setVariable("createReportServletPath", CREATE_REPORT_SERVLET);
            templateEngine.process(ATTENDEES_PAGE, ctx, response.getWriter());

        }
        else if(user.getRole().equals(UserRole.STUDENT)){
            //process request for the student
            AttendanceBean attendance = null;
            RoundBean round = null;
            CourseBean course = null;
            UserBean prof = null;

            AttendanceDAO attendanceDAO = new AttendanceDAO(connection);
            RoundDAO roundDAO = new RoundDAO(connection);
            CourseDAO courseDAO = new CourseDAO(connection);
            UserDAO userDAO = new UserDAO(connection);

            try {
                attendance = attendanceDAO.getAttendance(roundId, user.getId());
                round = roundDAO.getRoundById(roundId);
                course = courseDAO.getCourseByRoundId(roundId);
                prof = userDAO.getUserById(round.getProfessorId());
            }
            catch (UnavailableException e){
                System.out.println(e.getMessage());
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database access");
                return;
            }

            ctx.setVariable("attendance", attendance);
            ctx.setVariable("round", round);
            ctx.setVariable("course", course);
            ctx.setVariable("professor", prof);
            ctx.setVariable("refuseMarkServletPath", REFUSE_MARK_SERVLET);
            templateEngine.process(RESULT_PAGE, ctx, response.getWriter());
        }

    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
