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

import java.util.Map;

import org.mortbay.log.Log;

import com.dyuproject.ioc.factory.MultipleSourceFactory;

/**
 * @author David Yu
 * @created Feb 21, 2009
 */

public class DefaultParser extends Parser
{
    
    public DefaultParser()
    {
        this(new DefaultConvertorCache(), MultipleSourceFactory.getDefault());
    }
    
    public DefaultParser(ConvertorCache convertorCache)
    {
        this(convertorCache, MultipleSourceFactory.getDefault());
    }
    
    public DefaultParser(ConvertorCache convertorCache, SourceFactory sourceFactory)
    {
        setConvertorCache(convertorCache);
        setSourceFactory(sourceFactory);
    }
    
    public DefaultParser(SourceFactory sourceFactory)
    {
        this(new DefaultConvertorCache(), sourceFactory);
    }
    
    public void parse(Source source, ApplicationContext appContext)
    {
        Context context = new Context(this, appContext);
        try
        {
            setCurrentContext(context);
            appContext.wrap((Map<?,?>)parse(source));
        }
        finally
        {            
            setCurrentContext(null);
            context.clear();
        }
    }
    
    protected boolean isAllowed(char c)
    {
        return c=='_' || Character.isLetter(c) || Character.isDigit(c);
    }
    
    protected Object handleUnknown(Source source, char c)
    {
        if(c=='$')
        {
            source.next();
            StringBuffer buffer = new StringBuffer();
            while(source.hasNext() && isAllowed(source.peek()))
                buffer.append(source.next());
            
            if(buffer.length()==0)
            {
                Log.warn("empty reference string");
                return null;
            }
            Context context = getCurrentContext();
            if(context==null || context.getAppContext().getRefs()==null)
            {
                Log.warn("${} not found", buffer);
                return null;
            }

            return context.getAppContext().getRefs().get(buffer.toString());
        }
        return super.handleUnknown(source, c);
    }

}
