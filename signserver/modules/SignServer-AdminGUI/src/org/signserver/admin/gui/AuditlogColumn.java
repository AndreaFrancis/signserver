/*************************************************************************
 *                                                                       *
 *  SignServer: The OpenSource Automated Signing Server                  *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.signserver.admin.gui;

import java.util.HashMap;
import org.cesecore.audit.impl.integrityprotected.AuditRecordData;

/**
 * Representation of an audit log column with name and description.
 * 
 * TODO: Refactor as enum
 *
 * @author Markus Kilås
 * @version $Id$
 */
public class AuditlogColumn {
    
    private static final AuditlogColumn[] COLUMNS =  {
        new AuditlogColumn(AuditRecordData.FIELD_ADDITIONAL_DETAILS, "Details"),
        new AuditlogColumn(AuditRecordData.FIELD_AUTHENTICATION_TOKEN, "Admin Subject"),
        new AuditlogColumn(AuditRecordData.FIELD_CUSTOM_ID, "Admin Issuer"),
        new AuditlogColumn(AuditRecordData.FIELD_EVENTSTATUS, "Outcome"),
        new AuditlogColumn(AuditRecordData.FIELD_EVENTTYPE, "Event"),
        new AuditlogColumn(AuditRecordData.FIELD_MODULE, "Module"),
        new AuditlogColumn(AuditRecordData.FIELD_NODEID, "Node"),
        new AuditlogColumn(AuditRecordData.FIELD_SEARCHABLE_DETAIL1, "Admin Serial Number"),
        new AuditlogColumn(AuditRecordData.FIELD_SEARCHABLE_DETAIL2, "Worker ID"),
        new AuditlogColumn(AuditRecordData.FIELD_SERVICE, "Service"),
        new AuditlogColumn(AuditRecordData.FIELD_SEQUENCENUMBER, "Sequence Number"),
        new AuditlogColumn(AuditRecordData.FIELD_TIMESTAMP, "Time")
    };
    
    private static final HashMap<String, String> DESCRIPTIONS = new HashMap<String, String>();
    
    static {
        for (AuditlogColumn column : COLUMNS) {
            DESCRIPTIONS.put(column.getName(), column.getDescription());
        }
    }
    
    private String name;
    private String description;

    public AuditlogColumn(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description + " (" + name + ")";
    }

    public static AuditlogColumn[] getColumns() {
        return COLUMNS;
    }
    
    public static String getDescription(final String name) {
        return DESCRIPTIONS.get(name);
    }
    
}