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

package org.generationcp.browser.study.listeners;

import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.browser.cross.study.adapted.dialogs.ViewTraitObservationsDialog;
import org.generationcp.browser.study.TableViewerComponent;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class GidLinkButtonClickListener implements Button.ClickListener {

    private static final long serialVersionUID = -6751894969990825730L;
    private final static Logger LOG = LoggerFactory.getLogger(GidLinkButtonClickListener.class);
    private String[] CHILD_WINDOWS = {TableViewerComponent.TABLE_VIEWER_WINDOW_NAME, 
    		ViewTraitObservationsDialog.LINE_BY_TRAIT_WINDOW_NAME};
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    private String gid;

    public GidLinkButtonClickListener(String gid) {
        this.gid = gid;
    }

    @Override
    public void buttonClick(ClickEvent event) {
    	Window mainWindow;
    	Window eventWindow = event.getComponent().getWindow();
    	eventWindow.addStyleName(Reindeer.WINDOW_LIGHT);
    	if (ArrayUtils.contains(CHILD_WINDOWS, eventWindow.getName())) {
    		mainWindow = eventWindow.getParent();
    	} else {
    		mainWindow = eventWindow;
    	}
    	
        Tool tool = null;
        try {
            tool = workbenchDataManager.getToolWithName(ToolName.germplasm_browser.toString());
        } catch (MiddlewareQueryException qe) {
            LOG.error("QueryException", qe);
            /*MessageNotifier.showError(mainWindow, messageSource.getMessage(Message.DATABASE_ERROR),
                    "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));*/
        }
        
        ExternalResource germplasmBrowserLink = null;
        if (tool == null) {
            germplasmBrowserLink = new ExternalResource("http://localhost:18080/GermplasmStudyBrowser/main/germplasm-" + gid);
        } else {
            germplasmBrowserLink = new ExternalResource(tool.getPath().replace("germplasm/", "germplasm-") + gid);
        }
        
        Window germplasmWindow = new Window("Germplasm Information - " + gid);
        
        VerticalLayout layoutForGermplasm = new VerticalLayout();
        layoutForGermplasm.setMargin(false);
        //layoutForGermplasm.setWidth("620px");
        //layoutForGermplasm.setHeight("500px");
        layoutForGermplasm.setWidth("98%");
        layoutForGermplasm.setHeight("98%");
        
        Embedded germplasmInfo = new Embedded("", germplasmBrowserLink);
        germplasmInfo.setType(Embedded.TYPE_BROWSER);
        germplasmInfo.setSizeFull();
        layoutForGermplasm.addComponent(germplasmInfo);
        
//        germplasmWindow.addComponent(layoutForGermplasm);
        germplasmWindow.setContent(layoutForGermplasm);
        //germplasmWindow.setWidth("645px");
        //germplasmWindow.setHeight("600px");
        germplasmWindow.setWidth("90%");
        germplasmWindow.setHeight("90%");
        germplasmWindow.center();
        germplasmWindow.setResizable(false);
        germplasmWindow.addStyleName(Reindeer.WINDOW_LIGHT);
        germplasmWindow.setModal(true);
        
        mainWindow.addWindow(germplasmWindow);
    }

}
