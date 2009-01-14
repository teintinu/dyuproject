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

import java.util.Properties;

import junit.framework.TestCase;

import com.dyuproject.util.xml.Node;

/**
 * @author David Yu
 * @created Jan 14, 2009
 */

public class ManifestTest extends TestCase
{
    
    public void test1()
    {
        Properties foo = Manifest.getProperties("foo.properties");
        assertTrue(foo!=null);
        assertTrue(foo.size()==7);
        System.err.println(foo);
        
        Node persistence = Manifest.getNode("persistence.xml");
        System.err.println(persistence);
        assertTrue("persistence".equals(persistence.getName()));
        assertTrue(persistence.size()==1);
        Node unit = persistence.getNode("persistence-unit");
        assertTrue("todo-list-persistence".equals(unit.getAttribute("name")));
        assertTrue("RESOURCE_LOCAL".equals(unit.getAttribute("transaction-type")));
        assertTrue(unit.size()==3);
        
        Node provider = unit.getFirstNode();
        Node class1 = unit.getNode(1);
        Node class2 = unit.getLastNode();
        assertTrue("org.hibernate.ejb.HibernatePersistence".equals(provider.getText().toString()));
        assertTrue("com.dyuproject.demos.todolist.model.User".equals(class1.getText().toString()));
        assertTrue("com.dyuproject.demos.todolist.model.Todo".equals(class2.getText().toString()));
    }

}
