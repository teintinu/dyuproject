//========================================================================
//Copyright 2007-2008 David Yu dyuproject@gmail.com
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

package com.dyuproject.util.xml;

import java.util.Stack;


/**
 * Simple Handler to generate a root node with all its child nodes.
 * 
 * @author David Yu
 * @created Sep 18, 2008
 */

public class SimpleHandler implements LazyHandler
{

    private Stack<Node> _stack = new Stack<Node>();
    private Node _root;
    
    public Node getNode()
    {
        return _root;
    }
    
    public boolean rootElement(String name)
    {        
        _stack.clear();
        _root = new SimpleNode(name);
        _stack.push(_root);
        return true;
    }

    public boolean startElement(String name)
    {       
        _stack.push(new SimpleNode(name, _stack.peek()));
        return true;
    }

    public boolean endElement()
    {
        _stack.pop();
        return true;
    }    

    public void attribute(String name, String value)
    {        
        _stack.peek().setAttribute(name, value);            
    }

    public void characters(char[] data, int start, int length)
    {
        _stack.peek().addText(data, start, length);            
    }

}
