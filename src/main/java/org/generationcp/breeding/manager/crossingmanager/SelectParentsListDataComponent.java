package org.generationcp.breeding.manager.crossingmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.AppConstants;
import org.generationcp.breeding.manager.constants.ModeView;
import org.generationcp.breeding.manager.customcomponent.ActionButton;
import org.generationcp.breeding.manager.customcomponent.HeaderLabelLayout;
import org.generationcp.breeding.manager.customcomponent.TableWithSelectAllLayout;
import org.generationcp.breeding.manager.customcomponent.ViewListHeaderWindow;
import org.generationcp.breeding.manager.customcomponent.listinventory.CrossingManagerInventoryTable;
import org.generationcp.breeding.manager.inventory.ReservationStatusWindow;
import org.generationcp.breeding.manager.inventory.ReserveInventoryAction;
import org.generationcp.breeding.manager.inventory.ReserveInventorySource;
import org.generationcp.breeding.manager.inventory.ReserveInventoryUtil;
import org.generationcp.breeding.manager.inventory.ReserveInventoryWindow;
import org.generationcp.breeding.manager.listeners.InventoryLinkButtonClickListener;
import org.generationcp.breeding.manager.listimport.listeners.GidLinkClickListener;
import org.generationcp.commons.constant.ColumnLabels;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class SelectParentsListDataComponent extends VerticalLayout implements InitializingBean, 
							InternationalizableComponent, BreedingManagerLayout, 
							ReserveInventorySource {

	private static final String NO_LOT_FOR_THIS_GERMPLASM = "No Lot for this Germplasm";
	private static final String CLICK_TO_VIEW_INVENTORY_DETAILS = "Click to view Inventory Details";
	private static final String STRING_DASH = "-";

	private final class ListDataTableActionHandler implements Action.Handler {
		private static final long serialVersionUID = -2173636726748988046L;

		@Override
		public void handleAction(Action action, Object sender, Object target) {
			if(action.equals(ACTION_ADD_TO_FEMALE_LIST)){
				makeCrossesParentsComponent.dropToFemaleOrMaleTable(listDataTable, makeCrossesParentsComponent.getFemaleTable(), null);
				makeCrossesParentsComponent.assignEntryNumber(makeCrossesParentsComponent.getFemaleTable());
				makeCrossesParentsComponent.getParentTabSheet().setSelectedTab(0);
			} else if(action.equals(ACTION_ADD_TO_MALE_LIST)){
				makeCrossesParentsComponent.dropToFemaleOrMaleTable(listDataTable, makeCrossesParentsComponent.getMaleTable(), null);
				makeCrossesParentsComponent.assignEntryNumber(makeCrossesParentsComponent.getMaleTable());
				makeCrossesParentsComponent.getParentTabSheet().setSelectedTab(1);
			}
		}

		@Override
		public Action[] getActions(Object target, Object sender) {
			return LIST_DATA_TABLE_ACTIONS;
		}
	}

	private final class ActionMenuClickListener implements ContextMenu.ClickListener {
		private static final long serialVersionUID = -2343109406180457070L;

		@Override
		public void contextItemClick(ClickEvent event) {
		  // Get reference to clicked item
		  ContextMenuItem clickedItem = event.getClickedItem();
		  if(clickedItem.getName().equals(messageSource.getMessage(Message.SELECT_ALL))){
			  listDataTable.setValue(listDataTable.getItemIds());
		  }else if(clickedItem.getName().equals(messageSource.getMessage(Message.ADD_TO_FEMALE_LIST))){
			  Collection<?> selectedIdsToAdd = (Collection<?>)listDataTable.getValue();
			  if(!selectedIdsToAdd.isEmpty()){
				  makeCrossesParentsComponent.dropToFemaleOrMaleTable(listDataTable, makeCrossesParentsComponent.getFemaleTable(), null);
				  makeCrossesParentsComponent.assignEntryNumber(makeCrossesParentsComponent.getFemaleTable());
				  makeCrossesParentsComponent.getParentTabSheet().setSelectedTab(0);
			  } else {
				  MessageNotifier.showWarning(getWindow(), messageSource.getMessage(Message.WARNING) 
		                    , messageSource.getMessage(Message.ERROR_LIST_ENTRIES_MUST_BE_SELECTED));
			  }
		  }else if(clickedItem.getName().equals(messageSource.getMessage(Message.ADD_TO_MALE_LIST))){
			  Collection<?> selectedIdsToAdd = (Collection<?>)listDataTable.getValue();
			  if(!selectedIdsToAdd.isEmpty()){
				  makeCrossesParentsComponent.dropToFemaleOrMaleTable(listDataTable, makeCrossesParentsComponent.getMaleTable(), null);
				  makeCrossesParentsComponent.assignEntryNumber(makeCrossesParentsComponent.getMaleTable());
				  makeCrossesParentsComponent.getParentTabSheet().setSelectedTab(1);
			  } else {
				  MessageNotifier.showWarning(getWindow(), messageSource.getMessage(Message.WARNING) 
		                    , messageSource.getMessage(Message.ERROR_LIST_ENTRIES_MUST_BE_SELECTED));
			  }
		  }else if(clickedItem.getName().equals(messageSource.getMessage(Message.INVENTORY_VIEW))){
			  viewInventoryAction();
		  }
   }
	}

	private static final Logger LOG = LoggerFactory.getLogger(SelectParentsListDataComponent.class);
	private static final long serialVersionUID = 7907737258051595316L;
	private static final String CHECKBOX_COLUMN_ID="Checkbox Column ID";
	
	public static final String LIST_DATA_TABLE_ID = "SelectParentsListDataComponent List Data Table ID";
	public static final String CROSSING_MANAGER_PARENT_TAB_INVENTORY_TABLE = "Crossing manager parent tab inventory table";
	
	private static final Action ACTION_ADD_TO_FEMALE_LIST = new Action("Add to Female List");
	private static final Action ACTION_ADD_TO_MALE_LIST = new Action("Add to Male List");
	private static final Action[] LIST_DATA_TABLE_ACTIONS = new Action[] {ACTION_ADD_TO_FEMALE_LIST, ACTION_ADD_TO_MALE_LIST};
	
	private Integer germplasmListId;
	private GermplasmList germplasmList;
	private Long count;
	private Label listEntriesLabel;
	private Label totalListEntriesLabel;
	private Label totalSelectedListEntriesLabel;

	private Table listDataTable;
	private Button viewListHeaderButton;
	private String listName;
	
	private Button actionButton;
	private ContextMenu actionMenu;
	
	private Button inventoryViewActionButton;
	private ContextMenu inventoryViewActionMenu;
	private ContextMenuItem menuCopyToNewListFromInventory;
	private ContextMenuItem menuInventorySaveChanges;
	@SuppressWarnings("unused")
	private ContextMenuItem menuListView;
	@SuppressWarnings("unused")
	private ContextMenuItem menuReserveInventory;
	
	public static final String ACTIONS_BUTTON_ID = "Actions";
	
	private ViewListHeaderWindow viewListHeaderWindow;
	
	private TableWithSelectAllLayout tableWithSelectAllLayout;
	private CrossingManagerInventoryTable listInventoryTable;
	
	//Layout variables
	private HorizontalLayout headerLayout;
	private HorizontalLayout subHeaderLayout;
	
	private boolean hasChanges = false;
	
    //Inventory Related Variables
    private ReserveInventoryWindow reserveInventory;
    private ReservationStatusWindow reservationStatus;
    private ReserveInventoryUtil reserveInventoryUtil;
    private ReserveInventoryAction reserveInventoryAction;
    private Map<ListEntryLotDetails, Double> validReservationsToSave;
	
	private MakeCrossesParentsComponent makeCrossesParentsComponent;
	
	@Autowired
    private SimpleResourceBundleMessageSource messageSource;

	@Autowired
    private GermplasmListManager germplasmListManager;
	
	@Autowired
	private InventoryDataManager inventoryDataManager;
	
	@Autowired
	private OntologyDataManager ontologyDataManager;
	
	public SelectParentsListDataComponent(Integer germplasmListId, String listName, MakeCrossesParentsComponent makeCrossesParentsComponent){
		super();
		this.germplasmListId = germplasmListId;
		this.listName = listName;
		this.makeCrossesParentsComponent = makeCrossesParentsComponent;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		instantiateComponents();
		initializeValues();
		addListeners();
		layoutComponents();
		
		if(makeCrossesParentsComponent.getMakeCrossesMain().getModeView().equals(ModeView.LIST_VIEW)){
			changeToListView();
		} else if(makeCrossesParentsComponent.getMakeCrossesMain().getModeView().equals(ModeView.INVENTORY_VIEW)){
			viewInventoryActionConfirmed();
		}
	}

	@Override
	public void updateLabels() {
		//do nothing
	}

	@Override
	public void instantiateComponents() {
		retrieveListDetails();
		
		listEntriesLabel = new Label(messageSource.getMessage(Message.LIST_ENTRIES_LABEL));
		listEntriesLabel.setStyleName(Bootstrap.Typography.H4.styleName());
		listEntriesLabel.setWidth("160px");
		
		totalListEntriesLabel = new Label("", Label.CONTENT_XHTML);
       	totalListEntriesLabel.setWidth("120px");
       	updateNoOfEntries(count);
       	
       	totalSelectedListEntriesLabel = new Label("", Label.CONTENT_XHTML);
		totalSelectedListEntriesLabel.setWidth("95px");
		updateNoOfSelectedEntries(0);
       	
		viewListHeaderWindow = new ViewListHeaderWindow(germplasmList);
		
		viewListHeaderButton = new Button(messageSource.getMessage(Message.VIEW_HEADER));
		viewListHeaderButton.addStyleName(Reindeer.BUTTON_LINK);
		viewListHeaderButton.setDescription(viewListHeaderWindow.getListHeaderComponent().toString());
		
		actionButton = new ActionButton();
		actionButton.setData(ACTIONS_BUTTON_ID);
		
		inventoryViewActionButton = new ActionButton();
		inventoryViewActionButton.setData(ACTIONS_BUTTON_ID);
		
		actionMenu = new ContextMenu();
		actionMenu.setWidth("250px");
		actionMenu.addItem(messageSource.getMessage(Message.ADD_TO_MALE_LIST));
		actionMenu.addItem(messageSource.getMessage(Message.ADD_TO_FEMALE_LIST));
		actionMenu.addItem(messageSource.getMessage(Message.INVENTORY_VIEW));
		actionMenu.addItem(messageSource.getMessage(Message.SELECT_ALL));
		
		inventoryViewActionMenu = new ContextMenu();
		inventoryViewActionMenu.setWidth("295px");
		menuCopyToNewListFromInventory = inventoryViewActionMenu.addItem(messageSource.getMessage(Message.COPY_TO_NEW_LIST));
        menuReserveInventory = inventoryViewActionMenu.addItem(messageSource.getMessage(Message.RESERVE_INVENTORY));
        menuListView = inventoryViewActionMenu.addItem(messageSource.getMessage(Message.RETURN_TO_LIST_VIEW));
        menuInventorySaveChanges = inventoryViewActionMenu.addItem(messageSource.getMessage(Message.SAVE_CHANGES));
        inventoryViewActionMenu.addItem(messageSource.getMessage(Message.SELECT_ALL));
        resetInventoryMenuOptions();
        
		initializeListDataTable();
		initializeListInventoryTable(); //listInventoryTable
		
		viewListHeaderButton = new Button(messageSource.getMessage(Message.VIEW_LIST_HEADERS));
		viewListHeaderButton.setStyleName(BaseTheme.BUTTON_LINK);
		
	    //Inventory Related Variables
        validReservationsToSave = new HashMap<ListEntryLotDetails, Double>();
        
        // ListSelectionComponent is null when tool launched from BMS dashboard
        if (makeCrossesParentsComponent.getMakeCrossesMain() != null && makeCrossesParentsComponent.getMakeCrossesMain() != null){
        	SelectParentsComponent selectParentComponent = makeCrossesParentsComponent.getMakeCrossesMain().getSelectParentsComponent();
        	selectParentComponent.addUpdateListStatusForChanges(this, this.hasChanges);
        }
	}
	
	private void resetInventoryMenuOptions() {
        //disable the save button at first since there are no reservations yet
        menuInventorySaveChanges.setEnabled(false);
        
        //Temporarily disable to Copy to New List in InventoryView
        menuCopyToNewListFromInventory.setEnabled(false);
	}

	protected void initializeListDataTable(){
		setListDataTableWithSelectAll(new TableWithSelectAllLayout(count.intValue(),9,CHECKBOX_COLUMN_ID));
		getListDataTableWithSelectAll().setWidth("100%");
		
		listDataTable = getListDataTableWithSelectAll().getTable();
		listDataTable.setWidth("100%");
		listDataTable.setData(LIST_DATA_TABLE_ID);
		listDataTable.setSelectable(true);
		listDataTable.setMultiSelect(true);
		listDataTable.setColumnCollapsingAllowed(true);
		listDataTable.setColumnReorderingAllowed(true);
		listDataTable.setImmediate(true);
		listDataTable.setDragMode(TableDragMode.MULTIROW);
		
		listDataTable.addContainerProperty(CHECKBOX_COLUMN_ID, CheckBox.class, null);
		listDataTable.addContainerProperty(ColumnLabels.ENTRY_ID.getName(), Integer.class, null);
		listDataTable.addContainerProperty(ColumnLabels.DESIGNATION.getName(), Button.class, null);
		listDataTable.addContainerProperty(ColumnLabels.AVAILABLE_INVENTORY.getName(), Button.class, null);
		listDataTable.addContainerProperty(ColumnLabels.SEED_RESERVATION.getName(), String.class, null);
		listDataTable.addContainerProperty(ColumnLabels.STOCKID.getName(), Label.class, new Label(""));
		listDataTable.addContainerProperty(ColumnLabels.PARENTAGE.getName(), String.class, null);
		listDataTable.addContainerProperty(ColumnLabels.ENTRY_CODE.getName(), String.class, null);
		listDataTable.addContainerProperty(ColumnLabels.GID.getName(), Button.class, null);
		listDataTable.addContainerProperty(ColumnLabels.SEED_SOURCE.getName(), String.class, null);
		
		listDataTable.setColumnHeader(CHECKBOX_COLUMN_ID, messageSource.getMessage(Message.CHECK_ICON));
		listDataTable.setColumnHeader(ColumnLabels.ENTRY_ID.getName(), messageSource.getMessage(Message.HASHTAG));
		listDataTable.setColumnHeader(ColumnLabels.DESIGNATION.getName(), getTermNameFromOntology(ColumnLabels.DESIGNATION));
		listDataTable.setColumnHeader(ColumnLabels.AVAILABLE_INVENTORY.getName(), getTermNameFromOntology(ColumnLabels.AVAILABLE_INVENTORY));
		listDataTable.setColumnHeader(ColumnLabels.SEED_RESERVATION.getName(), getTermNameFromOntology(ColumnLabels.SEED_RESERVATION));
		listDataTable.setColumnHeader(ColumnLabels.STOCKID.getName(), getTermNameFromOntology(ColumnLabels.STOCKID));
		listDataTable.setColumnHeader(ColumnLabels.PARENTAGE.getName(), getTermNameFromOntology(ColumnLabels.PARENTAGE));
		listDataTable.setColumnHeader(ColumnLabels.ENTRY_CODE.getName(), getTermNameFromOntology(ColumnLabels.ENTRY_CODE));
		listDataTable.setColumnHeader(ColumnLabels.GID.getName(), getTermNameFromOntology(ColumnLabels.GID));
		listDataTable.setColumnHeader(ColumnLabels.SEED_SOURCE.getName(), getTermNameFromOntology(ColumnLabels.SEED_SOURCE));
		
		
		
		listDataTable.setColumnWidth(CHECKBOX_COLUMN_ID, 25);
		listDataTable.setColumnWidth(ColumnLabels.ENTRY_ID.getName(), 25);
		listDataTable.setColumnWidth(ColumnLabels.DESIGNATION.getName(), 130);
		listDataTable.setColumnWidth(ColumnLabels.AVAILABLE_INVENTORY.getName(), 70);
		listDataTable.setColumnWidth(ColumnLabels.SEED_RESERVATION.getName(), 70);
		listDataTable.setColumnWidth(ColumnLabels.SEED_RESERVATION.getName(), 130);
		listDataTable.setColumnWidth(ColumnLabels.PARENTAGE.getName(), 130);
		listDataTable.setColumnWidth(ColumnLabels.ENTRY_CODE.getName(), 100);
		listDataTable.setColumnWidth(ColumnLabels.GID.getName(), 60);
		listDataTable.setColumnWidth(ColumnLabels.SEED_SOURCE.getName(), 110);
		
		listDataTable.setVisibleColumns(new String[] { 
        		CHECKBOX_COLUMN_ID
        		,ColumnLabels.ENTRY_ID.getName()
        		,ColumnLabels.DESIGNATION.getName()
        		,ColumnLabels.AVAILABLE_INVENTORY.getName()
        		,ColumnLabels.SEED_RESERVATION.getName()
        		,ColumnLabels.STOCKID.getName()
        		,ColumnLabels.PARENTAGE.getName()
        		,ColumnLabels.ENTRY_CODE.getName()
        		,ColumnLabels.GID.getName()
        		,ColumnLabels.SEED_SOURCE.getName()});
	}
	
	private void initializeListInventoryTable(){
		listInventoryTable = new CrossingManagerInventoryTable(germplasmList.getId());
		listInventoryTable.setVisible(false);
		listInventoryTable.setMaxRows(9);
		listInventoryTable.getTable().setDragMode(TableDragMode.ROW);
		listInventoryTable.getTable().setData(CROSSING_MANAGER_PARENT_TAB_INVENTORY_TABLE);
	}

	private void retrieveListDetails() {
		try {
			germplasmList = germplasmListManager.getGermplasmListById(this.germplasmListId);
			count = germplasmListManager.countGermplasmListDataByListId(this.germplasmListId);
		} catch (MiddlewareQueryException e) {
			LOG.error("Error getting list details" + e.getMessage(), e);
		}
	}

	@Override
	public void initializeValues() {
		try{
			List<GermplasmListData> listEntries = inventoryDataManager.getLotCountsForList(germplasmListId, 0, Integer.MAX_VALUE);
			
			for(GermplasmListData entry : listEntries){
				String gid = String.format("%s", entry.getGid().toString());
                Button gidButton = new Button(gid, new GidLinkClickListener(gid,true));
                gidButton.setStyleName(BaseTheme.BUTTON_LINK);
                gidButton.setDescription("Click to view Germplasm information");
                
                Button desigButton = new Button(entry.getDesignation(), new GidLinkClickListener(gid,true));
                desigButton.setStyleName(BaseTheme.BUTTON_LINK);
                desigButton.setDescription("Click to view Germplasm information");
                
                CheckBox itemCheckBox = new CheckBox();
                itemCheckBox.setData(entry.getId());
                itemCheckBox.setImmediate(true);
    	   		itemCheckBox.addListener(new ClickListener() {
    	 			private static final long serialVersionUID = 1L;
    	 			@Override
    	 			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
    	 				CheckBox itemCheckBox = (CheckBox) event.getButton();
    	 				if(((Boolean) itemCheckBox.getValue()).equals(true)){
    	 					listDataTable.select(itemCheckBox.getData());
    	 				} else {
    	 					listDataTable.unselect(itemCheckBox.getData());
    	 				}
    	 			}
    	 		});
    	   		
    			
    			//#1 Available Inventory
    	   		//default value
    			String availInv = STRING_DASH; 
    			if(entry.getInventoryInfo().getLotCount().intValue() != 0){
    				availInv = entry.getInventoryInfo().getActualInventoryLotCount().toString().trim();
    			}
    			
    			InventoryLinkButtonClickListener inventoryLinkButtonClickListener = new InventoryLinkButtonClickListener(this,germplasmList.getId(),entry.getId(), entry.getGid());
    			Button inventoryButton = new Button(availInv, inventoryLinkButtonClickListener);
    			inventoryButton.setData(inventoryLinkButtonClickListener);
    			inventoryButton.setStyleName(BaseTheme.BUTTON_LINK);
    			inventoryButton.setDescription(CLICK_TO_VIEW_INVENTORY_DETAILS);
    			
    			if(availInv.equals(STRING_DASH)){
    				inventoryButton.setEnabled(false);
    				inventoryButton.setDescription(NO_LOT_FOR_THIS_GERMPLASM);
    			} else {
    				inventoryButton.setDescription(CLICK_TO_VIEW_INVENTORY_DETAILS);
    			}
    			
    			// Seed Reserved
    			//default value
    	   		String seedRes = STRING_DASH; 
    	   		if(entry.getInventoryInfo().getReservedLotCount().intValue() != 0){
    	   			seedRes = entry.getInventoryInfo().getReservedLotCount().toString().trim();
    	   		}
    	   		
    	   		
    	   		Item newItem = listDataTable.getContainerDataSource().addItem(entry.getId());    			
    	   		newItem.getItemProperty(CHECKBOX_COLUMN_ID).setValue(itemCheckBox);
    	   		newItem.getItemProperty(ColumnLabels.ENTRY_ID.getName()).setValue(entry.getEntryId());
    	   		newItem.getItemProperty(ColumnLabels.DESIGNATION.getName()).setValue(desigButton);
    	   		newItem.getItemProperty(ColumnLabels.AVAILABLE_INVENTORY.getName()).setValue(inventoryButton);
    	   		newItem.getItemProperty(ColumnLabels.SEED_RESERVATION.getName()).setValue(seedRes);
    	   		newItem.getItemProperty(ColumnLabels.PARENTAGE.getName()).setValue(entry.getGroupName());
    	   		newItem.getItemProperty(ColumnLabels.ENTRY_CODE.getName()).setValue(entry.getEntryCode());
    	   		newItem.getItemProperty(ColumnLabels.GID.getName()).setValue(gidButton);
    	   		newItem.getItemProperty(ColumnLabels.SEED_SOURCE.getName()).setValue(entry.getSeedSource());
    	   		
    	   		if (entry.getInventoryInfo().getStockIDs() != null){
    	   			Label stockIdsLabel = new Label(entry.getInventoryInfo().getStockIDs());
        	   		stockIdsLabel.setDescription(entry.getInventoryInfo().getStockIDs());
        	   		newItem.getItemProperty(ColumnLabels.STOCKID.getName()).setValue(stockIdsLabel);
    	   		}
    	   		
			}
		} catch(MiddlewareQueryException ex){
			LOG.error("Error with getting list entries for list: " + germplasmListId, ex);
			MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.ERROR_DATABASE), "Error in getting list entries.");
		}
	}

	@Override
	public void addListeners() {
		
		actionButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				actionMenu.show(event.getClientX(), event.getClientY());
			}
		});
		
		actionMenu.addListener(new ActionMenuClickListener());
		
		inventoryViewActionButton.addListener(new ClickListener() {
	   		 private static final long serialVersionUID = 272707576878821700L;
	
				 @Override
	   		 public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
					 inventoryViewActionMenu.show(event.getClientX(), event.getClientY());
	   		 }
	   	 });
		
		inventoryViewActionMenu.addListener(new ContextMenu.ClickListener() {
			private static final long serialVersionUID = -2343109406180457070L;
	
			@Override
			public void contextItemClick(ClickEvent event) {
			      // Get reference to clicked item
			      ContextMenuItem clickedItem = event.getClickedItem();
			      if(clickedItem.getName().equals(messageSource.getMessage(Message.SAVE_CHANGES))){	  
			    	  saveReservationChangesAction();
                  } else if(clickedItem.getName().equals(messageSource.getMessage(Message.RETURN_TO_LIST_VIEW))){
                	  viewListAction();
                  } else if(clickedItem.getName().equals(messageSource.getMessage(Message.COPY_TO_NEW_LIST))){
                	  // no implementation yet for this method
				  } else if(clickedItem.getName().equals(messageSource.getMessage(Message.RESERVE_INVENTORY))){
		          	  reserveInventoryAction();
                  } else if(clickedItem.getName().equals(messageSource.getMessage(Message.SELECT_ALL))){
                	  listInventoryTable.getTable().setValue(listInventoryTable.getTable().getItemIds());
		          }
		    }
		});
		
		viewListHeaderButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 329434322390122057L;

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				openViewListHeaderWindow();
			}
		});
		
		listDataTable.addActionHandler(new ListDataTableActionHandler());
		
        tableWithSelectAllLayout.getTable().addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				updateNoOfSelectedEntries();
			}
		});
        
        listInventoryTable.getTable().addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				updateNoOfSelectedEntries();
			}
		});
	}

	@Override
	public void layoutComponents() {
		setMargin(true);
		setSpacing(true);
		
		addComponent(actionMenu);
		addComponent(inventoryViewActionMenu);
		
		headerLayout = new HorizontalLayout();
		headerLayout.setWidth("100%");
		HeaderLabelLayout headingLayout = new HeaderLabelLayout(AppConstants.Icons.ICON_LIST_TYPES, listEntriesLabel);
		headerLayout.addComponent(headingLayout);
		headerLayout.addComponent(viewListHeaderButton);
		headerLayout.setComponentAlignment(headingLayout, Alignment.MIDDLE_LEFT);
		headerLayout.setComponentAlignment(viewListHeaderButton, Alignment.MIDDLE_RIGHT);
		
		HorizontalLayout leftSubHeaderLayout = new HorizontalLayout();
		leftSubHeaderLayout.setSpacing(true);
		leftSubHeaderLayout.addComponent(totalListEntriesLabel);
		leftSubHeaderLayout.addComponent(totalSelectedListEntriesLabel);
		leftSubHeaderLayout.setComponentAlignment(totalListEntriesLabel, Alignment.MIDDLE_LEFT);
		leftSubHeaderLayout.setComponentAlignment(totalSelectedListEntriesLabel, Alignment.MIDDLE_LEFT);
		
		subHeaderLayout = new HorizontalLayout();
		subHeaderLayout.setWidth("100%");
		subHeaderLayout.addComponent(leftSubHeaderLayout);
		subHeaderLayout.addComponent(actionButton);
		subHeaderLayout.setComponentAlignment(leftSubHeaderLayout, Alignment.MIDDLE_LEFT);
		subHeaderLayout.setComponentAlignment(actionButton, Alignment.MIDDLE_RIGHT);
		
		addComponent(headerLayout);
		addComponent(subHeaderLayout);
		addComponent(tableWithSelectAllLayout);
		addComponent(listInventoryTable);

	}
	
	private void updateNoOfEntries(long count){
		if(makeCrossesParentsComponent.getMakeCrossesMain().getModeView().equals(ModeView.LIST_VIEW)){
			if(count == 0) {
				totalListEntriesLabel.setValue(messageSource.getMessage(Message.NO_LISTDATA_RETRIEVED_LABEL));
			} else {
				totalListEntriesLabel.setValue(messageSource.getMessage(Message.TOTAL_LIST_ENTRIES) + ": " 
		        		 + "  <b>" + count + "</b>");
	        }
		//Inventory View
		} else {
			totalListEntriesLabel.setValue(messageSource.getMessage(Message.TOTAL_LOTS) + ": " 
	        		 + "  <b>" + count + "</b>");
		}
	}
	
	private void updateNoOfEntries(){
		int entryCount = 0;
		if(makeCrossesParentsComponent.getMakeCrossesMain().getModeView().equals(ModeView.LIST_VIEW)){
			entryCount = listDataTable.getItemIds().size();
		
		//Inventory View
		} else { 
			entryCount = listInventoryTable.getTable().size();
		}
		updateNoOfEntries(entryCount);
	}
	
	private void updateNoOfSelectedEntries(int count){
		totalSelectedListEntriesLabel.setValue("<i>" + messageSource.getMessage(Message.SELECTED) + ": " 
	        		 + "  <b>" + count + "</b></i>");
	}
	
	private void updateNoOfSelectedEntries(){
		int entryCount = 0;
		
		if(makeCrossesParentsComponent.getMakeCrossesMain().getModeView().equals(ModeView.LIST_VIEW)){
			Collection<?> selectedItems = (Collection<?>)tableWithSelectAllLayout.getTable().getValue();
			entryCount = selectedItems.size();
		} else {
			Collection<?> selectedItems = (Collection<?>)listInventoryTable.getTable().getValue();
			entryCount = selectedItems.size();
		}
		
		updateNoOfSelectedEntries(entryCount);
	}
	
	/*--------------------------------------INVENTORY RELATED FUNCTIONS---------------------------------------*/
	
	private void viewListAction(){
		
		if(!hasUnsavedChanges()){
			makeCrossesParentsComponent.getMakeCrossesMain().setModeView(ModeView.LIST_VIEW);
		}else{
			String message = "You have unsaved reservations for this list. " +
					"You will need to save them before changing views. " +
					"Do you want to save your changes?";
			
			makeCrossesParentsComponent.getMakeCrossesMain().showUnsavedChangesConfirmDialog(message, ModeView.LIST_VIEW);
		}
	}	
	
	public void changeToListView(){
		if(listInventoryTable.isVisible()){
			tableWithSelectAllLayout.setVisible(true);
			listInventoryTable.setVisible(false);
			
			subHeaderLayout.removeComponent(inventoryViewActionButton);
			subHeaderLayout.addComponent(actionButton);
			subHeaderLayout.setComponentAlignment(actionButton, Alignment.MIDDLE_RIGHT);
			
			listEntriesLabel.setValue(messageSource.getMessage(Message.LIST_ENTRIES_LABEL));
	        updateNoOfEntries();
	        updateNoOfSelectedEntries();
	        
	        this.removeComponent(listInventoryTable);
	        this.addComponent(tableWithSelectAllLayout);
	        
	        this.requestRepaint();
		}
	}
	
	private void viewInventoryAction(){
		if(!hasUnsavedChanges()){
			makeCrossesParentsComponent.getMakeCrossesMain().setModeView(ModeView.INVENTORY_VIEW);
		} else {
			String message = "You have unsaved changes to the list you are currently editing.. " +
					"You will need to save them before changing views. " +
					"Do you want to save your changes?";
			makeCrossesParentsComponent.getMakeCrossesMain().showUnsavedChangesConfirmDialog(message, ModeView.INVENTORY_VIEW);
		}
	}
	
	public void viewInventoryActionConfirmed(){
		resetListInventoryTableValues();
		changeToInventoryView();
	}
	
	public void changeToInventoryView(){
		if(tableWithSelectAllLayout.isVisible()){
			tableWithSelectAllLayout.setVisible(false);
			listInventoryTable.setVisible(true);
			
			subHeaderLayout.removeComponent(actionButton);
	        subHeaderLayout.addComponent(inventoryViewActionButton);
	        subHeaderLayout.setComponentAlignment(inventoryViewActionButton, Alignment.MIDDLE_RIGHT);
	        
	        listEntriesLabel.setValue(messageSource.getMessage(Message.LOTS));
	        updateNoOfEntries();
	        updateNoOfSelectedEntries();
	        
	        this.removeComponent(tableWithSelectAllLayout);
	        this.addComponent(listInventoryTable);
	        
	        this.requestRepaint();
		}
	}
	
	public void reserveInventoryAction() {
		//checks if the screen is in the inventory view
		if(!inventoryViewActionMenu.isVisible()){
			MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.WARNING), 
					"Please change to Inventory View first.");
		} else {
			List<ListEntryLotDetails> lotDetailsGid = listInventoryTable.getSelectedLots();
			
			if( lotDetailsGid == null || lotDetailsGid.isEmpty()){
				MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.WARNING), 
						"Please select at least 1 lot to reserve.");
			} else {
		        //this util handles the inventory reservation related functions
		        reserveInventoryUtil = new ReserveInventoryUtil(this,lotDetailsGid);
				reserveInventoryUtil.viewReserveInventoryWindow();
			}
		}
	}

	@Override
	public void updateListInventoryTable(
			Map<ListEntryLotDetails, Double> validReservations,
			boolean withInvalidReservations) {
		for(Map.Entry<ListEntryLotDetails, Double> entry: validReservations.entrySet()){
			ListEntryLotDetails lot = entry.getKey();
			Double newRes = entry.getValue();
			
			Item itemToUpdate = listInventoryTable.getTable().getItem(lot);
			itemToUpdate.getItemProperty(ColumnLabels.NEWLY_RESERVED.getName()).setValue(newRes);
		}
		
		removeReserveInventoryWindow(reserveInventory);
		
		//update lot reservatios to save
		updateLotReservationsToSave(validReservations);
		
		//enable now the Save Changes option
		menuInventorySaveChanges.setEnabled(true);
		
		//if there are no valid reservations
		if(validReservations.isEmpty()){
			MessageNotifier.showRequiredFieldError(getWindow(), messageSource.getMessage(Message.COULD_NOT_MAKE_ANY_RESERVATION_ALL_SELECTED_LOTS_HAS_INSUFFICIENT_BALANCES) + ".");
		} else if(!withInvalidReservations){
			MessageNotifier.showMessage(getWindow(), messageSource.getMessage(Message.SUCCESS), 
					"All selected entries will be reserved in their respective lots.", 
					3000);
		}		
	}

	private void updateLotReservationsToSave(
			Map<ListEntryLotDetails, Double> validReservations) {
		for(Map.Entry<ListEntryLotDetails, Double> entry : validReservations.entrySet()){
			ListEntryLotDetails lot = entry.getKey();
			Double amountToReserve = entry.getValue();
			
			if(validReservationsToSave.containsKey(lot)){
				validReservationsToSave.remove(lot);
				
			}
			
			validReservationsToSave.put(lot,amountToReserve);
		}
		
		if(!validReservationsToSave.isEmpty()){
			setHasUnsavedChanges(true);
		}
	}

	@Override
	public void addReserveInventoryWindow(
			ReserveInventoryWindow reserveInventory) {
		this.reserveInventory = reserveInventory;
		makeCrossesParentsComponent.getWindow().addWindow(this.reserveInventory);
	}

	@Override
	public void addReservationStatusWindow(
			ReservationStatusWindow reservationStatus) {
		this.reservationStatus = reservationStatus;
		removeReserveInventoryWindow(reserveInventory);
		makeCrossesParentsComponent.getWindow().addWindow(this.reservationStatus);
	}

	@Override
	public void removeReserveInventoryWindow(
			ReserveInventoryWindow reserveInventory) {
		this.reserveInventory = reserveInventory;
		makeCrossesParentsComponent.getWindow().removeWindow(this.reserveInventory);
	}

	@Override
	public void removeReservationStatusWindow(
			ReservationStatusWindow reservationStatus) {
		this.reservationStatus = reservationStatus;
		makeCrossesParentsComponent.getWindow().removeWindow(this.reservationStatus);
	}
	
	public void saveReservationChangesAction() {
		if(hasUnsavedChanges()){
			reserveInventoryAction = new ReserveInventoryAction(this);
			boolean success = reserveInventoryAction.saveReserveTransactions(getValidReservationsToSave(), germplasmList.getId());
			if(success){
				refreshInventoryColumns(getValidReservationsToSave());
				resetListInventoryTableValues();
				MessageNotifier.showMessage(getWindow(), messageSource.getMessage(Message.SUCCESS), 
						"All reservations were saved.");
			}
		}
	}
	
	public void refreshInventoryColumns(
			Map<ListEntryLotDetails, Double> validReservationsToSave2) {
		
		Set<Integer> entryIds = new HashSet<Integer>();
		for(Entry<ListEntryLotDetails, Double> details : validReservationsToSave.entrySet()){
			entryIds.add(details.getKey().getId());
		 }
		
		List<GermplasmListData> germplasmListDataEntries = new ArrayList<GermplasmListData>();
		
		try {
			if (!entryIds.isEmpty()) {
                germplasmListDataEntries = this.inventoryDataManager.getLotCountsForListEntries(germplasmList.getId(), new ArrayList<Integer>(entryIds));
            }
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}
		
		for (GermplasmListData listData : germplasmListDataEntries){
			Item item = listDataTable.getItem(listData.getId());
			
			//#1 Available Inventory
			//default value
			String availInv = STRING_DASH; 
			if(listData.getInventoryInfo().getLotCount().intValue() != 0){
				availInv = listData.getInventoryInfo().getActualInventoryLotCount().toString().trim();
			}
			Button inventoryButton = new Button(availInv, new InventoryLinkButtonClickListener(makeCrossesParentsComponent, germplasmList.getId(),listData.getId(), listData.getGid()));
			inventoryButton.setStyleName(BaseTheme.BUTTON_LINK);
			inventoryButton.setDescription(CLICK_TO_VIEW_INVENTORY_DETAILS);
			
			if(availInv.equals(STRING_DASH)){
				inventoryButton.setEnabled(false);
				inventoryButton.setDescription(NO_LOT_FOR_THIS_GERMPLASM);
			} else {
				inventoryButton.setDescription(CLICK_TO_VIEW_INVENTORY_DETAILS);
			}
			item.getItemProperty(ColumnLabels.AVAILABLE_INVENTORY.getName()).setValue(inventoryButton);
			
		
			// Seed Reserved
			//default value
	   		String seedRes = STRING_DASH; 
	   		if(listData.getInventoryInfo().getReservedLotCount().intValue() != 0){
	   			seedRes = listData.getInventoryInfo().getReservedLotCount().toString().trim();
	   		}
			
	   		item.getItemProperty(ColumnLabels.SEED_RESERVATION.getName()).setValue(seedRes);
		}		
	}
	
    public void resetListInventoryTableValues() {
    	if(germplasmList != null){
    		listInventoryTable.updateListInventoryTableAfterSave();
    	} else {
    		listInventoryTable.reset();
    	}
		
		resetInventoryMenuOptions();
		
		//reset the reservations to save. 
		validReservationsToSave.clear();
		
		setHasUnsavedChanges(false);
	}
	
	/*--------------------------------END OF INVENTORY RELATED FUNCTIONS--------------------------------------*/
	
	public Map<ListEntryLotDetails, Double> getValidReservationsToSave(){
		return validReservationsToSave;
	}

	public boolean hasUnsavedChanges() {		
		return hasChanges;
	}

	private void openViewListHeaderWindow(){
		this.getWindow().addWindow(viewListHeaderWindow);
	}
	
	public Table getListDataTable(){
		return this.listDataTable;
	}

	public String getListName() {
		return this.listName;
	}
	
	public GermplasmList getGermplasmList(){
		return germplasmList;
	}
	
	public void setHasUnsavedChanges(Boolean hasChanges) {
		this.hasChanges = hasChanges;
		
		if(hasChanges){
			menuInventorySaveChanges.setEnabled(true);
		} else {
			menuInventorySaveChanges.setEnabled(false);
		}
		
		SelectParentsComponent selectParentComponent = makeCrossesParentsComponent.getMakeCrossesMain().getSelectParentsComponent();
		selectParentComponent.addUpdateListStatusForChanges(this, this.hasChanges);
	}
	
	public Integer getGermplasmListId(){
		return germplasmListId;
	}
	
	protected String getTermNameFromOntology(ColumnLabels columnLabels) {
		return columnLabels.getTermNameFromOntology(ontologyDataManager);
	}

	protected void setListDataTableWithSelectAll(TableWithSelectAllLayout tableWithSelectAllLayout) {
		this.tableWithSelectAllLayout = tableWithSelectAllLayout;
	}
	
	protected TableWithSelectAllLayout getListDataTableWithSelectAll() {
		return tableWithSelectAllLayout;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public void setOntologyDataManager(OntologyDataManager ontologyDataManager) {
		this.ontologyDataManager = ontologyDataManager;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
