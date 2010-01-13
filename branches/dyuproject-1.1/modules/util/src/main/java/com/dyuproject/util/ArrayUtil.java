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
    
    /**
     * Copies the elements of the {@code oldArray} to a new array with extra space to 
     * append the given element {@code toAppend}.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] append(T[] oldArray, T toAppend)
    {
        Class<?> component = oldArray.getClass().getComponentType();
        T[] array = (T[])Array.newInstance(component, oldArray.length+1);
        System.arraycopy(oldArray, 0, array, 0, oldArray.length);
        array[oldArray.length] = toAppend;
        return array;
    }    
    
    /**
     * Copies the elements of the {@code oldArray} to a new array with extra space to 
     * append the given element {@code toAppend1} and the array {@code toAppend2}.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] append(T[] oldArray, T toAppend1, T[] toAppend2)
    {
        Class<?> component = oldArray.getClass().getComponentType();
        T[] array = (T[])Array.newInstance(component, 
                oldArray.length + 1 + toAppend2.length);
        System.arraycopy(oldArray, 0, array, 0, oldArray.length);
        array[oldArray.length] = toAppend1;
        System.arraycopy(toAppend2, 0, array, oldArray.length+1, toAppend2.length);
        return array;
    }
    
    /**
     * Copies the elements of the {@code oldArray} to a new array with extra space to 
     * append the given array {@code toAppend}.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] append(T[] oldArray, T[] toAppend)
    {
        Class<?> component = oldArray.getClass().getComponentType();
        T[] array = (T[])Array.newInstance(component, 
                oldArray.length + toAppend.length);
        System.arraycopy(oldArray, 0, array, 0, oldArray.length);
        System.arraycopy(toAppend, 0, array, oldArray.length, toAppend.length);
        return array;
    }
    
    /**
     * Copies the elements of the {@code oldArray} to a new array with extra space to 
     * append the given array {@code toAppend1} and the element {@code toAppend2}.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] append(T[] oldArray, T[] toAppend1, T toAppend2)
    {
        Class<?> component = oldArray.getClass().getComponentType();
        T[] array = (T[])Array.newInstance(component, 
                oldArray.length + toAppend1.length + 1);
        System.arraycopy(oldArray, 0, array, 0, oldArray.length);
        System.arraycopy(toAppend1, 0, array, oldArray.length, toAppend1.length);
        array[array.length-1] = toAppend2;
        return array;
    }
    
    /**
     * Copies the elements of the {@code oldArray} to a new array with extra space to 
     * append the given array {@code toAppend1} and the array {@code toAppend2}.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] append(T[] oldArray, T[] toAppend1, T[] toAppend2)
    {
        Class<?> component = oldArray.getClass().getComponentType();
        T[] array = (T[])Array.newInstance(component, 
                oldArray.length + toAppend1.length + toAppend2.length);
        System.arraycopy(oldArray, 0, array, 0, oldArray.length);
        System.arraycopy(toAppend1, 0, array, oldArray.length, toAppend1.length);
        System.arraycopy(toAppend2, 0, array, oldArray.length + toAppend1.length, toAppend2.length);
        return array;
    }
    
    /**
     * Returns a copy of the old array {@code oldArray} but with the element at the 
     * give index {@code idx} removed.
     * 
     * @throws {@link IllegalArgumentException} if the array index is out of bounds.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] remove(T[] oldArray, int idx)
    {
        if(idx<0 || idx>=oldArray.length)
            throw new IllegalArgumentException("array index " + idx + " out of bounds");
        
        Class<?> component = oldArray.getClass().getComponentType();
        T[] array = (T[])Array.newInstance(component, oldArray.length-1);
        if(idx==0)
            System.arraycopy(oldArray, 1, array, 0, array.length);
        else
        {
            System.arraycopy(oldArray, 0, array, 0, idx);
            System.arraycopy(oldArray, idx+1, array, idx, array.length-idx);
        }
        return array;
    }

}
