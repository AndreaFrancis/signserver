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

package org.odftoolkit.odfdom.dom.element.text;

import org.odftoolkit.odfdom.OdfName;
import org.odftoolkit.odfdom.OdfNamespace;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.OdfElement;
import org.odftoolkit.odfdom.dom.attribute.text.TextTableNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.text.TextTableTypeAttribute;
import org.odftoolkit.odfdom.dom.attribute.text.TextDatabaseNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleNumFormatAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleNumLetterSyncAttribute;
import org.odftoolkit.odfdom.dom.attribute.text.TextValueAttribute;

import org.odftoolkit.odfdom.dom.element.form.FormConnectionResourceElement;

/**
 * DOM implementation of OpenDocument element  {@odf.element text:database-row-number}.
 *
 */
public abstract class TextDatabaseRowNumberElement extends OdfElement
{        
    public static final OdfName ELEMENT_NAME = OdfName.get( OdfNamespace.get(OdfNamespaceNames.TEXT), "database-row-number" );


	/**
	 * Create the instance of <code>TextDatabaseRowNumberElement</code> 
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public TextDatabaseRowNumberElement( OdfFileDom ownerDoc )
	{
		super( ownerDoc, ELEMENT_NAME	);
	}

	/**
	 * Get the element name 
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element text:database-row-number}.
	 */
	public OdfName getOdfName()
	{
		return ELEMENT_NAME;
	}

	/**
	 * Initialization of the mandatory attributes of {@link  TextDatabaseRowNumberElement}
	 *
     * @param textTableNameAttributeValue  The mandatory attribute {@odf.attribute  text:table-name}"
     *
	 */
	public void init(String textTableNameAttributeValue)
	{
		setTextTableNameAttribute( textTableNameAttributeValue );
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>TextTableNameAttribute</code> , See {@odf.attribute text:table-name}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getTextTableNameAttribute()
	{
		TextTableNameAttribute attr = (TextTableNameAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.TEXT), "table-name" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>TextTableNameAttribute</code> , See {@odf.attribute text:table-name}
	 *
	 * @param textTableNameValue   The type is <code>String</code>
	 */
	public void setTextTableNameAttribute( String textTableNameValue )
	{
		TextTableNameAttribute attr =  new TextTableNameAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( textTableNameValue );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>TextTableTypeAttribute</code> , See {@odf.attribute text:table-type}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getTextTableTypeAttribute()
	{
		TextTableTypeAttribute attr = (TextTableTypeAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.TEXT), "table-type" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>TextTableTypeAttribute</code> , See {@odf.attribute text:table-type}
	 *
	 * @param textTableTypeValue   The type is <code>String</code>
	 */
	public void setTextTableTypeAttribute( String textTableTypeValue )
	{
		TextTableTypeAttribute attr =  new TextTableTypeAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( textTableTypeValue );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>TextDatabaseNameAttribute</code> , See {@odf.attribute text:database-name}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getTextDatabaseNameAttribute()
	{
		TextDatabaseNameAttribute attr = (TextDatabaseNameAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.TEXT), "database-name" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>TextDatabaseNameAttribute</code> , See {@odf.attribute text:database-name}
	 *
	 * @param textDatabaseNameValue   The type is <code>String</code>
	 */
	public void setTextDatabaseNameAttribute( String textDatabaseNameValue )
	{
		TextDatabaseNameAttribute attr =  new TextDatabaseNameAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( textDatabaseNameValue );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>StyleNumFormatAttribute</code> , See {@odf.attribute style:num-format}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getStyleNumFormatAttribute()
	{
		StyleNumFormatAttribute attr = (StyleNumFormatAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.STYLE), "num-format" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>StyleNumFormatAttribute</code> , See {@odf.attribute style:num-format}
	 *
	 * @param styleNumFormatValue   The type is <code>String</code>
	 */
	public void setStyleNumFormatAttribute( String styleNumFormatValue )
	{
		StyleNumFormatAttribute attr =  new StyleNumFormatAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( styleNumFormatValue );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>StyleNumLetterSyncAttribute</code> , See {@odf.attribute style:num-letter-sync}
	 *
	 * @return - the <code>Boolean</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Boolean getStyleNumLetterSyncAttribute()
	{
		StyleNumLetterSyncAttribute attr = (StyleNumLetterSyncAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.STYLE), "num-letter-sync" ) );
		if( attr != null ){
			return Boolean.valueOf( attr.booleanValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>StyleNumLetterSyncAttribute</code> , See {@odf.attribute style:num-letter-sync}
	 *
	 * @param styleNumLetterSyncValue   The type is <code>Boolean</code>
	 */
	public void setStyleNumLetterSyncAttribute( Boolean styleNumLetterSyncValue )
	{
		StyleNumLetterSyncAttribute attr =  new StyleNumLetterSyncAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setBooleanValue( styleNumLetterSyncValue.booleanValue() );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>TextValueAttribute</code> , See {@odf.attribute text:value}
	 *
	 * @return - the <code>Integer</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getTextValueAttribute()
	{
		TextValueAttribute attr = (TextValueAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.TEXT), "value" ) );
		if( attr != null ){
			return Integer.valueOf( attr.intValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>TextValueAttribute</code> , See {@odf.attribute text:value}
	 *
	 * @param textValueValue   The type is <code>Integer</code>
	 */
	public void setTextValueAttribute( Integer textValueValue )
	{
		TextValueAttribute attr =  new TextValueAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setIntValue( textValueValue.intValue() );
	}

	/**
	 * Create child element {@odf.element form:connection-resource}.
	 *
     * @param xlinkHrefAttributeValue  the <code>String</code> value of <code>XlinkHrefAttribute</code>, see {@odf.attribute  xlink:href} at specification
	 * @return   return  the element {@odf.element form:connection-resource}
	 * DifferentQName 
	 */
    
	public FormConnectionResourceElement newFormConnectionResourceElement(String xlinkHrefAttributeValue)
	{
		FormConnectionResourceElement  formConnectionResource = ((OdfFileDom)this.ownerDocument).newOdfElement(FormConnectionResourceElement.class);
		formConnectionResource.setXlinkHrefAttribute( xlinkHrefAttributeValue );
		this.appendChild( formConnectionResource);
		return  formConnectionResource;      
	}
    
}
