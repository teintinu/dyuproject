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

package com.dyuproject.demos.simpleservice;

import java.io.IOException;

import org.codehaus.jra.Get;
import org.codehaus.jra.HttpResource;
import org.codehaus.jra.Post;

import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.service.AbstractService;

/**
 * @author David Yu
 * @created Dec 4, 2008
 */

public class MyService extends AbstractService
{

    protected void init()
    {        
        
    }
    
    @HttpResource(location="/")
    @Get
    public void root() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("hello world service!");
    }
    
    @HttpResource(location="/foo")
    @Get
    public void foo() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("foo!");
    }
    
    @HttpResource(location="/foo/{a}")
    @Get
    public void fooById() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("foo with pathParameter: " + context.getPathElement(1));
    }
    
    @HttpResource(location="/foo/{a}/ff")
    @Get
    public void fooByIdff() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("foo with pathParameter: " + context.getPathElement(1) + " | ff");
    }
    
    @HttpResource(location="/foo/bar")
    @Get
    public void fooBar() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("foo & bar!");
    }
    
    @HttpResource(location="/foo/bar/ff")
    @Get
    public void fooBarFF() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("foo & bar & ff!");
    }
    
    @HttpResource(location="/foo/baz")
    @Post
    public void fooBaz() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("foo & baz @ POST!");
    }
    
    @HttpResource(location="/hello")
    @Get
    public void hello() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("hello there!");
    }
    
    @HttpResource(location="/a/b/c")
    @Get
    public void abc() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("abc");
    }
    
    @HttpResource(location="/a/b/c/d/e")
    @Get
    public void abcde() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("abcde");
    }
    
    @HttpResource(location="/a/b/$/d/e")
    @Get
    public void ab$de() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("ab | " + context.getPathElement(2) +  " | de");
    }
    
    @HttpResource(location="/1/2/$")
    @Get
    public void _12$() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("12$");
    }
    
    @HttpResource(location="/3/$/4")
    @Get
    public void _3$4() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("3$5");
    }
    
    @HttpResource(location="/5/$/6/$/7")
    @Get
    public void _5$6$7() throws IOException
    {
        RequestContext context = getWebContext().getRequestContext();
        context.getResponse().setContentType("text/plain");
        context.getResponse().getOutputStream().print("5" + context.getPathElement(1) + "6"+ context.getPathElement(3) + "7");
    }


}
