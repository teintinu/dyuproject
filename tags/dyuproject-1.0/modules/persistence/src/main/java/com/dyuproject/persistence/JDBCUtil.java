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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.dyuproject.util.Delim;

/**
 * @author David Yu
 */

public class JDBCUtil 
{
    
    private static final String FROM = " from ";    
    private static final int START = "select ".length();
    private static final List<Map<String,String>> EMPTY_LIST = Collections.emptyList();
    
    public static boolean checkExisting(Connection connection, String sql, Object[] params) 
    throws Exception
    {
        PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, 
                ResultSet.CONCUR_READ_ONLY);                
        if(params!=null) 
        {
            for(int i=0; i<params.length; i++)
                ps.setObject(i+1, params[i]);
        }
        return ps.executeQuery().next();
    }
    
    public static int doUpdate(Connection connection, String sql, Object[] values) throws Exception 
    {      
        PreparedStatement ps = connection.prepareStatement(sql);
        if(values!=null) 
        {
            for(int i=0; i<values.length; i++)
                ps.setObject(i+1, values[i]);
        }
        return ps.executeUpdate();
    }
    
    public static int doDelete(Connection connection, String[] sql, long id) throws Exception 
    {
        connection.setAutoCommit(false);
        int rows = 0;
        for(int i=0; i<sql.length; i++)
        {
            PreparedStatement ps = connection.prepareStatement(sql[i]);            
            ps.setLong(1, id);
            rows += ps.executeUpdate();
        }
        connection.commit();
        return rows;
    }    
    
    public static int[] doBatchUpdate(Connection connection, String sql, List<Object[]> values) 
    throws Exception 
    {
        connection.setAutoCommit(false);
        PreparedStatement ps = connection.prepareStatement(sql);        
        if(values!=null) 
        {
            int size = values.size();           
            for(int i=0; i<size; i++) 
            {
                Object[] value = (Object[])values.get(i);
                for(int j=0; j<value.length; j++)
                    ps.setObject(j+1, value[j]);
                ps.addBatch();
            }
        }
        int[] results = ps.executeBatch();
        connection.commit();
        return results;
    }
    
    public static Map<String,String> doQueryUnique(Connection connection, Properties mapping, 
            String sql, Object[] params) throws Exception 
    {     
        PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, 
                ResultSet.CONCUR_READ_ONLY);                
        if(params!=null) 
        {
            for(int i=0; i<params.length; i++)
                ps.setObject(i+1, params[i]);
        }       
        Map<String,String> row = null;
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        if(rs.next()) 
        {
            row = new HashMap<String,String>();
            for(int i=0; i<colCount; i++) 
            {
                String colName = rsmd.getColumnName(i+1).toLowerCase();                 
                row.put(mapping.getProperty(colName, colName), rs.getString(i+1));
            }           
        }   
        return row;
    }
    
    public static Map<String,String> doQueryUnique(Connection connection, Properties mapping, 
            String sql, long id) throws Exception 
    {     
        PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, 
                ResultSet.CONCUR_READ_ONLY);                
        ps.setLong(1, id);
        Map<String,String> row = null;
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        if(rs.next()) 
        {
            row = new HashMap<String,String>();
            for(int i=0; i<colCount; i++) 
            {
                String colName = rsmd.getColumnName(i+1).toLowerCase();                 
                row.put(mapping.getProperty(colName, colName), rs.getString(i+1));
            }           
        }   
        return row;
    }
    
    public static List<Map<String,String>> doQuery(Connection connection, Properties mapping, 
            String sql, Object[] params) throws Exception 
    {     
        PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, 
                ResultSet.CONCUR_READ_ONLY);                
        if(params!=null) 
        {
            for(int i=0; i<params.length; i++)
                ps.setObject(i+1, params[i]);
        }       
        List<Map<String,String>> results = EMPTY_LIST;
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        String[] props = null;
        while(rs.next()) 
        {
            if(props==null)
            {                
                props = new String[colCount];
                for(int i=0; i<colCount; i++)
                {
                    String colName = rsmd.getColumnName(i+1).toLowerCase();
                    props[i] = mapping.getProperty(colName, colName);
                }
                results = new ArrayList<Map<String,String>>();
            }
            Map<String,String> row = new HashMap<String,String>();
            for(int i=0; i<colCount; i++)                                           
                row.put(props[i], rs.getString(i+1));            
            results.add(row);
        }   
        return results;
    }
    
    public static List<Map<String,String>> doQuery(Connection connection, Properties mapping, 
            String sql, long id) throws Exception 
    {     
        PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, 
                ResultSet.CONCUR_READ_ONLY);                
        ps.setLong(1, id);
        List<Map<String,String>> results = EMPTY_LIST;
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        String[] props = null;
        while(rs.next()) 
        {
            if(props==null)
            {                
                props = new String[colCount];
                for(int i=0; i<colCount; i++)
                {
                    String colName = rsmd.getColumnName(i+1).toLowerCase();
                    props[i] = mapping.getProperty(colName, colName);
                }
                results = new ArrayList<Map<String,String>>();
            }
            Map<String,String> row = new HashMap<String,String>();
            for(int i=0; i<colCount; i++)                                           
                row.put(props[i], rs.getString(i+1));            
            results.add(row);
        }  
        return results;
    }
    
    public static Object[] doQueryAsArray(Connection connection, Properties mapping, String sql, 
            Object[] params) throws Exception 
    {     
        return doQuery(connection, mapping, sql, params).toArray();
    }
    
    public static Object[] doQueryAsArray(Connection connection, Properties mapping, String sql, 
            long id) throws Exception 
    {     
        return doQuery(connection, mapping, sql, id).toArray();
    }
    
    public static Map<String,String> doQueryUniqueWithAggregate(Connection connection, 
            Properties mapping, String sql, Object[] params) throws Exception 
    {     
        PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);                
        if(params!=null) 
        {
            for(int i=0; i<params.length; i++)
                ps.setObject(i+1, params[i]);
        }       
        Map<String,String> row = null;
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        String[] props = Delim.COMMA.split(sql.substring(START, sql.lastIndexOf(FROM)).trim());
        if(rs.next()) 
        {
            row = new HashMap<String,String>();
            for(int i=0; i<colCount; i++) 
            {
                String colName = rsmd.getColumnName(i+1);
                colName = 0<colName.length() ? colName.toLowerCase() : props[i].trim();     
                row.put(mapping.getProperty(colName, colName), rs.getString(i+1));
            }           
        }
        return row;
    }
    
    public static Map<String,String> doQueryUniqueWithAggregate(Connection connection, 
            Properties mapping, String sql, long id) throws Exception 
    {     
        PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);                
        ps.setLong(1, id);
        Map<String,String> row = null;
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        String[] props = Delim.COMMA.split(sql.substring(START, sql.lastIndexOf(FROM)).trim());
        if(rs.next()) 
        {
            row = new HashMap<String,String>();
            for(int i=0; i<colCount; i++) 
            {
                String colName = rsmd.getColumnName(i+1);
                colName = 0<colName.length() ? colName.toLowerCase() : props[i].trim();     
                row.put(mapping.getProperty(colName, colName), rs.getString(i+1));
            }           
        }
        return row;
    }
    
    public static List<Map<String,String>> doQueryWithAggregate(Connection connection, 
            Properties mapping, String sql, Object[] params) throws Exception 
    {     
        PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);                
        if(params!=null) 
        {
            for(int i=0; i<params.length; i++)
                ps.setObject(i+1, params[i]);
        }       
        List<Map<String,String>> results = new ArrayList<Map<String,String>>(); 
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        String[] props = Delim.COMMA.split(sql.substring(START, sql.lastIndexOf(FROM)).trim());
        while(rs.next()) 
        {
            Map<String,String> row = new HashMap<String,String>();
            for(int i=0; i<colCount; i++) 
            {
                String colName = rsmd.getColumnName(i+1);
                colName = 0<colName.length() ? colName.toLowerCase() : props[i].trim();     
                row.put(mapping.getProperty(colName, colName), rs.getString(i+1));
            }
            results.add(row);
        }
        return results;
    }
    
    public static List<Map<String,String>> doQueryWithAggregate(Connection connection, 
            Properties mapping, String sql, long id) throws Exception 
    {     
        PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);                
        ps.setLong(1, id);
        List<Map<String,String>> results = new ArrayList<Map<String,String>>(); 
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        String[] props = Delim.COMMA.split(sql.substring(START, sql.lastIndexOf(FROM)).trim());
        while(rs.next()) 
        {
            Map<String,String> row = new HashMap<String,String>();
            for(int i=0; i<colCount; i++) 
            {
                String colName = rsmd.getColumnName(i+1);
                colName = 0<colName.length() ? colName.toLowerCase() : props[i].trim();             
                row.put(mapping.getProperty(colName, colName), rs.getString(i+1));
            }
            results.add(row);
        }
        return results;
    }
    
    public static Object[] doQueryWithAggregateAsArray(Connection connection, Properties mapping, 
            String sql, long id) throws Exception 
    {     
        return doQueryWithAggregate(connection, mapping, sql, id).toArray();
    }
    
    public static Object[] doQueryWithAggregateAsArray(Connection connection, Properties mapping, 
            String sql, Object[] params) throws Exception 
    {     
        return doQueryWithAggregate(connection, mapping, sql, params).toArray();
    }

}
