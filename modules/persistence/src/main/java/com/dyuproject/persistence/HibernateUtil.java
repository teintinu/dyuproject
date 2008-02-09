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
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.NullableType;

/**
 * @author David Yu
 */

public abstract class HibernateUtil 
{
	
	private static Log log = LogFactory.getLog(HibernateUtil.class);
	public static final String VALUE= "value";
	public static final String U_VALUE = "uvalue";
	public static final String Q_VALUE = "qvalue";
	public static final String FROM = " from ";
	public static final Pattern COMMA = Pattern.compile(",");
	public static final int START = "select ".length();	
	
	public static String toLikeParam(String name)
	{
	    return "%".concat(name).concat("%");
	}
	
	private static void flushAndCommit(Session session) 
	{		
		session.flush();
		session.getTransaction().commit();				
	}
	
	private static void rollback(Session session) 
	{
		log.warn("Executed Transaction.rollback() ...");
		session.getTransaction().rollback();		
	}
	
	public static void save(Session session, Object obj)
	{
	    try 
	    {	            
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
	
	public static void save(Session session, Object[] beans)
	{
        try 
        {            
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
	
    public static void delete(Session session, Object obj) 
    {        
        try 
        {            
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
	
	public static List executeNamedQuery(Session session, String namedQuery, Object[] values) 
	throws Exception 
	{
		List results = null;
		try 
		{		
			session.beginTransaction();
			Query query = session.getNamedQuery(namedQuery);
			if(values!=null) 
			{
				for(int i=0; i<values.length; i++)
					query.setParameter(i, values[i]);
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
	
	public static List executeNamedQuery(Session session, String namedQuery, Object[] values, 
	        NullableType[] types) throws Exception 
	{			
		List results = null;
		try 
		{		
			session.beginTransaction();
			Query query = session.getNamedQuery(namedQuery);
			for(int i=0; i<values.length; i++)
				query.setParameter(i, values[i], types[i]);
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
	
	public static List executeNamedQuery(Session session, String namedQuery, 
			Object value) throws Exception 
	{
		return executeNamedQuery(session, namedQuery, VALUE, value);
	}
	
	public static List executeNamedQuery(Session session, String namedQuery, String prop, 
			Object value) throws Exception 
	{
		List results = null;
		try 
		{		
			session.beginTransaction();
			Query query = session.getNamedQuery(namedQuery);
			query.setParameter(prop, value);
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
	
	public static List executeNamedQuery(Session session, String namedQuery, Object value, 
	        NullableType type) throws Exception 
	{			
		return executeNamedQuery(session, namedQuery, VALUE, value, type);
	}
	
	public static List executeNamedQuery(Session session, String namedQuery, String prop, 
			Object value, NullableType type) throws Exception 
	{
		List results = null;
		try 
		{		
			session.beginTransaction();
			Query query = session.getNamedQuery(namedQuery);
			query.setParameter(prop, value, type);
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
	
	public static List nativeQueryRead(Session session, String nativeQuery) 
	{
		List results = null;
		try 
		{
			session.beginTransaction();
			results =session.createSQLQuery(nativeQuery).list();
			flushAndCommit(session);
		}
		catch(HibernateException he) 
		{
			rollback(session);
			throw he;
		}
		return results;
	}
	
	public static int nativeQueryWrite(Session session, String nativeQuery) 
	{
		int results = 0;
		try 
		{
			session.beginTransaction();
			results =session.createSQLQuery(nativeQuery).executeUpdate();
			flushAndCommit(session);
		}
		catch(HibernateException he) 
		{
			rollback(session);
			throw he;
		}
		return results;
	}
	
	public static int executeNamedUpdateQuery(Session session, String namedQuery, 
			Object value) throws Exception 
	{
		return executeNamedUpdateQuery(session, namedQuery, VALUE, value);
	}
	
	public static int executeNamedUpdateQuery(Session session, String namedQuery, String prop, 
			Object value) throws Exception 
	{
		int results = 0;
		try 
		{		
			session.beginTransaction();
			Query query = session.getNamedQuery(namedQuery);
			query.setParameter(prop, value);
			results = query.executeUpdate();
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
	
	public static int executeNamedUpdateQuery(Session session, String namedQuery, 
			Object[] values) throws Exception 
	{
		int results = 0;
		try 
		{		
			session.beginTransaction();
			Query query = session.getNamedQuery(namedQuery);
			if(values!=null) 
			{
				for(int i=0; i<values.length; i++)
					query.setParameter(i, values[i]);
			}
			results = query.executeUpdate();
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
	
	public static int executeNamedUpdateQuery(Session session, String namedQuery, Object[] values, 
			NullableType[] types) throws Exception 
	{
		int results = 0;
		try 
		{		
			session.beginTransaction();
			Query query = session.getNamedQuery(namedQuery);
			for(int i=0; i<values.length; i++)
				query.setParameter(i, values[i], types[i]);
			results = query.executeUpdate();
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
	
	public static int executeNamedUpdateQuery(Session session, String namedQuery, 
			Object qvalue, Object uvalue) throws Exception 
	{
		return executeNamedUpdateQuery(session, namedQuery, Q_VALUE, qvalue, U_VALUE, uvalue);
	}
	
	public static int executeNamedUpdateQuery(Session session, String namedQuery, String qprop, 
			Object qvalue, String uprop, Object uvalue) throws Exception {
		int results = 0;
		try 
		{		
			session.beginTransaction();
			Query query = session.getNamedQuery(namedQuery);
			query.setParameter(qprop, qvalue);
			query.setParameter(uprop, uvalue);
			results = query.executeUpdate();
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
	
	public static int executeNamedUpdateQuery(Session session, String namedQuery, Object qvalue, 
			Object uvalue, NullableType qtype, NullableType utype) throws Exception 
	{
		return executeNamedUpdateQuery(session, namedQuery, Q_VALUE, qvalue, U_VALUE, uvalue, qtype, utype);
	}
	
	public static int executeNamedUpdateQuery(Session session, String namedQuery, String qprop, 
			Object qvalue, String uprop, Object uvalue, NullableType qtype, 
			NullableType utype) throws Exception 
	{
		int results = 0;
		try 
		{		
			session.beginTransaction();
			Query query = session.getNamedQuery(namedQuery);
			query.setParameter(qprop, qvalue, qtype);
			query.setParameter(uprop, uvalue, utype);
			results = query.executeUpdate();
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
	
	public static List pageNamedQuery(Session session, String namedQuery, 
			Object[] values, int page, int pageSize) throws Exception 
	{
		List results = null;
		try 
		{		
			session.beginTransaction();
			Query query = session.getNamedQuery(namedQuery);
			if(values!=null) 
			{
				for(int i=0; i<values.length; i++)
				query.setParameter(i, values[i]);
			}
			results = query.setFirstResult(page*pageSize).setMaxResults(pageSize+1).list();			
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
	
	public static List pageNamedQuery(Session session, String namedQuery, Object value, int page,
			 int pageSize) throws Exception 
	{
		return pageNamedQuery(session, namedQuery, VALUE, value, page, pageSize);
	}
	
	
	public static List pageNamedQuery(Session session, String namedQuery, String prop, Object value,
			 int page, int pageSize) throws Exception 
	{
		List results = null;
		try 
		{		
			session.beginTransaction();
			Query query = session.getNamedQuery(namedQuery);
			query.setParameter(prop, value);
			results = query.setFirstResult(page*pageSize).setMaxResults(pageSize+1).list();			
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
	
	public static List pageQuery(Session session, String namedQuery, Object[] values, int page,
			int pageSize) throws Exception 
	{
		List results = null;
		try 
		{		
			session.beginTransaction();
			Query query = session.createQuery(namedQuery);
			if(values!=null) 
			{
				for(int i=0; i<values.length; i++)
				query.setParameter(i, values[i]);
			}
			results = query.setFirstResult(page*pageSize).setMaxResults(pageSize+1).list();			
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
	
	public static boolean checkExisting(Session session, String sql, Object[] params) throws Exception
	{
	    boolean exists = false;
	    try
	    {
	        exists = JDBCUtil.checkExisting(session.connection(), sql, params);
	    }
	    finally
	    {
	        session.close();
	    }
	    return exists;
	}
	
	public static int jdbcSQLUpdate(Session session, String sql, Object[] values) throws Exception 
	{
		int result = 0;
		try 
		{
			result = JDBCUtil.doUpdate(session.connection(), sql, values);
		}
		finally 
		{
			session.close();
		}
		return result;
	}
	
	public static int[] jdbcSQLBatchUpdate(Session session, String sql, List<Object[]> values) 
	throws Exception 
	{
		int[] results = null;
		try 
		{
			results = JDBCUtil.doBatchUpdate(session.connection(), sql, values);
		}
		finally 
		{
			session.close();
		}
		return results;
	}
	
	public static Map<String,String> jdbcSQLQueryUnique(Session session, Properties mapping, String sql, 
			Object[] params) throws Exception 
	{		
		Map<String,String> row = null;		
		try 
		{
			row = JDBCUtil.doQueryUnique(session.connection(), mapping, sql, params);
		}
		finally 
		{
			session.close();
		}
		return row;
	}
	
	public static Map<String,String> jdbcSQLQueryUnique(Session session, Properties mapping, String sql, long id) 
	throws Exception 
	{		
		Map<String,String> row = null;		
		try 
		{
			row = JDBCUtil.doQueryUnique(session.connection(), mapping, sql, id);
		}
		finally 
		{
			session.close();
		}
		return row;
	}
	
	public static List<Map<String,String>> jdbcSQLQuery(Session session, Properties mapping, String sql, 
	        Object[] params) throws Exception 
	{		
		List<Map<String,String>> result = null;		
		try 
		{
			result = JDBCUtil.doQuery(session.connection(), mapping, sql, params);
		}
		finally 
		{
			session.close();
		}
		return result;
	}
	
	public static List<Map<String,String>> jdbcSQLQuery(Session session, Properties mapping, String sql, long id) 
	throws Exception 
	{		
		List<Map<String,String>> result = null;		
		try 
		{
			result = JDBCUtil.doQuery(session.connection(), mapping, sql, id);
		}
		finally 
		{
			session.close();
		}
		return result;
	}
	
	public static Object[] jdbcSQLQueryAsArray(Session session, Properties mapping, String sql, 
			Object[] params) throws Exception 
	{		
		return jdbcSQLQuery(session, mapping, sql, params).toArray();
	}
	
	public static Object[] jdbcSQLQueryAsArray(Session session, Properties mapping, String sql, 
	        long id) throws Exception 
	{		
		return jdbcSQLQuery(session, mapping, sql, id).toArray();
	}
	
	public static Map<String,String> jdbcSQLQueryUniqueWithAggregate(Session session, Properties mapping, 
			String sql, Object[] params) throws Exception 
	{		
	    Map<String,String> row = null;		
		try 
		{
			row = JDBCUtil.doQueryUniqueWithAggregate(session.connection(), mapping, sql, params);
		}
		finally 
		{
			session.close();
		}
		return row;
	}
	
	public static Map<String,String> jdbcSQLQueryUniqueWithAggregate(Session session, Properties mapping, 
			String sql, long id) throws Exception 
	{		
		Map<String,String> row = null;		
		try 
		{
			row = JDBCUtil.doQueryUniqueWithAggregate(session.connection(), mapping, sql, id);
		}
		finally 
		{
			session.close();
		}
		return row;
	}
	
	public static List<Map<String,String>> jdbcSQLQueryWithAggregate(Session session, Properties mapping, String sql, 
			Object[] params) throws Exception 
	{		
		List<Map<String,String>> result = null;		
		try 
		{
			result = JDBCUtil.doQueryWithAggregate(session.connection(), mapping, sql, params);
		}
		finally 
		{
			session.close();
		}
		return result;
	}
	
	public static List<Map<String,String>> jdbcSQLQueryWithAggregate(Session session, Properties mapping, String sql, 
			long id) throws Exception 
	{		
		List<Map<String,String>> result = null;		
		try 
		{
			result = JDBCUtil.doQueryWithAggregate(session.connection(), mapping, sql, id);
		}
		finally 
		{
			session.close();
		}
		return result;
	}
	
	public static Object[] jdbcSQLQueryWithAggregateAsArray(Session session, Properties mapping, 
			String sql, long id) throws Exception 
	{		
		return jdbcSQLQueryWithAggregateAsArray(session, mapping, sql, id);
	}
	
	public static Object[] jdbcSQLQueryWithAggregateAsArray(Session session, Properties mapping, 
			String sql, Object[] params) throws Exception 
	{		
		return jdbcSQLQueryWithAggregateAsArray(session, mapping, sql, params);
	}
	
	/* ============================================================================= */
	
	
	
}
