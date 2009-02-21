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

import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author David Yu
 * @created Feb 21, 2009
 */

public class ApplicationContextTest extends TestCase
{
    
    public void testClassPathResource() throws IOException
    {
        String resource = "classpath:com/dyuproject/ioc/basic.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        Person person = (Person)ac.findPojo("person");
        assertTrue(person.getAge()==20);
    }
    
    public void testBasic() throws Exception
    {        
        String resource = "src/test/resources/com/dyuproject/ioc/basic.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        Person person = (Person)ac.findPojo("person");
        assertTrue("john_doe".equals(person.getUsername()));
        assertTrue(person.getAge()==20);
    }
    
    public void testImport() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/ioc/test_import.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        assertTrue(ac.getPojo("person")==null);
        Person person = (Person)ac.findPojo("person");
        assertTrue(person.getAge()==20);
        
        Person person1 = (Person)ac.getPojo("person1");
        assertTrue(person.getAge()==person1.getAge());
        assertTrue(person.getEmail().endsWith(person1.getEmail()));
        
    }

}
