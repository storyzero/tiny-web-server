package com.storyzero.servlet.container;

import com.storyzero.servlet.HelloServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by kakao on 2016. 2. 18..
 */
public class TinyServletContainer {
    private static HashMap<String, Class> servletMap;

    static
    {
        servletMap = new HashMap<>();
        servletMap.put("/hello", HelloServlet.class);
    }

    public static boolean hasServlet(String uri) {
        return servletMap.containsKey(uri);
    }

    public static void service(HttpServletRequest req, HttpServletResponse resp) {
        Class klass = servletMap.get(req.getRequestURI());
        Object inst = null;

        try {
            inst = klass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (inst == null) {
            // set resp for error??
        }

        HttpServlet servlet = (HttpServlet) inst;
        try {
            servlet.service(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
