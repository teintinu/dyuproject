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

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * @author David Yu
 */

public class HibDefaultDao extends AbstractHibDao implements DefaultDao 
{	

	public Object load(Class clazz, Long id) 
	{		
		Session session = null;
		Object result = null;
		try 
		{			
			session = openSession();
			session.beginTransaction();
			result = session.load(clazz,id);
			flushAndCommit(session);
		} 
		catch(HibernateException he) 
		{
			rollback(session);
			throw he;
		}
		finally 
		{
			session.close();
		}
		return result;
	}
	
	public void save(Object obj) 
	{
		Session session = null;
		try 
		{
			session= openSession();
			session.beginTransaction();
			session.save(obj);
			flushAndCommit(session);
		}
		catch(HibernateException he) 
		{
			rollback(session);
			throw he;
		}
		finally 
		{
			session.close();
		}
	}
	
	public void save(Object beans[]) 
	{
		Session session = null;
		try 
		{
			session = openSession();
			session.beginTransaction();
			for(int i=0; i<beans.length; i++) 
			    session.save(beans[i]);  
			flushAndCommit(session);
		}
		catch(HibernateException he) 
		{			
			rollback(session);			
			throw he;
		}
		finally 
		{
			session.close();
		}
	}
	
	public void saveOrUpdate(Object obj) 
	{
		Session session = null;
		try 
		{
			session = openSession();
			session.beginTransaction();
			session.saveOrUpdate(obj);
			flushAndCommit(session);
		} 
		catch(HibernateException he) 
		{
			rollback(session);
			throw he;
		}
		finally 
		{
			session.close();
		}
	}
	
	public void update(Object obj) 
	{
		Session session = null;
		try 
		{
			session = openSession();
			session.beginTransaction();
			session.update(obj);
			flushAndCommit(session);
		}
		catch(HibernateException he) 
		{
			rollback(session);
			throw he;
		}
		finally 
		{
			session.close();
		}		
	}
	
	public void delete(Object obj) 
	{
		Session session = null;
		try 
		{
			session = openSession();
			session.beginTransaction();
			session.delete(obj);
			flushAndCommit(session);
		}
		catch(HibernateException he) 
		{
			rollback(session);
			throw he;
		}
		finally 
		{
			session.close();
		}
	}
	
	public boolean doJDBCUpdate(String sql, Object[] params) throws Exception 
	{		
		return 0<HibernateUtil.jdbcSQLUpdate(openSession(), sql, params);
	}
	
	public int[] doJDBCBatchUpdate(String sql, List<Object[]> params) throws Exception 
	{
		return HibernateUtil.jdbcSQLBatchUpdate(openSession(), sql, params);
	}

	public boolean doHQLUpdate(String updateHQL, Object[] params) throws Exception 
	{
		Session session = openSession();
		int result = 0;
		try 
		{			
			session.beginTransaction();
			Query query = session.createQuery(updateHQL);
			if(params!=null) {
				for(int i=0; i<params.length; i++)
					query.setParameter(i, params[i]);
			}				
			result = query.executeUpdate();			
			flushAndCommit(session);
		}
		catch(HibernateException he) 
		{
			rollback(session);
			throw he;
		}
		finally 
		{
			session.close();
		}
		return result>0;
	}

	public boolean doHQLNamedUpdate(String namedQuery, Object[] params) throws Exception 
	{					
		return 0<HibernateUtil.executeNamedUpdateQuery(openSession(), namedQuery, params);
	}
	
	public List doHQLQuery(String queryHQL, Object[] params) throws Exception 
	{
		Session session = openSession();
		List results = null;
		try 
		{			
			session.beginTransaction();
			Query query = session.createQuery(queryHQL);
			if(params!=null) 
			{
				for(int i=0; i<params.length; i++)
					query.setParameter(i, params[i]);
			}				
			results = query.list();		
			flushAndCommit(session);
		}
		catch(HibernateException he) 
		{
			rollback(session);
			throw he;
		}
		finally 
		{
			session.close();
		}
		return results;
	}

	public List doHQLNamedQuery(String namedQuery, Object[] params) throws Exception 
	{			
		return HibernateUtil.executeNamedQuery(openSession(), namedQuery, params);
	}
	
}
