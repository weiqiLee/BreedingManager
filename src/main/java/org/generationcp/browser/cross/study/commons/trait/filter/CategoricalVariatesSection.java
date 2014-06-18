package org.generationcp.browser.cross.study.commons.trait.filter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.generationcp.browser.application.Message;
import org.generationcp.browser.cross.study.adapted.dialogs.ViewTraitObservationsDialog;
import org.generationcp.browser.cross.study.adapted.main.listeners.AdaptedGermplasmButtonClickListener;
import org.generationcp.browser.cross.study.adapted.main.listeners.AdaptedGermplasmValueChangeListener;
import org.generationcp.browser.cross.study.adapted.main.pojos.CategoricalTraitFilter;
import org.generationcp.browser.cross.study.adapted.main.validators.CategoricalTraitLimitsValidator;
import org.generationcp.browser.cross.study.constants.CategoricalVariatesCondition;
import org.generationcp.browser.cross.study.constants.TraitWeight;
import org.generationcp.browser.cross.study.util.CrossStudyUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class CategoricalVariatesSection extends VerticalLayout implements InitializingBean, InternationalizableComponent{
	private static final long serialVersionUID = 9099796930978032454L;
	
	private static final Logger LOG = LoggerFactory.getLogger(CategoricalVariatesSection.class);
	
	private static final String TRAIT_COLUMN_ID = "Trait Column";
	private static final String NUM_LOCATIONS_COLUMN_ID = "Number of Locations";
	private static final String NUM_LINES_COLUMN_ID = "Number of Lines";
	private static final String NUM_OBSERVATIONS_COLUMN_ID = "Number of Observations";
	private static final String CLASS_COLUMN_ID = "Class";
	private static final String CONDITION_COLUMN_ID = "Condition";
	private static final String LIMITS_COLUMN_ID = "Limits";
	private static final String PRIORITY_COLUMN_ID = "Priority";
	public static final String TRAIT_BUTTON_ID = "CharacterTraitsSection Trait Button ID";
	
	private List<Integer> environmentIds = null;
	private List<Integer> selectedTraits = null;
	private List<Field> fieldsToValidate = new ArrayList<Field>();
	
	private Window parentWindow;
	private Label lblSectionTitle;
	private Table traitsTable;

	private List<CategoricalTraitInfo> categoricalValueObjects;
	private int categoricalTraitCount;
	private boolean emptyMessageShown = false;
	private List<CategoricalTraitFilter> filters;
	
	@Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
	private CrossStudyDataManager crossStudyDataManager;
	
	public CategoricalVariatesSection(List<Integer> environmentIds, Window parentWindow){
		super();
		this.environmentIds = environmentIds;
		this.parentWindow = parentWindow;
	}
	
	public CategoricalVariatesSection(List<Integer> environmentIds, List<Integer> selectedTraits, Window parentWindow) {
		super();
		this.environmentIds = environmentIds;
		this.selectedTraits = selectedTraits;
		this.parentWindow = parentWindow;
	}

	private void initializeComponents(){
		
		if(this.environmentIds != null && !this.environmentIds.isEmpty()){
			try{
				categoricalValueObjects = crossStudyDataManager.getTraitsForCategoricalVariates(environmentIds);
				if(selectedTraits != null) {
					categoricalValueObjects = filterUnwantedTraitsFromResults(categoricalValueObjects, selectedTraits);
				}
			} catch(MiddlewareQueryException ex){
				LOG.error("Error with getting categorical variate info given environment ids: " + this.environmentIds.toString(), ex);
				MessageNotifier.showError(parentWindow, "Database Error!", "Error with getting categorical variate info given environment ids. "
						+ messageSource.getMessage(Message.ERROR_REPORT_TO), Notification.POSITION_CENTERED);
			}
		}
		
		lblSectionTitle = new Label(messageSource.getMessage(Message.CATEGORICAL_TRAITS_SECTION_TITLE));
		
		traitsTable = new Table();
		traitsTable.setImmediate(true);
		traitsTable.setColumnCollapsingAllowed(true);
		traitsTable.setColumnReorderingAllowed(true);
	
		traitsTable.addContainerProperty(TRAIT_COLUMN_ID, Button.class, null);
		traitsTable.addContainerProperty(NUM_LOCATIONS_COLUMN_ID, Integer.class, null);
		traitsTable.addContainerProperty(NUM_LINES_COLUMN_ID, Integer.class, null);
		traitsTable.addContainerProperty(NUM_OBSERVATIONS_COLUMN_ID, Integer.class, null);
		if(categoricalValueObjects!=null){
			for(int i=0;i<getMaxCategoryValueCount(categoricalValueObjects);i++){
				traitsTable.addContainerProperty(CLASS_COLUMN_ID+i, String.class, null);
			}
		}
		traitsTable.addContainerProperty(CONDITION_COLUMN_ID, ComboBox.class, null);
		traitsTable.addContainerProperty(LIMITS_COLUMN_ID, TextField.class, null);
		traitsTable.addContainerProperty(PRIORITY_COLUMN_ID, ComboBox.class, null);
		
		
		
		traitsTable.setColumnHeader(TRAIT_COLUMN_ID, messageSource.getMessage(Message.HEAD_TO_HEAD_TRAIT)); // Trait
		traitsTable.setColumnHeader(NUM_LOCATIONS_COLUMN_ID, messageSource.getMessage(Message.NUMBER_OF_LOCATIONS)); // # of Locations
		traitsTable.setColumnHeader(NUM_LINES_COLUMN_ID, messageSource.getMessage(Message.NUMBER_OF_LINES)); // # of Lines
		traitsTable.setColumnHeader(NUM_OBSERVATIONS_COLUMN_ID, messageSource.getMessage(Message.NUMBER_OF_OBSERVATIONS)); // # of Observations
		if(categoricalValueObjects!=null){
			for(int i=0;i<getMaxCategoryValueCount(categoricalValueObjects);i++){
				traitsTable.setColumnHeader(CLASS_COLUMN_ID+i, messageSource.getMessage(Message.CLASS)+" "+(i+1));
			}
		}		
		traitsTable.setColumnHeader(CONDITION_COLUMN_ID, messageSource.getMessage(Message.CONDITION_HEADER)); // Condition
		traitsTable.setColumnHeader(LIMITS_COLUMN_ID, messageSource.getMessage(Message.LIMITS)); // Limits
		traitsTable.setColumnHeader(PRIORITY_COLUMN_ID, messageSource.getMessage(Message.PRIORITY)); // Priority
		
		
		
		traitsTable.setColumnWidth(TRAIT_COLUMN_ID, 120);
		traitsTable.setColumnWidth(NUM_LOCATIONS_COLUMN_ID, 120);
		traitsTable.setColumnWidth(NUM_LINES_COLUMN_ID, 120);
		traitsTable.setColumnWidth(NUM_OBSERVATIONS_COLUMN_ID, 120);
		if(categoricalValueObjects!=null){
			for(int i=0;i<getMaxCategoryValueCount(categoricalValueObjects);i++){
				traitsTable.setColumnWidth(CLASS_COLUMN_ID+i, 100);
			}
		}
		traitsTable.setColumnWidth(CONDITION_COLUMN_ID, 200);
		traitsTable.setColumnWidth(LIMITS_COLUMN_ID, 200);
		traitsTable.setColumnWidth(PRIORITY_COLUMN_ID, 200);
		
		setWidth((1200+(getMaxCategoryValueCount(categoricalValueObjects)*120))+"px");
	}

	private void initializeLayout(){
		setMargin(true);
		setSpacing(true);
		setWidth("995px");
		addComponent(lblSectionTitle);
		addComponent(traitsTable);
		traitsTable.setHeight("360px");
		traitsTable.setWidth("960px");
	}
	
	
	private int getMaxCategoryValueCount(List<CategoricalTraitInfo> categoricalValueObjects){
		int max=0;
		if(categoricalValueObjects!=null){
			for(int i=0;i<categoricalValueObjects.size();i++){
				int count = categoricalValueObjects.get(i).getValues().size();
				if(count > max)
					max = count;
			}
		}
		return max;
	}

	
	private void populateTraitsTable(){
		String limitsRequiredMessage = MessageFormat.format(messageSource.getMessage(Message.FIELD_IS_REQUIRED), 
                messageSource.getMessage(Message.LIMITS));
		
		if(this.environmentIds != null && !this.environmentIds.isEmpty()){
			
			if(categoricalValueObjects != null){
				categoricalTraitCount = categoricalValueObjects.size();
				if(categoricalValueObjects.isEmpty()){
					return;
				}
				
				for(CategoricalTraitInfo traitInfo : categoricalValueObjects){
					
					Button traitNameLink = new Button(traitInfo.getName());
					traitNameLink.setImmediate(true);
					traitNameLink.setStyleName(Reindeer.BUTTON_LINK);
					traitNameLink.setData(TRAIT_BUTTON_ID);
					traitNameLink.addListener(new AdaptedGermplasmButtonClickListener(this,traitInfo.getId(), traitInfo.getName(), "Categorical Variate", this.environmentIds));
					
					ComboBox conditionComboBox = CrossStudyUtil.getCategoricalVariatesComboBox();
					conditionComboBox.setEnabled(true);
					
					ComboBox priorityComboBox = CrossStudyUtil.getTraitWeightsComboBox();
					TextField txtLimits = new TextField();
					txtLimits.setEnabled(false);
					txtLimits.setImmediate(true);
					txtLimits.setRequired(true);
					txtLimits.setRequiredError(limitsRequiredMessage);
					txtLimits.addValidator(new CategoricalTraitLimitsValidator(conditionComboBox, traitInfo.getValues()));
					this.fieldsToValidate.add(txtLimits);
					txtLimits.setEnabled(false);
					
					Object[] itemObj = new Object[traitsTable.getColumnHeaders().length];

					itemObj[0] = traitNameLink;
					itemObj[1] = traitInfo.getLocationCount();
					itemObj[2] = traitInfo.getGermplasmCount();
					itemObj[3] = traitInfo.getObservationCount();

					int currentColumn = 4;
					for(int currentValueIndex=0;currentValueIndex<traitInfo.getValues().size();currentColumn++,currentValueIndex++){
						itemObj[currentColumn] = traitInfo.getValues().get(currentValueIndex).getName() + " (" + traitInfo.getValues().get(currentValueIndex).getCount() + ")"; 
					}
					
					//for cases wherein not all column count is equal
					for(;currentColumn<traitsTable.getColumnHeaders().length-3;currentColumn++){
						itemObj[currentColumn] = "";
					}
					
					itemObj[traitsTable.getColumnHeaders().length - 3] = conditionComboBox;
					itemObj[traitsTable.getColumnHeaders().length - 2] = txtLimits;
					itemObj[traitsTable.getColumnHeaders().length - 1] = priorityComboBox;
					
					traitsTable.addItem(itemObj, traitInfo);
					
					conditionComboBox.addListener(new AdaptedGermplasmValueChangeListener(this, txtLimits, priorityComboBox));
				}
			}
		}
	}
	
	// TODO : Rebecca is not happy with public/private method ordering
	// TODO : warning - On2 may need to revisit for performance
	private List<CategoricalTraitInfo> filterUnwantedTraitsFromResults(List<CategoricalTraitInfo> categoricalTraitInfos, List<Integer> desiredTraits) {
		List<CategoricalTraitInfo> filteredTraits = new ArrayList<CategoricalTraitInfo>();
		for (CategoricalTraitInfo cto : categoricalTraitInfos) {
			for (Integer traitId : desiredTraits) {
				if(cto.getId() == traitId.intValue()) {
					filteredTraits.add(cto);
				}
			}
		}
		return filteredTraits;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		setSpacing(true);
    	setMargin(true);
    	//setHeight("500px");
    	
		initializeComponents();
		initializeLayout();
		populateTraitsTable();
	}
	
	@Override
	public void updateLabels() {
		messageSource.setCaption(lblSectionTitle, Message.CATEGORICAL_TRAITS_SECTION_TITLE);
	}
	
	public void showEmptyTraitsMessage() {
		if(!emptyMessageShown && categoricalTraitCount == 0){
			MessageNotifier.showMessage(parentWindow, "Information", "There were no categorical traits observed in the environments you have selected."
					, 3000, Notification.POSITION_CENTERED);
			emptyMessageShown = true;
		}
	}

	public void showTraitObservationClickAction(Integer traitId, String variateType, String traitName,
			List<Integer> envIds) {
		Window parentWindow = this.getWindow();
		ViewTraitObservationsDialog viewTraitDialog = new ViewTraitObservationsDialog(this, parentWindow, variateType, traitId, traitName, envIds);
		viewTraitDialog.addStyleName(Reindeer.WINDOW_LIGHT);
		parentWindow.addWindow(viewTraitDialog);
	}
	
	// perform validation on limits textfields
	public boolean allFieldsValid(){
		try {
			for (Field field : this.fieldsToValidate){
				if (field.isEnabled())
					field.validate();
			}
			
			return true;
			
		} catch (InvalidValueException e) {
			MessageNotifier.showWarning(getWindow(), 
					this.messageSource.getMessage(Message.INCORRECT_LIMITS_VALUE), 
					e.getMessage(), Notification.POSITION_CENTERED);
			return false;
		}
	
	}

	@SuppressWarnings("unchecked")
	public List<CategoricalTraitFilter> getFilters(){
		this.filters = new ArrayList<CategoricalTraitFilter>();
		
		Collection<CategoricalTraitInfo> traitInfoObjects = (Collection<CategoricalTraitInfo>) this.traitsTable.getItemIds();
		for(CategoricalTraitInfo traitInfo : traitInfoObjects){
			Item tableRow = this.traitsTable.getItem(traitInfo);
			
			ComboBox conditionComboBox = (ComboBox) tableRow.getItemProperty(CONDITION_COLUMN_ID).getValue();
			CategoricalVariatesCondition condition = (CategoricalVariatesCondition) conditionComboBox.getValue();
			
			ComboBox priorityComboBox = (ComboBox) tableRow.getItemProperty(PRIORITY_COLUMN_ID).getValue();
			TraitWeight priority = (TraitWeight) priorityComboBox.getValue();
			
			TextField limitsField = (TextField) tableRow.getItemProperty(LIMITS_COLUMN_ID).getValue();
			String limitsString = limitsField.getValue().toString();
			
			if(condition != CategoricalVariatesCondition.DROP_TRAIT && priority != TraitWeight.IGNORED){
				if(condition == CategoricalVariatesCondition.KEEP_ALL){
					CategoricalTraitFilter filter = new CategoricalTraitFilter(traitInfo, condition, new ArrayList<String>(), priority);
					this.filters.add(filter);
				} else{
					if(limitsString != null && limitsString.length() > 0){
						StringTokenizer tokenizer = new StringTokenizer(limitsString, ",");
						List<String> givenLimits = new ArrayList<String>();
						
						while(tokenizer.hasMoreTokens()){
							String limit = tokenizer.nextToken().trim();
							givenLimits.add(limit);
						}
						
						CategoricalTraitFilter filter = new CategoricalTraitFilter(traitInfo, condition, givenLimits, priority);
						this.filters.add(filter);
					}
				}
			}
		}
		
		return this.filters;
	}
	
	/*
	 * If at least one trait is NOT dropped, allow to proceed
	 */
	public boolean allTraitsDropped(){
		if (this.filters == null){
			this.filters = getFilters();
		}
		if (!this.filters.isEmpty()){
			for (CategoricalTraitFilter filter: this.filters){
				if (!CategoricalVariatesCondition.DROP_TRAIT.equals(filter.getCondition())){
					return false;
				}
			}
		}
		return true;
	}

}