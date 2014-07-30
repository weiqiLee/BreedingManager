package org.generationcp.browser.germplasmlist;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.generationcp.browser.application.Message;
import org.generationcp.browser.cross.study.h2h.main.dialogs.SelectGermplasmListComponent;
import org.generationcp.browser.germplasmlist.listeners.GermplasmListButtonClickListener;
import org.generationcp.browser.germplasmlist.listeners.GermplasmListItemClickListener;
import org.generationcp.browser.germplasmlist.listeners.GermplasmListTreeExpandListener;
import org.generationcp.browser.germplasmlist.util.GermplasmListTreeUtil;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ItemStyleGenerator;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class ListManagerTreeComponent extends VerticalLayout implements
		InternationalizableComponent, InitializingBean, Serializable {

	protected static final Logger LOG = LoggerFactory.getLogger(ListManagerTreeComponent.class);
	
	protected static final long serialVersionUID = -224052511814636864L;
	protected final static int BATCH_SIZE = 50;
	public final static String REFRESH_BUTTON_ID = "ListManagerTreeComponent Refresh Button";
	public static final String CENTRAL = "CENTRAL";
	public static final String LOCAL = "LOCAL";
	
	protected Label heading;
	protected Tree germplasmListTree;
    protected AbsoluteLayout germplasmListBrowserMainLayout;
	protected Button refreshButton;
	
    @Autowired
    protected GermplasmListManager germplasmListManager;
    
    @Autowired
    protected SimpleResourceBundleMessageSource messageSource;
    
    protected boolean forGermplasmListWindow;

    protected HorizontalLayout controlButtonsLayout; 
    protected VerticalLayout treeContainerLayout;
    
    protected Integer listId;
    protected GermplasmListTreeUtil germplasmListTreeUtil;
    protected SelectGermplasmListComponent selectListComponent;
    
    protected final ThemeResource ICON_REFRESH = new ThemeResource("images/refresh-icon.png");
    
    protected Button addFolderBtn;
    protected Button deleteFolderBtn;
    protected Button renameFolderBtn;
    
    protected Object selectedListId;
    
    private boolean forSelectingFolderToSaveIn;
    
    public ListManagerTreeComponent(boolean forSelectingFolderToSaveIn, Integer folderId){
    	super();
    	this.forSelectingFolderToSaveIn = forSelectingFolderToSaveIn;
    	this.selectListComponent = null;
    	this.germplasmListBrowserMainLayout = null;
        this.forGermplasmListWindow = false;
        this.listId = folderId;
    }

    @Override
	public void afterPropertiesSet() throws Exception {
    	setSpacing(true);
		
    	heading = new Label();
		heading.setValue("List Location");
		heading.setStyleName(Bootstrap.Typography.H6.styleName());
		heading.setWidth("90px");
    	
		if (this.germplasmListBrowserMainLayout != null){
			initializeButtonPanel();
			addComponent(controlButtonsLayout);
		}
		
		if(forSelectingFolderToSaveIn){
			initializeButtonPanel();
			addComponent(controlButtonsLayout);
		}
    	
		germplasmListTree = new Tree();
		
		if(!forSelectingFolderToSaveIn){
			refreshButton = new Button();
			refreshButton.setData(REFRESH_BUTTON_ID);
			refreshButton.addListener(new GermplasmListButtonClickListener(this));
			refreshButton.setCaption(messageSource.getMessage(Message.REFRESH_LABEL));
			refreshButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		}
		
		treeContainerLayout = new VerticalLayout();
		treeContainerLayout.addComponent(germplasmListTree);
		
		addComponent(treeContainerLayout);
		if(!forSelectingFolderToSaveIn){
			addComponent(refreshButton);
		}
		
		createTree();
		
		germplasmListTreeUtil = new GermplasmListTreeUtil(this, germplasmListTree);
	}

	protected void initializeButtonPanel() {
		renameFolderBtn =new Button("<span class='bms-edit' style='left: 2px; color: #0083c0;font-size: 18px; font-weight: bold;'></span>");
        renameFolderBtn.setHtmlContentAllowed(true);
        renameFolderBtn.setDescription("Rename Item");
        renameFolderBtn.setStyleName(Reindeer.BUTTON_LINK);
        renameFolderBtn.setWidth("25px");
        renameFolderBtn.setHeight("30px");
        renameFolderBtn.setEnabled(false);
        renameFolderBtn.addListener(new Button.ClickListener() {
			protected static final long serialVersionUID = 1L;
			@Override
            public void buttonClick(Button.ClickEvent event) {
				germplasmListTreeUtil.renameFolderOrList(Integer.valueOf(selectedListId.toString()));
            }
        });
        
        addFolderBtn = new Button("<span class='bms-add' style='left: 2px; color: #00a950;font-size: 18px; font-weight: bold;'></span>");
        addFolderBtn.setHtmlContentAllowed(true);
        addFolderBtn.setDescription("Add New Folder");
        addFolderBtn.setStyleName(Reindeer.BUTTON_LINK);
        addFolderBtn.setWidth("25px");
        addFolderBtn.setHeight("30px");
        addFolderBtn.setEnabled(false);
        addFolderBtn.addListener(new Button.ClickListener() {
			protected static final long serialVersionUID = 1L;
			@Override
            public void buttonClick(Button.ClickEvent event) {
				germplasmListTreeUtil.addFolder(selectedListId);
            }
        });
        

        deleteFolderBtn = new Button("<span class='bms-delete' style='left: 2px; color: #f4a41c;font-size: 18px; font-weight: bold;'></span>");
        deleteFolderBtn.setHtmlContentAllowed(true);
        deleteFolderBtn.setDescription("Delete Selected List/Folder");
        deleteFolderBtn.setStyleName(Reindeer.BUTTON_LINK);
        deleteFolderBtn.setWidth("25px");
        deleteFolderBtn.setHeight("30px");
        deleteFolderBtn.setEnabled(false);
        deleteFolderBtn.addListener(new Button.ClickListener() {
			protected static final long serialVersionUID = 1L;
			@Override
            public void buttonClick(Button.ClickEvent event) {
				germplasmListTreeUtil.deleteFolderOrList(getListManagerTreeComponent(), Integer.valueOf(selectedListId.toString()));
            }
        });
        
        controlButtonsLayout = new HorizontalLayout();
        
        HorizontalLayout buttonsPanel = new HorizontalLayout();
        buttonsPanel.addComponent(addFolderBtn);
        buttonsPanel.addComponent(renameFolderBtn);
        buttonsPanel.addComponent(deleteFolderBtn);

        controlButtonsLayout.setSizeFull();
        controlButtonsLayout.setSpacing(true);
        controlButtonsLayout.addComponent(heading);
        controlButtonsLayout.addComponent(buttonsPanel);
        controlButtonsLayout.setComponentAlignment(heading, Alignment.MIDDLE_LEFT);
        controlButtonsLayout.setComponentAlignment(buttonsPanel, Alignment.MIDDLE_RIGHT);
	}

	@Override
	public void updateLabels() {
	}
	
	public void simulateItemClickForNewlyAdded(Integer listId, boolean openDetails ){
	    germplasmListTree.expandItem(LOCAL);
	    if(openDetails){
    	    germplasmListTree.setValue(listId);
	    }
	}
	
	/*
	 * Resets listid to null (in case list was launched via Dashboard)
	 * so that tree can be refreshed
	 */
	public void refreshTree(){
		this.listId = null; 
		createTree();
	}

    public void createTree() {
    	treeContainerLayout.removeComponent(germplasmListTree);
   		germplasmListTree.removeAllItems();
   		germplasmListTree = createGermplasmListTree();
        germplasmListTree.addStyleName("listManagerTree");
        germplasmListTree.setHeight("220px");
        
        if(selectListComponent != null){
        	germplasmListTree.addStyleName("listManagerTree-long");
        }
        
        germplasmListTree.setItemStyleGenerator(new ItemStyleGenerator() {
        	protected static final long serialVersionUID = -5690995097357568121L;

			@Override
            public String getStyle(Object itemId) {
				
				GermplasmList currentList = null;
				
				try {
					currentList = germplasmListManager.getGermplasmListById(Integer.valueOf(itemId.toString()));
				} catch (NumberFormatException e) {
					currentList = null;
				} catch (MiddlewareQueryException e) {
					LOG.error("Erro with getting list by id: " + itemId, e);
					currentList = null;
				} 
				
            	if(itemId.equals(LOCAL) || itemId.equals(CENTRAL)){
            		return "listManagerTreeRootNode"; 
            	} else if(currentList!=null && currentList.getType().equals("FOLDER")){
            		return "listManagerTreeRegularParentNode";
            	} else {
            		return "listManagerTreeRegularChildNode";
            	}

            }
        });

        germplasmListTree.setImmediate(true);
        
        if (this.forSelectingFolderToSaveIn){
        	germplasmListTreeUtil = new GermplasmListTreeUtil(this, germplasmListTree);
        }
        treeContainerLayout.addComponent(germplasmListTree);
        germplasmListTree.requestRepaint();

    }

    protected Tree createGermplasmListTree() {
        List<GermplasmList> localGermplasmListParent = new ArrayList<GermplasmList>();
        List<GermplasmList> centralGermplasmListParent = new ArrayList<GermplasmList>();

        try {
            localGermplasmListParent = this.germplasmListManager.getAllTopLevelListsBatched(BATCH_SIZE, Database.LOCAL);
        } catch (MiddlewareQueryException e) {
            LOG.error("Error in getting top level lists.", e);
            if (getWindow() != null){
                MessageNotifier.showWarning(getWindow(), 
                        messageSource.getMessage(Message.ERROR_DATABASE),
                    messageSource.getMessage(Message.ERROR_IN_GETTING_TOP_LEVEL_FOLDERS));
            }
            localGermplasmListParent = new ArrayList<GermplasmList>();
        }
        
        if(!forSelectingFolderToSaveIn){
	        try {
	            centralGermplasmListParent = this.germplasmListManager.getAllTopLevelListsBatched(BATCH_SIZE, Database.CENTRAL);
	        } catch (MiddlewareQueryException e) {
	        	LOG.error("Error in getting top level lists.", e);
	            if (getWindow() != null){
	                MessageNotifier.showWarning(getWindow(), 
	                        messageSource.getMessage(Message.ERROR_DATABASE),
	                    messageSource.getMessage(Message.ERROR_IN_GETTING_TOP_LEVEL_FOLDERS));
	            }
	            centralGermplasmListParent = new ArrayList<GermplasmList>();
	        }
        }
        
        Tree germplasmListTree = new Tree();
        if (this.forSelectingFolderToSaveIn){
        	germplasmListTree.setDragMode(TreeDragMode.NODE);
        }

        germplasmListTree.addItem(LOCAL);
        germplasmListTree.setItemCaption(LOCAL, "Program Lists");
        
        if(!forSelectingFolderToSaveIn){
	        germplasmListTree.addItem(CENTRAL);
	        germplasmListTree.setItemCaption(CENTRAL, "Public Lists");
        }
        
        for (GermplasmList localParentList : localGermplasmListParent) {
        	if(!forSelectingFolderToSaveIn || isFolder(localParentList.getId())){
	            germplasmListTree.addItem(localParentList.getId());
	            germplasmListTree.setItemCaption(localParentList.getId(), localParentList.getName());
	            germplasmListTree.setChildrenAllowed(localParentList.getId(), hasChildList(localParentList.getId()));
	            germplasmListTree.setParent(localParentList.getId(), LOCAL);
        	}
        }
        
        if(!forSelectingFolderToSaveIn){
	        for (GermplasmList centralParentList : centralGermplasmListParent) {
	            germplasmListTree.addItem(centralParentList.getId());
	            germplasmListTree.setItemCaption(centralParentList.getId(), centralParentList.getName());
	            germplasmListTree.setChildrenAllowed(centralParentList.getId(), hasChildList(centralParentList.getId()));
	            germplasmListTree.setParent(centralParentList.getId(), CENTRAL);
	        }
        }
        
        germplasmListTree.addListener(new GermplasmListTreeExpandListener(this));
        germplasmListTree.addListener(new GermplasmListItemClickListener(this));
        
        try{
        	if(listId != null){
	        	GermplasmList list = germplasmListManager.getGermplasmListById(listId);
	    		
	    		if(list != null){
	    			Deque<GermplasmList> parents = new ArrayDeque<GermplasmList>();
	    			GermplasmListTreeUtil.traverseParentsOfList(germplasmListManager, list, parents);
	    			
	    			if(listId < 0){
	                	germplasmListTree.expandItem(LOCAL);
	    			} else{
	    				germplasmListTree.expandItem(CENTRAL);
	    			}
	    			
	    			while(!parents.isEmpty()){
	    				GermplasmList parent = parents.pop();
	    				germplasmListTree.setChildrenAllowed(parent.getId(), true);
	    				addGermplasmListNode(parent.getId().intValue(), germplasmListTree);
	    				germplasmListTree.expandItem(parent.getId());
	    			}
	    			
	    			germplasmListTree.select(listId);
	    			germplasmListTree.setValue(listId);
	    			setSelectedListId(listId);
	    		}
	        }
        } catch(MiddlewareQueryException ex){
    		LOG.error("Error with getting parents for hierarchy of list id: " + listId, ex);
    	}
        
        if(forSelectingFolderToSaveIn){
        	germplasmListTree.setNullSelectionAllowed(false);
        }
        
        return germplasmListTree;
    }
    
    public void updateButtons(Object itemId){
    	setSelectedListId(itemId);
    	if (forSelectingFolderToSaveIn){
    		try {
    			//If any of the central lists/folders is selected
    			if(Integer.valueOf(itemId.toString())>0){
    				addFolderBtn.setEnabled(false);
    				renameFolderBtn.setEnabled(false);
    				deleteFolderBtn.setEnabled(false);
    				//If any of the local folders/lists are selected
    			} else if(Integer.valueOf(itemId.toString())<=0){
    				addFolderBtn.setEnabled(true);
    				renameFolderBtn.setEnabled(true);
    				deleteFolderBtn.setEnabled(true);
    				//The rest of the local lists
    			} else {
    				addFolderBtn.setEnabled(true);
    				renameFolderBtn.setEnabled(true);
    				deleteFolderBtn.setEnabled(false);
    			}
    		} catch(NumberFormatException e) {
    			//If selected item is "Shared Lists"
    			if(itemId.toString().equals("CENTRAL")) {
    				addFolderBtn.setEnabled(false);
    				renameFolderBtn.setEnabled(false);
    				deleteFolderBtn.setEnabled(false);
    				//If selected item is "Program Lists"
    			} else if(itemId.toString().equals(LOCAL)) {
    				addFolderBtn.setEnabled(true);
    				renameFolderBtn.setEnabled(false);
    				deleteFolderBtn.setEnabled(false);
    				//Any non-numeric itemID (nothing goes here as of the moment)
    			} else {
    				addFolderBtn.setEnabled(true);
    				renameFolderBtn.setEnabled(true);
    				deleteFolderBtn.setEnabled(true);
    			}
    		}
    	}
    }
    	
    
    public void listManagerTreeItemClickAction(int germplasmListId) throws InternationalizableException{

        try {
    		
        	GermplasmList germplasmList = germplasmListManager.getGermplasmListById(germplasmListId);
        	
        	selectedListId = germplasmListId;
        	
        	boolean hasChildList = hasChildList(germplasmListId);
        	boolean isEmptyFolder = isEmptyFolder(germplasmList);
        	if (!isEmptyFolder){

        		if (this.selectListComponent != null){
        			this.selectListComponent.getListInfoComponent().displayListInfo(germplasmList);
        		//toggle folder
	        	} else if(hasChildList){
	        		expandOrCollapseListTreeNode(Integer.valueOf(germplasmListId));
	        	}
        		
        		germplasmListTree.select(germplasmListId);
        		germplasmListTree.setValue(germplasmListId);
        	}
			        
        } catch (NumberFormatException e) {
        	LOG.error("Error clicking of list.", e);
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_INVALID_FORMAT),
                    messageSource.getMessage(Message.ERROR_IN_NUMBER_FORMAT),
                    Notification.POSITION_CENTERED);
        }catch (MiddlewareQueryException e){
        	LOG.error("Error in displaying germplasm list details.", e);
            throw new InternationalizableException(e, Message.ERROR_DATABASE,
                    Message.ERROR_IN_CREATING_GERMPLASMLIST_DETAILS_WINDOW);
        }
        
    }    
	
    protected boolean hasChildList(int listId) {

        List<GermplasmList> listChildren = new ArrayList<GermplasmList>();

        try {
            listChildren = this.germplasmListManager.getGermplasmListByParentFolderId(listId, 0, 1);
        } catch (MiddlewareQueryException e) {
        	LOG.error("Error in getting germplasm lists by parent id.", e);
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_DATABASE), 
                    messageSource.getMessage(Message.ERROR_IN_GETTING_GERMPLASM_LISTS_BY_PARENT_FOLDER_ID));
            listChildren = new ArrayList<GermplasmList>();
        }
        
        return !listChildren.isEmpty();
    }

    protected boolean isEmptyFolder(GermplasmList list) throws MiddlewareQueryException{
        boolean isFolder = list.getType().equalsIgnoreCase("FOLDER");
        return isFolder && !hasChildList(list.getId());
    }
    
    public boolean isFolder(Object itemId){
    	try {
    		int listId = Integer.valueOf(itemId.toString());
    		GermplasmList germplasmList = germplasmListManager.getGermplasmListById(listId);
    		if(germplasmList==null)
    			return false;
    		return germplasmList.getType().equalsIgnoreCase("FOLDER");
    	} catch (MiddlewareQueryException e){
    		return false;
    	} catch (NumberFormatException e){
    		if(listId!=null && (listId.toString().equals(LOCAL) || listId.toString().equals(CENTRAL))){
    			return true;
    		} else {
    			return false;
    		}
    	}
    }
    
    public void addGermplasmListNode(int parentGermplasmListId) throws InternationalizableException{
        List<GermplasmList> germplasmListChildren = new ArrayList<GermplasmList>();

        try {
            germplasmListChildren = this.germplasmListManager.getGermplasmListByParentFolderIdBatched(parentGermplasmListId, BATCH_SIZE);
        } catch (MiddlewareQueryException e) {
            LOG.error("Error in getting germplasm lists by parent id.", e);
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_DATABASE), 
                    messageSource.getMessage(Message.ERROR_IN_GETTING_GERMPLASM_LISTS_BY_PARENT_FOLDER_ID));
            germplasmListChildren = new ArrayList<GermplasmList>();
        }

        for (GermplasmList listChild : germplasmListChildren) {
        	if(!forSelectingFolderToSaveIn || isFolder(listChild.getId())){
	            germplasmListTree.addItem(listChild.getId());
	            germplasmListTree.setItemCaption(listChild.getId(), listChild.getName());
	            germplasmListTree.setParent(listChild.getId(), parentGermplasmListId);
	            // allow children if list has sub-lists
	            germplasmListTree.setChildrenAllowed(listChild.getId(), hasChildList(listChild.getId()));
        	}
        }
        germplasmListTree.select(parentGermplasmListId);
        germplasmListTree.setValue(parentGermplasmListId);
    }
    
    public void addGermplasmListNode(int parentGermplasmListId, Tree germplasmListTree) throws InternationalizableException{
        List<GermplasmList> germplasmListChildren = new ArrayList<GermplasmList>();

        try {
            germplasmListChildren = this.germplasmListManager.getGermplasmListByParentFolderIdBatched(parentGermplasmListId, BATCH_SIZE);
        } catch (MiddlewareQueryException e) {
            LOG.error("Error in getting germplasm lists by parent id.", e);
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_DATABASE), 
                    messageSource.getMessage(Message.ERROR_IN_GETTING_GERMPLASM_LISTS_BY_PARENT_FOLDER_ID));
            germplasmListChildren = new ArrayList<GermplasmList>();
        }

        for (GermplasmList listChild : germplasmListChildren) {
        	if(!forSelectingFolderToSaveIn || isFolder(listChild.getId())){
	            germplasmListTree.addItem(listChild.getId());
	            germplasmListTree.setItemCaption(listChild.getId(), listChild.getName());
	            germplasmListTree.setParent(listChild.getId(), parentGermplasmListId);
	            // allow children if list has sub-lists
	            germplasmListTree.setChildrenAllowed(listChild.getId(), hasChildList(listChild.getId()));
        	}
        }
        germplasmListTree.select(parentGermplasmListId);
    }
    
    
    public static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        }
        return true;
    }
    
    public void expandOrCollapseListTreeNode(Object nodeId){
    	if(!this.germplasmListTree.isExpanded(nodeId)){
    		this.germplasmListTree.expandItem(nodeId);
    	} else{
    		this.germplasmListTree.collapseItem(nodeId);
    	}
    	germplasmListTree.setValue(nodeId);
    	germplasmListTree.select(nodeId);
    }
    
    public Tree getGermplasmListTree(){
    	return germplasmListTree;
    }
    
    public void setSelectedListId(Object listId){
    	this.selectedListId = listId;
    	germplasmListTree.select(listId);
    	germplasmListTree.setValue(listId);
    }
    
    public void setListId(Integer listId){
    	this.listId = listId;
    }
    
    public Object getSelectedListId(){
    	return selectedListId;
    }
    
    private ListManagerTreeComponent getListManagerTreeComponent(){
    	return this;
    }
    
    public Tree getTree(){
    	return this.germplasmListTree;
    }
    
    @Override
    public Window getWindow() {
    	if(super.getWindow().getParent() != null){
    		return super.getWindow().getParent();
    	} else {
    		return super.getWindow();
    	}
    }

}