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
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.attribute.text.TextStyleNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.text.TextNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.text.TextProtectedAttribute;
import org.odftoolkit.odfdom.dom.attribute.text.TextProtectionKeyAttribute;
import org.odftoolkit.odfdom.dom.attribute.text.TextProtectionKeyDigestAlgorithmAttribute;
import org.odftoolkit.odfdom.dom.attribute.xml.XmlIdAttribute;


/**
 * DOM implementation of OpenDocument element  {@odf.element text:user-index}.
 *
 */
public abstract class TextUserIndexElement extends OdfStylableElement
{        
    public static final OdfName ELEMENT_NAME = OdfName.get( OdfNamespace.get(OdfNamespaceNames.TEXT), "user-index" );


	/**
	 * Create the instance of <code>TextUserIndexElement</code> 
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public TextUserIndexElement( OdfFileDom ownerDoc )
	{
		super( ownerDoc, ELEMENT_NAME, OdfStyleFamily.Section, OdfName.get( OdfNamespace.get(OdfNamespaceNames.TEXT), "style-name" )	);
	}

	/**
	 * Get the element name 
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element text:user-index}.
	 */
	public OdfName getOdfName()
	{
		return ELEMENT_NAME;
	}

	/**
	 * Initialization of the mandatory attributes of {@link  TextUserIndexElement}
	 *
     * @param textNameAttributeValue  The mandatory attribute {@odf.attribute  text:name}"
     *
	 */
	public void init(String textNameAttributeValue)
	{
		setTextNameAttribute( textNameAttributeValue );
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>TextStyleNameAttribute</code> , See {@odf.attribute text:style-name}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getTextStyleNameAttribute()
	{
		TextStyleNameAttribute attr = (TextStyleNameAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.TEXT), "style-name" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>TextStyleNameAttribute</code> , See {@odf.attribute text:style-name}
	 *
	 * @param textStyleNameValue   The type is <code>String</code>
	 */
	public void setTextStyleNameAttribute( String textStyleNameValue )
	{
		TextStyleNameAttribute attr =  new TextStyleNameAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( textStyleNameValue );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>TextNameAttribute</code> , See {@odf.attribute text:name}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getTextNameAttribute()
	{
		TextNameAttribute attr = (TextNameAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.TEXT), "name" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>TextNameAttribute</code> , See {@odf.attribute text:name}
	 *
	 * @param textNameValue   The type is <code>String</code>
	 */
	public void setTextNameAttribute( String textNameValue )
	{
		TextNameAttribute attr =  new TextNameAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( textNameValue );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>TextProtectedAttribute</code> , See {@odf.attribute text:protected}
	 *
	 * @return - the <code>Boolean</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Boolean getTextProtectedAttribute()
	{
		TextProtectedAttribute attr = (TextProtectedAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.TEXT), "protected" ) );
		if( attr != null ){
			return Boolean.valueOf( attr.booleanValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>TextProtectedAttribute</code> , See {@odf.attribute text:protected}
	 *
	 * @param textProtectedValue   The type is <code>Boolean</code>
	 */
	public void setTextProtectedAttribute( Boolean textProtectedValue )
	{
		TextProtectedAttribute attr =  new TextProtectedAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setBooleanValue( textProtectedValue.booleanValue() );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>TextProtectionKeyAttribute</code> , See {@odf.attribute text:protection-key}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getTextProtectionKeyAttribute()
	{
		TextProtectionKeyAttribute attr = (TextProtectionKeyAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.TEXT), "protection-key" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>TextProtectionKeyAttribute</code> , See {@odf.attribute text:protection-key}
	 *
	 * @param textProtectionKeyValue   The type is <code>String</code>
	 */
	public void setTextProtectionKeyAttribute( String textProtectionKeyValue )
	{
		TextProtectionKeyAttribute attr =  new TextProtectionKeyAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( textProtectionKeyValue );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>TextProtectionKeyDigestAlgorithmAttribute</code> , See {@odf.attribute text:protection-key-digest-algorithm}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getTextProtectionKeyDigestAlgorithmAttribute()
	{
		TextProtectionKeyDigestAlgorithmAttribute attr = (TextProtectionKeyDigestAlgorithmAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.TEXT), "protection-key-digest-algorithm" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return TextProtectionKeyDigestAlgorithmAttribute.DEFAULT_VALUE;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>TextProtectionKeyDigestAlgorithmAttribute</code> , See {@odf.attribute text:protection-key-digest-algorithm}
	 *
	 * @param textProtectionKeyDigestAlgorithmValue   The type is <code>String</code>
	 */
	public void setTextProtectionKeyDigestAlgorithmAttribute( String textProtectionKeyDigestAlgorithmValue )
	{
		TextProtectionKeyDigestAlgorithmAttribute attr =  new TextProtectionKeyDigestAlgorithmAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( textProtectionKeyDigestAlgorithmValue );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>XmlIdAttribute</code> , See {@odf.attribute xml:id}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getXmlIdAttribute()
	{
		XmlIdAttribute attr = (XmlIdAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.XML), "id" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>XmlIdAttribute</code> , See {@odf.attribute xml:id}
	 *
	 * @param xmlIdValue   The type is <code>String</code>
	 */
	public void setXmlIdAttribute( String xmlIdValue )
	{
		XmlIdAttribute attr =  new XmlIdAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( xmlIdValue );
	}

	/**
	 * Create child element {@odf.element text:user-index-source}.
	 *
     * @param textIndexNameAttributeValue  the <code>String</code> value of <code>TextIndexNameAttribute</code>, see {@odf.attribute  text:index-name} at specification
	 * @return   return  the element {@odf.element text:user-index-source}
	 * DifferentQName 
	 */
    
	public TextUserIndexSourceElement newTextUserIndexSourceElement(String textIndexNameAttributeValue)
	{
		TextUserIndexSourceElement  textUserIndexSource = ((OdfFileDom)this.ownerDocument).newOdfElement(TextUserIndexSourceElement.class);
		textUserIndexSource.setTextIndexNameAttribute( textIndexNameAttributeValue );
		this.appendChild( textUserIndexSource);
		return  textUserIndexSource;      
	}
    
	/**
	 * Create child element {@odf.element text:index-body}.
	 *
	 * @return   return  the element {@odf.element text:index-body}
	 * DifferentQName 
	 */
	public TextIndexBodyElement newTextIndexBodyElement()
	{
		TextIndexBodyElement  textIndexBody = ((OdfFileDom)this.ownerDocument).newOdfElement(TextIndexBodyElement.class);
		this.appendChild( textIndexBody);
		return  textIndexBody;
	}                   
               
}
