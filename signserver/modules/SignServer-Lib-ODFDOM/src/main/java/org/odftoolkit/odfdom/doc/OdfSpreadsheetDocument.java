/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * Copyright 2009 IBM. All rights reserved.
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
package org.odftoolkit.odfdom.doc;

import org.odftoolkit.odfdom.doc.office.OdfOfficeSpreadsheet;


/**
 * This class represents an empty ODF spreadsheet file.
 * Note: The way of receiving a new empty OdfSpreadsheetDocument will probably change. 
 * In the future the streams and DOM representation of an OpenDocument file will
 * be clonable and this stream buffering will be neglected.
 * 
 */
public class OdfSpreadsheetDocument extends OdfDocument {

    private static String EMPTY_SPREADSHEET_DOCUMENT_PATH = "/OdfSpreadsheetDocument.ods";
    private static Resource EMPTY_SPREADSHEET_DOCUMENT_RESOURCE
                    = new Resource(EMPTY_SPREADSHEET_DOCUMENT_PATH);
    
    /**
     * Creates an empty spreadsheet document.
     * @return ODF spreadsheet document based on a default template* 
     * @throws java.lang.Exception - if the document could not be created
     */
    public static OdfSpreadsheetDocument newSpreadsheetDocument() throws Exception {
        return (OdfSpreadsheetDocument) OdfDocument.loadTemplate(EMPTY_SPREADSHEET_DOCUMENT_RESOURCE);
    }
    
    // Using static factory instead of constructor
    protected OdfSpreadsheetDocument(){};
    
    /**
     * Get the media type
     * 
     * @return the mediaTYPE string of this package
     */
    @Override
    public String getMediaType() {
        return OdfDocument.OdfMediaType.SPREADSHEET.toString();
    }

    /**
     * Get the content root of a spreadsheet document.
     *
     * @return content root, representing the office:spreadsheet tag
     * @throws Exception if the file DOM could not be created.
     */
    public OdfOfficeSpreadsheet getContentRoot() throws Exception {
    	return super.getContentRoot(OdfOfficeSpreadsheet.class);
    }

    private static final String TO_STRING_METHOD_TOKEN = "\n" + OdfDocument.OdfMediaType.SPREADSHEET + " - ID: ";

    @Override
    public String toString() {
        return TO_STRING_METHOD_TOKEN + this.hashCode() + " " + getPackage().getBaseURI();
    }
}
