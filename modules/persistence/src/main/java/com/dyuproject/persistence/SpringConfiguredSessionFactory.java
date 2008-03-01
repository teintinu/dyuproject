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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.classic.Session;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;

/**
 * @author David Yu
 */

public class SpringConfiguredSessionFactory implements SessionFactory 
{
	
	private static Log log = LogFactory.getLog(SpringConfiguredSessionFactory.class);
	private static final Map<String,DataSource> __configuredDataSources = new HashMap<String,DataSource>();
	private DataSource _dataSource;
	private SessionFactory _sessionFactory;
	private Resource[] _mappingDirectoryLocations;
	private Properties _hibernateProperties;
	private Configuration _config;
	private File _exportDir;
	
	public SpringConfiguredSessionFactory() 
	{
		_config = new Configuration();
	}

	public void close() throws HibernateException 
	{		
		_sessionFactory.close();
	}

	public void evict(Class c) throws HibernateException 
	{		
		_sessionFactory.evict(c);
	}

	public void evict(Class c, Serializable s) throws HibernateException 
	{	
		_sessionFactory.evict(c, s);
	}

	public void evictCollection(String c) throws HibernateException 
	{		
		_sessionFactory.evictCollection(c);
	}

	public void evictCollection(String c, Serializable s)	throws HibernateException 
	{		
		_sessionFactory.evictCollection(c, s);
	}

	public void evictEntity(String e) throws HibernateException 
	{		
		_sessionFactory.evictEntity(e);
	}

	public void evictEntity(String e, Serializable s)	throws HibernateException 
	{
		_sessionFactory.evictEntity(e, s);
	}

	public void evictQueries() throws HibernateException 
	{
		_sessionFactory.evictQueries();		
	}

	public void evictQueries(String q) throws HibernateException 
	{
		_sessionFactory.evictQueries(q);	
	}

	public Map getAllClassMetadata() throws HibernateException 
	{		
		return _sessionFactory.getAllClassMetadata();
	}

	public Map getAllCollectionMetadata() throws HibernateException 
	{		
		return _sessionFactory.getAllCollectionMetadata();
	}

	public ClassMetadata getClassMetadata(Class cmd) throws HibernateException 
	{
		return _sessionFactory.getClassMetadata(cmd);
	}

	public ClassMetadata getClassMetadata(String cmd) throws HibernateException 
	{		
		return _sessionFactory.getClassMetadata(cmd);
	}

	public CollectionMetadata getCollectionMetadata(String cmd)	throws HibernateException 
	{		
		return _sessionFactory.getCollectionMetadata(cmd);
	}

	public Session getCurrentSession() throws HibernateException 
	{		
		return _sessionFactory.getCurrentSession();
	}

	public Set getDefinedFilterNames() 
	{		
		return _sessionFactory.getDefinedFilterNames();
	}

	public FilterDefinition getFilterDefinition(String fdef) throws HibernateException 
	{		
		return _sessionFactory.getFilterDefinition(fdef);
	}

	public Statistics getStatistics() 
	{		
		return _sessionFactory.getStatistics();
	}

	public boolean isClosed() 
	{		
		return _sessionFactory.isClosed();
	}

	public Session openSession() throws HibernateException 
	{		
		return _sessionFactory.openSession();
	}

	public Session openSession(Connection con) 
	{		
		return _sessionFactory.openSession(con);
	}

	public Session openSession(Interceptor i) throws HibernateException 
	{		
		return _sessionFactory.openSession(i);
	}

	public Session openSession(Connection con, Interceptor i) 
	{		
		return _sessionFactory.openSession(con, i);
	}

	public StatelessSession openStatelessSession() 
	{		
		return _sessionFactory.openStatelessSession();
	}

	public StatelessSession openStatelessSession(Connection con) 
	{		
		return _sessionFactory.openStatelessSession(con);
	}

	public Reference getReference() throws NamingException 
	{		
		return _sessionFactory.getReference();
	}
	
	/* ============================================================================== */
	
	public void setDataSource(DataSource ds) 
	{
		_dataSource = ds;		
	}
	
	public void setMappingResources(String[] resources)
	{
	    ClassLoader loader = getClass().getClassLoader();
	    for(int i=0; i<resources.length; i++)
	        _config.addInputStream(loader.getResourceAsStream(resources[i]));
	}
	
	public void setMappingDirectoryLocations(Resource[] mdl) 
	{
		_mappingDirectoryLocations = mdl;
		for (int i=0; i<_mappingDirectoryLocations.length; i++) 
		{
			try 
			{
				File file = _mappingDirectoryLocations[i].getFile();			 
				if (!file.isDirectory()) 
					throw new IllegalArgumentException(_mappingDirectoryLocations[i] + "is not a dir");			
				_config.addDirectory(file);
			} 
			catch(Exception e) 
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	public static DataSource removeConfiguredDataSource(String id) 
	{
		return (DataSource)__configuredDataSources.remove(id);
	}
	
	public void setHibernateProperties(Properties props) 
	{
		_hibernateProperties = props;
		_config.addProperties(_hibernateProperties);
	}
	
	public void setExportDir(String exportDir)
	{
	    _exportDir = new File(exportDir);
	    if(!_exportDir.exists())
	    {
	        File parent =  _exportDir.getParentFile();
	        if(parent==null || (parent.exists() && parent.isDirectory()))
	            _exportDir.mkdirs();
	        else
	            _exportDir = null;
	    }
	    else if(!_exportDir.isDirectory())
	        _exportDir = null;	        
	}
	
	public void setInitialize(String dummy) 
	{
		if(_dataSource==null || _hibernateProperties==null)
			throw new IllegalStateException("dataSource, dataSourceId, mappingDirectoryLocations and hibernateProperties must be set.");		
		String dataSourceId = String.valueOf(_dataSource.hashCode()) + String.valueOf(System.currentTimeMillis());
		__configuredDataSources.put(dataSourceId, _dataSource);
		_config.setProperty("dataSourceId", dataSourceId);
		_config.setProperty(Environment.RELEASE_CONNECTIONS, ConnectionReleaseMode.ON_CLOSE.toString());
		_config.setProperty(Environment.CONNECTION_PROVIDER, LocalConnectionProvider.class.getName());
		_sessionFactory = _config.buildSessionFactory();
		if(_exportDir!=null)
		{		    
		    File outputFile = new File(_exportDir, _config.getProperty("hibernate.dialect")
		            .replace('.', '_').concat(".sql"));
		    String delim = System.getProperty("export.delim");
		    try 
		    {
		        SchemaExport sc = new SchemaExport(_config).setOutputFile(outputFile.getCanonicalPath());
		        if(delim!=null)
		            sc.setDelimiter(delim);
		        sc.create(false, false);
		    }
		    catch(IOException ioe)
		    {
		        throw new RuntimeException(ioe);
		    }
		}
		log.warn(dummy);
	}
	
	public static void main(String[] args) throws Exception
	{
	    if(args.length==0)
	    {
	        System.err.println("usage java com.dyuproject.persistence.SpringConfuredSessionFactory path/to/my/resource/or/file");
	        return;
	    }
	    String path = args[0];
	    File file = new File(path);
	    if(file.exists())
	    {
	        new FileSystemXmlApplicationContext(file.getCanonicalPath());
	    }
	    else
	    {
	        new ClassPathXmlApplicationContext(path);
	    }	        
	}

}
