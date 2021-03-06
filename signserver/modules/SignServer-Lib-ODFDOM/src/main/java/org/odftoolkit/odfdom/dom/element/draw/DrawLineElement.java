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

package org.odftoolkit.odfdom.dom.element.draw;

import org.odftoolkit.odfdom.OdfName;
import org.odftoolkit.odfdom.OdfNamespace;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgX1Attribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgY1Attribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgX2Attribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgY2Attribute;
import org.odftoolkit.odfdom.dom.attribute.draw.DrawLayerAttribute;
import org.odftoolkit.odfdom.dom.attribute.draw.DrawTransformAttribute;
import org.odftoolkit.odfdom.dom.attribute.draw.DrawTextStyleNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.draw.DrawCaptionIdAttribute;

import org.odftoolkit.odfdom.dom.element.svg.SvgTitleElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgDescElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeEventListenersElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;

/**
 * DOM implementation of OpenDocument element  {@odf.element draw:line}.
 *
 */
public abstract class DrawLineElement extends DrawShapeElementBase
{        
    public static final OdfName ELEMENT_NAME = OdfName.get( OdfNamespace.get(OdfNamespaceNames.DRAW), "line" );


	/**
	 * Create the instance of <code>DrawLineElement</code> 
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public DrawLineElement( OdfFileDom ownerDoc )
	{
		super( ownerDoc, ELEMENT_NAME	);
	}

	/**
	 * Get the element name 
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element draw:line}.
	 */
	public OdfName getOdfName()
	{
		return ELEMENT_NAME;
	}

	/**
	 * Initialization of the mandatory attributes of {@link  DrawLineElement}
	 *
     * @param svgX1AttributeValue  The mandatory attribute {@odf.attribute  svg:x1}"
     * @param svgY1AttributeValue  The mandatory attribute {@odf.attribute  svg:y1}"
     * @param svgX2AttributeValue  The mandatory attribute {@odf.attribute  svg:x2}"
     * @param svgY2AttributeValue  The mandatory attribute {@odf.attribute  svg:y2}"
     *
	 */
	public void init(String svgX1AttributeValue, String svgY1AttributeValue, String svgX2AttributeValue, String svgY2AttributeValue)
	{
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgX1Attribute</code> , See {@odf.attribute svg:x1}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgX1Attribute()
	{
		SvgX1Attribute attr = (SvgX1Attribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.SVG), "x1" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgX1Attribute</code> , See {@odf.attribute svg:x1}
	 *
	 * @param svgX1Value   The type is <code>String</code>
	 */
	public void setSvgX1Attribute( String svgX1Value )
	{
		SvgX1Attribute attr =  new SvgX1Attribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( svgX1Value );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgY1Attribute</code> , See {@odf.attribute svg:y1}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgY1Attribute()
	{
		SvgY1Attribute attr = (SvgY1Attribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.SVG), "y1" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgY1Attribute</code> , See {@odf.attribute svg:y1}
	 *
	 * @param svgY1Value   The type is <code>String</code>
	 */
	public void setSvgY1Attribute( String svgY1Value )
	{
		SvgY1Attribute attr =  new SvgY1Attribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( svgY1Value );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgX2Attribute</code> , See {@odf.attribute svg:x2}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgX2Attribute()
	{
		SvgX2Attribute attr = (SvgX2Attribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.SVG), "x2" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgX2Attribute</code> , See {@odf.attribute svg:x2}
	 *
	 * @param svgX2Value   The type is <code>String</code>
	 */
	public void setSvgX2Attribute( String svgX2Value )
	{
		SvgX2Attribute attr =  new SvgX2Attribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( svgX2Value );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgY2Attribute</code> , See {@odf.attribute svg:y2}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgY2Attribute()
	{
		SvgY2Attribute attr = (SvgY2Attribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.SVG), "y2" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgY2Attribute</code> , See {@odf.attribute svg:y2}
	 *
	 * @param svgY2Value   The type is <code>String</code>
	 */
	public void setSvgY2Attribute( String svgY2Value )
	{
		SvgY2Attribute attr =  new SvgY2Attribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( svgY2Value );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>DrawLayerAttribute</code> , See {@odf.attribute draw:layer}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getDrawLayerAttribute()
	{
		DrawLayerAttribute attr = (DrawLayerAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.DRAW), "layer" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>DrawLayerAttribute</code> , See {@odf.attribute draw:layer}
	 *
	 * @param drawLayerValue   The type is <code>String</code>
	 */
	public void setDrawLayerAttribute( String drawLayerValue )
	{
		DrawLayerAttribute attr =  new DrawLayerAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( drawLayerValue );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>DrawTransformAttribute</code> , See {@odf.attribute draw:transform}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getDrawTransformAttribute()
	{
		DrawTransformAttribute attr = (DrawTransformAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.DRAW), "transform" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>DrawTransformAttribute</code> , See {@odf.attribute draw:transform}
	 *
	 * @param drawTransformValue   The type is <code>String</code>
	 */
	public void setDrawTransformAttribute( String drawTransformValue )
	{
		DrawTransformAttribute attr =  new DrawTransformAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( drawTransformValue );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>DrawTextStyleNameAttribute</code> , See {@odf.attribute draw:text-style-name}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getDrawTextStyleNameAttribute()
	{
		DrawTextStyleNameAttribute attr = (DrawTextStyleNameAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.DRAW), "text-style-name" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>DrawTextStyleNameAttribute</code> , See {@odf.attribute draw:text-style-name}
	 *
	 * @param drawTextStyleNameValue   The type is <code>String</code>
	 */
	public void setDrawTextStyleNameAttribute( String drawTextStyleNameValue )
	{
		DrawTextStyleNameAttribute attr =  new DrawTextStyleNameAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( drawTextStyleNameValue );
	}


	/**
	 * Receives the value of the ODFDOM attribute representation <code>DrawCaptionIdAttribute</code> , See {@odf.attribute draw:caption-id}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getDrawCaptionIdAttribute()
	{
		DrawCaptionIdAttribute attr = (DrawCaptionIdAttribute) getOdfAttribute( OdfName.get( OdfNamespace.get(OdfNamespaceNames.DRAW), "caption-id" ) );
		if( attr != null ){
			return String.valueOf( attr.getValue() );
		}
		return null;
	}
		 
	/**
	 * Sets the value of ODFDOM attribute representation <code>DrawCaptionIdAttribute</code> , See {@odf.attribute draw:caption-id}
	 *
	 * @param drawCaptionIdValue   The type is <code>String</code>
	 */
	public void setDrawCaptionIdAttribute( String drawCaptionIdValue )
	{
		DrawCaptionIdAttribute attr =  new DrawCaptionIdAttribute( (OdfFileDom)this.ownerDocument );
		setOdfAttribute( attr );
		attr.setValue( drawCaptionIdValue );
	}

	/**
	 * Create child element {@odf.element svg:title}.
	 *
	 * @return   return  the element {@odf.element svg:title}
	 * DifferentQName 
	 */
	public SvgTitleElement newSvgTitleElement()
	{
		SvgTitleElement  svgTitle = ((OdfFileDom)this.ownerDocument).newOdfElement(SvgTitleElement.class);
		this.appendChild( svgTitle);
		return  svgTitle;
	}                   
               
	/**
	 * Create child element {@odf.element svg:desc}.
	 *
	 * @return   return  the element {@odf.element svg:desc}
	 * DifferentQName 
	 */
	public SvgDescElement newSvgDescElement()
	{
		SvgDescElement  svgDesc = ((OdfFileDom)this.ownerDocument).newOdfElement(SvgDescElement.class);
		this.appendChild( svgDesc);
		return  svgDesc;
	}                   
               
	/**
	 * Create child element {@odf.element office:event-listeners}.
	 *
	 * @return   return  the element {@odf.element office:event-listeners}
	 * DifferentQName 
	 */
	public OfficeEventListenersElement newOfficeEventListenersElement()
	{
		OfficeEventListenersElement  officeEventListeners = ((OdfFileDom)this.ownerDocument).newOdfElement(OfficeEventListenersElement.class);
		this.appendChild( officeEventListeners);
		return  officeEventListeners;
	}                   
               
	/**
	 * Create child element {@odf.element draw:glue-point}.
	 *
     * @param drawEscapeDirectionAttributeValue  the <code>String</code> value of <code>DrawEscapeDirectionAttribute</code>, see {@odf.attribute  draw:escape-direction} at specification
	 * @param drawIdAttributeValue  the <code>String</code> value of <code>DrawIdAttribute</code>, see {@odf.attribute  draw:id} at specification
	 * @param svgXAttributeValue  the <code>String</code> value of <code>SvgXAttribute</code>, see {@odf.attribute  svg:x} at specification
	 * @param svgYAttributeValue  the <code>String</code> value of <code>SvgYAttribute</code>, see {@odf.attribute  svg:y} at specification
	 * @return   return  the element {@odf.element draw:glue-point}
	 * DifferentQName 
	 */
    
	public DrawGluePointElement newDrawGluePointElement(String drawEscapeDirectionAttributeValue, String drawIdAttributeValue, String svgXAttributeValue, String svgYAttributeValue)
	{
		DrawGluePointElement  drawGluePoint = ((OdfFileDom)this.ownerDocument).newOdfElement(DrawGluePointElement.class);
		drawGluePoint.setDrawEscapeDirectionAttribute( drawEscapeDirectionAttributeValue );
		drawGluePoint.setDrawIdAttribute( drawIdAttributeValue );
		drawGluePoint.setSvgXAttribute( svgXAttributeValue );
		drawGluePoint.setSvgYAttribute( svgYAttributeValue );
		this.appendChild( drawGluePoint);
		return  drawGluePoint;      
	}
    
	/**
	 * Create child element {@odf.element text:p}.
	 *
	 * @return   return  the element {@odf.element text:p}
	 * DifferentQName 
	 */
	public TextPElement newTextPElement()
	{
		TextPElement  textP = ((OdfFileDom)this.ownerDocument).newOdfElement(TextPElement.class);
		this.appendChild( textP);
		return  textP;
	}                   
               
	/**
	 * Create child element {@odf.element text:list}.
	 *
	 * @return   return  the element {@odf.element text:list}
	 * DifferentQName 
	 */
	public TextListElement newTextListElement()
	{
		TextListElement  textList = ((OdfFileDom)this.ownerDocument).newOdfElement(TextListElement.class);
		this.appendChild( textList);
		return  textList;
	}                   
               
}
