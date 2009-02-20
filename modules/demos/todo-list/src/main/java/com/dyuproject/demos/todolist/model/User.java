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

package com.dyuproject.demos.todolist.model;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mortbay.util.ajax.JSON;
import org.mortbay.util.ajax.JSON.Output;

/**
 * @author David Yu
 * @created May 21, 2008
 */

@Entity
@Table(name="users", uniqueConstraints=@UniqueConstraint(columnNames="username"))
@SuppressWarnings("serial")
public class User implements Serializable, JSON.Convertible
{
    
    private Long _id;
    
    private String _firstName;
    private String _lastName;
    private String _email;
    
    private String _username;
    private String _password;
    
    //private Set<Todo> _todos;
    
    public void setId(Long id)
    {
        _id = id;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId()
    {
        return _id;
    }    
    
    public void setFirstName(String firstName)
    {
        _firstName = firstName;
    }
    
    @Column(name="first_name", nullable=false, length=40)
    public String getFirstName()
    {
        return _firstName;
    }
    
    public void setLastName(String lastName)
    {
        _lastName = lastName;
    }
    
    @Column(name="last_name", nullable=false, length=40)
    public String getLastName()
    {
        return _lastName;
    }
    
    public void setEmail(String email)
    {
        _email = email;
    }
    
    @Column(name="email", nullable=false, length=40)
    public String getEmail()
    {
        return _email;
    }
    
    public void setUsername(String username)
    {
        _username = username;
    }
    
    @Column(name="username", nullable=false, length=40)
    public String getUsername()
    {
        return _username;
    }
    
    public void setPassword(String password)
    {
        _password = password;
    }
    
    @Column(name="password", nullable=false, length=40)
    public String getPassword()
    {
        return _password;
    }
    
    /*public void setTodos(Set<Todo> todos)
    {
        _todos = todos;
    }
    
    public void addTodo(Todo todo)
    {
        if(_todos==null)
            _todos = new HashSet<Todo>();
        _todos.add(todo);
    }    

    @OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    public Set<Todo> getTodos()
    {
        return _todos;
    }*/

    public void fromJSON(Map map)
    {
        _id = (Long)map.get("i");
        _firstName = (String)map.get("f");
        _lastName = (String)map.get("l");
        _email = (String)map.get("e");
        _username = (String)map.get("u");
    }

    public void toJSON(Output out)
    {
        out.addClass(getClass());
        out.add("i", _id);
        out.add("f", _firstName);
        out.add("l", _lastName);
        out.add("e", _email);
        out.add("u", _username);
    }

}
