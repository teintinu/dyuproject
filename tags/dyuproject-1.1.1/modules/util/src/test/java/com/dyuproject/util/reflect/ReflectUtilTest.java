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

package com.dyuproject.util.reflect;

import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author David Yu
 * @created Jan 13, 2009
 */

public class ReflectUtilTest extends TestCase
{
    
    public void testReflect()
    {
        Map<String,Method> getters = ReflectUtil.getGetterMethods(Foo.class);
        Map<String,Method> setters = ReflectUtil.getSetterMethods(Foo.class);
        assertTrue(getters.size()==3 && setters.size()==3);
        assertTrue(getters.get("name")!=null);
        assertTrue(getters.get("bar")!=null);
        assertTrue(getters.get("baz")!=null);
        assertTrue(setters.get("name")!=null);
        assertTrue(setters.get("bar")!=null);
        assertTrue(setters.get("baz")!=null);
    }
    
    public static class Foo
    {
        String _name;
        boolean _bar;
        Long _baz;
        public String getName()
        {
            return _name;
        }
        public void setName(String name)
        {
            _name = name;
        }
        public boolean isBar()
        {
            return _bar;
        }
        public void setBar(boolean bar)
        {
            _bar = bar;
        }
        public Long getBaz()
        {
            return _baz;
        }
        public void setBaz(Long baz)
        {
            _baz = baz;
        }
    }

}
