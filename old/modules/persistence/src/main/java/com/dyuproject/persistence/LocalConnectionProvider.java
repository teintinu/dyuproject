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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;

/**
 * @author David Yu
 */

public class LocalConnectionProvider implements ConnectionProvider {
	
	private DataSource _dataSource;

	public void close() throws HibernateException 
	{		
		
	}

	public void closeConnection(Connection con) throws SQLException 
	{
		con.close();
	}

	public void configure(Properties props) throws HibernateException 
	{
		_dataSource = SpringConfiguredSessionFactory.removeConfiguredDataSource(
		        props.getProperty("dataSourceId"));
		if(_dataSource==null)
			throw new IllegalStateException("dataSource not obtained");
	}

	public Connection getConnection() throws SQLException 
	{		
		return _dataSource.getConnection();
	}

	public boolean supportsAggressiveRelease() 
	{		
		return false;
	}

}
