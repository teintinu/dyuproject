package ${package}.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Application entry point
 * 
 */

public class Application implements EntryPoint
{

    public void onModuleLoad()
    {
        RootPanel.get().add(new Label("${artifactId}! from gwt-jetty-maven archetype"));
        
    }

}
