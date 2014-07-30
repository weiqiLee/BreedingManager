package org.generationcp.breeding.manager.listmanager.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.breeding.manager.crossingmanager.SelectParentsListDataComponent;
import org.generationcp.breeding.manager.customcomponent.listinventory.ListInventoryTable;
import org.generationcp.breeding.manager.customcomponent.listinventory.ListManagerInventoryTable;
import org.generationcp.breeding.manager.inventory.InventoryDropTargetContainer;
import org.generationcp.breeding.manager.inventory.ListDataAndLotDetails;
import org.generationcp.breeding.manager.listmanager.listeners.GidLinkButtonClickListener;
import org.generationcp.breeding.manager.listmanager.sidebyside.ListComponent;
import org.generationcp.breeding.manager.listmanager.sidebyside.ListManagerMain;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.GermplasmListData;

import com.vaadin.data.Item;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableTransferable;
import com.vaadin.ui.themes.BaseTheme;

public class InventoryTableDropHandler extends DropHandlerMethods implements DropHandler {

	private static final long serialVersionUID = 1L;
	private Integer lastDroppedListId;
	
	private List<ListDataAndLotDetails> listDataAndLotDetails;
	
	private boolean hasChanges = false; // TODO this will be REMOVED after refactoring the DropHandler Method
	
	public InventoryTableDropHandler(ListManagerMain listManagerMain, GermplasmDataManager germplasmDataManager, GermplasmListManager germplasmListManager, InventoryDataManager inventoryDataManager, Table targetTable) {
		this.listManagerMain = listManagerMain;
		this.germplasmDataManager = germplasmDataManager;
		this.germplasmListManager = germplasmListManager;
		this.inventoryDataManager = inventoryDataManager;
		this.targetTable = targetTable;
		
		listDataAndLotDetails = new ArrayList<ListDataAndLotDetails>();
	}
	
	public InventoryTableDropHandler(InventoryDropTargetContainer inventoryDropTargetContainer, GermplasmDataManager germplasmDataManager, GermplasmListManager germplasmListManager, InventoryDataManager inventoryDataManager, Table targetTable) {
		this.inventoryDropTargetContainer = inventoryDropTargetContainer;
		this.germplasmDataManager = germplasmDataManager;
		this.germplasmListManager = germplasmListManager;
		this.inventoryDataManager = inventoryDataManager;
		this.targetTable = targetTable;
		
		listDataAndLotDetails = new ArrayList<ListDataAndLotDetails>();
	}

	@Override
	public void drop(DragAndDropEvent event) {
		
		if(event.getTransferable() instanceof TableTransferable){
			
			TableTransferable transferable = (TableTransferable) event.getTransferable();
	        Table sourceTable = transferable.getSourceComponent();
	        String sourceTableData = sourceTable.getData().toString();
	        AbstractSelectTargetDetails dropData = ((AbstractSelectTargetDetails) event.getTargetDetails());
	        //targetTable = (Table) dropData.getTarget();
			
	        if(sourceTableData.equals(MATCHING_GERMPLASMS_TABLE_DATA)){
	        	String message = "Please switch to list view first before adding a germplasm entry to the list.";
	        	
	        	MessageNotifier.showWarning(listManagerMain.getWindow(),"Warning!", message);
				
			} else if(sourceTableData.equals(ListManagerInventoryTable.INVENTORY_TABLE_DATA) && !sourceTable.equals(targetTable)){
				super.setHasUnsavedChanges(true);
				
				lastDroppedListId = ((ListComponent) transferable.getSourceComponent().getParent().getParent()).getGermplasmListId();
				
				List<ListEntryLotDetails> lotDetails = new ArrayList<ListEntryLotDetails>();
				
				//If table has selected items, add selected items
				if(hasSelectedItems(sourceTable)){
					lotDetails.addAll(getInventoryTableSelectedItemIds(sourceTable));
				} //If none, add what was dropped
				else if(transferable.getSourceComponent().getParent().getParent() instanceof ListComponent){
					lotDetails.add((ListEntryLotDetails) transferable.getItemId());
				}
				addSelectedInventoryDetails(lotDetails, sourceTable);
				
			} else if(sourceTableData.equals(SelectParentsListDataComponent.CROSSING_MANAGER_PARENT_TAB_INVENTORY_TABLE) && !sourceTable.equals(targetTable)){
				inventoryDropTargetContainer.setHasUnsavedChanges(true);
				hasChanges = true;
				
				lastDroppedListId = ((SelectParentsListDataComponent) transferable.getSourceComponent().getParent().getParent()).getGermplasmListId();
				
				List<ListEntryLotDetails> lotDetails = new ArrayList<ListEntryLotDetails>();
				
				//If table has selected items, add selected items
				if(hasSelectedItems(sourceTable)){
					lotDetails.addAll(getInventoryTableSelectedItemIds(sourceTable));
				} //If none, add what was dropped
				else if(transferable.getSourceComponent().getParent().getParent() instanceof SelectParentsListDataComponent){
					lotDetails.add((ListEntryLotDetails) transferable.getItemId());
				}
				addSelectedInventoryDetails(lotDetails, sourceTable);
								
				
			} else {
				LOG.error("Error During Drop: Unknown table data: "+sourceTableData);
			}
					
		//If source is from tree
		} else {
			//Not handled
		}
	}

	@Override
	public AcceptCriterion getAcceptCriterion() {
		return AcceptAll.get();
	}

	/**
	 * Use this to handle drop events from list inventory view of list tab to list inventory view of list builder
	 * @param selectedItemIds
	 * @param sourceTable
	 */
	private void addSelectedInventoryDetails(List<ListEntryLotDetails> selectedItemIds, Table sourceTable){
		
		List<Integer> uniqueEntryIds = getUniqueEntryNumbers(selectedItemIds, sourceTable);
		List<ListEntryLotDetails> allLotDetailsToBeAdded = new ArrayList<ListEntryLotDetails>();
		
		for(Integer uniqueEntryId : uniqueEntryIds){
			allLotDetailsToBeAdded.addAll(getLotDetailsWithEntryId(uniqueEntryId, sourceTable));
		}
		
		int nextId = getInventoryTableNextEntryId();
		int lastLrecId = allLotDetailsToBeAdded.get(0).getId();
		
		for(ListEntryLotDetails lotDetail : allLotDetailsToBeAdded){
			if(lastLrecId!=lotDetail.getId())
				nextId++;
			
			addItemToDestinationTable(lotDetail, nextId, sourceTable, targetTable);
			lastLrecId = lotDetail.getId();
		}
	
		//Update counter
		if(listManagerMain!=null)
			listManagerMain.getListBuilderComponent().refreshListInventoryItemCount();
		else if(inventoryDropTargetContainer!=null)
			inventoryDropTargetContainer.refreshListInventoryItemCount();
		
	}
    
	
	
	/**
	 * Get distinct entry #'s, given a list of selected item ids and the source table
	 * @param selectedItemIds
	 * @param sourceTable
	 * @return
	 */
	private List<Integer> getUniqueEntryNumbers(List<ListEntryLotDetails> selectedItemIds, Table sourceTable){
		List<Integer> uniqueEntryIds = new ArrayList<Integer>();
		for(ListEntryLotDetails lotDetail : selectedItemIds){
			
			Item item = sourceTable.getItem(lotDetail);
			if(item!=null){
				int currentEntryId = (Integer) item.getItemProperty(ListInventoryTable.ENTRY_NUMBER_COLUMN_ID).getValue();
				if(!uniqueEntryIds.contains(currentEntryId))
					uniqueEntryIds.add(currentEntryId);
			}
		
		}
		
		return uniqueEntryIds;
	}
	
	/**
	 * Get lotDetails with given entry ID, given the entry ID and the source table
	 * @param entryId
	 * @param sourceTable
	 * @return
	 */
	private List<ListEntryLotDetails> getLotDetailsWithEntryId(Integer entryId, Table sourceTable){
        List<ListEntryLotDetails> allLotDetails = new ArrayList<ListEntryLotDetails>();
        List<ListEntryLotDetails> matchingLotDetails = new ArrayList<ListEntryLotDetails>();
	    allLotDetails.addAll((Collection<? extends ListEntryLotDetails>) sourceTable.getItemIds());
	    
	    for(ListEntryLotDetails lotDetail : allLotDetails){

			Item item = sourceTable.getItem(lotDetail);
			if(item!=null){
				int currentEntryId = (Integer) item.getItemProperty(ListInventoryTable.ENTRY_NUMBER_COLUMN_ID).getValue();
				if(currentEntryId == entryId){
					matchingLotDetails.add(lotDetail);
				}
			}
			
	    	
	    }
	    
	    return matchingLotDetails;
	}	


	/**
	 * To be called after (callback) saving new entries in list builder (on list inventory view) to update lrecid of listentrylotdetails
	 * @param lrecId
	 * @param entryId
	 */
	public void assignLrecIdToRowsFromListWithEntryId(Integer listId, Integer entryId){
	   List<ListEntryLotDetails> itemIds = getLotDetailsWithEntryId(entryId, targetTable);
	   for(ListEntryLotDetails itemId : itemIds){
		  try {
			 GermplasmListData listData = germplasmListManager.getGermplasmListDataByListIdAndEntryId(listId, entryId);
			 if(listData!=null)
				 itemId.setId(listData.getId());
		  } catch (MiddlewareQueryException e) {
			 e.printStackTrace();
          }
	   }
	}

	
	/**
	 * Use this to handle drop events from list inventory view of list tab to list inventory view of list builder
	 * @param selectedItemIds
	 * @param sourceTable
	 */
	public void addGermplasmListInventoryData(Integer listId){
		
		List<GermplasmListData> inventoryDetails;
		
		if(inventoryDropTargetContainer!=null)
			inventoryDropTargetContainer.setHasUnsavedChanges(true);
		
		try {
			inventoryDetails = inventoryDataManager.getLotDetailsForList(listId,0,Integer.MAX_VALUE);
		
			Integer lastEntryId = getInventoryTableLastEntryId();
			
			if(inventoryDetails!=null){
				for(GermplasmListData inventoryDetail : inventoryDetails){
					
					listDataAndLotDetails.add(new ListDataAndLotDetails(listId, inventoryDetail.getId(), inventoryDetail.getEntryId()));
					//listDataAndLotDetails.add(new ListDataAndLotDetails(listId, inventoryDetail.getId(), listDataAndLotDetails.size()+1));
					
					Integer entryId = lastEntryId+inventoryDetail.getEntryId();
					String designation = inventoryDetail.getDesignation();
					
					ListDataInventory listDataInventory = inventoryDetail.getInventoryInfo();
					@SuppressWarnings("unchecked")
					List<ListEntryLotDetails> lotDetails = (List<ListEntryLotDetails>)listDataInventory.getLotRows();
					
					if(lotDetails!=null){
						for(ListEntryLotDetails lotDetail : lotDetails){
							Item newItem = targetTable.addItem(lotDetail);
							
							CheckBox itemCheckBox = new CheckBox();
					        itemCheckBox.setData(lotDetail);
					        itemCheckBox.setImmediate(true);
					   		itemCheckBox.addListener(new ClickListener() {
					 			private static final long serialVersionUID = 1L;
					 			@Override
					 			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
					 				CheckBox itemCheckBox = (CheckBox) event.getButton();
					 				if(((Boolean) itemCheckBox.getValue()).equals(true)){
					 					targetTable.select(itemCheckBox.getData());
					 				} else {
					 					targetTable.unselect(itemCheckBox.getData());
					 				}
					 			}
					 			 
					 		});
							
					   		Button desigButton = new Button(String.format("%s", designation), 
					   					new GidLinkButtonClickListener(inventoryDetail.getGid().toString(), true));
				            desigButton.setStyleName(BaseTheme.BUTTON_LINK);
					   		
					   		newItem.getItemProperty(ListInventoryTable.TAG_COLUMN_ID).setValue(itemCheckBox);
							newItem.getItemProperty(ListInventoryTable.ENTRY_NUMBER_COLUMN_ID).setValue(entryId);
							newItem.getItemProperty(ListInventoryTable.DESIGNATION_COLUMN_ID).setValue(desigButton);
							newItem.getItemProperty(ListInventoryTable.LOCATION_COLUMN_ID).setValue(lotDetail.getLocationOfLot().getLname());
							newItem.getItemProperty(ListInventoryTable.UNITS_COLUMN_ID).setValue(lotDetail.getScaleOfLot().getName());
							newItem.getItemProperty(ListInventoryTable.AVAIL_COLUMN_ID).setValue(lotDetail.getAvailableLotBalance());
							newItem.getItemProperty(ListInventoryTable.TOTAL_COLUMN_ID).setValue(lotDetail.getActualLotBalance());
							newItem.getItemProperty(ListInventoryTable.RESERVED_COLUMN_ID).setValue(lotDetail.getReservedTotalForEntry());
							newItem.getItemProperty(ListInventoryTable.NEWLY_RESERVED_COLUMN_ID).setValue(0);
							newItem.getItemProperty(ListInventoryTable.COMMENT_COLUMN_ID).setValue(lotDetail.getCommentOfLot());
							newItem.getItemProperty(ListInventoryTable.LOT_ID_COLUMN_ID).setValue(lotDetail.getLotId());
							
						}
					}
				}
			}

			//Update counter
			if(listManagerMain!=null)
				listManagerMain.getListBuilderComponent().refreshListInventoryItemCount();
			else if(inventoryDropTargetContainer!=null)
				inventoryDropTargetContainer.refreshListInventoryItemCount();

		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private Item addItemToDestinationTable(ListEntryLotDetails lotDetail, Integer entryId, Table sourceTable, final Table targetTable){
		
		listDataAndLotDetails.add(new ListDataAndLotDetails(lastDroppedListId, lotDetail.getId(), entryId));
		
		ListEntryLotDetails newLotDetail = lotDetail.makeClone();
		Item newItem = targetTable.addItem(newLotDetail);
		newLotDetail.setId(0);
		
		CheckBox itemCheckBox = new CheckBox();
        itemCheckBox.setData(newLotDetail);
        itemCheckBox.setImmediate(true);
   		itemCheckBox.addListener(new ClickListener() {
 			private static final long serialVersionUID = 1L;
 			@Override
 			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
 				CheckBox itemCheckBox = (CheckBox) event.getButton();
 				if(((Boolean) itemCheckBox.getValue()).equals(true)){
 					targetTable.select(itemCheckBox.getData());
 				} else {
 					targetTable.unselect(itemCheckBox.getData());
 				}
 			}
 			 
 		});
		
   		Button sourceDesigButton = new Button();
   		Button desigButton = new Button();
   		
   		Item itemFromSourceTable = sourceTable.getItem(lotDetail);
   		if(itemFromSourceTable!=null){
   			sourceDesigButton = (Button) itemFromSourceTable.getItemProperty(ListInventoryTable.DESIGNATION_COLUMN_ID).getValue();
   			if(sourceDesigButton!=null){
   				desigButton.setValue(sourceDesigButton.getValue());
   				desigButton.setCaption(sourceDesigButton.getCaption());
   				for(Object listener : sourceDesigButton.getListeners(ClickEvent.class)){
   					desigButton.addListener((GidLinkButtonClickListener) listener);	
   				}
   			}
   		}
   		
        desigButton.setStyleName(BaseTheme.BUTTON_LINK);
   		
   		newItem.getItemProperty(ListInventoryTable.TAG_COLUMN_ID).setValue(itemCheckBox);
		newItem.getItemProperty(ListInventoryTable.ENTRY_NUMBER_COLUMN_ID).setValue(entryId);
		newItem.getItemProperty(ListInventoryTable.DESIGNATION_COLUMN_ID).setValue(desigButton);
		newItem.getItemProperty(ListInventoryTable.LOCATION_COLUMN_ID).setValue(lotDetail.getLocationOfLot().getLname());
		newItem.getItemProperty(ListInventoryTable.UNITS_COLUMN_ID).setValue(lotDetail.getScaleOfLot().getName());
		newItem.getItemProperty(ListInventoryTable.AVAIL_COLUMN_ID).setValue(lotDetail.getAvailableLotBalance());
		newItem.getItemProperty(ListInventoryTable.TOTAL_COLUMN_ID).setValue(lotDetail.getActualLotBalance());
		newItem.getItemProperty(ListInventoryTable.RESERVED_COLUMN_ID).setValue(0);
		newItem.getItemProperty(ListInventoryTable.NEWLY_RESERVED_COLUMN_ID).setValue(0);
		newItem.getItemProperty(ListInventoryTable.COMMENT_COLUMN_ID).setValue(lotDetail.getCommentOfLot());
		newItem.getItemProperty(ListInventoryTable.LOT_ID_COLUMN_ID).setValue(lotDetail.getLotId());
		
		return newItem;
	}

	
	private List<ListEntryLotDetails> getInventoryTableItemIds(Table table){
		List<ListEntryLotDetails> lotDetails = new ArrayList<ListEntryLotDetails>();
		lotDetails.addAll((Collection<? extends ListEntryLotDetails>) table.getItemIds());
		return lotDetails;
	}
	
	private List<ListEntryLotDetails> getInventoryTableSelectedItemIds(Table table){
		List<ListEntryLotDetails> lotDetails = new ArrayList<ListEntryLotDetails>();
		lotDetails.addAll((Collection<? extends ListEntryLotDetails>) table.getValue());
		return lotDetails;
	}	
	
	private Integer getInventoryTableLastEntryId(){
		int topId = 0;
		for(ListEntryLotDetails lotDetails : (Collection<? extends ListEntryLotDetails>) targetTable.getItemIds()){
			
			Integer entryId = 0;
			Item item = targetTable.getItem(lotDetails);
			if(item!=null)
				entryId = (Integer) item.getItemProperty(ListInventoryTable.ENTRY_NUMBER_COLUMN_ID).getValue();
			
			if(entryId > topId)
				topId = entryId;
			
		}
		return topId;
	}
	
	private Integer getInventoryTableNextEntryId(){
		return getInventoryTableLastEntryId()+1;
	}	
	
	private Integer getInventoryTableNextTempLrecId(){
		int nextId = 0;
		for(ListEntryLotDetails lotDetails : (Collection<? extends ListEntryLotDetails>) targetTable.getItemIds()){
			
			if(lotDetails.getId() < nextId){
				nextId = lotDetails.getId();
			}
		}
		return nextId-1;
	}	
	
	public List<ListDataAndLotDetails> getListDataAndLotDetails(){
		return listDataAndLotDetails;
	}

	public void setListDataAndLotDetails(List<ListDataAndLotDetails> listDataAndLotDetails){
		this.listDataAndLotDetails = listDataAndLotDetails;
	}
	
	public void resetListDataAndLotDetails(){
		this.listDataAndLotDetails.clear();
	}

	/*
	 * TODO this methods will be removed after successfull refactoring of code in DROP HANDLER CLASS
	 * */
	public boolean hasChanges() {
		return hasChanges;
	}

	public void setHasChanges(boolean hasChanges) {
		this.hasChanges = hasChanges;
	}
	
	
	
}