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

package com.dyuproject.web.ws.rest.handler;

import java.lang.reflect.Method;
import java.util.Map;
import com.dyuproject.persistence.AbstractHibDao;
import com.dyuproject.persistence.HibernateUtil;
import com.dyuproject.util.reflect.ParameterType;
import com.dyuproject.util.reflect.ParameterMappedBean;
import com.dyuproject.util.reflect.ReflectUtil;
import com.dyuproject.web.ws.WebServiceException;
import com.dyuproject.web.ws.WebServiceHandler;
import com.dyuproject.web.ws.error.ResourceUnavailable;
import com.dyuproject.web.ws.rest.RESTResource;

/**
 * @author David Yu
 * @created Mar 14, 2008
 */

public class HibCRUDResource extends AbstractHibDao implements RESTResource.Handler, WebServiceHandler
{
    
    private Class _entityClass;
    private RESTResource _resource;
    private String _name;
    private boolean _plural = true;
    private String _get, _getByIdAndParentId, _getByParentId, _deleteQuery;
    private String _entityId;
    private String _parentId;
    private String _parentProperty;
    private Class _parentEntityClass;//parent = one , this = many one-to-many
    private Map<String, Method> _methodMap;
    private Method _entityMethod, _parentEntityMethod;
    private ParameterType _entityType, _parentEntityType;
    
    public HibCRUDResource()
    {
        
    }
    
    public HibCRUDResource(String entityClassName)
    {
        setEntityClassName(entityClassName);
    }
    
    public void init()
    {
        if(getSessionFactory()==null)
            throw new IllegalStateException("sessionFactory not set.");
        if(_entityClass==null)
            throw new IllegalStateException("entityClass not set.");

        if(_entityId==null)
            _entityId = "id";
        _entityMethod = ReflectUtil.getSimpleSetterMethod(_entityId, _entityClass);
        if(_entityMethod==null)
            throw new IllegalStateException("entityId *" + _entityId + "* property incorrect.");
        _entityType = ReflectUtil.getParameterType(_entityMethod.getParameterTypes()[0]);
        
        if(_parentEntityClass!=null)
        {            
            if(_parentId==null)
                _parentId = "id";
            _parentEntityMethod = ReflectUtil.getSimpleSetterMethod(_parentId, _parentEntityClass);
            if(_parentEntityMethod==null)
                throw new IllegalStateException("parentId *" + _parentId + "* property incorrect.");
            _parentEntityType = ReflectUtil.getParameterType(_parentEntityMethod.getParameterTypes()[0]);
            if(_parentProperty==null)
                _parentProperty = ReflectUtil.toProperty(0, _parentEntityClass.getSimpleName());
            
            _getByParentId = "from ".concat(_entityClass.getSimpleName())
                .concat(" e where e." + _parentProperty).concat(" = ?");
            _getByIdAndParentId = "from ".concat(_entityClass.getSimpleName())
                .concat(" e where e.id = ? and e." + _parentProperty).concat(" = ?");
        }
        
        _methodMap = ParameterMappedBean.class.isAssignableFrom(_entityClass) ? null :
            ReflectUtil.getSimpleSetterMethods(_entityClass);
        _methodMap.remove(_entityId);
        
        _get = "from ".concat(_entityClass.getSimpleName());
        _deleteQuery = "delete from ".concat(_entityClass.getSimpleName()).concat(" c where c.")
            .concat(_entityId).concat(" = ?");
        _resource = new RESTResource(_name, this);        
        _resource.init();
    }
    
    public void setEntityClassName(String entityClassName)
    {
        try
        {
            _entityClass = getClass().getClassLoader().loadClass(entityClassName);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
        _name = _plural ? _entityClass.getSimpleName().toLowerCase().concat("s") :
            _entityClass.getSimpleName().toLowerCase();
    }
    
    public void setParentEntityClassName(String parentEntityClassName)
    {
        try
        {
            _parentEntityClass = getClass().getClassLoader().loadClass(parentEntityClassName);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public void setEntityClass(Class entityClass)
    {
        _entityClass = entityClass;
    }
    
    public void setPlural(boolean plural)
    {
        _plural = plural;
        if(!plural && _entityClass!=null)
            _name = _entityClass.getSimpleName().toLowerCase();
    }
    
    public void setEntityId(String entityId)
    {
        _entityId = entityId;
    }
    
    public void setParentId(String parentId)
    {
        _parentId = parentId;
    }
    
    public void setParentProperty(String parentProperty)
    {
        _parentProperty = parentProperty;
    }

    public Object handleDelete(long id) throws Exception
    {        
        return HibernateUtil.executeUpdateQuery(openSession(), _deleteQuery, new Object[]{id});
    }

    public Object handleDelete(long id, long arg1) throws Exception
    {        
        return handleDelete(id);
    }

    public Object handleGet() throws Exception
    {        
        return HibernateUtil.executeQuery(openSession(), _get, null);
    }

    public Object handleGet(long id) throws Exception
    {        
        return HibernateUtil.load(openSession(), _entityClass, id);
    }

    public Object handleGet(long id, long parentId) throws Exception
    {
        if(_parentEntityClass==null)
            return ResourceUnavailable.getInstance();
        if(id==-1)
        {
            return HibernateUtil.executeQuery(openSession(), _getByParentId, 
                    new Object[]{parentId});
        }
        return HibernateUtil.executeQuery(openSession(), _getByIdAndParentId, 
                new Object[]{id, parentId});
    }

    public Object handlePost(Map<String, String> params) throws Exception
    {
        Object bean = _entityClass.newInstance();
        if(_methodMap==null)
            ((ParameterMappedBean)bean).construct(params);
        else        
            ReflectUtil.applySimpleSetters(bean, _methodMap, params);        
        return HibernateUtil.save(openSession(), bean) ? bean : null;
    }

    public Object handlePost(Map<String, String> params, long parentId) throws Exception
    {
        if(_parentEntityClass==null)
            return ResourceUnavailable.getInstance();
        Object bean = _entityClass.newInstance();
        if(_methodMap==null)
            ((ParameterMappedBean)bean).construct(params);
        else        
            ReflectUtil.applySimpleSetters(bean, _methodMap, params);
        Object parent = _parentEntityClass.newInstance();
        try
        {
            _parentEntityMethod.invoke(parent, new Object[]{parentId});
        }
        catch(Exception e)
        {
            _parentEntityMethod.invoke(parent, new Object[]{_parentEntityType.create(
                    String.valueOf(parentId))});
        }        
        return HibernateUtil.save(openSession(), bean) ? bean : null;
    }

    public Object handlePut(long id, Map<String, String> params) throws Exception
    {
        Object bean = _entityClass.newInstance();
        if(_methodMap==null)
            ((ParameterMappedBean)bean).construct(params);
        else        
            ReflectUtil.applySimpleSetters(bean, _methodMap, params);
        try
        {
            _entityMethod.invoke(bean, new Object[]{id});
        }
        catch(Exception e)
        {
            _entityMethod.invoke(bean, new Object[]{_entityType.create(String.valueOf(id))});
        }        
        return HibernateUtil.update(openSession(), bean) ? bean : null;
    }

    public Object handlePut(long id, Map<String, String> params, long parentId) throws Exception
    {
        if(_parentEntityClass==null)
            return ResourceUnavailable.getInstance();
        Object bean = _entityClass.newInstance();
        if(_methodMap==null)
            ((ParameterMappedBean)bean).construct(params);
        else        
            ReflectUtil.applySimpleSetters(bean, _methodMap, params);
        try
        {
            _entityMethod.invoke(bean, new Object[]{id});
        }
        catch(Exception e)
        {
            _entityMethod.invoke(bean, new Object[]{_entityType.create(String.valueOf(id))});
        }
        Object parent = _parentEntityClass.newInstance();
        try
        {
            _parentEntityMethod.invoke(parent, new Object[]{parentId});
        }
        catch(Exception e)
        {
            _parentEntityMethod.invoke(parent, new Object[]{_parentEntityType.create(
                    String.valueOf(parentId))});
        } 
        return HibernateUtil.update(openSession(), bean) ? bean : null;
    }

    public String getName()
    {        
        return _name;
    }

    public final Object handle(String[] arg0, Map<String, String> arg1)
            throws WebServiceException, Exception
    {        
        return _resource.handle(arg0, arg1);
    }

}
