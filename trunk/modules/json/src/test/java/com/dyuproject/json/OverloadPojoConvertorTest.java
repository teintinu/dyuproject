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
import java.util.Set;

import junit.framework.TestCase;

import org.mortbay.util.ajax.JSON.ReaderSource;

import com.dyuproject.json.test.BusyEmployee;
import com.dyuproject.json.test.Employee;
import com.dyuproject.json.test.OverloadTask;
import com.dyuproject.json.test.Task;

/**
 * @author David Yu
 * @created Feb 23, 2009
 */

public class OverloadPojoConvertorTest extends TestCase
{
    
    public void testOverloadTask() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/json/test/overload_task.json";
        StandardJSON json = new StandardJSON(new OverloadConvertorCache());
        File file = new File(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        Map<String,Object> map = (Map<String,Object>)json.parse(new ReaderSource(reader));
        Task task = (Task)map.get("task");
        assertTrue(task!=null);
        assertTrue("10".equals(task.getStatus()));
        
        OverloadTask otask = (OverloadTask)map.get("overload_task");
        assertTrue(otask!=null);
        assertTrue(otask.getStatus()==null);
        assertTrue(otask.getStatusInt()==5);
        
        OverloadTask otask1 = (OverloadTask)map.get("overload_task1");
        assertTrue(otask1!=null);
        assertTrue(otask1.getStatus()==null);
        assertTrue(otask1.getStatusPojo()!=null);
        assertTrue("7.5".equals(otask1.getStatusPojo().getId()));
    }
    
    public void testCollection() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/json/test/employees.json";
        StandardJSON json = new StandardJSON(new OverloadConvertorCache());
        File file = new File(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        Map<String,Object> map = (Map<String,Object>)json.parse(new ReaderSource(reader));
        Employee e = (Employee)map.get("employee");
        BusyEmployee be = (BusyEmployee)map.get("busy_employee");
        assertTrue(e!=null);
        assertTrue(be!=null);
        assertTrue("John".equals(e.getFirstName()));
        assertTrue("John".equals(be.getFirstName()));
        checkTasks(e.getTasks());
        checkTasks(be.getTasks());
        checkExtraTasks(be.getExtraTasks());
    }
    
    static void checkTasks(List<Task> tasks)
    {
        assertTrue(tasks!=null && tasks.size()==3);
        assertTrue("10".equals(tasks.get(0).getStatus()));
        assertTrue(tasks.get(1) instanceof OverloadTask);
        assertTrue(tasks.get(2) instanceof OverloadTask);
        OverloadTask otask = (OverloadTask)tasks.get(1);
        OverloadTask otask1 = (OverloadTask)tasks.get(2);
        assertTrue(otask.getStatusInt()==5);
        assertTrue(otask1.getStatusPojo()!=null);
        assertTrue("7.5".equals(otask1.getStatusPojo().getId())); 
    }
    
    static void checkExtraTasks(Set<Task> tasks)
    {
        assertTrue(tasks!=null && tasks.size()==3);
        for(Task t : tasks)
        {
            if(t instanceof OverloadTask)
            {
                OverloadTask otask = (OverloadTask)t;
                if(otask.getStatusInt()!=0)
                {
                    assertTrue(otask.getStatusInt()==5);
                }
                else
                {
                    assertTrue(otask.getStatusPojo()!=null);
                    assertTrue("7.5".equals(otask.getStatusPojo().getId())); 
                }
            }
            else
            {
                assertTrue("10".equals(t.getStatus()));
            }
        }
    }

}
