package ${package}.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ${package}.model.Model.User;
import ${package}.json.ModelJSON;

/**
 * HelloServlet
 * 
 */

@SuppressWarnings("serial")
public class HelloServlet extends HttpServlet
{

    final ModelJSON json = new ModelJSON();
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        response.setContentType("text/json");
        User johndoe =  User.newBuilder().setId(1).setEmail("johndoe@email.com").build();
        json.writeTo(response.getOutputStream(), johndoe);
    }

}
