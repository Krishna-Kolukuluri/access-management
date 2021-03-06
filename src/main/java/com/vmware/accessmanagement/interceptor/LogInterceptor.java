package com.vmware.accessmanagement.interceptor;

import com.vmware.accessmanagement.service.RequestResponseLoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor class for logs
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

    @Autowired
    RequestResponseLoggingService loggingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //&&request.getMethod().equals(HttpMethod.GET.name())
        if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())) {
            loggingService.logRequest(request, null);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse, Object o,
                                Exception e) throws Exception {

    }
}
