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

import java.util.List;

/**
 * XML to POJO bean
 * 
 * @author David Yu
 * @created Sep 18, 2008
 */

public interface Node
{
    
    public void setParent(Node parent);
    
    public Node getParent();
    
    public boolean isRoot();
    
    public void setIndex(int index, Node parent);
    
    public int getIndex();
    
    public int size();
    
    public void setName(String name);
    
    public String getName();
    
    public void addText(char[] data, int start, int length);
    
    public void addText(String text);
    
    public void addText(StringBuilder text);
    
    public void addText(StringBuffer text);
    
    public StringBuilder getText();
    
    public void setAttribute(String name, String value);
    
    public boolean hasAttribute(String name);
    
    public String removeAttribute(String name);
    
    public String getAttribute(String name);
    
    public void addNode(Node node);
    
    public Node removeNode(int index);
    
    public boolean removeNode(Node node);
    
    public List<Node> getNodes();
    
    public List<Node> getNodes(String name);
    
    public Node getFirstNode();
    
    public Node getLastNode();
    
    public Node getNode(int index);
    
    public Node getNode(String name);    
    
    public Node getNode(String name, int startingIndex);
    
    public int findNode(String name);
    
    public int findNode(String name, int startingIndex);   

}
