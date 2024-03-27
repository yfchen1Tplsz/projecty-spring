package com.wind.rider.ioc01.infrastructure.componet;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

public class CustomRequestScope implements Scope {

    private final Map<String, Object> requestScopedObjects = new HashMap<>();

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        if (!requestScopedObjects.containsKey(name)) {
            requestScopedObjects.put(name, objectFactory.getObject());
        }
        return requestScopedObjects.get(name);
    }

    @Override
    public Object remove(String name) {
        return requestScopedObjects.remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        // No destruction callbacks supported
    }

    @Override
    public Object resolveContextualObject(String key) {
        if ("request".equals(key)) {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                return requestAttributes.getRequest();
            }
        }
        return null;
    }

    @Override
    public String getConversationId() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            if (request != null) {
                return request.getSession().getId();
            }
        }
        return null;
    }
}