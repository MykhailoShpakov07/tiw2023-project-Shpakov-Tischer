package com.example.project2023shpakovtischer.controllers;

import com.example.project2023shpakovtischer.beans.CourseBean;
import com.example.project2023shpakovtischer.beans.RoundBean;
import com.example.project2023shpakovtischer.beans.UserBean;
import com.example.project2023shpakovtischer.dao.CourseDAO;
import com.example.project2023shpakovtischer.dao.RoundDAO;
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

import static com.example.project2023shpakovtischer.utils.Paths.*;

@WebServlet(name = "GetCourseRounds", value = GET_COURSE_ROUNDS_SERVLET)
public class GetCourseRounds extends HttpServlet {

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

        List<RoundBean> rounds = null;
        CourseBean course = null;
        CourseDAO courseDAO = new CourseDAO(connection);
        RoundDAO roundDAO = new RoundDAO(connection);
        int courseId = -1;
        UserBean user = (UserBean) request.getSession().getAttribute("user");

        try {
            courseId = Integer.parseInt(request.getParameter("courseId"));
        }
        catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid courseId parameter");
        }

        try {
            course = courseDAO.getCourseById(courseId);
            if(user.getRole().equals(UserRole.PROFESSOR)) {
                rounds = roundDAO.getRoundsByCourseId(courseId);
            } else if (user.getRole().equals(UserRole.STUDENT)) {
                rounds = roundDAO.getRoundsByCourseIdAndStudentId(courseId, user.getId());
            }
        } catch (UnavailableException e) {
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database access while retrieving course rounds");
        }

        ctx.setVariable("course", course);
        ctx.setVariable("getRoundServletPath", GET_ROUND_SERVLET);
        ctx.setVariable("rounds", rounds);
        templateEngine.process(COURSE_ROUNDS_PAGE, ctx, response.getWriter());
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
