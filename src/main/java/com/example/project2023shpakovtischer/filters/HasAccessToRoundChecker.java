package com.example.project2023shpakovtischer.filters;

import com.example.project2023shpakovtischer.beans.AttendanceBean;
import com.example.project2023shpakovtischer.beans.RoundBean;
import com.example.project2023shpakovtischer.beans.UserBean;
import com.example.project2023shpakovtischer.dao.AttendanceDAO;
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
        int roundId = 0;
        try {
            roundId = Integer.parseInt(request.getParameter("roundId"));
        } catch (NumberFormatException e){
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "roundId parameter is not an integer!");
        }
        int userId = ((UserBean) request.getSession().getAttribute("user")).getId();
        UserRole role = ((UserBean) request.getSession().getAttribute("user")).getRole();
        if(role.equals(UserRole.PROFESSOR)){
            RoundDAO roundDAO = new RoundDAO(connection);
            RoundBean round = roundDAO.getRoundById(roundId);
            if (round == null){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, " roundId is not valid!");
            }
            else {
                if (round.getProfessorId() == userId) {
                    chain.doFilter(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Non sei autorizzato ad accedere a questo round!");
                }
            }
        } else if (role.equals(UserRole.STUDENT)) {
            RoundDAO roundDAO = new RoundDAO(connection);
            RoundBean round = roundDAO.getRoundById(roundId);

            if (round == null){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "roundId is not valid!");
            }
            else {
                AttendanceDAO attendanceDAO = new AttendanceDAO(connection);
                AttendanceBean attendance = attendanceDAO.getAttendance(roundId, userId);

                if (attendance != null){
                    chain.doFilter(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not authorized to access this round!");
                }
            }

        }
    }

}
