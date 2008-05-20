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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

/**
 * @author David Yu
 */

public abstract class AbstractHibDao 
{
	
	private SessionFactory _sessionFactory;
	
	public void setSessionFactory(SessionFactory sf) 
	{
		_sessionFactory = sf;
	}
	
	public SessionFactory getSessionFactory() 
	{
		return _sessionFactory;
	}
	
	protected Session openSession() 
	{
		return _sessionFactory.openSession();
	}
	
	protected static void flushAndCommit(Session session) 
	{		
		session.flush();
		session.getTransaction().commit();				
	}
	
	protected static void rollback(Session session) 
	{
		session.getTransaction().rollback();
	}
	
	protected static ProjectionList getProjectionListByProps(String[] props) 
	{
		if(props==null || props.length==0)
			throw new IllegalArgumentException("bean props must not be null/empty");
		ProjectionList pl = Projections.projectionList();
		for(int i=0; i<props.length; i++)
			pl.add(Projections.property(props[i]));
		return pl;
	}
	
	public static Object load(Session session, Class clazz, Long id) 
	{		
		Object result = null;
		try 
		{			
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
	
}
