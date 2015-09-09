package com.jsjy.order.manager.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

/**
 * 添加自定义filter
 * Created by shaojieyue on 9/9/15.
 */
@Component
public class SessionFilter implements Filter{
    public static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("check the session.");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    public void destroy() {

    }
}
