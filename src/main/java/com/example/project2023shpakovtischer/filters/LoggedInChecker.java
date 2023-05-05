package com.example.project2023shpakovtischer.filters;

import com.example.project2023shpakovtischer.beans.UserBean;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.project2023shpakovtischer.utils.Paths.*;

@WebFilter(filterName = "LoggedInChecker")
public class LoggedInChecker extends HttpFilter {
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());

        boolean isLogin = request.getRequestURI().equals(request.getContextPath() + LOGIN_PAGE)
                || request.getRequestURI().equals(request.getContextPath() + CHECK_LOGIN_SERVLET)
                || request.getRequestURI().equals(request.getContextPath() + DEFAULT_SERVLET);

        if(isLogin) {
            if(request.getSession().getAttribute("user") == null) {
                ctx.setVariable("message", "");
                ctx.removeVariable("message");
                chain.doFilter(request, response);
            }
            else {
                response.sendRedirect(getServletContext().getContextPath() + GET_COURSES_SERVLET);
            }
        }
        else {
            UserBean user = (UserBean) request.getSession().getAttribute("user");
            if (user != null) {
                ctx.setVariable("message", "");
                ctx.removeVariable("message");
                chain.doFilter(request, response);
            } else {
                ctx.setVariable("message", "You must be logged in to access this page !");
                templateEngine.process(LOGIN_PAGE, ctx, response.getWriter());
            }
        }
    }

}
