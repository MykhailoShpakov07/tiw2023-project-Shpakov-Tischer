package com.example.project2023shpakovtischer.filters;


import com.example.project2023shpakovtischer.dao.AttendanceDAO;
import com.example.project2023shpakovtischer.dao.CourseDAO;
import com.example.project2023shpakovtischer.dao.RoundDAO;
import com.example.project2023shpakovtischer.enums.UserRole;
import com.example.project2023shpakovtischer.javaBeans.AttendanceBean;
import com.example.project2023shpakovtischer.javaBeans.CourseBean;
import com.example.project2023shpakovtischer.javaBeans.RoundBean;
import com.example.project2023shpakovtischer.javaBeans.UserBean;
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

@WebFilter(filterName = "HasAccessToRoundChecker")
public class HasAccessToRoundChecker extends HttpFilter {
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
        int RoundId = 0;
        try {
            RoundId = Integer.parseInt(request.getParameter("RoundId"));
        } catch (NumberFormatException e){
            response.sendError(400);
        }
        int UserId = ((UserBean) request.getSession().getAttribute("user")).getId();
        UserRole role = ((UserBean) request.getSession().getAttribute("user")).getRole();
        if(role.equals(UserRole.PROFESSOR)){
            RoundDAO roundDAO = new RoundDAO(connection);
            RoundBean round = roundDAO.getRoundById(RoundId);
            if (round.getProfessorId() == UserId){
                chain.doFilter(request, response);
            } else {
                response.sendError(403);
            }
        } else if (role.equals(UserRole.STUDENT)) {
            AttendanceDAO attendanceDAO = new AttendanceDAO(connection);
            AttendanceBean attendance = attendanceDAO.getAttendance(RoundId, UserId);

            if (attendance != null){
                chain.doFilter(request, response);
            } else {
                response.sendError(403);
            }
        }
    }

}
