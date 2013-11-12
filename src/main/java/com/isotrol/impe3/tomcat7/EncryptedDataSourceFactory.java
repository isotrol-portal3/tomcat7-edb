/**
 * This file is part of Port@l
 * Port@l 3.0 - Portal Engine and Management System
 * Copyright (C) 2010  Isotrol, SA.  http://www.isotrol.com
 *
 * Port@l is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Port@l is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Port@l.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.isotrol.impe3.tomcat7;


import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.commons.dbcp.BasicDataSourceFactory;


/**
 * Data source factory that uses encrypted usernames and passwords.
 * @author Andres Rodriguez.
 */
public final class EncryptedDataSourceFactory extends BasicDataSourceFactory {

	/**
	 * <p>Create and return a new <code>EncryptedDataSourceFactory</code> instance. If no instance can be created,
	 * return <code>null</code> instead.</p>
	 * 
	 * @param obj The possibly null object containing location or reference information that can be used in creating an
	 * object
	 * @param name The name of this object relative to <code>nameCtx</code>
	 * @param nameCtx The context relative to which the <code>name</code> parameter is specified, or <code>null</code>
	 * if <code>name</code> is relative to the default initial context
	 * @param environment The possibly null environment that is used in creating this object
	 * 
	 * @exception Exception if an exception occurs creating the instance
	 */
	@SuppressWarnings("rawtypes")
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception {
		if (obj instanceof Reference) {
			// TODO
		}
		return super.getObjectInstance(obj, name, nameCtx, environment);
	}

	private void decryptParameter(String paramName, Reference ref) throws Exception {
		Enumeration<RefAddr> enu = ref.getAll();
		while (enu.hasMoreElements()) {
			RefAddr addr = enu.nextElement();
			if (addr.getType().equals(paramName)) {
				// TODO
				return;
			}
		}
		throw new Exception(String.format("Parameter [%s] not found for EncryptedDataSourceFactory", paramName));
	}
	
	private RefAddr decryptValue(String paramName, RefAddr addr) throws Exception {
		try {
			// TODO
			return addr;
		} catch(Exception e) {
			throw new Exception(String.format("Unable to decrypt parameter [%s] for EncryptedDataSourceFactory", paramName), e);
		}
	}

}
