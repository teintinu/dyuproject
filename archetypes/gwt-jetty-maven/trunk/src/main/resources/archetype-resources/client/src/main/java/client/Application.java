package ${package}.client;

import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;



/**
 * Application entry point
 * 
 */

public class Application implements EntryPoint
{

    static final String JSON_URL = "/hello/";

    public void onModuleLoad()
    {
        Button button = new Button("Click me", new ClickHandler() {
            public void onClick(ClickEvent event) 
            {
                sendRequest();
            }
        });
        RootPanel.get().add(button);
    }
    
    static void sendRequest()
    {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, JSON_URL);
        try 
        {
            Request request = builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) 
                {
                    displayError("Couldn't retrieve JSON");
                }
                public void onResponseReceived(Request request, Response response) 
                {
                    if (response.getStatusCode() == 200)
                        showUser(User.parse(response.getText()));
                    else
                        displayError("Couldn't retrieve JSON");
                }
            });
        }
        catch (RequestException e) 
        {
            displayError("Couldn't retrieve JSON");
        } 
    }
    
    static void showUser(User user)
    {
        RootPanel.get().add(new Label(user.getId() + " " + user.getEmail()));
    }
    
    static void displayError(String error)
    {
        Window.alert(error);
    }

}
