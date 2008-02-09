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

package com.dyuproject.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author David Yu
 * @date Feb 9, 2008
 */

public class AbstractHibJdbcDao extends AbstractHibDao
{
    
    private Properties _propertyMapping;

    public void setPropertyMapping(Properties propertyMapping)
    {
        _propertyMapping = propertyMapping;
    }
    
    public void setPropertyMappingResource(String resource)
    {
        if(_propertyMapping!=null)
            throw new IllegalStateException("property mapping already set");
        try
        {
            _propertyMapping = new Properties();
            _propertyMapping.load(getClass().getClassLoader().getResourceAsStream(resource));            
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public void setPropertyMappingLocation(File location) 
    {
        if(_propertyMapping!=null)
            throw new IllegalStateException("property mapping already set");
        try 
        {
            _propertyMapping = new Properties();
            _propertyMapping.load(new FileInputStream(location));
        } 
        catch(Exception e) 
        {
            throw new RuntimeException(e);
        }
    }
    
    public Properties getPropertyMapping()
    {
        return _propertyMapping;
    }
}
