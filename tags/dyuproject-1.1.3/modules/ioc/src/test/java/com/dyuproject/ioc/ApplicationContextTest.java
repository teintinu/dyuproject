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

import junit.framework.TestCase;

import com.dyuproject.ioc.test.Person;

/**
 * @author David Yu
 * @created Feb 21, 2009
 */

public class ApplicationContextTest extends TestCase
{
    
    public void testBasic() throws Exception
    {        
        String resource = "src/test/resources/com/dyuproject/ioc/test/basic.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        Person person = (Person)ac.findPojo("person");
        assertTrue("John".equals(person.getFirstName()));
        assertTrue(20==person.getAge());
    }
    
    public void testImportBasic() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/ioc/test/import_basic.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        assertTrue(ac.getPojo("person")==null);
        Person person = (Person)ac.findPojo("person");
        assertTrue("John".equals(person.getFirstName()));
        assertTrue(20==person.getAge());
        
        Person person1 = (Person)ac.getPojo("person1");
        assertTrue("John".equals(person1.getFirstName()));
        assertTrue(21==person1.getAge());
    }
    
    public void testReference() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/ioc/test/reference.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        Person person2 = (Person)ac.findPojo("person2");
        assertTrue("John".equals(person2.getFirstName()));
        assertTrue(22==person2.getAge());
    }
    
    public void testImportReference() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/ioc/test/import_reference.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        Person person3 = (Person)ac.findPojo("person3");
        assertTrue("John".equals(person3.getFirstName()));
        assertTrue(23==person3.getAge());
    }
    
    public void testImportBasicReference() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/ioc/test/import_basic_reference.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        Person person = (Person)ac.findPojo("person");
        Person person1 = (Person)ac.findPojo("person1");
        Person person2 = (Person)ac.findPojo("person2");
        Person person3 = (Person)ac.findPojo("person3");
        Person person4 = (Person)ac.findPojo("person4");
        assertTrue(person!=null);
        assertTrue(person1!=null);
        assertTrue(person2!=null);
        assertTrue(person3!=null);
        assertTrue(person4!=null);
        assertTrue(person2==person3);
        assertTrue(20==person.getAge());
        assertTrue(21==person1.getAge());
        assertTrue(23==person3.getAge());
        assertTrue(24==person4.getAge());
    }

}
