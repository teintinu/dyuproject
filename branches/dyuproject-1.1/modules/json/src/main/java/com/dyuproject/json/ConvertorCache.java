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

package com.dyuproject.json;

import org.mortbay.util.ajax.JSON.Convertor;

/**
 * A convertor cache to allow re-use of convertors.
 * 
 * @author David Yu
 * @created Feb 21, 2009
 */

public interface ConvertorCache extends Convertor
{
    
    /**
     * Gets a convertor from the given {@code clazz} and will create one if 
     * not found the flag {@code create} is true
     */
    public Convertor getConvertor(Class<?> clazz, boolean create);
    /**
     * Gets a convertor from the given {@code clazz} and will create one if 
     * not found the flag {@code create} is true; If {@code addClass} is true, 
     * the convertor will be configured to include the classname upon serialization.
     */
    public Convertor getConvertor(Class<?> clazz, boolean create, boolean addClass);
    /**
     * Gets a convertor from the given {@code clazz}.
     */
    public Convertor getConvertor(Class<?> clazz);
    /**
     * Adds the {@code convertor} mapped to the given {@code clazz}.
     */
    public boolean addConvertor(Class<?> clazz, Convertor convertor);
    /**
     * Checks if a convertor is mapped to the given {@code clazz}.
     */
    public boolean hasConvertor(Class<?> clazz);
    /**
     * Creats a convertor based from the given {@code clazz}.
     */
    public Convertor newConvertor(Class<?> clazz);
    /**
     * Creats a convertor based from the given {@code clazz}; If {@code addClass} is true, 
     * the convertor will be configured to include the classname upon serialization.
     */
    public Convertor newConvertor(Class<?> clazz, boolean addClass);

}
