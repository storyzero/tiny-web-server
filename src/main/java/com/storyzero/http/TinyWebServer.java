package com.storyzero.http;

import com.storyzero.servlet.container.TinyHttpServletRequest;
import com.storyzero.servlet.container.TinyHttpServletResponse;
import com.storyzero.servlet.container.TinyServletContainer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TinyWebServer implements Runnable {
    private static final int THREAD_COUNT = 5;
    private static final int PORT = 1212;

    protected ServerSocket serverSocket;
    protected Thread[] threadArr;

    public static void main(String[] args) {
        TinyWebServer application = new TinyWebServer(THREAD_COUNT);
        application.start();
    }

    public TinyWebServer(int num) {
        try {
            serverSocket = new ServerSocket(PORT);
            threadArr = new Thread[num];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        for (int i = 0; i < threadArr.length; i++) {
            threadArr[i] = new Thread(this);
            threadArr[i].start();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println(getTime() + " Waiting");

                Socket socket = serverSocket.accept();
                System.out.println(getTime() + " accepted");

                HashMap<String, String> reqMap = readRequest(socket);

                if (!TinyServletContainer.hasServlet(reqMap.get("uri"))) {
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                    String content = "<HTML>" +
                            "<HEAD><TITLE>Hello</TITLE></HEAD>" +
                            "<BODY><H2>tiny web server is work!</H2>" +
                            "</BODY></HTML>";
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/html; charset=UTF-8");
                    out.println("Content-Length: " + content.length());
                    out.println("");
                    out.println("<HTML>");
                    out.println("<HEAD><TITLE>Hello</TITLE></HEAD>");
                    out.println("<BODY>");
                    out.println("<H2>tiny web server is work!</H2>");
                    out.println("</BODY></HTML>");
                    out.flush();
                    System.out.println(getTime() + " sended");
                } else {
                    TinyHttpServletRequest req = new TinyHttpServletRequest(socket, reqMap);
                    TinyHttpServletResponse resp = new TinyHttpServletResponse(socket);
                    TinyServletContainer.service(req, resp);
                }

                socket.close();
                System.out.println(getTime() + " closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected HashMap<String, String> readRequest(Socket socket) {
        HashMap<String, String> reqMap = new HashMap<>();

        try {
            LineNumberReader r = new LineNumberReader(new InputStreamReader(socket.getInputStream()));

            String l = r.readLine();
            String[] arr = l.split("\\s");
            reqMap.put("method", arr[0]);
            reqMap.put("uri", arr[1]);
            reqMap.put("httpversion", arr[2]);

            while (!(l = r.readLine()).isEmpty()) {
                int idx = l.indexOf(":");
                reqMap.put(l.substring(0, idx),
                        l.substring(idx + 1, l.length()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reqMap;
    }

    protected static String getTime() {
        return new SimpleDateFormat("[hh:mm:ss]").format(new Date()) + Thread.currentThread().getName();
    }
}