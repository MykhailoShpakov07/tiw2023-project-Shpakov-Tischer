package com.example.project2023shpakovtischer.controllers;

import com.example.project2023shpakovtischer.beans.AttendanceBean;
import com.example.project2023shpakovtischer.beans.UserBean;
import com.example.project2023shpakovtischer.dao.AttendanceDAO;
import com.example.project2023shpakovtischer.utils.ConnectionHandler;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static com.example.project2023shpakovtischer.utils.Paths.GET_ROUND_SERVLET;
import static com.example.project2023shpakovtischer.utils.Paths.REFUSE_MARK_SERVLET;

@WebServlet(name = "RefuseMark", value = REFUSE_MARK_SERVLET)
public class RefuseMark extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public void init() throws ServletException {
        try {
            connection = ConnectionHandler.getConnection(getServletContext());
        }
        catch (UnavailableException e){
            throw new RuntimeException(e);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int studentId = 0;
        int roundId = 0;
        try {
            studentId = ((UserBean) request.getSession().getAttribute("user")).getId();
            roundId = Integer.parseInt(request.getParameter("roundId"));
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid roundId parameter");
            return;
        }
        AttendanceDAO attendanceDAO = new AttendanceDAO(connection);
        try {
            AttendanceBean attendance = attendanceDAO.getAttendance(roundId, studentId);
            if (attendance.getEvaluationStatus().getValue() == 3) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The mark has already been refused");
                return;
            } else if (attendance.getEvaluationStatus().getValue() == 4) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The mark has already been reported");
                return;
            } else if (attendance.getEvaluationStatus().getValue() < 2) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "It is not possible to refuse the mark at this time");
                return;
            }
            attendanceDAO.refuseMark(roundId, studentId);
        } catch (UnavailableException e) {
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "It was not possible to refuse the mark");
            return;
        }
        response.sendRedirect(getServletContext().getContextPath() + GET_ROUND_SERVLET + "?roundId=" + roundId);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public void destroy() {
        try{
        ConnectionHandler.closeConnection(connection);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

}
