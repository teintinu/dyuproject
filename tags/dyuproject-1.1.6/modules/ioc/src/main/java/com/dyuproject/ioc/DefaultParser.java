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

import java.util.HashMap;
import java.util.Map;

import org.mortbay.log.Log;

import com.dyuproject.ioc.Resource.Resolver;
import com.dyuproject.json.ConvertorCache;

/**
 * @author David Yu
 * @created Feb 21, 2009
 */

public class DefaultParser extends Parser
{
    
    public DefaultParser()
    {
        this(new DefaultConvertorCache(), DefaultResolver.DEFAULT);
    }
    
    public DefaultParser(ConvertorCache convertorCache)
    {
        this(convertorCache, DefaultResolver.DEFAULT);
    }
    
    public DefaultParser(ConvertorCache convertorCache, Resolver resolver)
    {
        super(convertorCache, resolver);
    }
    
    public DefaultParser(Resolver resolver)
    {
        this(new DefaultConvertorCache(), resolver);
    }
    
    @SuppressWarnings("unchecked")
    public void parse(Resource resource, ApplicationContext appContext)
    {
        if(!resource.isResolved())
            throw new IllegalStateException("resource not resolved.");
        
        Context context = new Context(resource, appContext, this);
        try
        {
            Context.setCurrent(context);
            Map<String,Object> pojos = (Map<String,Object>)parse(resource.getSource());
            if(appContext.isWrapPojos())
                appContext.wrap(pojos);
        }
        finally
        {            
            Context.setCurrent(null);
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
            Context context = Context.getCurrent();
            if(context==null)
            {
                Log.warn("${} not found", buffer);
                return null;
            }

            return context.getAppContext().findPojo(buffer.toString());
        }
        return super.handleUnknown(source, c);
    }
    
    @SuppressWarnings("unchecked")
    protected Map newMap()
    {
        Map<String,Object> map = new HashMap<String,Object>();
        Context context = Context.getCurrent();
        if(context!=null && context.getAppContext().isWrapPojos())
            context.getAppContext().wrap(map);
        return map;
    }

}
