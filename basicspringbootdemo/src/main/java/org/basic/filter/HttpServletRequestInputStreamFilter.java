package org.basic.filter;

import org.basic.utils.InputStreamHttpServletRequestWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/4 15:12
 * @Description:
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE +1)
public class HttpServletRequestInputStreamFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 转换可以多次换取流的request
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        InputStreamHttpServletRequestWrapper inputStreamHttpServletRequestWrapper = new InputStreamHttpServletRequestWrapper(httpServletRequest);
        filterChain.doFilter(inputStreamHttpServletRequestWrapper,servletResponse);
    }
}
