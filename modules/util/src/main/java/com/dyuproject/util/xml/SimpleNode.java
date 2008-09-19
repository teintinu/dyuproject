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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple XML to POJO bean
 * 
 * @author David Yu
 * @created Sep 18, 2008
 */

public class SimpleNode implements Node
{
    
    private Node _parent;
    private String _name;
    private List<Node> _nodes;
    private Map<String,String> _attributes;
    private StringBuilder _text;
    
    public SimpleNode(String name)
    {
        setName(name);
    }
    
    public SimpleNode(String name, Node parent)
    {
        setName(name);
        if(parent!=null)
            parent.addNode(this);
    }
    
    public void setParent(Node parent)
    {
        if(_parent!=null)
        {
            if(parent==_parent)
                return;
            _parent.removeNode(this);
        }
        _parent = parent;   
    }
    
    public Node getParent()
    {
        return _parent;
    }
    
    public boolean isRoot()
    {
        return _parent!=null;
    }
    
    public int size()
    {
        return _nodes==null ? 0 : _nodes.size();
    }
    
    public boolean hasAttribute(String name)
    {
        return _attributes!=null && _attributes.get(name)!=null;
    }
    
    public void setAttribute(String name, String value)
    {
        if(_attributes==null)
            _attributes = new HashMap<String,String>();        
        
        _attributes.put(name, value);
    }
    
    public String getAttribute(String name)
    {
        return _attributes==null ? null : _attributes.get(name);
    }
    
    public void setName(String name)
    {
        _name = name;
    }
    
    public String getName()
    {
        return _name;
    }
    
    public List<Node> getNodes()
    {
        return _nodes;
    }
    
    public List<Node> getNodes(String name)
    {
        if(_nodes==null)
            return null;
        
        ArrayList<Node> list = new ArrayList<Node>();
        for(Node n : _nodes)
        {
            if(n.getName().equals(name))
                list.add(n);
        }
        return list.isEmpty() ? null : list;
    }
    
    public void addNode(Node node)
    {
        if(node.getParent()==this)
            return;
        if(_nodes==null)
            _nodes = new ArrayList<Node>();
        
        node.setParent(this);        
        _nodes.add(node);
    }
    
    public Node getNode(int index)
    {
        return _nodes==null || _nodes.size()==0 ? null : _nodes.get(index);
    }
    
    public Node getLastNode()
    {
        return _nodes==null || _nodes.size()==0 ? null : _nodes.get(_nodes.size()-1);            
    }
    
    public Node getFirstNode()
    {
        return _nodes==null || _nodes.size()==0 ? null : _nodes.get(0);
    }
    
    public void addText(char[] buf, int start, int length)
    {
        if(_text==null)
            _text = new StringBuilder();
        _text.append(buf, start, length);
    }
    
    public void addText(String text)
    {
        if(_text==null)
            _text = new StringBuilder();
        _text.append(text);
    }
    
    public void addText(StringBuilder text)
    {
        if(_text==null)
            _text = new StringBuilder();
        _text.append(text);
    }
    
    public void addText(StringBuffer text)
    {
        if(_text==null)
            _text = new StringBuilder();
        _text.append(text);
    }
    
    public StringBuilder getText()
    {
        return _text;
    }
    
    public String toString()
    {
        StringBuilder buffer = new StringBuilder().append('\n');
        buffer.append('<').append(_name);
        if(_attributes!=null)
        {                
            for(String s : _attributes.keySet())
                buffer.append(' ').append(s).append('=').append('"').append(_attributes.get(s)).append('"');                
        }           
        
        if(_nodes!=null)
        {
            buffer.append('>');
            for(Node n : _nodes)
                buffer.append(n.toString());
            if(_text!=null)
                buffer.append(_text);
        }
        else if(_text!=null)
        {
            buffer.append('>');            
            buffer.append(_text);
        }
        else        
            return buffer.append('/').append('>').toString();        
        
        buffer.append('<').append('/').append(_name).append('>');
        return buffer.toString();
    }

    public Node getNode(String name)
    {        
        return getNode(name, 0);
    }

    public Node getNode(String name, int startingIndex)
    {
        if(_nodes!=null)
        {
            for(int i=startingIndex; i<_nodes.size(); i++)
            {
                Node node = _nodes.get(i);
                if(node.getName().equals(name))
                    return node;
            }
        }           
        return null;
    }
    
    public int findNode(String name)
    {
        return findNode(name);
    }
    
    public int findNode(String name, int startingIndex)
    {
        if(_nodes!=null)
        {
            for(int i=startingIndex; i<_nodes.size(); i++)
            {
                if(_nodes.get(i).getName().equals(name))
                    return i;
            }
        }           
        return -1;
    }

    public String removeAttribute(String name)
    {
        return _attributes==null ? null : _attributes.remove(name);
    }

    public Node removeNode(int index)
    {
        return _nodes==null ? null : _nodes.remove(index);
    }
    
    public boolean removeNode(Node node)
    {
        return _nodes!=null && _nodes.remove(node);
    }
    
}
