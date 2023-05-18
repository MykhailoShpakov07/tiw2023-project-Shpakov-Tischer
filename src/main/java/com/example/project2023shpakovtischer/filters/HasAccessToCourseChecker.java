package com.example.project2023shpakovtischer.filters;

import com.example.project2023shpakovtischer.beans.CourseBean;
import com.example.project2023shpakovtischer.beans.RoundBean;
import com.example.project2023shpakovtischer.beans.UserBean;
import com.example.project2023shpakovtischer.dao.CourseDAO;
import com.example.project2023shpakovtischer.dao.RoundDAO;
import com.example.project2023shpakovtischer.enums.UserRole;
import com.example.project2023shpakovtischer.utils.ConnectionHandler;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

@WebFilter(filterName = "HasAccessToCourseChecker")
public class HasAccessToCourseChecker extends HttpFilter {
    private Connection connection = null;
    public void init(FilterConfig config) throws ServletException {
        connection = ConnectionHandler.getConnection(config.getServletContext());
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        int CourseId = 0;
        try {
            CourseId = Integer.parseInt(request.getParameter("courseId"));
        } catch (NumberFormatException e){
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "courseId is not an integer!");
            return;
        }
        //LoggedInChecker must be applied before this filter
        int UserId = ((UserBean) request.getSession().getAttribute("user")).getId();
        UserRole role = ((UserBean) request.getSession().getAttribute("user")).getRole();
        if(role.equals(UserRole.PROFESSOR)){
            CourseDAO courseDAO = new CourseDAO(connection);
            CourseBean course = courseDAO.getCourseById(CourseId);
            if(course == null){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid courseId");
                return;
            }
            else {
                if (course.getProfessorId() == UserId) {
                    chain.doFilter(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not authorized to access this course");
                    return;
                }
            }
        } else if (role.equals(UserRole.STUDENT)) {
            RoundDAO roundDAO = new RoundDAO(connection);
            ArrayList<RoundBean> rounds = roundDAO.getRoundsByCourseIdAndStudentId(CourseId, UserId);
            //check if the course exists
            CourseDAO courseDAO = new CourseDAO(connection);
            CourseBean course = courseDAO.getCourseById(CourseId);
            if(course == null){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid courseId");
                return;
            }
            else {
                //check if user has at least one round relative to that course
                if (rounds.size() > 0) {
                    chain.doFilter(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not authorized to access this course");
                    return;
                }
            }
        }
    }
}
