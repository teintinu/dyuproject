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
 * An xml node for persistence - much like DOM.
 * 
 * @author David Yu
 * @created Sep 18, 2008
 */

public interface Node
{
    
    /**
     * Sets the parent.
     */
    public void setParent(Node parent);
    
    /**
     * Gets the parent.
     */
    public Node getParent();
    
    /**
     * Checks whether this node is root.
     */
    public boolean isRoot();
    
    /**
     * Gets the number of child elements of this node.
     */
    public int size();
    
    /**
     * Sets the name.
     */
    public void setName(String name);
    
    /**
     * Gets the name.
     */
    public String getName();
    
    /**
     * Sets the namespace.
     */
    public void setNamespace(String namespace);
    
    /**
     * Gets the namespace.
     */
    public String getNamespace();
    
    /**
     * Adds text content as char array.
     */
    public void addText(char[] data, int start, int length);
    
    /**
     * Adds text content as String.
     */
    public void addText(String text);
    
    /**
     * Adds text content as StringBuilder.
     */
    public void addText(StringBuilder text);
    
    /**
     * Adds text content as StringBuffer.
     */
    public void addText(StringBuffer text);
    
    /**
     * Gets the text content as StringBuilder.
     */
    public StringBuilder getText();
    
    /**
     * Sets an attribute with the given {@code name} and {@code value}.
     */
    public void setAttribute(String name, String value);
    
    /**
     * Checks whether the attribute {@code name} is present. 
     */
    public boolean hasAttribute(String name);
    
    /**
     * Removes an attribute mapped with the given {@code name}.
     */
    public String removeAttribute(String name);
    
    /**
     * Gets an attribute mapped with the given {@code name}.
     */
    public String getAttribute(String name);
    
    /**
     * Adds a child node.
     */
    public void addNode(Node node);
    
    /**
     * Removes a child node from the given {@code index}.
     */
    public Node removeNode(int index);
    
    /**
     * Removes a child node.
     */
    public boolean removeNode(Node node);
    
    /**
     * Gets the list of child nodes.
     */
    public List<Node> getNodes();
    
    /**
     * Gets the list of child nodes with the given {@code name} (filtered).
     */
    public List<Node> getNodes(String name);
    
    /**
     * Gets the first child node.
     */
    public Node getFirstNode();
    
    /**
     * Gets the last child node.
     */
    public Node getLastNode();
    
    /**
     * Gets a child node from the given {@code index}.
     */
    public Node getNode(int index);
    
    /**
     * Gets the first child node with the given {@code name}.
     */
    public Node getNode(String name);    
    
    /**
     * Gets the first child node with the given {@code name}, starting at {@code startingIndex}.
     */
    public Node getNode(String name, int startingIndex);
    
    /**
     * Gets the first child node with the given {@code name}, starting from the last.
     */
    public Node getNodeFromLast(String name);    
    
    /**
     * Gets the first child node with the given {@code name}, starting from the last with the 
     * specified {@code startingIndex}.
     */
    public Node getNodeFromLast(String name, int startingIndex);
    
    /**
     * Gets the index of the child node with the given {@code name}.
     */
    public int indexOf(String name);
    
    /**
     * Gets the index of the child node with the given {@code name}, starting 
     * at {@code startingIndex}.
     */
    public int indexOf(String name, int startingIndex);
    
    /**
     * Gets the index of the child node with the given {@code name}, starting from the last.
     */
    public int lastIndexOf(String name);
    
    /**
     * Gets the index of the child node with the given {@code name}, starting from last with 
     * the specified {@code startingIndex}.
     */
    public int lastIndexOf(String name, int startingIndex);

}
