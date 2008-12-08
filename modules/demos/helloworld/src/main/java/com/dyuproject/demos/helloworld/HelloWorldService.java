//========================================================================
//Copyright 2007-2008 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.demos.helloworld;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;

import org.codehaus.jra.Get;
import org.codehaus.jra.HttpResource;
import org.codehaus.jra.Post;

import com.dyuproject.web.mvc.dispatcher.VelocityDispatcher;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.WebContext;
import com.dyuproject.web.rest.service.AbstractService;

/**
 * @author David Yu
 * @created Dec 6, 2008
 */

public class HelloWorldService extends AbstractService
{

    protected void init()
    {        
        getWebContext().addViewDispatcher("velocity", new VelocityDispatcher());
    }
    
    /* --------- VIEWS (JSP) --------- */
    
    @HttpResource(location="/")
    @Get
    public void root() throws IOException, ServletException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        getWebContext().getJSPDispatcher().dispatch("index.jsp", rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/helloworld")
    @Get
    public void helloworld() throws IOException, ServletException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getRequest().setAttribute("message", "Hello world!");
        rc.getRequest().setAttribute("timestamp", new Date());
        getWebContext().getJSPDispatcher().dispatch("helloworld/index.jsp", rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/helloworld")
    @Post
    public void helloworldPost() throws IOException, ServletException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getRequest().setAttribute("message", "This is a POST request");
        rc.getRequest().setAttribute("timestamp", new Date());
        getWebContext().getJSPDispatcher().dispatch("helloworld/index.jsp", rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/helloworld/$")
    @Get
    public void helloworldEntity() throws IOException, ServletException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getRequest().setAttribute("message", "get entity: " + rc.getPathElement(1));
        rc.getRequest().setAttribute("timestamp", new Date());
        getWebContext().getJSPDispatcher().dispatch("helloworld/index.jsp", rc.getRequest(), rc.getResponse());
    }
    
    /* --------- Digest Authentication (See DigestAuthInterceptor) --------- */
    
    @HttpResource(location="/helloworld/protected")
    @Get
    public void helloworldProtected() throws IOException, ServletException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getRequest().setAttribute("message", "This resource needed authentication ... 30-second expiry. (see DigestAuthInterceptor)");
        rc.getRequest().setAttribute("timestamp", new Date());
        getWebContext().getJSPDispatcher().dispatch("helloworld/index.jsp", rc.getRequest(), rc.getResponse());
    }
    
    /* --------- VIEWS (VELOCITY) --------- */
    
    @HttpResource(location="/velocity/helloworld")
    @Get
    public void velocityHelloworld() throws IOException, ServletException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getRequest().setAttribute("message", "Hello world!");
        rc.getRequest().setAttribute("timestamp", new Date());
        getWebContext().getViewDispatcher("velocity").dispatch("helloworld/index.vm", rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/velocity/helloworld/$")
    @Get
    public void velocityHelloworldEntity() throws IOException, ServletException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getRequest().setAttribute("message", "get entity: " + rc.getPathElement(2));
        rc.getRequest().setAttribute("timestamp", new Date());
        getWebContext().getViewDispatcher("velocity").dispatch("helloworld/index.vm", rc.getRequest(), rc.getResponse());
    }
    
    /* --------- BROWSER FORM VERBS --------- */
    
    @HttpResource(location="/bean/new")
    @Get
    public void beanCreate() throws IOException, ServletException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getRequest().setAttribute("message", "add new entity");
        getWebContext().getJSPDispatcher().dispatch("index.jsp", rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/bean/$/edit")
    @Get
    public void beanUpdate() throws IOException, ServletException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getRequest().setAttribute("message", "edit bean: " + rc.getPathElement(1));
        getWebContext().getJSPDispatcher().dispatch("index.jsp", rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/bean/$/delete")
    @Get
    public void beanDelete() throws IOException, ServletException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getRequest().setAttribute("message", "delete bean: " + rc.getPathElement(1));
        getWebContext().getJSPDispatcher().dispatch("index.jsp", rc.getRequest(), rc.getResponse());
    }    
    
    /* --------- INTERCEPTED RESOURCES --------- */
    // /foo/* /foo/bar/* and /foo/bar/baz will be intercepted.
    
    @HttpResource(location="/foo")
    @Get
    public void foo() throws IOException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getResponse().setContentType("text/plain");
        rc.getResponse().getOutputStream().print(" # foo #");
    }
    
    @HttpResource(location="/foo/bar")
    @Get
    public void fooBar() throws IOException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getResponse().setContentType("text/plain");
        rc.getResponse().getOutputStream().print(" # foo bar #");
    }
    
    @HttpResource(location="/foo/bar/baz")
    @Get
    public void fooBarBaz() throws IOException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getResponse().setContentType("text/plain");
        rc.getResponse().getOutputStream().print(" # foo bar baz #");
    }
    
    /* --------- PLAIN TEXT RESOURCES --------- */
    
    @HttpResource(location="/hello")
    @Get
    public void hello() throws IOException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getResponse().setContentType("text/plain");
        rc.getResponse().getOutputStream().print("hello!");
    }
    
    @HttpResource(location="/hello/$")
    @Get
    public void helloStranger() throws IOException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getResponse().setContentType("text/plain");
        rc.getResponse().getOutputStream().print("hello " + rc.getPathElement(1) + "!");
    }
    
    @HttpResource(location="/hello/$/world")
    @Get
    public void helloStrangerWorld() throws IOException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getResponse().setContentType("text/plain");
        rc.getResponse().getOutputStream().print("hello " + rc.getPathElement(1) + " world!");
    }
    
    @HttpResource(location="/hello/sexy/$")
    @Get
    public void helloSexyStranger() throws IOException
    {
        RequestContext rc = WebContext.getCurrentRequestContext();
        rc.getResponse().setContentType("text/plain");
        rc.getResponse().getOutputStream().print("hello sexy " + rc.getPathElement(2) + "!");
    }

}