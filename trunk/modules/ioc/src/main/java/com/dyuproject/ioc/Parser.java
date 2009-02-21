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

import org.mortbay.util.ajax.JSON;



/**
 * @author David Yu
 * @created Feb 21, 2009
 */

public abstract class Parser extends JSON
{
    
    private static Parser __default;
    
    private static final ThreadLocal<Context> __context = new ThreadLocal<Context>();    
    
    public static Parser getDefault()
    {
        // TODO support pluggable parse handlers
        if(__default==null)
        {
            synchronized(Parser.class)
            {
                if(__default==null)
                    __default = new DefaultParser();
            }
        }
        return __default;
    }
    
    public static Context getCurrentContext()
    {
        return __context.get();
    }
    
    protected static void setCurrentContext(Context context)
    {
        __context.set(context);
    }
    
    protected ConvertorCache _convertorCache;
    protected SourceFactory _sourceFactory;
    
    public ConvertorCache getConvertorCache()
    {
        return _convertorCache;
    }    
    
    protected void setConvertorCache(ConvertorCache convertorCache)
    {
        _convertorCache = convertorCache;
    }
    
    public SourceFactory getSourceFactory()
    {
        return _sourceFactory;
    }
    
    protected void setSourceFactory(SourceFactory sourceFactory)
    {
        _sourceFactory = sourceFactory;
    }
    
    protected Convertor getConvertor(Class clazz)
    {
        return getConvertorCache().getConvertor(clazz, true);
    }
    
    public abstract void parse(Source source, ApplicationContext appContext);
    
    public static class Context
    {
        
        private Parser _parser;
        private ApplicationContext _appContext;
        
        protected Context(Parser parser, ApplicationContext appContext)
        {
            _parser = parser;
            _appContext = appContext;
        }
        
        /*protected ApplicationContext setAppContext(ApplicationContext appContext)
        {
            ApplicationContext last = _appContext;
            _appContext = appContext;
            return last;
        }*/
        
        public Parser getParser()
        {
            return _parser;
        }
        
        public ApplicationContext getAppContext()
        {
            return _appContext;
        }
        
        void clear()
        {
            _parser = null;
            _appContext = null;
        }
        
    }

}
