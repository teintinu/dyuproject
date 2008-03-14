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

package com.dyuproject.util;

/**
 * @author David Yu
 * @created Mar 14, 2008
 */

public abstract class ParameterType
{
    
    public static final ParameterType STRING = new ParameterType(){
        public Object create(String value)
        {
            return value;
        }
        public Class getTypeClass()
        {
            return String.class;
        }
    };
    
    public static final ParameterType BOOLEAN = new ParameterType(){
        public Object create(String value)
        {
            return new Boolean(value);
        }
        public Class getTypeClass()
        {
            return Boolean.class;
        }
    };
    
    public static final ParameterType PRIMITIVE_BOOLEAN = new ParameterType(){
        public Object create(String value)
        {
            return Boolean.parseBoolean(value);
        }
        public Class getTypeClass()
        {
            return Boolean.TYPE;
        }
    };
    
    public static final ParameterType INTEGER = new ParameterType(){
        public Object create(String value)
        {
            return new Integer(value);
        }
        public Class getTypeClass()
        {
            return Integer.class;
        }
    };
    
    public static final ParameterType PRIMITIVE_INTEGER = new ParameterType(){
        public Object create(String value)
        {
            return Integer.parseInt(value);
        }
        public Class getTypeClass()
        {
            return Integer.TYPE;
        }
    };
    
    public static final ParameterType LONG = new ParameterType(){
        public Object create(String value)
        {
            return new Long(value);
        }
        public Class getTypeClass()
        {
            return Long.class;
        }
    };
    
    public static final ParameterType PRIMITIVE_LONG = new ParameterType(){
        public Object create(String value)
        {
            return Long.parseLong(value);
        }
        public Class getTypeClass()
        {
            return Long.TYPE;
        }
    };
    
    public static final ParameterType FLOAT = new ParameterType(){
        public Object create(String value)
        {
            return new Float(value);
        }
        public Class getTypeClass()
        {
            return Float.class;
        }
    };
    
    public static final ParameterType PRIMITIVE_FLOAT = new ParameterType(){
        public Object create(String value)
        {
            return Float.parseFloat(value);
        }
        public Class getTypeClass()
        {
            return Float.TYPE;
        }
    };
    
    public static final ParameterType DOUBLE = new ParameterType(){
        public Object create(String value)
        {
            return new Double(value);
        }
        public Class getTypeClass()
        {
            return Double.class;
        }
    };
    
    public static final ParameterType PRIMITIVE_DOUBLE = new ParameterType(){
        public Object create(String value)
        {
            return Double.parseDouble(value);
        }
        public Class getTypeClass()
        {
            return Double.TYPE;
        }
    };
    
    public static final ParameterType SHORT = new ParameterType(){
        public Object create(String value)
        {
            return new Short(value);
        }
        public Class getTypeClass()
        {
            return Short.TYPE;
        }
    };
    
    public static final ParameterType PRIMITIVE_SHORT = new ParameterType(){
        public Object create(String value)
        {
            return Short.parseShort(value);
        }
        public Class getTypeClass()
        {
            return Short.class;
        }
    };
    
    private ParameterType()
    {
        
    }
    
    public abstract Object create(String value);
    public abstract Class getTypeClass();
    
    public int hashCode()
    {
        return getTypeClass().hashCode();
    }
    
}
