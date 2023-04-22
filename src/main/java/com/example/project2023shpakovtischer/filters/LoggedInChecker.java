package com.example.project2023shpakovtischer.filters;

import com.example.project2023shpakovtischer.javaBeans.UserBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter
public class LoggedInChecker extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        UserBean user = (UserBean) request.getSession().getAttribute("user");
        if (user == null) {
            //render login page with error message

        }
        else{

        }
    }

}
