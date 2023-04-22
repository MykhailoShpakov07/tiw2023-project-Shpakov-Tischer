package com.example.project2023shpakovtischer.controllers;

import com.example.project2023shpakovtischer.dao.CourseDAO;
import com.example.project2023shpakovtischer.dao.RoundDAO;
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
import java.util.List;

import static com.example.project2023shpakovtischer.utils.Paths.GET_COURSE_ROUNDS_SERVLET;
import static com.example.project2023shpakovtischer.utils.Paths.HOME_PAGE;

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
        //TODO complete
        ServletContext servletContext = getServletContext();
        String path = servletContext.getContextPath();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        List<RoundBean> rounds = null;
        CourseBean course = null;
        CourseDAO courseDAO = new CourseDAO(connection);
        RoundDAO roundDAO = new RoundDAO(connection);
        int courseId = -1;

        UserBean user = (UserBean) request.getSession().getAttribute("user");

        if (user == null) {
            //render login page with error message
            path = path + "/login.html";
            ctx.setVariable("message", "You must be logged in to view this page");
            templateEngine.process(path, ctx, response.getWriter());
        }

        try {
            courseId = Integer.parseInt(request.getParameter("courseId"));
        }
        catch (NumberFormatException e) {
            ctx.setVariable("message", "Invalid course id");
            templateEngine.process(HOME_PAGE, ctx, response.getWriter());
        }

        try {
            course = courseDAO.getCourseById(courseId);
            rounds = roundDAO.getRoundsByCourseId(courseId);
        } catch (UnavailableException e) {
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database access");
        }

        ctx.setVariable("course", course);
        ctx.setVariable("rounds", rounds);
        templateEngine.process(path, ctx, response.getWriter());
    }
}
