package org.generationcp.breeding.manager.crossingmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.breeding.manager.action.SaveGermplasmListAction;
import org.generationcp.breeding.manager.action.SaveGermplasmListActionSource;
import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.AppConstants;
import org.generationcp.breeding.manager.crossingmanager.listeners.CrossingManagerActionHandler;
import org.generationcp.breeding.manager.crossingmanager.listeners.CrossingManagerImportButtonClickListener;
import org.generationcp.breeding.manager.crossingmanager.listeners.ParentsTableCheckboxListener;
import org.generationcp.breeding.manager.crossingmanager.pojos.GermplasmListEntry;
import org.generationcp.breeding.manager.customcomponent.SaveListAsDialog;
import org.generationcp.breeding.manager.customcomponent.SaveListAsDialogSource;
import org.generationcp.breeding.manager.customcomponent.TableWithSelectAllLayout;
import org.generationcp.breeding.manager.listmanager.constants.ListDataTablePropertyID;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.Table.TableTransferable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class MakeCrossesParentsComponent extends AbsoluteLayout implements BreedingManagerLayout,
		InitializingBean, InternationalizableComponent, SaveListAsDialogSource, SaveGermplasmListActionSource {
	
	public static final String MAKE_CROSS_BUTTON_ID = "Make Cross Button";

	private static final Logger LOG = LoggerFactory.getLogger(MakeCrossesParentsComponent.class);
	private static final long serialVersionUID = -4789763601080845176L;
	
	private static final int PARENTS_TABLE_ROW_COUNT = 10;
	private static final String MALE_PARENTS_LABEL = "Male Parents";
	private static final String FEMALE_PARENTS_LABEL = "Female Parents";
    
    private static final String TAG_COLUMN_ID = "Tag";
    private static final String ENTRY_NUMBER_COLUMN_ID = "Entry Number Column ID";
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private Label lblFemaleParent;
    private Label lblMaleParent;
    private Label crossingMethodLabel;
    private ComboBox crossingMethodComboBox;
    private CheckBox chkBoxMakeReciprocalCrosses;
    private Button btnMakeCross;
    private Table femaleParents;
    private CheckBox femaleParentsTagAll;
    private Table maleParents;
    private CheckBox maleParentsTagAll;
    private GridLayout gridLayoutSelectingParents;
    private GridLayout gridLayoutSelectingParentOptions;
    private VerticalLayout layoutCrossOption;
    
    private TableWithSelectAllLayout femaleTableWithSelectAll;
    private TableWithSelectAllLayout maleTableWIthSelectAll;
    
    private Button saveFemaleListButton;
    private Button saveMaleListButton;
    private GermplasmList femaleParentList;
    private GermplasmList maleParentList;
    
    private ParentContainer femaleParentContainer;
    private ParentContainer maleParentContainer;
    
    private String femaleListNameForCrosses;
    private String maleListNameForCrosses;
    
    private SaveListAsDialog saveListAsWindow;
    
    private CrossingManagerMakeCrossesComponent makeCrossesMain;
    
    public MakeCrossesParentsComponent(CrossingManagerMakeCrossesComponent parentComponent){
    	this.makeCrossesMain = parentComponent;
	}
    

    @Override
    public void attach() {
    	super.attach();
    	updateLabels();
    }
	@Override
	public void updateLabels() {
        messageSource.setCaption(lblFemaleParent, Message.LABEL_FEMALE_PARENTS);
        messageSource.setCaption(lblMaleParent, Message.LABEL_MALE_PARENTS);
        messageSource.setCaption(chkBoxMakeReciprocalCrosses, Message.MAKE_CROSSES_CHECKBOX_LABEL);
        messageSource.setCaption(btnMakeCross, Message.MAKE_CROSSES_BUTTON_LABEL);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		instantiateComponents();
		initializeValues();
		addListeners();
		layoutComponents();
	}

	@Override
	public void instantiateComponents() {
        this.setMargin(true, true, true, true);
        
        lblFemaleParent= new Label(); 
        
        initializeFemaleParentsTable();
        
        crossingMethodLabel = new Label(messageSource.getMessage(Message.CROSSING_METHOD));
        crossingMethodLabel.addStyleName(AppConstants.CssStyles.BOLD);
        
        crossingMethodComboBox = new ComboBox();
        crossingMethodComboBox.setNewItemsAllowed(false);
        crossingMethodComboBox.setNullSelectionAllowed(false);
        crossingMethodComboBox.setWidth("500px");
        
        chkBoxMakeReciprocalCrosses = new CheckBox();
    
        btnMakeCross= new Button();
        btnMakeCross.setData(MAKE_CROSS_BUTTON_ID);
        btnMakeCross.addStyleName(Bootstrap.Buttons.INFO.styleName());

        lblMaleParent=new Label();
        
        initializeMaleParentsTable();
        
        saveFemaleListButton = new Button(messageSource.getMessage(Message.SAVE_LABEL));
        saveFemaleListButton.addStyleName(Bootstrap.Buttons.INFO.styleName());
        saveFemaleListButton.setEnabled(false);
        
        
        saveMaleListButton = new Button(messageSource.getMessage(Message.SAVE_LABEL));
        saveMaleListButton.addStyleName(Bootstrap.Buttons.INFO.styleName());
        saveMaleListButton.setEnabled(false);

        maleParentContainer = new ParentContainer(saveMaleListButton, maleTableWIthSelectAll, 
        		MALE_PARENTS_LABEL, Message.SUCCESS_SAVE_FOR_MALE_LIST);
        femaleParentContainer = new ParentContainer(saveFemaleListButton, femaleTableWithSelectAll, 
        		FEMALE_PARENTS_LABEL, Message.SUCCESS_SAVE_FOR_FEMALE_LIST);
	}

	
	private void initializeMaleParentsTable() {
		maleTableWIthSelectAll = new TableWithSelectAllLayout(PARENTS_TABLE_ROW_COUNT, TAG_COLUMN_ID);
        maleParents = maleTableWIthSelectAll.getTable();
        maleParentsTagAll = maleTableWIthSelectAll.getCheckBox();
        
        maleParents.setWidth(240, UNITS_PIXELS);
        maleParents.setNullSelectionAllowed(true);
        maleParents.setSelectable(true);
        maleParents.setMultiSelect(true);
        maleParents.setImmediate(true);
        maleParents.addContainerProperty(TAG_COLUMN_ID, CheckBox.class, null);
        maleParents.addContainerProperty(ENTRY_NUMBER_COLUMN_ID, Integer.class, Integer.valueOf(0));
        maleParents.setColumnHeader(ENTRY_NUMBER_COLUMN_ID, "#");
        maleParents.addContainerProperty(MALE_PARENTS_LABEL, String.class, null);
        maleParents.setColumnWidth(TAG_COLUMN_ID, 25);
        maleParents.setDragMode(TableDragMode.ROW);
        
        maleParents.setItemDescriptionGenerator(new ItemDescriptionGenerator() {                             
			private static final long serialVersionUID = -3207714818504151649L;

			public String generateDescription(Component source, Object itemId, Object propertyId) {
				if(propertyId != null && propertyId == MALE_PARENTS_LABEL) {
			    	Table theTable = (Table) source;
			    	Item item = theTable.getItem(itemId);
			    	String name = (String) item.getItemProperty(MALE_PARENTS_LABEL).getValue();
			    	return name;
			    }                                                                       
			    return null;
			}
		});
	}

	
	private void setupMaleTableDropHandler() {
		maleParents.setDropHandler(new DropHandler() {
            private static final long serialVersionUID = -6464944116431652229L;

				@SuppressWarnings("unchecked")
				public void drop(DragAndDropEvent dropEvent) {
                    TableTransferable transferable = (TableTransferable) dropEvent.getTransferable();
                        
                    Table sourceTable = (Table) transferable.getSourceComponent();
                    Table targetTable = (Table) dropEvent.getTargetDetails().getTarget();
                    
                    AbstractSelectTargetDetails dropData = ((AbstractSelectTargetDetails) dropEvent.getTargetDetails());
                    Object targetItemId = dropData.getItemIdOver();

                    if(sourceTable.equals(maleParents)){
                    	Collection<GermplasmListEntry> selectedEntries = (Collection<GermplasmListEntry>) sourceTable.getValue();
                        
	                    //Check first if item is dropped on top of itself
	                    if(!transferable.getItemId().equals(targetItemId)){
	                        String maleParentValue = (String) sourceTable.getItem(transferable.getItemId()).getItemProperty(MALE_PARENTS_LABEL).getValue();
	                        GermplasmListEntry maleItemId = (GermplasmListEntry) transferable.getItemId();
	                        CheckBox tag = (CheckBox) sourceTable.getItem(maleItemId).getItemProperty(TAG_COLUMN_ID).getValue();
	                        	
	                        sourceTable.removeItem(transferable.getItemId());
	                        
							Item item = targetTable.addItemAfter(targetItemId, maleItemId);
	                      	item.getItemProperty(MALE_PARENTS_LABEL).setValue(maleParentValue);
	                      	item.getItemProperty(TAG_COLUMN_ID).setValue(tag);
	                      	
	                      	if(selectedEntries.contains(maleItemId)){
								tag.setValue(true);
								tag.addListener(new ParentsTableCheckboxListener(targetTable, maleItemId, maleParentsTagAll));
					            tag.setImmediate(true);
					            targetTable.select(transferable.getItemId());
							}
	                	}
                    } else if(sourceTable.getData().equals(SelectParentsListDataComponent.LIST_DATA_TABLE_ID)){
                    	dropToFemaleOrMaleTable(sourceTable, maleParents);
                    }
                    
                    assignEntryNumber(maleParents);
                }

                public AcceptCriterion getAcceptCriterion() {
                	return AcceptAll.get();
                }
        });
	}

	private void initializeFemaleParentsTable() {
		femaleTableWithSelectAll = new TableWithSelectAllLayout(PARENTS_TABLE_ROW_COUNT, TAG_COLUMN_ID);
        femaleParents = femaleTableWithSelectAll.getTable();
        femaleParentsTagAll = femaleTableWithSelectAll.getCheckBox();
        
//        femaleParents.setHeight(290, UNITS_PIXELS);
        femaleParents.setWidth(240, UNITS_PIXELS);
        femaleParents.setNullSelectionAllowed(true);
        femaleParents.setSelectable(true);
        femaleParents.setMultiSelect(true);
        femaleParents.setImmediate(true);
        femaleParents.addContainerProperty(TAG_COLUMN_ID, CheckBox.class, null);
        femaleParents.addContainerProperty(ENTRY_NUMBER_COLUMN_ID, Integer.class, Integer.valueOf(0));
        femaleParents.setColumnHeader(ENTRY_NUMBER_COLUMN_ID, "#");
        femaleParents.addContainerProperty(FEMALE_PARENTS_LABEL, String.class, null);
        femaleParents.setColumnWidth(TAG_COLUMN_ID, 25);
        femaleParents.setDragMode(TableDragMode.ROW);
        femaleParents.setItemDescriptionGenerator(new ItemDescriptionGenerator() {                             
			private static final long serialVersionUID = -3207714818504151649L;

			public String generateDescription(Component source, Object itemId, Object propertyId) {
				if(propertyId != null && propertyId == FEMALE_PARENTS_LABEL) {
			    	Table theTable = (Table) source;
			    	Item item = theTable.getItem(itemId);
			    	String name = (String) item.getItemProperty(FEMALE_PARENTS_LABEL).getValue();
			    	return name;
			    }                                                                       
			    return null;
			}
		});
	}

	private void setupFemaleDropHandler() {
		femaleParents.setDropHandler(new DropHandler() {
            private static final long serialVersionUID = -3048433522366977000L;

				@SuppressWarnings("unchecked")
				public void drop(DragAndDropEvent dropEvent) {
					TableTransferable transferable = (TableTransferable) dropEvent.getTransferable();
                       
                    Table sourceTable = (Table) transferable.getSourceComponent();
                    Table targetTable = (Table) dropEvent.getTargetDetails().getTarget();
                        
                    AbstractSelectTargetDetails dropData = ((AbstractSelectTargetDetails) dropEvent.getTargetDetails());
                    Object targetItemId = dropData.getItemIdOver();
                    
                    if(sourceTable.equals(femaleParents)){
	                    Collection<GermplasmListEntry> selectedEntries = (Collection<GermplasmListEntry>) sourceTable.getValue();
	
	                    //Check first if item is dropped on top of itself
	                    if(!transferable.getItemId().equals(targetItemId)){
	                		String femaleParentValue = (String) sourceTable.getItem(transferable.getItemId()).getItemProperty(FEMALE_PARENTS_LABEL).getValue();
	                		GermplasmListEntry femaleItemId = (GermplasmListEntry) transferable.getItemId();
	                		CheckBox tag = (CheckBox) sourceTable.getItem(femaleItemId).getItemProperty(TAG_COLUMN_ID).getValue();
							
	                		sourceTable.removeItem(transferable.getItemId());
	                		
							Item item = targetTable.addItemAfter(targetItemId, transferable.getItemId());
	                    	item.getItemProperty(FEMALE_PARENTS_LABEL).setValue(femaleParentValue);
	                      	item.getItemProperty(TAG_COLUMN_ID).setValue(tag);
	                      	
	                      	if(selectedEntries.contains(femaleItemId)){
	                      		tag.setValue(true);
								tag.addListener(new ParentsTableCheckboxListener(targetTable, femaleItemId, femaleParentsTagAll));
					            tag.setImmediate(true);
					            targetTable.select(transferable.getItemId());
							} 	
	                      	
	                    }
                    } else if(sourceTable.getData().equals(SelectParentsListDataComponent.LIST_DATA_TABLE_ID)){
                    	dropToFemaleOrMaleTable(sourceTable, femaleParents);
                    }
                    
                    assignEntryNumber(femaleParents);
                }

                public AcceptCriterion getAcceptCriterion() {
                	return AcceptAll.get();
                }
        });
	}

	@Override
	public void initializeValues() {
		crossingMethodComboBox.addItem(CrossType.MULTIPLY);
        crossingMethodComboBox.setItemCaption(CrossType.MULTIPLY, messageSource.getMessage(Message.MAKE_CROSSES_OPTION_GROUP_ITEM_ONE_LABEL));
        crossingMethodComboBox.addItem(CrossType.TOP_TO_BOTTOM);
        crossingMethodComboBox.setItemCaption(CrossType.TOP_TO_BOTTOM, messageSource.getMessage(Message.MAKE_CROSSES_OPTION_GROUP_ITEM_TWO_LABEL));
        crossingMethodComboBox.select(CrossType.MULTIPLY);
	}

	@Override
	public void addListeners() {
		setupFemaleDropHandler();
        femaleParents.addActionHandler(new CrossingManagerActionHandler(this));
        
        setupMaleTableDropHandler();
        maleParents.addActionHandler(new CrossingManagerActionHandler(this));
        
        btnMakeCross.addListener(new CrossingManagerImportButtonClickListener(this));
        
        saveFemaleListButton.addListener(new ClickListener(){
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				saveFemaleParentList();
			}
        	
        });
        
        saveMaleListButton.addListener(new ClickListener(){
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				saveMaleParentList();
			}
        	
        });
	}

	@Override
	public void layoutComponents() {
        //Widget Layout
        gridLayoutSelectingParents = new GridLayout(2,1);
        gridLayoutSelectingParents.setSpacing(true);
        
        AbsoluteLayout femaleParentsTableLayout = new AbsoluteLayout();
        femaleParentsTableLayout.setWidth("260px");
        femaleParentsTableLayout.setHeight("350px");
        femaleParentsTableLayout.addComponent(saveFemaleListButton,"top:0px;right:0px");
        femaleParentsTableLayout.addComponent(femaleTableWithSelectAll, "top:30px;left:20px");
        gridLayoutSelectingParents.addComponent(femaleParentsTableLayout,0,0);
        gridLayoutSelectingParents.setComponentAlignment(femaleParents,  Alignment.MIDDLE_CENTER);
        
        AbsoluteLayout maleParentsTableLayout = new AbsoluteLayout();
        maleParentsTableLayout.setWidth("260px");
        maleParentsTableLayout.setHeight("350px");
        maleParentsTableLayout.addComponent(saveMaleListButton, "top:0px;right:0px");
        maleParentsTableLayout.addComponent(maleTableWIthSelectAll, "top:30px;left:20px");
        gridLayoutSelectingParents.addComponent(maleParentsTableLayout,1,0);
        gridLayoutSelectingParents.setComponentAlignment(maleParents,  Alignment.MIDDLE_CENTER);
        
        gridLayoutSelectingParents.setWidth(550, UNITS_PIXELS);
        
        gridLayoutSelectingParentOptions = new GridLayout(1,1);
        gridLayoutSelectingParentOptions.setSpacing(true);
        gridLayoutSelectingParentOptions.setMargin(true, false, false, true);
        
        layoutCrossOption = new VerticalLayout();
        layoutCrossOption.setSpacing(true);
        layoutCrossOption.addComponent(crossingMethodLabel);
        layoutCrossOption.addComponent(crossingMethodComboBox);
        layoutCrossOption.addComponent(chkBoxMakeReciprocalCrosses);
        layoutCrossOption.addComponent(btnMakeCross);
        layoutCrossOption.setComponentAlignment(btnMakeCross,Alignment.MIDDLE_CENTER);
        
        gridLayoutSelectingParentOptions.setWidth(500, UNITS_PIXELS);
        gridLayoutSelectingParentOptions.setMargin(true, false, false, false);
        
        gridLayoutSelectingParentOptions.addComponent(layoutCrossOption,0,0);
        gridLayoutSelectingParentOptions.setComponentAlignment(layoutCrossOption,  Alignment.TOP_LEFT);
        
        addComponent(gridLayoutSelectingParents, "top:0px; left:0px;");
        addComponent(gridLayoutSelectingParentOptions, "top:330px; left:20px;");


	}
	
	@SuppressWarnings("unchecked")
	private void dropToFemaleOrMaleTable(Table sourceTable, Table targetTable){
		List<Integer> selectedListEntries = new ArrayList<Integer>();
    	selectedListEntries.addAll((Collection<Integer>) sourceTable.getValue());
    	List<Integer> entryIdsInSourceTable = new ArrayList<Integer>();
    	entryIdsInSourceTable.addAll((Collection<Integer>) sourceTable.getItemIds());
    	
    	for(Integer itemId : entryIdsInSourceTable){
    		if(selectedListEntries.contains(itemId)){
	    		Integer entryId = (Integer) sourceTable.getItem(itemId).getItemProperty(ListDataTablePropertyID.ENTRY_ID.getName()).getValue();
	    		Button designationButton = (Button) sourceTable.getItem(itemId).getItemProperty(ListDataTablePropertyID.DESIGNATION.getName()).getValue(); 
	    		String designation = designationButton.getCaption();
	    		Button gidButton = (Button) sourceTable.getItem(itemId).getItemProperty(ListDataTablePropertyID.GID.getName()).getValue();
	    		Integer gid = Integer.valueOf(Integer.parseInt(gidButton.getCaption()));
	    		
	    		if(!checkIfGIDisInTable(targetTable, gid)){
		    		String seedSource = getSeedSource(sourceTable,entryId);
		    		
		    		GermplasmListEntry entryObject = new GermplasmListEntry(itemId, gid, entryId, designation, seedSource);
		    		Item item = targetTable.addItem(entryObject);
		    		if(item != null){
			    		if(targetTable.equals(femaleParents)){
			    			item.getItemProperty("Female Parents").setValue(entryObject.getDesignation());
			    			this.saveFemaleListButton.setEnabled(true);
			    		} else{
			    			item.getItemProperty(MALE_PARENTS_LABEL).setValue(entryObject.getDesignation());
			    			this.saveMaleListButton.setEnabled(true);
			    		}
			    		
			    		CheckBox tag = new CheckBox();
			    		if(targetTable.equals(femaleParents)){
			    			tag.addListener(new ParentsTableCheckboxListener(targetTable, entryObject, femaleParentsTagAll));
			    		} else{
			    			tag.addListener(new ParentsTableCheckboxListener(targetTable, entryObject, maleParentsTagAll));
			    		}
			            tag.setImmediate(true);
			            item.getItemProperty(TAG_COLUMN_ID).setValue(tag);
			        }
	    		}
    		}
            
            targetTable.requestRepaint();
        }
	}
	
	private boolean checkIfGIDisInTable(Table targetTable, Integer gid){
		for(Object itemId : targetTable.getItemIds()){
			GermplasmListEntry entry = (GermplasmListEntry) itemId;
			if(gid.equals(entry.getGid())){
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private void assignEntryNumber(Table parentsTable){
		int entryNumber = 1;
		List<GermplasmListEntry> itemIds = new ArrayList<GermplasmListEntry>();
		itemIds.addAll((Collection<GermplasmListEntry>) parentsTable.getItemIds());
		
		for(GermplasmListEntry entry : itemIds){
			Item item = parentsTable.getItem(entry);
    		item.getItemProperty(ENTRY_NUMBER_COLUMN_ID).setValue(Integer.valueOf(entryNumber));
    		entry.setEntryId(entryNumber);
			entryNumber++;
		}
	}
	
	private void saveFemaleParentList() {
    	saveListAsWindow = null;
    	if(femaleParentList != null){
    		saveListAsWindow = new SaveListAsDialog(this,femaleParentList);
    	}
    	else{
    		saveListAsWindow = new SaveListAsDialog(this,null);
    	}
        
        saveListAsWindow.addStyleName(Reindeer.WINDOW_LIGHT);
        saveListAsWindow.setData(femaleParentContainer);
        this.getWindow().addWindow(saveListAsWindow);
    }
    
    private void saveMaleParentList() {
    	saveListAsWindow = null;
    	
    	if(maleParentList != null){
    		saveListAsWindow = new SaveListAsDialog(this,maleParentList);
    	}
    	else{
    		saveListAsWindow = new SaveListAsDialog(this,null);
    	}
        
        saveListAsWindow.addStyleName(Reindeer.WINDOW_LIGHT);
        saveListAsWindow.setData(maleParentContainer);
        this.getWindow().addWindow(saveListAsWindow);
    }

	@SuppressWarnings("unchecked")
	@Override
	public void saveList(GermplasmList list) {
		ParentContainer parentContainer = (ParentContainer)saveListAsWindow.getData();
		List<GermplasmListEntry> listEntries = new ArrayList<GermplasmListEntry>();
		listEntries.addAll((Collection<GermplasmListEntry>) parentContainer.getTableWithSelectAll().getTable().getItemIds());
		
		//TO DO correct the entryID, get from the parent table
		// Create Map <Key: "GID+ENTRYID">, <Value:CheckBox Obj>
		SaveGermplasmListAction saveListAction = new SaveGermplasmListAction(this, list, listEntries);
		try {
			GermplasmList savedList = saveListAction.saveRecords();
			updateCrossesSeedSource(parentContainer, savedList);
			updateUIForSuccessfulSaving(parentContainer, savedList);

		} catch (MiddlewareQueryException e) {
			LOG.error("Error in saving the Parent List",e);
			e.printStackTrace();
		}
	}


	private void updateCrossesSeedSource(ParentContainer parentContainer,
			GermplasmList savedList) {
		if (parentContainer.equals(femaleParentContainer)){
			this.femaleParentList = savedList;
			if (femaleListNameForCrosses != null && !femaleListNameForCrosses.equals(femaleParentList.getName())){
				femaleListNameForCrosses = femaleParentList.getName();
				makeCrossesMain.updateCrossesSeedSource(femaleListNameForCrosses, 
						maleListNameForCrosses);
			}
		} else {
			this.maleParentList = savedList;
			if (maleListNameForCrosses != null && !maleListNameForCrosses.equals(maleParentList.getName())){
				maleListNameForCrosses = maleParentList.getName();
				makeCrossesMain.updateCrossesSeedSource(femaleListNameForCrosses, 
						maleListNameForCrosses);
			}
		}
	}
	
	private void updateUIForSuccessfulSaving(ParentContainer parentContainer, GermplasmList list) {
		parentContainer.getButton().setEnabled(false);
		makeCrossesMain.toggleNextButton();
		
		makeCrossesMain.selectListInTree(list.getId());
		makeCrossesMain.updateUIForDeletedList(list);
		
		MessageNotifier.showMessage(getWindow(), messageSource.getMessage(Message.SUCCESS), 
				messageSource.getMessage(parentContainer.getSuccessMessage()));
	}
	
	public String getSeedSource(Table table, Integer entryId){
		String seedSource = "";
		if(table.getParent().getParent() instanceof SelectParentsListDataComponent ){
			SelectParentsListDataComponent parentComponent = (SelectParentsListDataComponent) table.getParent().getParent();
			String listname = parentComponent.getListName();			
			seedSource = listname + " : " + entryId;
		}
		
		return seedSource;
	}

    
    /**
     * Implemented something similar to table.getValue(), because that method returns
     *     a collection of items, but does not follow the sorting done by the 
     *     drag n drop sorting, this one does
     * @param table
     * @return List of selected germplasm list entries
     */
    @SuppressWarnings("unchecked")
	private List<GermplasmListEntry> getCorrectSortedValue(Table table){
    	List<GermplasmListEntry> allItemIds = new ArrayList<GermplasmListEntry>();
    	List<GermplasmListEntry> selectedItemIds = new ArrayList<GermplasmListEntry>();
    	List<GermplasmListEntry> sortedSelectedValues = new ArrayList<GermplasmListEntry>();
    	
    	allItemIds.addAll((Collection<GermplasmListEntry>) table.getItemIds());
    	selectedItemIds.addAll((Collection<GermplasmListEntry>) table.getValue());

    	for(GermplasmListEntry entry : allItemIds){
			CheckBox tag = (CheckBox) table.getItem(entry).getItemProperty(TAG_COLUMN_ID).getValue();
			Boolean tagValue = (Boolean) tag.getValue();
			if(tagValue.booleanValue()){
				selectedItemIds.add(entry);
			} 
		}
    	
    	for(GermplasmListEntry itemId : allItemIds){
    		for(GermplasmListEntry selectedItemId : selectedItemIds){
    			if(itemId.equals(selectedItemId))
    				sortedSelectedValues.add(selectedItemId);    			
    		}
    	}
    	return sortedSelectedValues;
    }
    
    public Table getFemaleTable(){
    	return femaleParents;
    }
    
    public Table getMaleTable(){
    	return maleParents;
    }
    
    public GermplasmList getFemaleList(){
    	return femaleParentList;
    }
    
    public GermplasmList getMaleList(){
    	return maleParentList;
    }
    
    public void makeCrossButtonAction(){
    	List<GermplasmListEntry> femaleList = getCorrectSortedValue(femaleParents);
    	List<GermplasmListEntry> maleList = getCorrectSortedValue(maleParents);
      
    	CrossType type = (CrossType) crossingMethodComboBox.getValue();
    	
    	femaleListNameForCrosses = getFemaleList() != null ? getFemaleList().getName() : "";
    	maleListNameForCrosses = getMaleList() != null ? getMaleList().getName() : "";
    	
    	makeCrossesMain.makeCrossButtonAction(femaleList, maleList, 
    			femaleListNameForCrosses, maleListNameForCrosses, type, chkBoxMakeReciprocalCrosses.booleanValue());
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public void updateListDataTable(List<GermplasmListData> savedListEntries) {
		ParentContainer container = (ParentContainer) saveListAsWindow.getData();

			
		List<GermplasmListEntry> selectedItemIds = new ArrayList<GermplasmListEntry>();
		Table table = container.getTableWithSelectAll().getTable();
		
		selectedItemIds.addAll((Collection<GermplasmListEntry>) table.getValue());
		table.removeAllItems();
		
		for(GermplasmListData entry : savedListEntries){
			GermplasmListEntry itemId = new GermplasmListEntry(entry.getId(),entry.getGid(), entry.getEntryId(), entry.getDesignation(), entry.getSeedSource());
			
			Item newItem = table.addItem(itemId);
			
			CheckBox tag = new CheckBox();
			newItem.getItemProperty(TAG_COLUMN_ID).setValue(tag);
			
			tag.addListener(new ParentsTableCheckboxListener(table, itemId, container.getTableWithSelectAll().getCheckBox()));
            tag.setImmediate(true);
            
            if(selectedItemIds.contains(itemId)){
            	table.select(itemId);
            }
            
			newItem.getItemProperty(ENTRY_NUMBER_COLUMN_ID).setValue(entry.getEntryId());
			newItem.getItemProperty(container.getColumnName()).setValue(entry.getDesignation());

		}
		
		table.requestRepaint();
	}
	
	private class ParentContainer {
		private Button button;
		private TableWithSelectAllLayout tableWithSelectAll;
		private String columnName;
		private Message successMessage;
		
		public ParentContainer(Button button, TableWithSelectAllLayout tableWithSelectAll,
				String columnName, Message successMessage) {
			super();
			this.button = button;
			this.tableWithSelectAll = tableWithSelectAll;
			this.columnName = columnName;
			this.successMessage = successMessage;
		}

		public Button getButton() {
			return button;
		}
	
		public TableWithSelectAllLayout getTableWithSelectAll() {
			return tableWithSelectAll;
		}


		public Message getSuccessMessage() {
			return successMessage;
		}

		public String getColumnName() {
			return columnName;
		}
	}

}