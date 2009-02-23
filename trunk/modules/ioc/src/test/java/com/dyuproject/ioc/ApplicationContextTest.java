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
        assertTrue("john_doe".equals(person.getUsername()));
        assertTrue(person.getAge()==20);
    }
    
    public void testImportBasic() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/ioc/test/import_basic.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        assertTrue(ac.getPojo("person")==null);
        Person person = (Person)ac.findPojo("person");
        assertTrue(person.getAge()==20);
        
        Person person1 = (Person)ac.getPojo("person1");
        assertTrue(person1.getAge()==21);
        assertTrue(person.getEmail().equals(person1.getEmail()));        
    }
    
    public void testReference() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/ioc/test/reference.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        Person person2 = (Person)ac.findPojo("person2");
        assertTrue(person2.getAge()==22);
    }
    
    public void testImportReference() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/ioc/test/import_reference.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        Person person3 = (Person)ac.findPojo("person3");
        assertTrue(person3.getFirstName().equals("John"));
        assertTrue(person3.getAge()==23);
    }

}
