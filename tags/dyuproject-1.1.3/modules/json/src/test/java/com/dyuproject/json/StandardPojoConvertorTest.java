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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.mortbay.util.ajax.JSON.ReaderSource;

import com.dyuproject.json.test.Employee;
import com.dyuproject.json.test.Task;

/**
 * @author David Yu
 * @created Feb 23, 2009
 */

public class StandardPojoConvertorTest extends TestCase
{
    
    public void testCollection() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/json/test/employee.json";
        StandardJSON json = new StandardJSON();
        File file = new File(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        Map<String,Object> map = (Map<String,Object>)json.parse(new ReaderSource(reader));
        Employee e = (Employee)map.get("employee");
        assertTrue(e!=null);
        assertTrue("John".equals(e.getFirstName()));
        List<Task> tasks = e.getTasks();
        assertTrue(tasks!=null && tasks.size()==3);
        assertTrue("10".equals(tasks.get(0).getStatus()));
        assertTrue("15".equals(tasks.get(1).getStatus()));
        assertTrue("20".equals(tasks.get(2).getStatus()));
    }

}
