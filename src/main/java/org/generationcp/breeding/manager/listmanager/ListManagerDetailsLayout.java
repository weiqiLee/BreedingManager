package org.generationcp.breeding.manager.listmanager;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.listmanager.listeners.GermplasmListButtonClickListener;
import org.generationcp.breeding.manager.listmanager.listeners.GermplasmListTabChangeListener;
import org.generationcp.breeding.manager.util.ConfirmDialogSelectedTabCloseHandler;
import org.generationcp.breeding.manager.util.Util;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

@Configurable
public class ListManagerDetailsLayout extends VerticalLayout implements
		InternationalizableComponent, InitializingBean {
	
	public static final String CLOSE_ALL_TABS_ID = "ListManagerTreeComponent Close All Tabs ID";
	public static final String TAB_DESCRIPTION_PREFIX = "List ID: ";

	private static final long serialVersionUID = 8092751288890434894L;
	@Autowired
	private GermplasmListManager germplasmListManager;
	
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	private ListManagerTreeComponent treeComponent;
	private ListManagerMain listManagerMain;
	
	private TabSheet detailsTabSheet;
    private AbsoluteLayout parentLayout;
	private Label heading;
	private Button btnCloseAllTabs;
	private HorizontalLayout headingBar;
	private Label noListLabel;
	
	private boolean forGermplasmListWindow;

	public ListManagerDetailsLayout(ListManagerMain listManagerMain, ListManagerTreeComponent treeComponent, AbsoluteLayout parentLayout, boolean forGermplasmListWindow){
		this.listManagerMain = listManagerMain;
    	this.treeComponent = treeComponent;
    	this.parentLayout = parentLayout;
    	this.forGermplasmListWindow = forGermplasmListWindow;
    }
    
    public ListManagerDetailsLayout(ListManagerMain listManagerMain, AbsoluteLayout parentLayout, boolean forGermplasmListWindow){
    	this.listManagerMain = listManagerMain;
    	this.parentLayout = parentLayout;
    	this.forGermplasmListWindow = forGermplasmListWindow;
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		detailsTabSheet = new TabSheet();
		if (this.treeComponent != null){
			detailsTabSheet.setHeight("465px");
		} else {
			detailsTabSheet.setHeight("411px");
		}
		
		noListLabel = new Label();
	}

	@Override
	public void updateLabels() {

	}

	
    public void createListInfoFromBrowseScreen(int germplasmListId) throws MiddlewareQueryException {
        GermplasmList germplasmList=getGermplasmList(germplasmListId);
        if(germplasmList != null){
        	String tabName = germplasmList.getName();
        	createTab(germplasmListId, germplasmList, tabName);
        } else{
        	noListLabel.setCaption("There is no list in the database with id: " + germplasmListId);
        	parentLayout.addComponent(noListLabel, "top:20px;left:340px");
        }
    }
    
    public void createListInfoFromSearchScreen(int germplasmListId) throws MiddlewareQueryException {
        GermplasmList germplasmList=getGermplasmList(germplasmListId);
        String tabName = "List - " + germplasmList.getName();
        
		createTab(germplasmListId, germplasmList, tabName);
    }
    
    
    public void createGermplasmInfoTab(int germplasmId) throws MiddlewareQueryException {
        String tabName = "Germplasm - " + germplasmId;
		createTab(germplasmId, null, tabName);
    }

    
	private void createTab(int id, GermplasmList germplasmList, String tabName) {
		
		boolean tabExists = false;
		//workaround since Browse Lists and Search Lists have different tab name formats
		if (germplasmList != null){
			tabExists = Util.isTabDescriptionExist(detailsTabSheet, generateTabDescription(germplasmList.getId()));
		} else { 
			tabExists = Util.isTabExist(detailsTabSheet, tabName);
		}
		if (!tabExists) {
			
			VerticalLayout layout = new VerticalLayout();
        	Component component = createTabContent(id, germplasmList, tabName);
        	layout.addComponent(component);
            
            Tab tab = detailsTabSheet.addTab(layout, tabName, null);
            if (germplasmList != null){
            	tab.setDescription(generateTabDescription(germplasmList.getId()));
            }
            tab.setClosable(true);
            
            if(detailsTabSheet.getComponentCount() <= 1){
            	initializeLayout();
            }
            addListeners(layout, component);
            
            
        } else {
            Tab tab;
            if (germplasmList != null){
            	tab = Util.getTabWithDescription(detailsTabSheet, generateTabDescription(germplasmList.getId()));
            } else {
            	tab = Util.getTabToFocus(detailsTabSheet, tabName);
            }
            if (tab != null){
            	detailsTabSheet.setSelectedTab(tab.getComponent());
            }
        }
	}

	private Component createTabContent(int id, GermplasmList germplasmList, String tabName) {
		
		if (germplasmList != null){
			return new ListManagerTreeMenu(this, id,
					germplasmList.getName(), germplasmList.getStatus(), germplasmList.getUserId(), 
					false, forGermplasmListWindow, listManagerMain);
		} else {
			return new BrowseGermplasmTreeMenu(this.listManagerMain, id);
		}
		
	}

	private void addListeners(VerticalLayout layout, Component component) {
		detailsTabSheet.setSelectedTab(layout);
		detailsTabSheet.setCloseHandler(new ConfirmDialogSelectedTabCloseHandler());
		detailsTabSheet.addListener(new GermplasmListTabChangeListener(component));
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
	}

	private void initializeLayout() {
		//reset
    	parentLayout.removeComponent(detailsTabSheet);
    	parentLayout.removeComponent(noListLabel);
    	
    	btnCloseAllTabs = new Button(messageSource.getMessage(Message.CLOSE_ALL_TABS));
    	btnCloseAllTabs.setData(CLOSE_ALL_TABS_ID);
    	btnCloseAllTabs.setImmediate(true);
    	btnCloseAllTabs.setStyleName(Reindeer.BUTTON_LINK);
    	btnCloseAllTabs.addListener(new GermplasmListButtonClickListener(this));
    	btnCloseAllTabs.setVisible(false);
    	
    	heading = new Label();
    	heading.setWidth("300px");
    	if (this.treeComponent != null){
    		heading.setValue(messageSource.getMessage(Message.MANAGE_LISTS)); //Browse Lists screen
    	} else {
    		heading.setValue(messageSource.getMessage(Message.DETAILS));
    	}
		heading.setStyleName(Bootstrap.Typography.H3.styleName());
		
		headingBar = new HorizontalLayout();
		headingBar.setWidth("100%");
		headingBar.setHeight("27px");
		headingBar.addComponent(heading);
		headingBar.addComponent(btnCloseAllTabs);
		headingBar.setComponentAlignment(heading, Alignment.BOTTOM_LEFT);
		headingBar.setComponentAlignment(btnCloseAllTabs, Alignment.BOTTOM_RIGHT);
		
		//Browse Lists exact layout
		if (this.treeComponent != null){
			parentLayout.addComponent(headingBar,"top:20px; left:250px;");
	    	parentLayout.addComponent(detailsTabSheet, "top:55px;left:250px");
        
        //Search Lists exact layout
		} else {
			parentLayout.addComponent(headingBar,"top:80px; left:390px;");
	    	parentLayout.addComponent(detailsTabSheet, "top:115px;left:390px");
		} 	
		
		parentLayout.setWidth("98%");
		parentLayout.setStyleName(Runo.TABSHEET_SMALL);
	}
    
	
    public void closeAllListDetailTabButtonClickAction() {
    	Util.closeAllTab(detailsTabSheet);
        parentLayout.removeComponent(headingBar);
        parentLayout.removeComponent(detailsTabSheet);
    }
    
    private GermplasmList getGermplasmList(int germplasmListId) throws MiddlewareQueryException {
        return this.germplasmListManager.getGermplasmListById(germplasmListId);
    }
    
    public ListManagerTreeComponent getTreeComponent(){
    	return this.treeComponent;
    }
    
    public static String generateTabDescription(Integer listId){
    	return TAB_DESCRIPTION_PREFIX + listId;
    }
    
    public TabSheet getTabSheet(){
    	return detailsTabSheet;
    }
    
    public void removeListTab(Integer listId){
    	String tabDescription = generateTabDescription(listId);
    	Tab tab = Util.getTabWithDescription(detailsTabSheet, tabDescription);
        if (tab != null){
        	detailsTabSheet.removeTab(tab);
        }
    }
    
    public void renameListTab(Integer listId, String newName){
    	String tabDescription = generateTabDescription(listId);
    	Tab tab = Util.getTabWithDescription(detailsTabSheet, tabDescription);
        if (tab != null){
        	tab.setCaption(newName);
        	VerticalLayout verticalLayout = (VerticalLayout) tab.getComponent();
			ListDetailComponent listDetailComponent = ((ListManagerTreeMenu)verticalLayout.getComponent(0)).getListManagerListDetailComponent();
			listDetailComponent.setLblName(newName);
        }
    }
    
    
    

}