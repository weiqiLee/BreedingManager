/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.browser.cross.study.h2h.main.listeners;

import org.generationcp.browser.cross.study.h2h.main.dialogs.GermplasmListTreeComponent;
import org.generationcp.browser.cross.study.h2h.main.dialogs.SelectGermplasmListTreeComponent;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Layout;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Window.Notification;

public class SelectListTreeExpandListener implements Tree.ExpandListener{
    
    private static final Logger LOG = LoggerFactory.getLogger(SelectListTreeExpandListener.class);
    private static final long serialVersionUID = -5145904396164706110L;

    private Layout source;

    public SelectListTreeExpandListener(Layout source) {
        this.source = source;
    }

    @Override
    public void nodeExpand(ExpandEvent event) {
    	try {
	        if (source instanceof SelectGermplasmListTreeComponent) {
	                ((SelectGermplasmListTreeComponent) source).addGermplasmListNode(Integer.valueOf(event.getItemId().toString()));
	        
	        } else if (source instanceof GermplasmListTreeComponent && 
	        		(!event.getItemId().toString().equals("CENTRAL") && !event.getItemId().toString().equals("LOCAL"))) {
	           		((GermplasmListTreeComponent) source).addGermplasmListNode(Integer.valueOf(event.getItemId().toString()));
	        }
    	} catch (InternationalizableException e) {
    		LOG.error(e.toString() + "\n" + e.getStackTrace());
    		e.printStackTrace();
    		MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription(), Notification.POSITION_CENTERED);
    	}
    }

}