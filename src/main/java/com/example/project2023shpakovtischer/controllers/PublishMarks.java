package com.example.project2023shpakovtischer.controllers;

import com.example.project2023shpakovtischer.dao.AttendanceDAO;
import com.example.project2023shpakovtischer.utils.ConnectionHandler;

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

import static com.example.project2023shpakovtischer.utils.Paths.GET_ROUND_SERVLET;
import static com.example.project2023shpakovtischer.utils.Paths.PUBLISH_MARKS_SERVLET;


@WebServlet(name = "PublishMarks", value = PUBLISH_MARKS_SERVLET)
public class PublishMarks extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        try {
            connection = ConnectionHandler.getConnection(servletContext);
        } catch (UnavailableException e) {
            System.out.println(e.getMessage());
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        String path = servletContext.getContextPath();
        int roundId = -1;

        try {
            roundId = Integer.parseInt(request.getParameter("roundId"));
        }
        catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid roundId parameter");
        }

        //no need to control if user can publish marks, because publishMarks() method
        //does nothing if there are no marks to publish
        AttendanceDAO attendanceDAO = new AttendanceDAO(connection);
        try {
            attendanceDAO.publishMarks(roundId);
        } catch (UnavailableException e) {
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in database while publishing marks");
        }

        response.sendRedirect(path + GET_ROUND_SERVLET + "?roundId=" + roundId);
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
