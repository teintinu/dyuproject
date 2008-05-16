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

/**
 * @author David Yu
 */

public interface DefaultDao 
{
		
	public Object load(Class clazz, Long id) throws Exception;
	public void save(Object bean) throws Exception;
	public void save(Object beans[]) throws Exception;
	public void saveOrUpdate(Object bean) throws Exception;	
	public void update(Object bean) throws Exception;
	public void delete(Object bean) throws Exception;

	public boolean doHQLUpdate(String query, Object[] params) throws Exception;
	public boolean doHQLNamedUpdate(String namedQuery, Object[] params) throws Exception;
	public List doHQLQuery(String queryHQL, Object[] params) throws Exception;
	public List doHQLNamedQuery(String namedQuery, Object[] params) throws Exception;
	
}
