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

package com.dyuproject.util;

import java.lang.reflect.Array;

/**
 * @author David Yu
 * @created Jan 16, 2009
 */

public final class ArrayUtil
{
    
    public static Object[] append(Object[] oldArray, Object toAppend)
    {
        Class<?> component = oldArray.getClass().getComponentType();
        Object[] array = (Object[])Array.newInstance(component, Array.getLength(oldArray)+1);
        System.arraycopy(oldArray, 0, array, 0, oldArray.length);
        array[oldArray.length] = toAppend;
        return array;
    }    
    
    public static Object[] append(Object[] oldArray, Object toAppend1, Object[] toAppend2)
    {
        Class<?> component = oldArray.getClass().getComponentType();
        Object[] array = (Object[])Array.newInstance(component, 
                Array.getLength(oldArray) + 1 + Array.getLength(toAppend2));
        System.arraycopy(oldArray, 0, array, 0, oldArray.length);
        array[oldArray.length] = toAppend1;
        System.arraycopy(toAppend2, 0, array, oldArray.length+1, toAppend2.length);
        return array;
    }
    
    public static Object[] append(Object[] oldArray, Object[] toAppend)
    {
        Class<?> component = oldArray.getClass().getComponentType();
        Object[] array = (Object[])Array.newInstance(component, 
                Array.getLength(oldArray) + Array.getLength(toAppend));
        System.arraycopy(oldArray, 0, array, 0, oldArray.length);
        System.arraycopy(toAppend, 0, array, oldArray.length, toAppend.length);
        return array;
    }
    
    public static Object[] append(Object[] oldArray, Object[] toAppend1, Object toAppend2)
    {
        Class<?> component = oldArray.getClass().getComponentType();
        Object[] array = (Object[])Array.newInstance(component, 
                Array.getLength(oldArray) + Array.getLength(toAppend1) + 1);
        System.arraycopy(oldArray, 0, array, 0, oldArray.length);
        System.arraycopy(toAppend1, 0, array, oldArray.length, toAppend1.length);
        array[array.length-1] = toAppend2;
        return array;
    }
    
    public static Object[] append(Object[] oldArray, Object[] toAppend1, Object[] toAppend2)
    {
        Class<?> component = oldArray.getClass().getComponentType();
        Object[] array = (Object[])Array.newInstance(component, 
                Array.getLength(oldArray) + Array.getLength(toAppend1) + Array.getLength(toAppend2));
        System.arraycopy(oldArray, 0, array, 0, oldArray.length);
        System.arraycopy(toAppend1, 0, array, oldArray.length, toAppend1.length);
        System.arraycopy(toAppend2, 0, array, oldArray.length + toAppend1.length, toAppend2.length);
        return array;
    }

}
