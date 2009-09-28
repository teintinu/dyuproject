package ${package}.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HelloServlet
 * 
 */

@SuppressWarnings("serial")
public class HelloServlet extends HttpServlet
{
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        response.setContentType("text/json");
        String message = "hello from ${artifactId}-server @ " + System.currentTimeMillis();
        PrintWriter pw = response.getWriter();
        pw.write("{\"msg\":\"" + message + "\"}");
        pw.close();
    }

}
