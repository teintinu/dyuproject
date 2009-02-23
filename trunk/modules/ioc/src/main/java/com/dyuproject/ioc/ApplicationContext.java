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
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.dyuproject.ioc.config.OverrideReferences;
import com.dyuproject.ioc.config.References;


/**
 * @author David Yu
 * @created Feb 20, 2009
 */

public class ApplicationContext
{
    
    public static final String DEFAULT_RESOURCE_LOCATION = "application.json";
    
    public static ApplicationContext load()
    {
        return load(DEFAULT_RESOURCE_LOCATION);
    }
    
    public static ApplicationContext load(String resource)
    {
        try
        {
            Parser parser = Parser.getDefault();
            return load(parser.getResolver().createResource(resource), parser);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static ApplicationContext load(File resource)
    {
        try
        {
            return load(FileResolver.getDefault().createResource(resource), Parser.getDefault());
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }        
    }
    
    public static ApplicationContext load(URL resource)
    {
        try
        {
            return load(URLResolver.getDefault().createResource(resource), Parser.getDefault());
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }        
    }
    
    public static ApplicationContext load(InputStream resource)
    {
        try
        {
            return load(new Resource(URLResolver.getDefault().newReader(resource)), 
                    Parser.getDefault());
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }        
    }
    
    public static ApplicationContext load(Reader resource)
    {
        return load(new Resource(resource), Parser.getDefault());
    }
    
    public static ApplicationContext load(Resource resource, Parser parser)
    {
        ApplicationContext ac = new ApplicationContext();
        parser.parse(resource, ac);
        return ac;
    }
    
    public static ApplicationContext load(String[] resources)
    {
        try
        {
            ApplicationContext ac = null;
            Parser parser = Parser.getDefault();
            for(String r : resources)
            {
                ac = new ApplicationContext(ac);
                parser.parse(parser.getResolver().createResource(r.trim()), ac);
            }
            return ac;
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    } 
    
    public static void main(String[] args)
    {
        if(args==null)
            load(DEFAULT_RESOURCE_LOCATION);
        else
            load(args);
    }

    
    static Object getPojo(String key, ApplicationContext ac)
    {
        if(ac._pojos==null)
            return ac._refs==null ? null : References.getRef(key, ac._refs);
        
        Object pojo = ac._pojos.get(key);
        return pojo!=null ? pojo : (ac._refs==null ? null : References.getRef(key, ac._refs));
    }
    
    static Object findRef(String key, ApplicationContext ac)
    {
        Object ref = References.getRef(key, ac._refs);
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
    private Map<String,Object> _pojos;
    private References _refs;
    
    public ApplicationContext()
    {
        
    }
    
    public ApplicationContext(ApplicationContext imported)
    {
        addImport(imported);
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
    
    public boolean setPojo(String key, Object value)
    {
        if(key==null)
            return false;
        
        if(_pojos==null)
            _pojos = new HashMap<String,Object>();
        
        _pojos.put(key, value);
        return true;
    }
    
    public Object findRef(String key)
    {
        return key==null ? null : findRef(key, this);
    }
    
    public Object getRef(String key)
    {
        return key==null || _refs==null ? null : References.getRef(key, _refs);
    }
    
    public boolean setRef(String key, Object value)
    {
        if(key==null)
            return false;
        
        if(_refs==null)
            _refs = new References(new HashMap<String,Object>());
        
        _refs.put(key, value);        
        return true;
    }
    
    public void addRefs(References refs)
    {
        if(refs==null)
            return;
        
        if(_refs==null)
            _refs = refs;
        else if(refs instanceof OverrideReferences)
        {
            References.wrapRefs(_refs, References.getLast(refs));
            _refs = refs;
        }
        else
            References.wrapRefs(refs, _refs);
    }
    
    public void addImport(ApplicationContext imported)
    {
        if(imported==null)
            return;
        
        addImport(imported, this);
    }
    
    public void wrap(Map<String,Object> pojos)
    {
        if(_pojos!=null)
            pojos.putAll(_pojos);

        _pojos = pojos;
    }
    
    public void destroy()
    {
        if(_imported!=null)
            _imported.destroy();
        _imported = null;
        
        if(_refs!=null)
            _refs.destroy();
        _refs = null;     
        
        if(_pojos!=null)
            _pojos.clear();
        _pojos = null;
    }
    
    

}
