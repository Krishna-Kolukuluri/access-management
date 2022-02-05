package com.vmware.accessmanagement.service;


import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class RequestResponseLoggingServiceImpl implements RequestResponseLoggingService {
    @Override
    public void logRequest(HttpServletRequest httpServletRequest, Object body) {
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, String> parameters = buildParametersMap(httpServletRequest);

        stringBuilder.append("REQUEST ");
        stringBuilder.append("method=[").append(httpServletRequest.getMethod()).append("] ");
        stringBuilder.append("path=[").append(httpServletRequest.getRequestURI()).append("] ");
        stringBuilder.append("headers=[").append(buildHeadersMap(httpServletRequest)).append("] ");

        if (!parameters.isEmpty()) {
            stringBuilder.append("parameters=[").append(parameters).append("] ");
            if(parameters.containsKey("logLevel")){
                setLogLevel(parameters.get("logLevel"));
            }
        }

        if (body != null) {
            stringBuilder.append("body=[" + body + "]");
        }

        log.info(stringBuilder.toString());
    }

    @Override
    public void logResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object body) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("RESPONSE ");
        stringBuilder.append("method=[").append(httpServletRequest.getMethod()).append("] ");
        stringBuilder.append("path=[").append(httpServletRequest.getRequestURI()).append("] ");
        stringBuilder.append("responseHeaders=[").append(buildHeadersMap(httpServletResponse)).append("] ");
        stringBuilder.append("responseBody=[").append(body).append("] ");

        log.debug(stringBuilder.toString());
        resetLogLevel();
    }

    private Map<String, String> buildParametersMap(HttpServletRequest httpServletRequest) {
        Map<String, String> resultMap = new HashMap<>();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = httpServletRequest.getParameter(key);
            resultMap.put(key, value);
        }

        return resultMap;
    }

    private Map<String, String> buildHeadersMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

    private Map<String, String> buildHeadersMap(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();

        Collection<String> headerNames = response.getHeaderNames();
        for (String header : headerNames) {
            map.put(header, response.getHeader(header));
        }

        return map;
    }

    private void setLogLevel(String logLevel){
        if(null != logLevel) {
            Level newLogLevel;
            if (logLevel.equalsIgnoreCase(Level.DEBUG.name())) {
                newLogLevel = Level.DEBUG;
            } else if (logLevel.equalsIgnoreCase(Level.ERROR.name())) {
                newLogLevel = Level.ERROR;
            } else if (logLevel.equalsIgnoreCase(Level.TRACE.name())) {
                newLogLevel = Level.TRACE;
            } else if (logLevel.equalsIgnoreCase(Level.WARN.name())) {
                newLogLevel = Level.WARN;
            } else if (logLevel.equalsIgnoreCase(Level.OFF.name())) {
                newLogLevel = Level.OFF;
            } else if (logLevel.equalsIgnoreCase(Level.FATAL.name())) {
                newLogLevel = Level.FATAL;
            } else if (logLevel.equalsIgnoreCase(Level.INFO.name())) {
                newLogLevel = Level.INFO;
            } else {
                newLogLevel = Level.ALL;
            }
            Configurator.setAllLevels(LogManager.getRootLogger().getName(), newLogLevel);
        }

    }

    private void resetLogLevel(){
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.INFO);
    }
}
