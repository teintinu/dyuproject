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
    
    private static final boolean __trimText = !"false".equals(System.getProperty(
            "simplehandler.trimText"));

    private final Stack<Node> _stack = new Stack<Node>();
    private Node _root;
    private final boolean _trimText;
    
    public SimpleHandler()
    {
        this(__trimText);
    }
    
    public SimpleHandler(boolean trimText)
    {
        _trimText = trimText;
    }
    
    public boolean isTrimText()
    {
        return _trimText;
    }
    
    public Node getNode()
    {
        return _root;
    }
    
    public boolean rootElement(String name, String namespace)
    {        
        _stack.clear();
        _root = new SimpleNode(name, namespace);
        _stack.push(_root);
        return true;
    }

    public boolean startElement(String name, String namespace)
    {       
        _stack.push(new SimpleNode(name, _stack.peek(), namespace));
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
        if(_trimText)
        {
            for(int end=start+length; end>start && Character.isWhitespace(data[--end]); length--);                
            
            if(length!=0)
                _stack.peek().addText(data, start, length);
        }
        else
        {
            _stack.peek().addText(data, start, length);
        }         
    }

}
