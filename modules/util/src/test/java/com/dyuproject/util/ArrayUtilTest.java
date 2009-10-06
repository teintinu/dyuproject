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

import junit.framework.TestCase;

/**
 * @author David Yu
 * @created Jan 16, 2009
 */

public class ArrayUtilTest extends TestCase
{
    
    public static class Foo
    {
        final int _id;
        
        public Foo(int id)
        {
            _id = id;
        }
        
        public String toString()
        {
            return String.valueOf(_id);
        }
    }
    
    public void testAppend()
    {
        Foo[] foos = new Foo[0];
        foos = ArrayUtil.append(foos, new Foo(1));
        foos = ArrayUtil.append(foos, new Foo(2));
        foos = ArrayUtil.append(foos, new Foo(3));
        foos = ArrayUtil.append(foos, new Foo(4));
        foos = ArrayUtil.append(foos, new Foo(5));
        assertTrue(foos.length==5);
        System.err.println("len: " + foos.length);
        
        foos = ArrayUtil.append(foos, foos);
        assertTrue(foos.length==10);
        System.err.println("len: " + foos.length);
        
        foos = ArrayUtil.append(foos, foos, foos);
        assertTrue(foos.length==30);
        System.err.println("len: " + foos.length);
        Foo _100 = new Foo(100);
        
        foos = ArrayUtil.append(foos, foos, _100);
        assertTrue(foos.length==61);
        assertTrue(_100==foos[60]);
        System.err.println("len: " + foos.length);
        Foo _200 = new Foo(200);
        
        foos = ArrayUtil.append(foos, _200, foos);
        assertTrue(foos.length==123);
        System.err.println("len: " + foos.length);
        assertTrue(_200==foos[61]);
    }
    
    public void testRemove()
    {
        Foo[] foos = new Foo[0];
        foos = ArrayUtil.append(foos, new Foo(1));
        foos = ArrayUtil.append(foos, new Foo(2));
        foos = ArrayUtil.append(foos, new Foo(3));
        foos = ArrayUtil.append(foos, new Foo(4));
        foos = ArrayUtil.append(foos, new Foo(5));
        
        print(foos);
        assertTrue(foos.length==5);
        assertTrue(foos[2]._id==3);
        
        // remove mid
        foos = ArrayUtil.remove(foos, 2);
        print(foos);
        assertTrue(foos.length==4);
        assertTrue(foos[0]._id==1);
        assertTrue(foos[1]._id==2);
        assertTrue(foos[2]._id==4);
        assertTrue(foos[3]._id==5);
        
        // remove first
        foos = ArrayUtil.remove(foos, 0);
        print(foos);
        assertTrue(foos.length==3);
        assertTrue(foos[0]._id==2);
        assertTrue(foos[1]._id==4);
        assertTrue(foos[2]._id==5);
        
        // remove last
        foos = ArrayUtil.remove(foos, 2);
        print(foos);
        assertTrue(foos.length==2);
        assertTrue(foos[0]._id==2);
        assertTrue(foos[1]._id==4);
        
    }
    
    static void print(Foo[] foos)
    {
        System.err.print('[');
        for(int i=0; i<foos.length; i++)
        {
            System.err.print(foos[i]);
        }
        System.err.println(']');
    }
    
    

}
