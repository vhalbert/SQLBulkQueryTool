/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.jboss.bqt.client;


/**
 * An instance of FileType represents a set of classes that work together to process a common file format
 * when reading or writing files.
 *  
 * @author vhalbert
 *
 */
public interface FileType {
	
	/**
	 * The {@link #FILE_TYPE_CLASSNAME} property indicates the classname that will provide the classes to use
	 * to process the type/format of the files (i.e., xml or text)
	 */
	public static final String FILE_TYPE_CLASSNAME = "file.type.classname"; //$NON-NLS-1$
	
	public static final String DEFAULT_FILE_TYPE_CLASSNAME = "org.jboss.bqt.client.xml.XMLFileType";

	
	String getErrorWriterClassName();
	
	String getExpectedResultsReaderClassName();
	
	String getExpectedResultsWriterClassName();
	
	String getQueryWriterClassName();
	
	String getQueryReaderClassName();

}