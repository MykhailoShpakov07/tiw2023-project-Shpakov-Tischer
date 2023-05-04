package com.example.project2023shpakovtischer.controllers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.project2023shpakovtischer.utils.Paths.LOGIN_PAGE;
import static com.example.project2023shpakovtischer.utils.Paths.LOGOUT_SERVLET;


@WebServlet(name = "Logout", value = LOGOUT_SERVLET)
public class Logout extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().invalidate();

        response.sendRedirect(getServletContext().getContextPath() + LOGIN_PAGE);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
