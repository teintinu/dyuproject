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

package com.dyuproject.persistence;

import java.io.Serializable;
import java.sql.Connection;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.SessionStatistics;

/**
 * @author David Yu
 */

public class HibSession implements Session {
	
	private static final long serialVersionUID = 1029384756L;
	private Session _session;
	
	HibSession(Session s) 
	{
		_session = s;
	}

	public Transaction beginTransaction() throws HibernateException 
	{		
		return _session.beginTransaction();
	}

	public void cancelQuery() throws HibernateException 
	{
		_session.cancelQuery();		
	}

	public void clear() 
	{
		_session.clear();
	}

	public Connection close() throws HibernateException 
	{
		// todo
		return _session.close();
	}

	public Connection connection() throws HibernateException 
	{		
		return _session.connection();
	}

	public boolean contains(Object arg0) 
	{		
		return _session.contains(arg0);
	}

	public Criteria createCriteria(Class arg0) 
	{		
		return _session.createCriteria(arg0);
	}

	public Criteria createCriteria(String arg0) 
	{		
		return _session.createCriteria(arg0);
	}

	public Criteria createCriteria(Class arg0, String arg1) 
	{		
		return _session.createCriteria(arg0, arg1);
	}

	public Criteria createCriteria(String arg0, String arg1) 
	{
		return _session.createCriteria(arg0, arg1);
	}

	public Query createFilter(Object arg0, String arg1) throws HibernateException 
	{			
		return _session.createFilter(arg0, arg1);
	}

	public Query createQuery(String arg0) throws HibernateException 
	{
		return _session.createQuery(arg0);
	}

	public SQLQuery createSQLQuery(String arg0) throws HibernateException 
	{		
		return _session.createSQLQuery(arg0);
	}

	public void delete(Object arg0) throws HibernateException 
	{
		_session.delete(arg0);		
	}

	public void delete(String arg0, Object arg1) throws HibernateException 
	{
		_session.delete(arg0, arg1);		
	}

	public void disableFilter(String arg0) 
	{
		_session.disableFilter(arg0);
		
	}

	public Connection disconnect() throws HibernateException 
	{		
		return _session.disconnect();
	}

	public Filter enableFilter(String arg0) 
	{		
		return _session.enableFilter(arg0);
	}

	public void evict(Object arg0) throws HibernateException 
	{
		_session.evict(arg0);		
	}

	public void flush() throws HibernateException 
	{
		_session.flush();		
	}

	public Object get(Class arg0, Serializable arg1) throws HibernateException 
	{
		return _session.get(arg0, arg1);
	}

	public Object get(String arg0, Serializable arg1) throws HibernateException 
	{
		return _session.get(arg0, arg1);
	}

	public Object get(Class arg0, Serializable arg1, LockMode arg2) throws HibernateException 
	{			
		return _session.get(arg0, arg1, arg2);
	}

	public Object get(String arg0, Serializable arg1, LockMode arg2) throws HibernateException 
	{			
		return _session.get(arg0, arg1, arg2);
	}

	public CacheMode getCacheMode() 
	{
		return _session.getCacheMode();
	}

	public LockMode getCurrentLockMode(Object arg0) throws HibernateException 
	{
		return _session.getCurrentLockMode(arg0);
	}

	public Filter getEnabledFilter(String arg0) 
	{
		return _session.getEnabledFilter(arg0);
	}

	public EntityMode getEntityMode() 
	{
		return _session.getEntityMode();
	}

	public String getEntityName(Object arg0) throws HibernateException 
	{
		return _session.getEntityName(arg0);
	}

	public FlushMode getFlushMode() 
	{
		return _session.getFlushMode();
	}

	public Serializable getIdentifier(Object arg0) throws HibernateException 
	{
		return _session.getIdentifier(arg0);
	}

	public Query getNamedQuery(String arg0) throws HibernateException 
	{
		return _session.getNamedQuery(arg0);
	}

	public Session getSession(EntityMode arg0) 
	{
		return _session.getSession(arg0);
	}

	public SessionFactory getSessionFactory() 
	{		
		return _session.getSessionFactory();
	}

	public SessionStatistics getStatistics() 
	{
		return _session.getStatistics();
	}

	public Transaction getTransaction() 
	{
		return _session.getTransaction();
	}

	public boolean isConnected() 
	{
		return _session.isConnected();
	}

	public boolean isDirty() throws HibernateException 
	{
		return _session.isDirty();
	}

	public boolean isOpen() 
	{
		return _session.isOpen();
	}

	public Object load(Class arg0, Serializable arg1) throws HibernateException 
	{
		return _session.load(arg0, arg1);
	}

	public Object load(String arg0, Serializable arg1) throws HibernateException 
	{			
		return _session.load(arg0, arg1);
	}

	public void load(Object arg0, Serializable arg1) throws HibernateException 
	{
		_session.load(arg0, arg1);		
	}

	public Object load(Class arg0, Serializable arg1, LockMode arg2) throws HibernateException 
	{			
		return _session.load(arg0, arg1, arg2);
	}

	public Object load(String arg0, Serializable arg1, LockMode arg2) throws HibernateException 
	{
		return _session.load(arg0, arg1, arg2);
	}

	public void lock(Object arg0, LockMode arg1) throws HibernateException 
	{
		_session.lock(arg0, arg1);		
	}

	public void lock(String arg0, Object arg1, LockMode arg2) throws HibernateException 
	{
		_session.lock(arg0, arg1, arg2);		
	}

	public Object merge(Object arg0) throws HibernateException 
	{
		return _session.merge(arg0);
	}

	public Object merge(String arg0, Object arg1) throws HibernateException 
	{
		return _session.merge(arg0, arg1);
	}

	public void persist(Object arg0) throws HibernateException 
	{
		_session.persist(arg0);		
	}

	public void persist(String arg0, Object arg1) throws HibernateException 
	{
		_session.persist(arg0, arg1);		
	}

	public void reconnect() throws HibernateException 
	{
		_session.reconnect();		
	}

	public void reconnect(Connection arg0) throws HibernateException 
	{
		_session.reconnect(arg0);		
	}

	public void refresh(Object arg0) throws HibernateException 
	{
		_session.refresh(arg0);		
	}

	public void refresh(Object arg0, LockMode arg1) throws HibernateException 
	{
		_session.refresh(arg0, arg1);		
	}

	public void replicate(Object arg0, ReplicationMode arg1) throws HibernateException 
	{
		_session.replicate(arg0, arg1);		
	}

	public void replicate(String arg0, Object arg1, ReplicationMode arg2) throws HibernateException 
	{
		_session.replicate(arg0, arg1, arg2);		
	}

	public Serializable save(Object arg0) throws HibernateException 
	{		
		return _session.save(arg0);
	}

	public Serializable save(String arg0, Object arg1) throws HibernateException 
	{
		return _session.save(arg0, arg1);
	}

	public void saveOrUpdate(Object arg0) throws HibernateException 
	{
		_session.saveOrUpdate(arg0);		
	}

	public void saveOrUpdate(String arg0, Object arg1) throws HibernateException 
	{
		_session.saveOrUpdate(arg0, arg1);		
	}

	public void setCacheMode(CacheMode arg0) 
	{
		_session.setCacheMode(arg0);		
	}

	public void setFlushMode(FlushMode arg0) 
	{
		_session.setFlushMode(arg0);		
	}

	public void setReadOnly(Object arg0, boolean arg1) 
	{
		_session.setReadOnly(arg0, arg1);		
	}

	public void update(Object arg0) throws HibernateException 
	{
		_session.update(arg0);		
	}

	public void update(String arg0, Object arg1) throws HibernateException 
	{
		_session.update(arg0, arg1);		
	}

}
