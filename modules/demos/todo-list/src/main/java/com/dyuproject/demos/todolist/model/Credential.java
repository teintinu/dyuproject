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

package com.dyuproject.demos.todolist.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author David Yu
 * @created Feb 25, 2009
 */

@Entity
@Table(name="credential", uniqueConstraints=@UniqueConstraint(columnNames="username"))
public class Credential
{
    
    private Long _id;
    
    private String _username;
    
    private String _password;
    
    private Integer _role;
    
    private User _user;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId()
    {
        return _id;
    }
    
    public void setId(Long id)
    {
        _id = id;
    }

    @Column(name="username", nullable=false, length=40)
    public String getUsername()
    {
        return _username;
    }

    public void setUsername(String username)
    {        
        _username = username;
    }

    @Column(name="password", nullable=false, length=40)
    public String getPassword()
    {
        return _password;
    }

    public void setPassword(String password)
    {        
        _password = password;
    }

    @Column(name="role")
    public Integer getRole()
    {
        return _role;
    }

    public void setRole(Integer role)
    {        
        _role = role;
    }
    
    //@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
    @OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
    @PrimaryKeyJoinColumn
    public User getUser()
    {
        return _user;
    }
    
    public void setUser(User user)
    {        
        _user = user;
    }

}
