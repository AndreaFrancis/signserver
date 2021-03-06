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

import java.awt.Color;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicHTML;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 * Renderer for the main list of workers.
 *
 * @author Markus Kilås
 * @version $Id$
 */
public class MyListCellRenderer extends javax.swing.JPanel 
        implements ListCellRenderer {

    /**
    * An empty <code>Border</code>. This field might not be used. To change the
    * <code>Border</code> used by this renderer override the
    * <code>getListCellRendererComponent</code> method and set the border
    * of the returned component directly.
    */
    private static final Border SAFE_NO_FOCUS_BORDER  //NOPMD //TODO Fix focus border
            = new EmptyBorder(1, 1, 1, 1);
    private static final Border DEFAULT_NO_FOCUS_BORDER
            = new EmptyBorder(1, 1, 1, 1);
    private static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER; //NOPMD //TODO Fix focus border

    private final ResourceMap resources = Application.getInstance(SignServerAdminGUIApplication.class).getContext().getResourceMap(MyListCellRenderer.class);

    /** Creates new form MyListCellRenderer */
    public MyListCellRenderer() {
        initComponents();
        setOpaque(true);
//        setBorder(getNoFocusBorder());
        setName("List.cellRenderer");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        listItemLabel1 = new javax.swing.JLabel();
        listItemLabel2 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(org.signserver.admin.gui.SignServerAdminGUIApplication.class).getContext().getResourceMap(MyListCellRenderer.class);
        jLabel3.setIcon(resourceMap.getIcon("worker.icon")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setBorder(null);
        jLabel3.setName("jLabel3"); // NOI18N

        listItemLabel1.setFont(resourceMap.getFont("listItemLabel1.font")); // NOI18N
        listItemLabel1.setText(resourceMap.getString("listItemLabel1.text")); // NOI18N
        listItemLabel1.setName("listItemLabel1"); // NOI18N

        listItemLabel2.setText(resourceMap.getString("listItemLabel2.text")); // NOI18N
        listItemLabel2.setName("listItemLabel2"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(listItemLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(listItemLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(listItemLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                        .addComponent(listItemLabel2))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private Border getNoFocusBorder() {
        return DEFAULT_NO_FOCUS_BORDER;
    }

    @Override
    public Component getListCellRendererComponent(final JList list,
            Object value, final int index, final boolean isSelected,
            final boolean cellHasFocus) {

        setComponentOrientation(list.getComponentOrientation());

        Color bg = null;
        Color fg = null;

        
        if (isSelected) {
            if (bg == null) {
                bg = list.getSelectionBackground();
            }
            if (fg == null) {
                fg = list.getSelectionForeground();
            }
        } else {
            if (bg == null) {
                bg = list.getBackground();
            }
            if (fg == null) {
                fg = list.getForeground();
            }
        }

        setBackground(bg);
        setForeground(fg);
        listItemLabel1.setForeground(fg);
        listItemLabel2.setForeground(fg);
//        if (value instanceof Icon) {
//            setIcon((Icon)value);
//            setText("");
//        }
//        else {
//            setIcon(null);
//            setText((value == null) ? "" : value.toString());
//        }
        if (value instanceof Worker) {
            final Worker worker  = (Worker) value;
            value = worker.getName() + " (" + worker.getWorkerId() + ")";

            listItemLabel1.setText((String) value);
            listItemLabel2.setText(worker.isActive()
                    ? "ACTIVE" : "OFFLINE");

            // Different icons for crypto workers, workers with crypto and plain workers
            if (worker.isCryptoWorker()) {
                jLabel3.setIcon(resources.getIcon("cryptoworker.icon"));
            } else if (worker.isCryptoConfigured()) {
                jLabel3.setIcon(resources.getIcon("workerkey.icon"));
            } else {
                jLabel3.setIcon(resources.getIcon("worker.icon"));
            }
        } else {
            listItemLabel1.setText((String) value);
            listItemLabel2.setText("");
        }

        setEnabled(list.isEnabled());
        setFont(list.getFont());

        Border border = null;
//        if (cellHasFocus) {
//            if (isSelected) {
//                border = UIManager.getBorder("List.focusSelectedCellHighlightBorder");
//            }
//            if (border == null) {
//                border = UIManager.getBorder("List.focusCellHighlightBorder");
//            }
//        } else
        {
            border = getNoFocusBorder();
        }
//        System.out.println("border: " + border);
        setBorder(border);

        return this;
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     *
     * @since 1.5
     * @return <code>true</code> if the background is completely opaque
     *         and differs from the JList's background;
     *         <code>false</code> otherwise
     */
    @Override
    public boolean isOpaque() {
        Color back = getBackground();
        Component p = getParent();
        if (p != null) {
            p = p.getParent();
        }
        // p should now be the JList.
        boolean colorMatch = (back != null) && (p != null) &&
            back.equals(p.getBackground()) &&
                        p.isOpaque();
        return !colorMatch && super.isOpaque();
    }

    /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    protected void firePropertyChange(final String propertyName,
            final Object oldValue, final Object newValue) {
        // Strings get interned...
        if (propertyName == "text"
                || ((propertyName == "font" || propertyName == "foreground")
                    && oldValue != newValue
                    && getClientProperty(BasicHTML.propertyKey) != null)) {

            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel listItemLabel1;
    private javax.swing.JLabel listItemLabel2;
    // End of variables declaration//GEN-END:variables

}
