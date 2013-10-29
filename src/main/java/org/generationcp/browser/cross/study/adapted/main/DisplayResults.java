package org.generationcp.browser.cross.study.adapted.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.generationcp.browser.application.Message;
import org.generationcp.browser.cross.study.adapted.dialogs.SaveToListDialog;
import org.generationcp.browser.cross.study.adapted.main.pojos.CategoricalTraitEvaluator;
import org.generationcp.browser.cross.study.adapted.main.pojos.CategoricalTraitFilter;
import org.generationcp.browser.cross.study.adapted.main.pojos.CharacterTraitEvaluator;
import org.generationcp.browser.cross.study.adapted.main.pojos.CharacterTraitFilter;
import org.generationcp.browser.cross.study.adapted.main.pojos.NumericTraitEvaluator;
import org.generationcp.browser.cross.study.adapted.main.pojos.NumericTraitFilter;
import org.generationcp.browser.cross.study.adapted.main.pojos.ObservationList;
import org.generationcp.browser.cross.study.adapted.main.pojos.TableResultRow;
import org.generationcp.browser.cross.study.adapted.main.pojos.TraitObservationScore;
import org.generationcp.browser.cross.study.constants.EnvironmentWeight;
import org.generationcp.browser.cross.study.h2h.main.pojos.EnvironmentForComparison;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.ObservationKey;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

@Configurable
public class DisplayResults extends AbsoluteLayout implements InitializingBean, InternationalizableComponent{

	private static final long serialVersionUID = 1L;

	private final static Logger LOG = LoggerFactory.getLogger(DisplayResults.class);
	
	private static final String TAG_COLUMN_ID = "DisplayResults Tag Column Id";
	private static final String LINE_NO = "DisplayResults Line No";
	private static final String LINE_GID = "DisplayResults Line GID";
	private static final String LINE_DESIGNATION = "DisplayResults Line Designation";
	private static final String COMBINED_SCORE_COLUMN_ID = "DisplayResults Combined Score";
	
    public static final String SAVE_BUTTON_ID = "DisplayResults Save Button ID";
    public static final String BACK_BUTTON_ID = "DisplayResults Back Button ID";
    public static final String NEXT_ENTRY_BUTTON_ID = "DisplayResults Next Entry Button ID";
    public static final String PREV_ENTRY_BUTTON_ID = "DisplayResults Prev Entry Button ID";
    
	private QueryForAdaptedGermplasmMain mainScreen;
	
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
	private CrossStudyDataManager crossStudyDataManager;
	
	@Autowired
	private GermplasmDataManager germplasmDataManager;
	
	private Table germplasmColTable;
	private Table traitsColTable;
	private Table combinedScoreTagColTable;
	
	private Integer NoOfTraitColumns;
	private List<String> columnHeaders;
	
	List<TableResultRow> tableRows;
	List<TableResultRow> tableRowsSelected;
	Integer	currentLineIndex; 
	
	private Button backButton;
	private Button saveButton;
	private Button nextEntryBtn;
	private Button prevEntryBtn;
	
	private List<Integer> environmentIds;
	List<EnvironmentForComparison> environments;
	
	private List<Integer> traitIds;
	private Map<Integer, String> germplasmIdNameMap;
	private Map<String, Integer> germplasmNameIdMap;
	
	private List<NumericTraitFilter> numericTraitFilter;
	private List<CharacterTraitFilter> characterTraitFilter;
	private List<CategoricalTraitFilter> categoricalTraitFilter;
	
	private List<Observation> observations;
	private Map<ObservationKey, ObservationList> observationsMap;
	
	private SaveToListDialog saveGermplasmListDialog;
	private Map<Integer, String> selectedGermplasmMap;
	private Map<Object, Boolean> columnOrdering; 
	
	public DisplayResults(QueryForAdaptedGermplasmMain mainScreen) {
		this.mainScreen = mainScreen;
	}

	@Override
	public void updateLabels() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		setHeight("550px");
		setWidth("1000px");	
		
		AbsoluteLayout resultTable = new AbsoluteLayout();
		resultTable.setHeight("450px");
		resultTable.setWidth("1000px");
		
		germplasmColTable = new Table();
		germplasmColTable.setWidth("340px");
		germplasmColTable.setHeight("420px");
		germplasmColTable.setImmediate(true);
		germplasmColTable.setPageLength(15);
		germplasmColTable.setColumnCollapsingAllowed(true);
		germplasmColTable.setColumnReorderingAllowed(false);
		//germplasmColTable.setSortDisabled(true);
		
		traitsColTable = new Table();
		traitsColTable.setWidth("560px");
		traitsColTable.setHeight("420px");
		traitsColTable.setImmediate(true);
		traitsColTable.setPageLength(15);
		traitsColTable.setColumnCollapsingAllowed(true);
		traitsColTable.setColumnReorderingAllowed(false);
		//traitsColTable.setSortDisabled(true);
		
		combinedScoreTagColTable = new Table(); 
		combinedScoreTagColTable.setWidth("60px");
		combinedScoreTagColTable.setHeight("420px");
		combinedScoreTagColTable.setImmediate(true);
		combinedScoreTagColTable.setPageLength(15);
		combinedScoreTagColTable.setColumnCollapsingAllowed(true);
		combinedScoreTagColTable.setColumnReorderingAllowed(false);
		//combinedScoreTagColTable.setSortDisabled(true);
		
		resultTable.addComponent(germplasmColTable, "top:20px;left:20px");
		resultTable.addComponent(traitsColTable, "top:20px;left:359px");
		resultTable.addComponent(combinedScoreTagColTable, "top:20px;left:919px");
		addComponent(new Label("<style> .v-table-column-selector { width:0; height:0; overflow:hidden; }" +
				".v-table-row, .v-table-row-odd { height: 25px; } </style>",Label.CONTENT_XHTML));
		addComponent(resultTable, "top:0px;left:0px");
		
		
		prevEntryBtn = new Button(messageSource.getMessage(Message.PREV_ENTRY));
		prevEntryBtn.setData(NEXT_ENTRY_BUTTON_ID);
		prevEntryBtn.addListener(new Button.ClickListener(){
			@Override
			public void buttonClick(ClickEvent event) {
				prevEntryButtonClickAction();
			}
		});
		prevEntryBtn.setWidth("100px");
		prevEntryBtn.setEnabled(true);
		addComponent(prevEntryBtn, "top:450px;left:770px");
		
		nextEntryBtn = new Button(messageSource.getMessage(Message.NEXT_ENTRY));
		nextEntryBtn.setData(NEXT_ENTRY_BUTTON_ID);
		nextEntryBtn.addListener(new Button.ClickListener(){
			@Override
			public void buttonClick(ClickEvent event) {
				nextEntryButtonClickAction();
			}
		});
		nextEntryBtn.setWidth("100px");
		nextEntryBtn.setEnabled(true);
		addComponent(nextEntryBtn, "top:450px;left:880px");
		
		
		backButton = new Button(messageSource.getMessage(Message.BACK));
		backButton.setData(BACK_BUTTON_ID);
		backButton.addListener(new Button.ClickListener(){
			@Override
			public void buttonClick(ClickEvent event) {
				backButtonClickAction();
			}
		});
		backButton.setWidth("100px");
		backButton.setEnabled(true);
		addComponent(backButton, "top:510px;left:770px");

		saveButton = new Button(messageSource.getMessage(Message.SAVE_GERMPLASMS_TO_NEW_LIST_LABEL));
		saveButton.setData(SAVE_BUTTON_ID);
		saveButton.addListener(new Button.ClickListener(){
			@Override
			public void buttonClick(ClickEvent event) {
				saveButtonClickAction();
			}
		});
		saveButton.setWidth("100px");
		saveButton.setEnabled(false);
		addComponent(saveButton, "top:510px;left:880px");
	}
	
	public void populateResultsTable(List<EnvironmentForComparison> environments, List<NumericTraitFilter> numericTraitFilter, 
			List<CharacterTraitFilter> characterTraitFilter, List<CategoricalTraitFilter> categoricalTraitFilter){
		this.environments = environments;
		this.environmentIds = getEnvironmentIds(environments);
		this.numericTraitFilter = numericTraitFilter;
		this.characterTraitFilter = characterTraitFilter;
		this.categoricalTraitFilter = categoricalTraitFilter;
		
		this.traitIds = getTraitIds(numericTraitFilter, characterTraitFilter, categoricalTraitFilter);
		
		this.germplasmIdNameMap = getGermplasm(traitIds,environmentIds);
		this.germplasmNameIdMap = getSortedGermplasmList(germplasmIdNameMap);
		this.selectedGermplasmMap = new HashMap<Integer,String>();
		
		//createResultsTable();
		germplasmColTable = createResultsTable(germplasmColTable);
		traitsColTable = createResultsTable(traitsColTable);
		combinedScoreTagColTable = createResultsTable(combinedScoreTagColTable);
		
		
		for(Object propertyId : germplasmColTable.getContainerPropertyIds()){
        	if(propertyId.toString().equals(LINE_NO)){ 
        		germplasmColTable.setColumnCollapsed(propertyId, false);
        	}
        	else if(propertyId.toString().equals(LINE_GID)){
        		germplasmColTable.setColumnCollapsed(propertyId, false);
        	}
        	else if(propertyId.toString().equals(LINE_DESIGNATION)){
        		germplasmColTable.setColumnCollapsed(propertyId, false);
        	}
        	else{
        		germplasmColTable.setColumnCollapsed(propertyId, true);
        	}
        }
		
		for(Object propertyId : traitsColTable.getContainerPropertyIds()){
        	if(propertyId.toString().equals(LINE_NO)){ 
        		traitsColTable.setColumnCollapsed(propertyId, true);
        	}
        	else if(propertyId.toString().equals(LINE_GID)){
        		traitsColTable.setColumnCollapsed(propertyId, true);
        	}
        	else if(propertyId.toString().equals(LINE_DESIGNATION)){
        		traitsColTable.setColumnCollapsed(propertyId, true);
        	}
        	else if(propertyId.toString().equals(TAG_COLUMN_ID)){
        		traitsColTable.setColumnCollapsed(propertyId, true);
        	}
        	else{
        		traitsColTable.setColumnCollapsed(propertyId, false);
        	}
        }
		
		for(Object propertyId : combinedScoreTagColTable.getContainerPropertyIds()){
			if(propertyId.toString().equals(TAG_COLUMN_ID)){
				combinedScoreTagColTable.setColumnCollapsed(propertyId, false);
        	}
        	else{
        		combinedScoreTagColTable.setColumnCollapsed(propertyId, true);
        	}
        }
		
		//header column listener
		initializeColumnOrdering();
		
		germplasmColTable.addListener(new Table.HeaderClickListener() {
		    public void headerClick(HeaderClickEvent event) {
		        //String column = (String) event.getPropertyId();
		        //System.out.println("Clicked " + column + "with " + event.getButtonName());
		    	Object property = event.getPropertyId();
		    	Object[] properties = new Object[]{property};
		    	
		    	boolean order = columnOrdering.get(property);
		    	order = order? false : true;
		    	
		    	columnOrdering.put(property, order);
		    	
		    	boolean[] ordering = new boolean[]{ order };
		        
		        traitsColTable.sort(properties, ordering);
		        combinedScoreTagColTable.sort(properties, ordering);
		    }
		});
		
		traitsColTable.addListener(new Table.HeaderClickListener() {
			public void headerClick(HeaderClickEvent event) {
		        //String column = (String) event.getPropertyId();
		        //System.out.println("Clicked " + column + "with " + event.getButtonName());
		    	Object property = event.getPropertyId();
		    	Object[] properties = new Object[]{property};
		    	
		    	boolean order = columnOrdering.get(property);
		    	order = order? false : true;
		    	
		    	columnOrdering.put(property, order);
		    	
		    	boolean[] ordering = new boolean[]{ order };
		        
		    	germplasmColTable.sort(properties, ordering);
		        combinedScoreTagColTable.sort(properties, ordering);
		    }
		});
		
		combinedScoreTagColTable.addListener(new Table.HeaderClickListener() {
			public void headerClick(HeaderClickEvent event) {
		        //String column = (String) event.getPropertyId();
		        //System.out.println("Clicked " + column + "with " + event.getButtonName());
		    	Object property = event.getPropertyId();
		    	Object[] properties = new Object[]{property};
		    	
		    	boolean order = columnOrdering.get(property);
		    	order = order? false : true;
		    	
		    	columnOrdering.put(property, order);
		    	
		    	boolean[] ordering = new boolean[]{ order };
		        
		        traitsColTable.sort(properties, ordering);
		        germplasmColTable.sort(properties, ordering);
		    }
		});
        		
	}
	
	public Table createResultsTable(Table resultTable){
		
		List<Object> propertyIds = new ArrayList<Object>();
        for(Object propertyId : resultTable.getContainerPropertyIds()){
            propertyIds.add(propertyId);
        }
        for(Object propertyId : propertyIds){
        	resultTable.removeContainerProperty(propertyId);
        	resultTable.removeGeneratedColumn(propertyId);
        }
        
        resultTable.removeAllItems();
		
        resultTable.addContainerProperty(LINE_NO, Integer.class, null);
        resultTable.addContainerProperty(LINE_GID, Integer.class, null);
        resultTable.addContainerProperty(LINE_DESIGNATION, String.class, null);
		
        resultTable.setColumnHeader(LINE_NO, "Line No");
        resultTable.setColumnHeader(LINE_GID, "Line GID");
        resultTable.setColumnHeader(LINE_DESIGNATION, "Line Designation");
		
		Integer NoOfColumns = 3;
		NoOfTraitColumns = 0;
		for(NumericTraitFilter trait : numericTraitFilter){
			String name = trait.getTraitInfo().getName().trim() + "\n No Obs";
			String weight = "Wt = " + trait.getPriority().getWeight() + "\n Score";
			Integer traitId = trait.getTraitInfo().getId();
			
			resultTable.addContainerProperty("DisplayResults " + name + traitId + " numeric", Integer.class, null);
			resultTable.addContainerProperty("DisplayResults " + weight + traitId + " numeric", Double.class, null);
			
			resultTable.setColumnHeader("DisplayResults " + name + traitId + " numeric", name);
			resultTable.setColumnHeader("DisplayResults " + weight + traitId + " numeric", weight);
			
			NoOfColumns+=2;
			NoOfTraitColumns += 2;
		}
		
		for(CharacterTraitFilter trait : characterTraitFilter){
			String name = trait.getTraitInfo().getName().trim() + "\n No Obs";
			String weight = "Wt = " + trait.getPriority().getWeight() + "\n Score";
			Integer traitId = trait.getTraitInfo().getId();
			
			resultTable.addContainerProperty("DisplayResults " + name + traitId + " character", Integer.class, null);
			resultTable.addContainerProperty("DisplayResults " + weight + traitId + " character", Double.class, null);
			
			resultTable.setColumnHeader("DisplayResults " + name + traitId + " character", name);
			resultTable.setColumnHeader("DisplayResults " + weight + traitId + " character", weight);
			
			NoOfColumns+=2;
			NoOfTraitColumns += 2;
		}
		
		for(CategoricalTraitFilter trait : categoricalTraitFilter){
			String name = trait.getTraitInfo().getName().trim() + "\n No Obs";
			String weight = "Wt = " + trait.getPriority().getWeight() + "\n Score";
			Integer traitId = trait.getTraitInfo().getId();
			
			resultTable.addContainerProperty("DisplayResults " + name + traitId + " categorical", Integer.class, null);
			resultTable.addContainerProperty("DisplayResults " + weight + traitId + " categorical", Double.class, null);
			
			resultTable.setColumnHeader("DisplayResults " + name + traitId + " categorical", name );
			resultTable.setColumnHeader("DisplayResults " + weight + traitId + " categorical", weight);
			
			NoOfColumns+=2;
			NoOfTraitColumns += 2;
		}
		
		resultTable.addContainerProperty(COMBINED_SCORE_COLUMN_ID, Double.class, null);
		resultTable.setColumnHeader(COMBINED_SCORE_COLUMN_ID, "Combined Score");
		NoOfColumns++;
		
		resultTable.addContainerProperty(TAG_COLUMN_ID, CheckBox.class, null);
		resultTable.setColumnHeader(TAG_COLUMN_ID, "Tag");
		NoOfColumns++;
		
		tableRows = getTableRowsResults();
		currentLineIndex = 0;
		populateRowsResultsTable(resultTable, NoOfColumns);
		
		return resultTable;
	}
		
	public void populateRowsResultsTable(Table resultTable, Integer NoOfColumns){
		int line_no = currentLineIndex + 1;
		int endOfListIndex = currentLineIndex + 15;
		
		if(endOfListIndex > this.tableRows.size()){
			endOfListIndex = this.tableRows.size();
		}
				
        for(TableResultRow row : tableRows.subList(currentLineIndex, endOfListIndex)){
        	//System.out.println(row.toString());
        	
			int gid = row.getGermplasmId();
			String germplasmName = germplasmIdNameMap.get(gid);
			
			Object[] itemObj = new Object[NoOfColumns];   
			
			itemObj[0] = line_no;
			itemObj[1] = gid;
			itemObj[2] = (germplasmName == null)? "" : germplasmName;
			
			columnHeaders = getColumnProperties(resultTable.getContainerPropertyIds());
			//System.out.println("TOTAL TRAIT COLUMNS NO: " + NoOfColumns);
			
			Map<NumericTraitFilter,TraitObservationScore> numericTOSMap = row.getNumericTOSMap();
			for(Map.Entry<NumericTraitFilter, TraitObservationScore> numericTOS : numericTOSMap.entrySet()){
				String traitName = numericTOS.getKey().getTraitInfo().getName().trim();
				Integer traitId = numericTOS.getKey().getTraitInfo().getId();
				
				String name = traitName + "\n No Obs";
				
				int index = columnHeaders.indexOf("DisplayResults " + name + traitId + " numeric");
				
				itemObj[index] = numericTOS.getValue().getNoOfObservation();
				itemObj[index + 1] = numericTOS.getValue().getWtScore();
				
				//System.out.println(name + " : "+ traitId + " : CURRENT INDEX3: " + index + "|" + numericTOS.getValue().getNoOfObservation() + "|" + numericTOS.getValue().getWtScore());
			}
			
			Map<CharacterTraitFilter,TraitObservationScore> characterTOSMap = row.getCharacterTOSMap();
			for(Map.Entry<CharacterTraitFilter, TraitObservationScore> characterTOS : characterTOSMap.entrySet()){
				String traitName = characterTOS.getKey().getTraitInfo().getName().trim();
				Integer traitId = characterTOS.getKey().getTraitInfo().getId();
				
				String name = traitName + "\n No Obs";
				
				int index = columnHeaders.indexOf("DisplayResults " + name + traitId + " character");
				
				itemObj[index] = characterTOS.getValue().getNoOfObservation();
				itemObj[index + 1] = characterTOS.getValue().getWtScore();
				
				//System.out.println(name + " : "+ traitId + " : CURRENT INDEX3: " + index + "|" + characterTOS.getValue().getNoOfObservation() + "|" + characterTOS.getValue().getWtScore());
			}
			
			Map<CategoricalTraitFilter,TraitObservationScore> categoricalTOSMap = row.getCategoricalTOSMap();
			for(Map.Entry<CategoricalTraitFilter, TraitObservationScore> categoricalTOS : categoricalTOSMap.entrySet()){
				String traitName = categoricalTOS.getKey().getTraitInfo().getName().trim();
				Integer traitId = categoricalTOS.getKey().getTraitInfo().getId();
				
				String name = traitName + "\n No Obs";
				
				int index = columnHeaders.indexOf("DisplayResults " + name + traitId + " categorical");
				
				itemObj[index] = categoricalTOS.getValue().getNoOfObservation();
				itemObj[index + 1] = categoricalTOS.getValue().getWtScore();
				
				//System.out.println(name + " : "+ traitId + " : CURRENT INDEX3: " + index + "|" + categoricalTOS.getValue().getNoOfObservation() + "|" + categoricalTOS.getValue().getWtScore());
			}
			
			itemObj[NoOfColumns - 2] = row.getCombinedScore();
			
			
			CheckBox box = new CheckBox();
			box.setImmediate(true);
			box.setData(row);
			if(selectedGermplasmMap.containsKey(gid)){
				box.setValue(true);
			}
			
			box.addListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					CheckBox box = (CheckBox) event.getSource();
					TableResultRow row = (TableResultRow) box.getData();
					
					if(box.booleanValue()){
						box.setValue(true);
					}
					else{
						box.setValue(false);
					}
					
					addItemForSelectedGermplasm(box,row);
					//MessageNotifier.showMessage(getWindow(), row.getGermplasmId().toString(), germplasmIdNameMap.get(row.getGermplasmId()));
				}
			});
			
			itemObj[NoOfColumns - 1] = box;
			
			resultTable.addItem(itemObj,row);
			
			line_no++;
		}
	}
	
	public List<String> getColumnHeaders(String[] headers){
		List<String> columnHeaders = new ArrayList<String>();
		
		for(int i = 0; i < headers.length; i++){
			columnHeaders.add(headers[i].trim());
			//System.out.println(columnHeaders.get(i));
		}
		
		return columnHeaders;
	}
	
	public List<String> getColumnProperties(Collection properties){
		List<String> columnHeaders = new ArrayList<String>();
		
		for(Object prop : properties){
			columnHeaders.add(prop.toString());
			//System.out.println(prop.toString());
		}
		
		return columnHeaders;
	}
	
	public void initializeColumnOrdering(){
		columnOrdering = new HashMap<Object,Boolean>();
		
		Collection columns = germplasmColTable.getContainerPropertyIds();
		for(Object column : columns){
			if(column.equals(LINE_DESIGNATION)){
				columnOrdering.put(column, false);
			}
			else{
				columnOrdering.put(column, true);
			}
			
		}
	}
	
	public List<TableResultRow> getTableRowsResults(){
		List<TableResultRow> tableRows = new ArrayList<TableResultRow>();
		
		try {
			List<Observation> observations = crossStudyDataManager.getObservationsForTraits(traitIds, environmentIds);
			observationsMap = getObservationsMap(observations);
			
			List<Integer> germplasmIds = new ArrayList<Integer>();
			germplasmIds.addAll(germplasmIdNameMap.keySet());
			
			for(Map.Entry<String, Integer> germplasm : germplasmNameIdMap.entrySet()){
				int germplasmId = germplasm.getValue();
				
				Map<NumericTraitFilter,TraitObservationScore> numericTOSMap = new HashMap<NumericTraitFilter,TraitObservationScore>();
				Map<CharacterTraitFilter,TraitObservationScore> characterTOSMap = new HashMap<CharacterTraitFilter,TraitObservationScore>();
				Map<CategoricalTraitFilter,TraitObservationScore> categoricalTOSMap = new HashMap<CategoricalTraitFilter,TraitObservationScore>();
				
				
				//NUMERIC TRAIT
				for(NumericTraitFilter trait : numericTraitFilter){
					Double envWt = 0.0;
					Integer noOfObservation = 0;
					Double scorePerTrait = 0.0;
					List<Integer> obsResults = new ArrayList<Integer>();
					
					for(EnvironmentForComparison env : environments){
						ObservationKey key = new ObservationKey(trait.getTraitInfo().getId(), germplasmId, env.getEnvironmentNumber());
						ObservationList obsList = observationsMap.get(key);
						
						if(obsList != null){ // if the observation exist
							//System.out.println("Trait Name: " + trait.getTraitInfo().getName());
							//System.out.println("ObservationKey: " + trait.getTraitInfo().getId()+"|"+germplasm.getKey()+"|"+env.getEnvironmentNumber());
							
							ComboBox weightComboBox = env.getWeightComboBox();
							EnvironmentWeight weight = (EnvironmentWeight) weightComboBox.getValue();
							envWt = Double.valueOf(weight.getWeight());
							
							noOfObservation = obsList.getObservationList().size();
							Double scorePerEnv = 0.0;
							for(Observation obs : obsList.getObservationList()){
								if(testNumericTraitVal(trait, obs)){
									scorePerEnv = scorePerEnv + 1;
								}
								else{
									scorePerEnv = scorePerEnv + (-1);
								}
							}
							//System.out.println("scorePerEnv  = " + envWt + " * ( " + scorePerEnv +" / " + noOfObservation + " );");
							scorePerEnv = envWt * ( scorePerEnv / noOfObservation );
							
							//System.out.println(scorePerTrait+"+=" + scorePerEnv + ";");
							scorePerTrait += scorePerEnv;
						}
					}
					
					//No Of Observation and Wt Score Per Trait
					TraitObservationScore tos = new TraitObservationScore(germplasmId,noOfObservation,scorePerTrait);
					numericTOSMap.put(trait,tos);
				}
				
				
				//CHARACTER TRAIT
				for(CharacterTraitFilter trait : characterTraitFilter){
					Double envWt = 0.0;
					Integer noOfObservation = 0;
					Double scorePerTrait = 0.0;
					List<Integer> obsResults = new ArrayList<Integer>();
					
					for(EnvironmentForComparison env : environments){
						ObservationKey key = new ObservationKey(trait.getTraitInfo().getId(), germplasmId, env.getEnvironmentNumber());
						ObservationList obsList = observationsMap.get(key);
						
						if(obsList != null){ // if the observation exist
							//System.out.println("Trait Name: " + trait.getTraitInfo().getName());
							//System.out.println("ObservationKey: " + trait.getTraitInfo().getId()+"|"+germplasm.getKey()+"|"+env.getEnvironmentNumber());
							
							ComboBox weightComboBox = env.getWeightComboBox();
							EnvironmentWeight weight = (EnvironmentWeight) weightComboBox.getValue();
							envWt = Double.valueOf(weight.getWeight());
							
							noOfObservation = obsList.getObservationList().size();
							Double scorePerEnv = 0.0;
							for(Observation obs : obsList.getObservationList()){
								if(testCharacterTraitVal(trait, obs)){
									scorePerEnv = scorePerEnv + 1;
								}
								else{
									scorePerEnv = scorePerEnv + (-1);
								}
							}
							//System.out.println("scorePerEnv  = " + envWt + " * ( " + scorePerEnv +" / " + noOfObservation + " );");
							scorePerEnv = envWt * ( scorePerEnv / noOfObservation );
							
							//System.out.println(scorePerTrait+"+=" + scorePerEnv + ";");
							scorePerTrait += scorePerEnv;
						}
					}
					
					//No Of Observation and Wt Score Per Trait
					TraitObservationScore tos = new TraitObservationScore(germplasmId,noOfObservation,scorePerTrait);
					characterTOSMap.put(trait,tos);
				}
				
				//CATEGORICAL TRAIT
				for(CategoricalTraitFilter trait : categoricalTraitFilter){
					Double envWt = 0.0;
					Integer noOfObservation = 0;
					Double scorePerTrait = 0.0;
					List<Integer> obsResults = new ArrayList<Integer>();
					
					for(EnvironmentForComparison env : environments){
						ObservationKey key = new ObservationKey(trait.getTraitInfo().getId(), germplasmId, env.getEnvironmentNumber());
						ObservationList obsList = observationsMap.get(key);
						
						if(obsList != null){ // if the observation exist
							//System.out.println("Trait Name: " + trait.getTraitInfo().getName());
							//System.out.println("ObservationKey: " + trait.getTraitInfo().getId()+"|"+germplasm.getKey()+"|"+env.getEnvironmentNumber());
							
							ComboBox weightComboBox = env.getWeightComboBox();
							EnvironmentWeight weight = (EnvironmentWeight) weightComboBox.getValue();
							envWt = Double.valueOf(weight.getWeight());
							
							noOfObservation = obsList.getObservationList().size();
							Double scorePerEnv = 0.0;
							for(Observation obs : obsList.getObservationList()){
								if(testCategoricalTraitVal(trait, obs)){
									scorePerEnv = scorePerEnv + 1;
								}
								else{
									scorePerEnv = scorePerEnv + (-1);
								}
							}
							//System.out.println("scorePerEnv  = " + envWt + " * ( " + scorePerEnv +" / " + noOfObservation + " );");
							scorePerEnv = envWt * ( scorePerEnv / noOfObservation );
							
							//System.out.println(scorePerTrait+"+=" + scorePerEnv + ";");
							scorePerTrait += scorePerEnv;
						}
					}
					
					//No Of Observation and Wt Score Per Trait
					TraitObservationScore tos = new TraitObservationScore(germplasmId,noOfObservation,scorePerTrait);
					categoricalTOSMap.put(trait,tos);
				}
				
				tableRows.add(new TableResultRow(germplasmId,numericTOSMap,characterTOSMap,categoricalTOSMap));
			}
			
		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tableRows;
	}
	
	public boolean testNumericTraitVal(NumericTraitFilter trait, Observation obsevation){
		NumericTraitEvaluator eval = new NumericTraitEvaluator(trait.getCondition(),
				trait.getLimits(), Double.valueOf(obsevation.getValue()));
		
		return eval.evaluate();
	}
	
	public boolean testCharacterTraitVal(CharacterTraitFilter trait, Observation obsevation){
		CharacterTraitEvaluator eval = new CharacterTraitEvaluator(trait.getCondition(),
				trait.getLimits(), obsevation.getValue());
		
		return eval.evaluate();
	}
	
	public boolean testCategoricalTraitVal(CategoricalTraitFilter trait, Observation obsevation){
		CategoricalTraitEvaluator eval = new CategoricalTraitEvaluator(trait.getCondition(),
				trait.getLimits(), obsevation.getValue());
		
		return eval.evaluate();
	}
	
	public Map<ObservationKey, ObservationList> getObservationsMap(List<Observation> observations){
		Map<ObservationKey, ObservationList> observationsMap = new HashMap<ObservationKey, ObservationList>();
		
		for(Observation obs : observations){
			ObservationKey key = obs.getId();
			
			if(!observationsMap.containsKey(key)){
				ObservationList list = new ObservationList(key);
				list.add(obs);
				observationsMap.put(key,list);
			}
			else{
				ObservationList obslist = observationsMap.get(key);
				List<Observation> list = obslist.getObservationList();
				list.add(obs);
				obslist.setObservationList(list);
				
				observationsMap.put(key, obslist);
			}
		}
		
		/*System.out.println("# of observations: " + observations.size());
		int countList = 0;
		for(Map.Entry<ObservationKey, ObservationList> obs : observationsMap.entrySet()){
			countList += obs.getValue().getObservationList().size();
		}
		System.out.println("# of observationsMap: " + countList);*/
		return observationsMap;
	}
	
	public List<Integer> getTraitIds(List<NumericTraitFilter> numericTraitFilter, 
			List<CharacterTraitFilter> characterTraitFilter, List<CategoricalTraitFilter> categoricalTraitFilter){
		List<Integer> traitIds = new ArrayList<Integer>();
		
		for(NumericTraitFilter trait : numericTraitFilter){
			traitIds.add(trait.getTraitInfo().getId());
		}
		
		for(CharacterTraitFilter trait : characterTraitFilter){
			traitIds.add(trait.getTraitInfo().getId());
		}
		
		for(CategoricalTraitFilter trait : categoricalTraitFilter){
			traitIds.add(trait.getTraitInfo().getId());
		}
		
		return traitIds;
	}
	
	public List<Integer> getEnvironmentIds(List<EnvironmentForComparison> environments){
		List<Integer> environmentIds = new ArrayList<Integer>();
		
		for(EnvironmentForComparison env : environments){
			environmentIds.add(env.getEnvironmentNumber());
		}
		
		return environmentIds;
	}
	
	public Map<Integer, String> getGermplasm(List<Integer> traitIds, List<Integer> environmentIds){
		Map<Integer,String> germplasmIdNameMap = new HashMap<Integer,String>();
		
		List<Integer> germplasmIds = new ArrayList<Integer>();
		List<Integer> traitIdList = new ArrayList<Integer>();
		traitIdList.addAll(traitIds);
		
		try {
			observations = crossStudyDataManager.getObservationsForTraits(traitIdList, environmentIds);
			
			Iterator obsIter = observations.iterator();
			while(obsIter.hasNext()){
				Observation observation = (Observation) obsIter.next();
				int id = observation.getId().getGermplasmId();
				if(!germplasmIds.contains(id)){
					germplasmIds.add(id);
				}
			}

			germplasmIdNameMap = germplasmDataManager.getPreferredNamesByGids(germplasmIds);
		} catch (MiddlewareQueryException ex) {
			ex.printStackTrace();
            LOG.error("Database error!", ex);
            MessageNotifier.showError(getWindow(), "Database Error!", "Please report to IBP.", Notification.POSITION_CENTERED);
		}
		
		return germplasmIdNameMap;
	}
	
	public Map<String, Integer> getSortedGermplasmList(Map<Integer, String> germplasmList){
		TreeMap<String, Integer> sorted = new TreeMap<String, Integer>();
		
		for(Map.Entry<Integer, String> entry : germplasmList.entrySet()){
			String name = entry.getValue();
			if(name == null){ name = ""; }
			
			Integer id = entry.getKey();
			sorted.put(name, id);
		}
		
		return sorted;
	}
	
	public void nextEntryButtonClickAction(){
        if(!(currentLineIndex + 15 > this.tableRows.size())){
        	germplasmColTable.removeAllItems();
            traitsColTable.removeAllItems();
            combinedScoreTagColTable.removeAllItems();
            
        	currentLineIndex += 15;
        	
            //populateRowsResultsTable();
            int NoOfColumns = this.NoOfTraitColumns + 5; 
            populateRowsResultsTable(germplasmColTable, NoOfColumns);
            populateRowsResultsTable(traitsColTable, NoOfColumns);
            populateRowsResultsTable(combinedScoreTagColTable, NoOfColumns);
        }
        else{
        	MessageNotifier.showWarning(getWindow(), "Notification", "No More Rows to display.");
        }
	}
	
	public void prevEntryButtonClickAction(){
        currentLineIndex -= 15;
        if( !(currentLineIndex < 0) ){
        	germplasmColTable.removeAllItems();
            traitsColTable.removeAllItems();
            combinedScoreTagColTable.removeAllItems();
            
            //populateRowsResultsTable();
            int NoOfColumns = this.NoOfTraitColumns + 5; 
            populateRowsResultsTable(germplasmColTable, NoOfColumns);
            populateRowsResultsTable(traitsColTable, NoOfColumns);
            populateRowsResultsTable(combinedScoreTagColTable, NoOfColumns);
        }
        else{
        	 currentLineIndex = 0;
        	MessageNotifier.showWarning(getWindow(), "Notification", "No More Rows to preview.");
        }
	}
	
	public void backButtonClickAction(){
		this.mainScreen.selectSecondTab();
	}
	
	public void saveButtonClickAction(){
		openDialogSaveList();
	}
	
	public void addItemForSelectedGermplasm(CheckBox box, TableResultRow row){
		Integer gid = row.getGermplasmId();
		String preferredName = germplasmIdNameMap.get(gid);
		
		if(selectedGermplasmMap.isEmpty()){
			selectedGermplasmMap.put(gid, preferredName);
		}
		else{
			if(selectedGermplasmMap.containsKey(gid)){
				selectedGermplasmMap.remove(gid);
			}
			else{
				selectedGermplasmMap.put(gid, preferredName);
			}
		}
		
		if(selectedGermplasmMap.size() > 0){
			this.saveButton.setEnabled(true);
		}
		else if(selectedGermplasmMap.size() == 0){
			this.saveButton.setEnabled(false);
		}
		
		//System.out.println("No of SelectedGermplasms: " + selectedGermplasmMap.size());
	}
	
    @SuppressWarnings("deprecation")
	private void openDialogSaveList() {
    	Window parentWindow = this.getWindow();
    	
    	saveGermplasmListDialog = new SaveToListDialog(mainScreen, this, parentWindow, selectedGermplasmMap);
	    
	    parentWindow.addWindow(saveGermplasmListDialog);
    }

}
