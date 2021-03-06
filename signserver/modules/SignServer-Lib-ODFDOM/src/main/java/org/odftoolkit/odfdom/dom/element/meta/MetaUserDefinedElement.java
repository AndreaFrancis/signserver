/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/

/*
 * This file is automatically generated.
 * Don't edit manually.
 */    

package org.odftoolkit.odfdom.dom.element.meta;

import org.odftoolkit.odfdom.OdfName;
import org.odftoolkit.odfdom.OdfNamespace;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.OdfElement;
import org.odftoolkit.odfdom.dom.attribute.meta.MetaNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.meta.MetaValueTypeAttribute;


/**
 * DOM implementation of OpenDocument element  {@odf.element meta:user-defined}.
 *
 */
public abstract class MetaUserDefinedElement extends OdfElement
{        
    public static final OdfName ELEMENT_NAME = OdfName.get( OdfNamespace.get(OdfNamespaceNames.META), "user-defined" );


	/**
	 * Create the instance of <code>MetaUserDefinedElement</code> 
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public MetaUserDefinedElement( OdfFileDom ownerDoc )
	{
		super( ownerDoc, ELEMENT_NAME	);
	}

	/**
	 * Get the element name 
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element meta:user-defined}.
	 */
	public OdfName getOdfName()
	{
		return ELEMENT_NAME;
	}

	/**
	 * Initialization of the mandatory attributes of {@link  MetaUserDefinedElement}
	 *
     * @param metaNameAttributeValue  The mandatory attribute {@odf.attribute  meta:name}"
     * @param metaValueTypeAttributeValue  The mandatory attribute {@odf.attribute  meta:value-type}"
     *
	 */
	public void init(String metaNameAttributeValue, String metaValueTypeAttributeValue)
	{
		setMetaNameAttribute( metaNameAttributeValue );
		setMetaValueTypeAttribute( metaValueTypeAttributeValue );
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>MetaNameAttribute</code> , See {@odf.attribute meta:name}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getMetaNameAttribute()
	{
		MetaNameAttribute attr = (MetaNameAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.META), "name" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>MetaNameAttribute</code> , See {@odf.attribute meta:name}
	 *
	 * @param metaNameValue   The type is <code>String</code>
	 */
	public void setMetaNameAttribute( String metaNameValue )
	{
		MetaNameAttribute attr =  new MetaNameAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( metaNameValue );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>MetaValueTypeAttribute</code> , See {@odf.attribute meta:value-type}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getMetaValueTypeAttribute()
	{
		MetaValueTypeAttribute attr = (MetaValueTypeAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.META), "value-type" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>MetaValueTypeAttribute</code> , See {@odf.attribute meta:value-type}
	 *
	 * @param metaValueTypeValue   The type is <code>String</code>
	 */
	public void setMetaValueTypeAttribute( String metaValueTypeValue )
	{
		MetaValueTypeAttribute attr =  new MetaValueTypeAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( metaValueTypeValue );
	}

}
