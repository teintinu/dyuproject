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

import org.mortbay.util.ajax.JSON.Convertor;
import org.mortbay.util.ajax.JSON.Output;

import com.dyuproject.ioc.config.Reference;

/**
 * @author David Yu
 * @created Feb 21, 2009
 */

public class DefaultConvertorCache extends StandardConvertorCache
{
    
    public DefaultConvertorCache()
    {
        addConvertor(Reference.class, new Convertor()
        {
            public Object fromJSON(Map map)
            {
                Object ref = map.get("ref");
                return ref==null ? null : getConvertor(ref.getClass(), true).fromJSON(map);
            }
            public void toJSON(Object obj, Output out)
            {
                getConvertor(obj.getClass(), true).toJSON(obj, out);                
            }            
        });
    }
    
    public Convertor createConvertor(Class<?> clazz)
    {
        return new DefaultPojoConvertor(clazz);
    }

}
