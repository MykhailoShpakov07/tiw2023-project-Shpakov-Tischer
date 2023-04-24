package com.example.project2023shpakovtischer.controllers;

import com.example.project2023shpakovtischer.dao.AttendanceDAO;
import com.example.project2023shpakovtischer.dao.CourseDAO;
import com.example.project2023shpakovtischer.dao.RoundDAO;
import com.example.project2023shpakovtischer.dao.UserDAO;
import com.example.project2023shpakovtischer.enums.AttendeesColumn;
import com.example.project2023shpakovtischer.enums.UserRole;
import com.example.project2023shpakovtischer.javaBeans.AttendanceBean;
import com.example.project2023shpakovtischer.javaBeans.CourseBean;
import com.example.project2023shpakovtischer.javaBeans.RoundBean;
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
import java.util.List;
import java.util.Optional;

import static com.example.project2023shpakovtischer.utils.Paths.*;


@WebServlet(name = "GetRoundAttendees", value = GET_ROUND_SERVLET)
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

        try {
            connection = ConnectionHandler.getConnection(servletContext);
        } catch (UnavailableException e) {
            System.out.println(e.getMessage());
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        String path = servletContext.getContextPath();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        String message = null;
        UserBean user = (UserBean) request.getSession().getAttribute("user");

        int roundId = -1;

        try{
            roundId = Integer.parseInt(request.getParameter("roundId"));
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid roundId parameter");
        }

        if (user.getRole().equals(UserRole.PROFESSOR)){
            //process request for the professor
            Optional<AttendeesColumn> optOrderLabel = null;
            Optional<Boolean> optReverse;
            AttendeesColumn orderLabel;
            Boolean reverse;

            try {
                optOrderLabel = Optional.ofNullable(request.getParameter("orderLabel"))
                        .map(Integer::parseInt).map(AttendeesColumn::getAttendeesColumnFromInt);
            }
            catch (IllegalArgumentException e){
                System.out.println(e.getMessage());
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid orderLabel parameter");
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

            try {
                attendances = attendanceDAO.getOrderedAttendances(roundId, orderLabel.getName(), reverse);
                round = roundDAO.getRoundById(roundId);
                course = courseDAO.getCourseByRoundId(roundId);
            }
            catch (UnavailableException e){
                System.out.println(e.getMessage());
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database access");
            }

            path = path + ATTENDEES_PAGE;
            ctx.setVariable("orderLabel", orderLabel.getName());
            ctx.setVariable("reverse", reverse);
            ctx.setVariable("attendances", attendances);
            ctx.setVariable("round", round);
            ctx.setVariable("course", course);
            templateEngine.process(path, ctx, response.getWriter());

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
            }

            path = path + RESULT_PAGE;
            ctx.setVariable("attendance", attendance);
            ctx.setVariable("round", round);
            ctx.setVariable("course", course);
            ctx.setVariable("prof", prof);
            templateEngine.process(path, ctx, response.getWriter());
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
