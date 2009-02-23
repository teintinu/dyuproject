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

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.dyuproject.ioc.test.BusyEmployee;
import com.dyuproject.ioc.test.Employee;
import com.dyuproject.ioc.test.OverloadTask;
import com.dyuproject.ioc.test.Task;

/**
 * @author David Yu
 * @created Feb 23, 2009
 */

public class StandardPojoConvertorTest extends TestCase
{
    
    public void testCollection() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/ioc/test/employees.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        Employee e = (Employee)ac.findPojo("employee");
        BusyEmployee be = (BusyEmployee)ac.findPojo("busy_employee");
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
