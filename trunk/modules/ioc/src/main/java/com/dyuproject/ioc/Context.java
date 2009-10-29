//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
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

package com.dyuproject.ioc;

/**
 * The parsing context
 * 
 * @author David Yu
 * @created Feb 23, 2009
 */

public final class Context
{
    
    private static final ThreadLocal<Context> __current = new ThreadLocal<Context>();
    
    /**
     * Gets the current thread local context.
     */
    public static Context getCurrent()
    {
        return __current.get();
    }
    
    /**
     * Sets the thread local context.
     */
    public static void setCurrent(Context context)
    {
        __current.set(context);
    }
    
    private Resource _resource;
    private ApplicationContext _appContext;
    private Parser _parser;
    
    public Context(Resource resource, ApplicationContext appContext, Parser parser)
    {
        _resource = resource;            
        _appContext = appContext;
        _parser = parser;
    }
    
    /**
     * Gets the {@link Resource} tied to this context.
     */
    public Resource getResource()
    {
        return _resource;
    }        
    
    /**
     * Gets the {@link ApplicationContext} tied to this context.
     */
    public ApplicationContext getAppContext()
    {
        return _appContext;
    }
    
    /**
     * Gets the {@link Parser} tied to this context.
     */
    public Parser getParser()
    {
        return _parser;
    }
    
    protected void clear()
    {
        _parser = null;
        _appContext = null;
        _resource = null;
    }

}
