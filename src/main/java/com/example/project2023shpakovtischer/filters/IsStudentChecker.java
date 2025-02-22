package com.example.project2023shpakovtischer.filters;

import com.example.project2023shpakovtischer.beans.UserBean;
import com.example.project2023shpakovtischer.enums.UserRole;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "IsStudentChecker")
public class IsStudentChecker extends HttpFilter {

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        UserBean user = (UserBean) request.getSession().getAttribute("user");

        if (user.getRole().equals(UserRole.STUDENT)) {
            chain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Only student can access this page!");
        }
    }
}
