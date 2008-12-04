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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.dyuproject.util.format.FormatConverter;
import com.dyuproject.util.format.FormatConverter.Builder;

/**
 * @author David Yu
 * @created May 21, 2008
 */
@Entity
@Table(name="todos")
@SuppressWarnings("serial")
public class Todo implements Serializable, FormatConverter.Bean
{
    
    private Long _id;
    
    private String _title;
    
    private String _content;
    
    private boolean _completed;
    
    private User _user;
    
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
    
    public void setTitle(String title)
    {
        _title = title;
    }
    
    @Column(name="title", length=40)
    public String getTitle()
    {
        return _title;
    }
    
    public void setContent(String content)
    {
        _content = content;
    }
    
    @Column(name="content")
    public String getContent()
    {
        return _content;
    }
    
    public void setCompleted(boolean completed)
    {
        _completed = completed;
    }
    
    @Column(name="completed")
    public boolean isCompleted()
    {
        return _completed;
    }
    
    public void setUser(User user)
    {
        _user = user;
    }
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="user_id")
    public User getUser()
    {
        return _user;
    }    

    public void convert(Builder builder, String format)
    {
        builder.put("id", getId());
        builder.put("title", getTitle());
        builder.put("content", getContent());
        builder.put("completed", isCompleted());
        builder.put("user", getUser());
    }
}
