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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

import org.mortbay.log.Log;
import org.mortbay.util.Loader;
import org.mortbay.util.ajax.JSON.ReaderSource;
import org.mortbay.util.ajax.JSON.Source;

import com.dyuproject.ioc.factory.FileSourceFactory;
import com.dyuproject.ioc.factory.URLSourceFactory;

/**
 * @author David Yu
 * @created Feb 20, 2009
 */

public class ApplicationContext
{
    
    public static ApplicationContext load(String resource)
    {
        try
        {
            Parser parser = Parser.getDefault();
            return load(parser.getSourceFactory().getSource(resource), parser);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static ApplicationContext load(File resource)
    {
        try
        {
            return load(FileSourceFactory.getInstance().getSource(resource), Parser.getDefault());
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }        
    }
    
    public static ApplicationContext load(URL resource)
    {
        try
        {
            return load(URLSourceFactory.getInstance().getSource(resource), Parser.getDefault());
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }        
    }
    
    public static ApplicationContext load(InputStream resource)
    {
        try
        {
            return load(URLSourceFactory.getInstance().getSource(resource), Parser.getDefault());
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }        
    }
    
    public static ApplicationContext load(Reader resource)
    {
        try
        {
            return load(new ReaderSource(resource), Parser.getDefault());
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }        
    }
    
    public static ApplicationContext load(Source source, Parser parser)
    {
        ApplicationContext appContext = new ApplicationContext();
        parser.parse(source, appContext);
        return appContext;
    }
   
    public static Class<?> loadClass(String className)
    {
        Class<?> clazz = null;
        try
        {
            clazz = Loader.loadClass(ApplicationContext.class, className);
        }            
        catch(Throwable e)
        {
            Log.warn(e);
        }
        return clazz;
    }
    
    static References getLast(References refs)
    {
        return refs._refs==null ? refs : getLast(refs._refs);
    }
    
    static void wrapRefs(References refs, References wrapper)
    {
        if(wrapper._refs==null)
            wrapper._refs = refs;
        else
            wrapRefs(refs, wrapper._refs);          
    }
    
    static Object getRef(String key, References refs)
    {
        if(refs._map==null)
            return refs._refs==null ? null : getRef(key, refs._refs);
        
        Object value = refs._map.get(key);        
        return value!=null ? value : (refs._refs==null ? null : getRef(key, refs._refs));
    }
    
    static Object getPojo(String key, ApplicationContext ac)
    {
        if(ac._pojos==null)
            return ac._refs==null ? null : getRef(key, ac._refs);
        
        Object pojo = ac._pojos.get(key);
        return pojo!=null ? pojo : (ac._refs==null ? null : getRef(key, ac._refs));
    }
    
    static Object findRef(String key, ApplicationContext ac)
    {
        Object ref = getRef(key, ac._refs);
        return ref!=null ? ref : (ac._imported==null ? null : findRef(key, ac._imported));
    }
    
    static Object findPojo(String key, ApplicationContext ac)
    {
        Object pojo = getPojo(key, ac);        
        return pojo!=null ? pojo : (ac._imported==null ? null : findPojo(key, ac._imported));
    }
    
    static void addImport(ApplicationContext imported, ApplicationContext ac)
    {
        if(ac._imported==null)
            ac._imported = imported;
        else
        {
            ApplicationContext last = ac._imported;
            ac._imported = imported;
            addImport(last, ac._imported);
        }
    }

    private ApplicationContext _imported;
    private Map<?,?> _pojos;
    private References _refs;
    
    protected ApplicationContext()
    {
        
    }
    
    References getRefs()
    {
        return _refs;
    }
    
    public Object findPojo(String key)
    {
        return key==null ? null : findPojo(key, this);
    }
    
    public Object getPojo(String key)
    {
        return key==null || _pojos==null ? null : getPojo(key, this);
    }
    
    public Object findRef(String key)
    {
        return key==null ? null : findRef(key, this);
    }
    
    public Object getRef(String key)
    {
        return key==null || _refs==null ? null : getRef(key, _refs);
    }
    
    void addRefs(References refs)
    {
        if(refs==null)
            return;
        
        if(_refs==null)
            _refs = refs;
        else if(refs instanceof OverrideReferences)
        {
            wrapRefs(_refs, getLast(refs));
            _refs = refs;
        }
        else
            wrapRefs(refs, _refs);
    }
    
    void addImport(ApplicationContext imported)
    {
        if(imported==null)
            return;
        
        addImport(imported, this);
    }
    
    void wrap(Map<?,?> pojos)
    {
        _pojos = pojos;
    }
    
    public boolean destroy()
    {
        _pojos = null;
        _refs = null;        
        return true;
    }
    
    

}
