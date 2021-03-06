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

package org.odftoolkit.odfdom.dom.element.svg;

import org.odftoolkit.odfdom.OdfName;
import org.odftoolkit.odfdom.OdfNamespace;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.OdfElement;


/**
 * DOM implementation of OpenDocument element  {@odf.element svg:font-face-src}.
 *
 */
public abstract class SvgFontFaceSrcElement extends OdfElement
{        
    public static final OdfName ELEMENT_NAME = OdfName.get( OdfNamespace.get(OdfNamespaceNames.SVG), "font-face-src" );


	/**
	 * Create the instance of <code>SvgFontFaceSrcElement</code> 
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public SvgFontFaceSrcElement( OdfFileDom ownerDoc )
	{
		super( ownerDoc, ELEMENT_NAME	);
	}

	/**
	 * Get the element name 
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element svg:font-face-src}.
	 */
	public OdfName getOdfName()
	{
		return ELEMENT_NAME;
	}


	/**
	 * Create child element {@odf.element svg:font-face-uri}.
	 *
     * @param xlinkHrefAttributeValue  the <code>String</code> value of <code>XlinkHrefAttribute</code>, see {@odf.attribute  xlink:href} at specification
	 * @param xlinkTypeAttributeValue  the <code>String</code> value of <code>XlinkTypeAttribute</code>, see {@odf.attribute  xlink:type} at specification
	 * @return   return  the element {@odf.element svg:font-face-uri}
	 * DifferentQName 
	 */
    
	public SvgFontFaceUriElement newSvgFontFaceUriElement(String xlinkHrefAttributeValue, String xlinkTypeAttributeValue)
	{
		SvgFontFaceUriElement  svgFontFaceUri = ((OdfFileDom)this.ownerDocument).newOdfElement(SvgFontFaceUriElement.class);
		svgFontFaceUri.setXlinkHrefAttribute( xlinkHrefAttributeValue );
		svgFontFaceUri.setXlinkTypeAttribute( xlinkTypeAttributeValue );
		this.appendChild( svgFontFaceUri);
		return  svgFontFaceUri;      
	}
    
	/**
	 * Create child element {@odf.element svg:font-face-name}.
	 *
	 * @return   return  the element {@odf.element svg:font-face-name}
	 * DifferentQName 
	 */
	public SvgFontFaceNameElement newSvgFontFaceNameElement()
	{
		SvgFontFaceNameElement  svgFontFaceName = ((OdfFileDom)this.ownerDocument).newOdfElement(SvgFontFaceNameElement.class);
		this.appendChild( svgFontFaceName);
		return  svgFontFaceName;
	}                   
               
}
