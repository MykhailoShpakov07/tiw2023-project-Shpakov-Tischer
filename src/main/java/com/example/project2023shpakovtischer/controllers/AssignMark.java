package com.example.project2023shpakovtischer.controllers;

import com.example.project2023shpakovtischer.dao.AttendanceDAO;
import com.example.project2023shpakovtischer.enums.Mark;
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

import static com.example.project2023shpakovtischer.utils.Paths.ASSIGN_MARK_SERVLET;
import static com.example.project2023shpakovtischer.utils.Paths.GET_ROUND_SERVLET;

@WebServlet(name = "AssignMark", value = ASSIGN_MARK_SERVLET)
public class AssignMark extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Connection connection = null;

    public void init() throws ServletException {
        try{
            connection = ConnectionHandler.getConnection(getServletContext());
        }
        catch (UnavailableException e){
            throw new RuntimeException(e);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        int roundId = 0;
        int studentId = 0;
        int mark = 0;
        try {
            roundId = Integer.parseInt(request.getParameter("roundId"));
            studentId = Integer.parseInt(request.getParameter("studentId"));
            mark = Integer.parseInt( request.getParameter("mark"));
            if(Mark.getMarkFromInt(mark) == null){
                throw new NumberFormatException("Invalid mark");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid student id or round id or mark");
            return;
        }
        AttendanceDAO attendanceDAO = new AttendanceDAO(connection);
        try {
            attendanceDAO.assignMark(roundId, studentId, mark);
        } catch (UnavailableException e) {
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in database while assigning mark");
            return;
        }

        response.sendRedirect(getServletContext().getContextPath() + GET_ROUND_SERVLET + "?roundId=" + roundId);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        doPost(request, response);
    }

    public void destroy() {
        try{
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}