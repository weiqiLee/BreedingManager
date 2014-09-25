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
package org.generationcp.breeding.manager.listmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.AppConstants;
import org.generationcp.breeding.manager.constants.ModeView;
import org.generationcp.breeding.manager.customcomponent.HeaderLabelLayout;
import org.generationcp.breeding.manager.customcomponent.UnsavedChangesSource;
import org.generationcp.breeding.manager.customcomponent.ViewListHeaderWindow;
import org.generationcp.breeding.manager.util.ListManagerDetailsTabCloseHandler;
import org.generationcp.breeding.manager.util.Util;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * @author Mark Agarrado
 */
@Configurable
public class ListSelectionLayout extends VerticalLayout implements InternationalizableComponent, 
							InitializingBean, BreedingManagerLayout, UnsavedChangesSource {

    protected static final Logger LOG = LoggerFactory.getLogger(ListSelectionLayout.class);
    private static final long serialVersionUID = -6583178887344009055L;
    
    public static final String CLOSE_ALL_TABS_ID = "ListManagerDetailsLayout Close All Tabs ID";
    public static final String TAB_DESCRIPTION_PREFIX = "List ID: ";
    
    @Autowired
    private GermplasmListManager germplasmListManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private final ListManagerMain source;
    
    private Label headingLabel;
    private Label noListLabel;
    
    private Button btnCloseAllTabs;
    private Button browseForLists;
    private Button searchForLists;
    private Button importList;
    private Label or;
    private Label toWorkWith;
    private Label or2;
    private Label aNewListLabel;

    private HorizontalLayout headerLayout;
    private HorizontalLayout listSelectionHeaderContainer;
    private HorizontalLayout searchOrBrowseContainer;
    
    private TabSheet detailsTabSheet;
    private Map<ListComponent,Boolean> listStatusForChanges;
    
    private final Integer listId;

    public ListSelectionLayout(final ListManagerMain source, final Integer listId) {
    	super();
        this.source = source;
        this.listId = listId;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        instantiateComponents();
        initializeValues();
        layoutComponents();
        addListeners();
        
        if(listId != null){
        	try{
        		createListDetailsTab(listId);
        	} catch(MiddlewareQueryException ex){
        		LOG.error("Error with opening list details tab of list with id: " + listId);
        	}
        }
        else{
        	displayDefault();
        }
    }
    
    @Override
    public void instantiateComponents() {

        noListLabel = new Label();
    	noListLabel.setImmediate(true);
    	
    	headingLabel = new Label();
    	headingLabel.setImmediate(true);
    	headingLabel.setWidth("300px");
    	headingLabel.setStyleName(Bootstrap.Typography.H4.styleName());
    	headingLabel.addStyleName(AppConstants.CssStyles.BOLD);
    	
    	headerLayout = new HorizontalLayout();
    	
    	detailsTabSheet = new TabSheet();
    	detailsTabSheet.setWidth("100%");
    	detailsTabSheet.addStyleName("listDetails");
    	setDetailsTabSheetHeight();
    	
        btnCloseAllTabs = new Button(messageSource.getMessage(Message.CLOSE_ALL_TABS));
        btnCloseAllTabs.setData(CLOSE_ALL_TABS_ID);
        btnCloseAllTabs.setImmediate(true);
        btnCloseAllTabs.setStyleName(Reindeer.BUTTON_LINK);
        //btnCloseAllTabs.addStyleName("closeAllTabsListManagerPosition");
        //btnCloseAllTabs.addStyleName("closeAllTabsListManagerPadding");
        
        browseForLists = new Button();
        browseForLists.setImmediate(true);
        browseForLists.setStyleName(Reindeer.BUTTON_LINK);
        
        searchForLists = new Button();
        searchForLists.setImmediate(true);
        searchForLists.setStyleName(Reindeer.BUTTON_LINK);
        
        importList = new Button();
        importList.setImmediate(true);
        importList.setStyleName(Reindeer.BUTTON_LINK);
        
        or = new Label();
        or.setImmediate(true);
        
        or2 = new Label();
        or2.setImmediate(true);
        
        toWorkWith = new Label();
        toWorkWith.setImmediate(true);
        
        aNewListLabel = new Label();
        aNewListLabel.setImmediate(true);
        
        listStatusForChanges = new HashMap<ListComponent,Boolean>();
    }

    @Override
    public void initializeValues() {
        headingLabel.setValue(messageSource.getMessage(Message.LIST_DETAILS));
        browseForLists.setCaption(messageSource.getMessage(Message.BROWSE_FOR_A_LIST) + " ");
        searchForLists.setCaption(messageSource.getMessage(Message.SEARCH_FOR_A_LIST) + " ");
        importList.setCaption(messageSource.getMessage(Message.IMPORT_A_LIST) + " ");
        or.setValue(messageSource.getMessage(Message.OR) + " ");
        or2.setValue(messageSource.getMessage(Message.OR) + " ");
        toWorkWith.setValue(messageSource.getMessage(Message.A_LIST_TO_WORK_WITH) + ", ");
        aNewListLabel.setValue(messageSource.getMessage(Message.A_NEW_LIST) + ".");
    }
    
    @Override
    public void layoutComponents() {
        this.setMargin(new MarginInfo(true,false,true,true));
        this.setWidth("100%");

        listSelectionHeaderContainer = new HorizontalLayout();
        listSelectionHeaderContainer.setHeight("26px");
        listSelectionHeaderContainer.setWidth("100%");

        final HeaderLabelLayout headerLbl = new HeaderLabelLayout(AppConstants.Icons.ICON_REVIEW_LIST_DETAILS,headingLabel);

        final HorizontalLayout searchOrBrowseLayout = new HorizontalLayout();
        
        searchOrBrowseContainer = new HorizontalLayout();
        searchOrBrowseContainer.setHeight("19px");
        searchOrBrowseContainer.setWidth("100%");
        
        // Ugh, bit of a hack - can't figure out how to space these nicely
        searchForLists.setWidth("43px");
        or.setWidth("16px");
        browseForLists.setWidth("48px");
        toWorkWith.setWidth("132px");
        
        or2.setWidth("16px");
        importList.setWidth("44px");
        aNewListLabel.setWidth("70px");

        searchOrBrowseLayout.addComponent(browseForLists);
        searchOrBrowseLayout.addComponent(or);
        searchOrBrowseLayout.addComponent(searchForLists);
        searchOrBrowseLayout.addComponent(toWorkWith);
        searchOrBrowseLayout.addComponent(or2);
        searchOrBrowseLayout.addComponent(importList);
        searchOrBrowseLayout.addComponent(aNewListLabel);
        
        searchOrBrowseContainer.addComponent(searchOrBrowseLayout);
        searchOrBrowseContainer.addComponent(btnCloseAllTabs);
        searchOrBrowseContainer.setComponentAlignment(btnCloseAllTabs,Alignment.TOP_RIGHT);
    
        final VerticalLayout header = new VerticalLayout();
        header.setWidth("100%");
        header.addComponent(noListLabel);
        header.addComponent(headerLbl);
        
        
        final VerticalLayout headerBtnContainer = new VerticalLayout();
        headerBtnContainer.setSizeUndefined();
        headerBtnContainer.setSpacing(true);
        headerBtnContainer.addComponent(source.listBuilderToggleBtn1);
        

        listSelectionHeaderContainer.addComponent(header);
        listSelectionHeaderContainer.addComponent(headerBtnContainer);
        listSelectionHeaderContainer.setExpandRatio(header,1.0F);
        listSelectionHeaderContainer.setComponentAlignment(headerBtnContainer,Alignment.TOP_RIGHT);

        this.addComponent(listSelectionHeaderContainer);
        this.addComponent(searchOrBrowseContainer);
        this.addComponent(detailsTabSheet);
        this.displayDefault();
    }
    
    public void setDetailsTabSheetHeight() {
    	detailsTabSheet.setHeight("647px");
	}

	public void displayDefault(){
    	noListLabel.setVisible(false);
        headerLayout.setVisible(true);
        btnCloseAllTabs.setVisible(false);
        detailsTabSheet.setVisible(false);
    }

    @Override
    public void addListeners() {
        ListManagerDetailsTabCloseHandler closeHandler = new ListManagerDetailsTabCloseHandler(this);
        btnCloseAllTabs.addListener(closeHandler);
        detailsTabSheet.setCloseHandler(closeHandler);
        detailsTabSheet.addListener(new TabSheet.SelectedTabChangeListener() {

            private static final long serialVersionUID = -7822326039221887888L;

            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if(detailsTabSheet.getComponentCount() <= 1){
                    btnCloseAllTabs.setVisible(false);
                }
                else{
                    btnCloseAllTabs.setVisible(true);
                }
            }
        });
        
        browseForLists.addListener(new Button.ClickListener() {

        	private static final long serialVersionUID = 6385074843600086746L;

			@Override
			public void buttonClick(final ClickEvent event) {
				source.getListSelectionComponent().openListBrowseDialog();
			}
        });
        
        searchForLists.addListener(new Button.ClickListener() {

        	private static final long serialVersionUID = 6385074843600086746L;

			@Override
			public void buttonClick(final ClickEvent event) {
				source.getListSelectionComponent().openListSearchDialog();
			}
        });

        importList.addListener(new Button.ClickListener() {
        	
        	private static final long serialVersionUID = 6385074843600086746L;

			@Override
			public void buttonClick(final ClickEvent event) {
				source.getListSelectionComponent().openListImportDialog();
			}
        });        
        
    }

    @Override
    public void updateLabels() {
        headingLabel.setValue(messageSource.getMessage(Message.LIST_DETAILS)); 
        browseForLists.setCaption(messageSource.getMessage(Message.BROWSE_FOR_A_LIST) + " ");
        searchForLists.setCaption(messageSource.getMessage(Message.SEARCH_FOR_A_LIST) + " ");
        or.setValue(messageSource.getMessage(Message.OR) + " ");
        toWorkWith.setValue(messageSource.getMessage(Message.A_LIST_TO_WORK_WITH));
    }

    public void createListDetailsTab(Integer listId) throws MiddlewareQueryException{
        GermplasmList germplasmList = germplasmListManager.getGermplasmListById(listId);
        if (germplasmList == null) {
            hideDetailsTabsheet();
            this.noListLabel.setCaption("There is no list in the database with id: " + listId);
            this.noListLabel.setVisible(true);
        } else {
            noListLabel.setVisible(false);
            final String tabName = germplasmList.getName();
            this.createTab(listId, germplasmList, tabName);
            this.showDetailsTabsheet();
        }
    }
    
    private void createTab(final int id, final GermplasmList germplasmList, final String tabName) {
        
    	final boolean tabExists = Util.isTabDescriptionExist(detailsTabSheet, generateTabDescription(germplasmList.getId()));
        
        if (!tabExists) {
            
        	final Component tabContent = new ListTabComponent(source, this, germplasmList);
            final Tab tab = detailsTabSheet.addTab(tabContent, tabName, null);
            
            if (germplasmList != null){
                tab.setDescription(generateTabDescription(germplasmList.getId()));
            }
            
            tab.setClosable(true);
            detailsTabSheet.setSelectedTab(tabContent);
            
        } else {
            final Tab tab = Util.getTabWithDescription(detailsTabSheet, generateTabDescription(germplasmList.getId()));

            if (tab != null){
                detailsTabSheet.setSelectedTab(tab.getComponent());
            }
        }
    }
    
    private String generateTabDescription(Integer listId){
        return TAB_DESCRIPTION_PREFIX + listId;
    }
    
    public TabSheet getDetailsTabsheet() {
        return this.detailsTabSheet;
    }
    
    public void showDetailsTabsheet() {
        detailsTabSheet.setVisible(true);
        this.addComponent(detailsTabSheet);
        this.requestRepaint();
    }
    
    public void hideDetailsTabsheet() {
        btnCloseAllTabs.setVisible(false);
        detailsTabSheet.setVisible(false);
        
        this.removeComponent(detailsTabSheet);
        this.requestRepaint();
    }
    
    public void repaintTabsheet() {
    	if(detailsTabSheet.isVisible()){
    	    this.removeAllComponents();
    	    this.addComponent(listSelectionHeaderContainer);
    	    this.addComponent(searchOrBrowseContainer);
    	    this.addComponent(detailsTabSheet);
    	
            detailsTabSheet.setVisible(true);
            
            if(detailsTabSheet.getComponentCount() > 1){
            	btnCloseAllTabs.setVisible(true);
            }
            this.requestRepaint();
    	}    	
    }
    
    public void renameTab(Integer listId, String newName){
    	
    	//dennis put change header name here
        String tabDescription = generateTabDescription(listId);
        Tab tab = Util.getTabWithDescription(detailsTabSheet, tabDescription);
        if (tab != null){
            tab.setCaption(newName);
            ListTabComponent listDetails = (ListTabComponent) tab.getComponent();
            listDetails.setListNameLabel(newName);
            
            if(tab.getComponent() instanceof ListTabComponent){
            	((ListTabComponent) tab.getComponent()).getGermplasmList().setName(newName);
            	
            	GermplasmList germplasmList = ((ListTabComponent) tab.getComponent()).getListComponent().getGermplasmList();
            	germplasmList.setName(newName);
            	((ListTabComponent) tab.getComponent()).getListComponent().setViewListHeaderWindow(new ViewListHeaderWindow(germplasmList));
            }
        }
    }
    
    public void removeTab(Integer listId){
        String tabDescription = generateTabDescription(listId);
        Tab tab = Util.getTabWithDescription(detailsTabSheet, tabDescription);
        if (tab != null){
            detailsTabSheet.removeTab(tab);
        }
        
        if(detailsTabSheet.getComponentCount() == 0){
            this.hideDetailsTabsheet();
        }
    }

	@Override
	public void setHasUnsavedChangesMain(boolean hasChanges) {
		source.setHasUnsavedChangesMain(hasChanges);
	}

	public Map<ListComponent,Boolean> getListStatusForChanges(){
		return listStatusForChanges;
	}
	
	public void addUpdateListStatusForChanges(ListComponent listComponent, Boolean status){
		removeListStatusForChanges(listComponent);
		listStatusForChanges.put(listComponent, status);
		
		if(hasUnsavedChanges()){
			setHasUnsavedChangesMain(true);
		}
		else{
			setHasUnsavedChangesMain(false);
		}
	}
	
	public boolean hasUnsavedChanges() {
		List<Boolean> listOfStatus = new ArrayList<Boolean>();
		
		listOfStatus.addAll(listStatusForChanges.values());
		
		for(Boolean status: listOfStatus){
			if(status){
				return true;
			}
		}
		
		return false;
	}

	public void removeListStatusForChanges(ListComponent listComponent){
		if(listStatusForChanges.containsKey(listComponent)){
			listStatusForChanges.remove(listComponent);
		}
	}
	
	public void updateViewForAllLists(ModeView modeView){
		List<ListComponent> listComponents = new ArrayList<ListComponent>();
		listComponents.addAll(listStatusForChanges.keySet());
		
		if(modeView.equals(ModeView.LIST_VIEW)){
			for(ListComponent listComponent : listComponents){
				listComponent.changeToListView();
			}
		}
		else if(modeView.equals(ModeView.INVENTORY_VIEW)){
			for(ListComponent listComponent : listComponents){
				listComponent.viewInventoryActionConfirmed();
			}
		}
	}
	
	public void updateHasChangesForAllList(Boolean hasChanges){
		List<ListComponent> listComponents = new ArrayList<ListComponent>();
		listComponents.addAll(listStatusForChanges.keySet());
		
		for(ListComponent listComponent : listComponents){
			listComponent.setHasUnsavedChanges(hasChanges);
		}
	}

	public void resetListViewForCancelledChanges() {
		List<ListComponent> listComponents = new ArrayList<ListComponent>();
		listComponents.addAll(listStatusForChanges.keySet());
		
		for(ListComponent listComponent : listComponents){
			if(listComponent.hasUnsavedChanges()){
				listComponent.resetListDataTableValues();
			}
		}
	}
	
	public void resetInventoryViewForCancelledChanges() {
		List<ListComponent> listComponents = new ArrayList<ListComponent>();
		listComponents.addAll(listStatusForChanges.keySet());
		
		for(ListComponent listComponent : listComponents){
			if(listComponent.hasUnsavedChanges()){
				listComponent.resetListInventoryTableValues();
			}
		}
	}

}