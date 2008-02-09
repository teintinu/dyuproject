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

package com.dyuproject.web;

import java.util.HashMap;
import java.util.Map;

/**
 * @author David Yu
 * @created Feb 9, 2008
 */

public class HttpMethod
{

    public static final HttpMethod GET = new HttpMethod("GET", 0);
    public static final HttpMethod POST = new HttpMethod("POST", 1);
    public static final HttpMethod PUT = new HttpMethod("PUT", 2);
    public static final HttpMethod DELETE = new HttpMethod("DELETE", 3);
    
    private static final Map<String, HttpMethod> __methods = new HashMap<String, HttpMethod>();
    static 
    {
        __methods.put(GET.getName(), GET);
        __methods.put(POST.getName(), POST);
        __methods.put(PUT.getName(), PUT);
        __methods.put(DELETE.getName(), DELETE);
    }
    
    public static HttpMethod get(String method)
    {
        return __methods.get(method);
    }
    
    public static int getType(String method)
    {
        HttpMethod m = __methods.get(method);
        return m==null ? -1 : m.getType();
    }
    
    private String _name;
    private int _type;
    
    private HttpMethod(String name, int type)
    {
        _name = name;
        _type = type;
    }
    
    public String getName()
    {
        return _name;
    }
    
    public int getType()
    {
        return _type;
    }
    
    public int hashCode()
    {
        return _name.hashCode();
    }
    
    public static abstract class Hash
    {
        public static final int GET = "GET".hashCode();
        public static final int POST = "POST".hashCode();
        public static final int PUT = "PUT".hashCode();
        public static final int DELETE = "DELETE".hashCode();
    }
    
}
