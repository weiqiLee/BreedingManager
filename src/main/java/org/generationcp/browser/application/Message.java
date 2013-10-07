package org.generationcp.browser.application;


public enum Message {
    
     WELCOME_TITLE
     ,WELCOME_LABEL
     ,QUESTION_LABEL
     ,GERMPLASM_BUTTON_LABEL
     ,GERMPLASM_LIST_BUTTON_LABEL
     ,STUDY_BUTTON_LABEL
     ,GERMPLASM_BY_PHENO_LABEL
     ,RETRIEVE_GERMPLASM_BY_PHENO_LABEL

     ,GERMPLASM_BY_PHENO_TITLE
     ,GERMPLASM_BROWSER_TITLE
     ,STUDY_BROWSER_TITLE
     ,SEARCH_GERMPLASM_BY_PHENO_TITLE
     ,GERMPLASM_LIST_BROWSER_TITLE

     ,EXPORT_TO_CSV_LABEL
     ,EXPORT_TO_EXCEL_LABEL
     ,OPEN_TABLE_VIEWER_LABEL
     ,SAVE_FIELDBOOK_EXCEL_FILE_LABEL
     ,CANCEL_SAVE_FIELDBOOK_EXCEL_FILE_LABEL
     ,FILE_NAME_LABEL
     ,BROWSE_LABEL
     ,INPUT_FILE_NAME_TEXT
     ,DESTINATION_FOLDER_TEXT
     ,SAVE_FIELDBOOK_EXCEL_FILE_SUCCESSFUL_TEXT
     ,REPORT_TITLE1_TEXT
     ,REPORT_TITLE2_TEXT

     ,GERMPLASM_LIST_DETAILS_TAB
     ,GERMPLASM_LIST_DATA_TAB
     ,GERMPLASM_LIST_SEED_INVENTORY_TAB
     ,STUDY_DETAILS_TEXT
     ,GERMPLASM_DETAILS_TEXT
     ,GERMPLASMLIST_DETAILS_TEXT
     ,FACTORS_TEXT
     ,VARIATES_TEXT
     ,EFFECTS_TEXT
     ,DATASETS_TEXT
     ,DB_LOCAL_TEXT
     ,DB_CENTRAL_TEXT

     ,DATE_LABEL
     ,NAME_LABEL
     ,METHOD_LABEL
     ,LOCATION_LABEL
     ,TYPEDESC_LABEL
     ,TYPE_LABEL
     ,COUNTRY_LABEL
     ,SEASON_LABEL
 
     ,TITLE_LABEL
     ,OBJECTIVE_LABEL
     ,GID_LABEL
     ,PREFNAME_LABEL
     ,CREATION_METHOD_LABEL
     ,CREATION_DATE_LABEL
     ,REFERENCE_LABEL
     ,START_DATE_LABEL
     ,END_DATE_LABEL
     ,NO_DATASETS_RETRIEVED_LABEL

     ,STUDY_EFFECT_HEADER
     ,DATASET_TEXT
     ,EFFECT_TEXT
     ,CLICK_DATASET_TO_VIEW_TEXT
     ,DATASET_OF_TEXT
     ,REPRESENTATION_TEXT
     ,FACT_STRING
     ,ID_HEADER
     ,NAME_HEADER
     ,DESCRIPTION_HEADER
     ,PROPERTY_HEADER
     ,SCALE_HEADER
     ,LOCATION_HEADER
     ,ENTITY_ID_HEADER
     ,LOT_BALANCE_HEADER
     ,LOT_COMMENT_HEADER
     ,METHOD_HEADER
     ,DATATYPE_HEADER
     ,DATE_HEADER
     ,REFRESH_LABEL
     ,STUDY_DETAILS_LABEL
     ,GERMPLASM_LIST_DETAILS_LABEL
     ,LISTDATA_GID_HEADER
     ,LISTDATA_ENTRY_ID_HEADER
     ,LISTDATA_ENTRY_CODE_HEADER
     ,LISTDATA_SEEDSOURCE_HEADER
     ,LISTDATA_DESIGNATION_HEADER
     ,LISTDATA_GROUPNAME_HEADER
     ,LISTDATA_STATUS_HEADER
     ,NO_LISTDATA_RETRIEVED_LABEL
     ,CHARACTERISTICS_LABEL
     ,NAMES_LABEL
     ,ATTRIBUTES_LABEL
     ,GENERATION_HISTORY_LABEL
     ,PEDIGREE_TREE_LABEL
     ,LISTS_LABEL
     ,CLICK_TO_VIEW_GERMPLASM_DETAILS
     ,CLICK_TO_VIEW_STUDY_DETAILS
     ,SEARCH_FOR_LABEL
     ,SEARCH_LABEL
     ,SEARCH_RESULT_LABEL
     ,CLEAR_LABEL
     ,ADD_CRITERIA_LABEL
     ,DELETE_LABEL
     ,DELETE_ALL_LABEL
     ,YOU_CAN_DELETE_THE_CURRENTLY_SELECTED_CRITERIA_DESC
     ,YOU_CAN_DELETE_ALL_THE_CRITERIA_DESC
     ,STEP1_LABEL
     ,STEP2_LABEL
     ,STEP3_LABEL
     ,STEP4_LABEL
     ,STEP5_LABEL
     ,FINAL_STEP_LABEL
     ,SELECT_A_VALUE_FROM_THE_OPTIONS_BELOW_LABEL
     ,TO_ENTER_A_RANGE_OF_VALUES_FOLLOW_THIS_EXAMPLE_LABEL 
     ,SAVE_GERMPLASM_LIST_BUTTON_LABEL
     ,CLOSE_ALL_GERMPLASM_DETAIL_TAB_LABEL
     ,LIST_NAME_LABEL
     ,SAVE_GERMPLASM_LIST_WINDOW_LABEL 
     ,SAVE_LABEL 
     ,CANCEL_LABEL
     ,OK_LABEL
     ,GROUP_RELATIVES_LABEL 
     ,MANAGEMENT_NEIGHBORS_LABEL 
     ,INVENTORY_INFORMATION_LABEL 
     ,DERIVATIVE_NEIGHBORHOOD_LABEL  
     ,MAINTENANCE_NEIGHBORHOOD_LABEL  
     ,DESCRIPTION_LABEL
     ,STATUS_LABEL
     ,LIST_OWNER_LABEL
     ,HIDDEN_LABEL
     ,LOCKED_LABEL
     ,FINAL_LABEL
     ,NUMBER_OF_STEPS_FORWARD_LABEL 
     ,NUMBER_OF_STEPS_BACKWARD_LABEL  
     ,DISPLAY_BUTTON_LABEL
     ,NULL_STUDY_DETAILS
     ,NULL_GERMPLASM_DETAILS
     ,NULL_GERMPLASMLIST_DETAILS
     ,GERMPLASM_STUDY_INFORMATION_LABEL
     ,STUDY_ID_LABEL
     ,STUDY_NAME_LABEL
     ,NUMBER_OF_ROWS
     ,EXACT_STUDY_NAME_TEXT
     ,PEDIGREE_LEVEL_LABEL
     ,NO_LISTDATA_INVENTORY_RETRIEVED_LABEL
     ,COPY_TO_NEW_LIST_WINDOW_LABEL
     ,SAVE_GERMPLASMS_TO_NEW_LIST_LABEL
     ,VALUE_HEADER

     // ERROR NOTIFICATION MESSAGES
     ,EMPTY_STRING
     ,ERROR_DATABASE
     ,ERROR_INTERNAL 
     ,ERROR_PLEASE_CONTACT_ADMINISTRATOR 
     ,ERROR_CONFIGURATION 
     ,ERROR_IN_CREATING_SEARCH_GERMPLASM_BY_PHENO_TAB 
     ,ERROR_IN_ADDING_GERMPLASM_LIST_NAME_AND_DATA 
     ,ERROR_IN_DISPLAYING_NEW_GERMPLASM_DETAIL_TAB 
     ,ERROR_IN_DISPLAYING_RQUESTED_DETAIL 
     ,ERROR_IN_DISPLAYING_GERMPLASM_DETAIL_TAB 
     ,ERROR_INVALID_INPUT_MUST_BE_NUMERIC
     ,ERROR_INVALID_NUMBER_FORMAT_MUST_BE_NUMERIC 
     ,ERROR_IN_GENERATING_PEDIGREE_TREE 
     ,ERROR_IN_SEARCH 
     ,ERROR_IN_DISPLAYING_DETAILS 
     ,ERROR_IN_DISPLAYING_TRAIT_TABLE 
     ,ERROR_IN_GETTING_VARIABLES_OF_DATASET  
     ,ERROR_IN_GETTING_TRAITS
     ,ERROR_IN_SHOWING_FACTOR_DETAILS
     ,ERROR_IN_SHOWING_VARIATE_DETAILS
     ,ERROR_IN_GETTING_STUDY_DETAIL_BY_ID 
     ,ERROR_IN_GETTING_REPRESENTATION_BY_STUDY_ID 
     ,ERROR_IN_GETTING_STUDY_FACTOR 
     ,ERROR_IN_GETTING_GERMPLASM_IDS_BY_PHENO_DATA
     ,ERROR_IN_GETTING_TOP_LEVEL_STUDIES 
     ,ERROR_IN_GETTING_TOP_LEVEL_FOLDERS
     ,ERROR_IN_NUMBER_FORMAT 
     ,ERROR_IN_CREATING_STUDY_INFO_TAB 
     ,ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID 
     ,ERROR_IN_GETTING_GERMPLASM_LISTS_BY_PARENT_FOLDER_ID
     ,ERROR_IN_GETTING_STUDY_VARIATE
     ,ERROR_IN_GETTING_VALUES_BY_SCALE_ID 
     ,ERROR_INVALID_FORMAT 
     ,ERROR_INPUT 
     ,ERROR_NO_SELECTED_TRAIT_SCALE_METHOD
     ,ERROR_IN_GETTING_GERMPLASM_DETAILS 
     ,ERROR_WITH_GERMPLASM_SEARCH_RESULT
     ,ERROR_IN_GETTING_GERMPLASM_LIST_BY_ID
     ,ERROR_IN_GETTING_NAMES_BY_GERMPLASM_ID
     ,ERROR_IN_GETTING_ATTRIBUTES_BY_GERMPLASM_ID
     ,ERROR_IN_GETTING_GENERATION_HISTORY
     ,ERROR_IN_GETTING_GERMPLASM_LIST_RESULT_BY_PREFERRED_NAME
     ,ERROR_IN_GETTING_PREFERRED_NAME_BY_GERMPLASM_ID
     ,ERROR_IN_GETTING_REPORT_ON_LOTS_BY_ENTITY_TYPE_AND_ENTITY_ID
     ,ERROR_IN_ADDING_GERMPLASM_LIST
     ,ERROR_IN_COUNTING_TRAITS
     ,ERROR_IN_GETTING_SCALES_BY_TRAIT_ID
     ,ERROR_IN_GETTING_TRAIT_METHOD
     ,ERROR_IN_GETTING_DISCRETE_VALUES_OF_SCALE
     ,ERROR_NULL_TABLE
     ,ERROR_IN_GETTING_DERIVATIVE_NEIGHBORHOOD
     ,ERROR_IN_CREATING_STUDY_DETAILS_WINDOW
     ,ERROR_IN_CREATING_GERMPLASM_DETAILS_WINDOW
     ,ERROR_IN_CREATING_GERMPLASMLIST_DETAILS_WINDOW
     ,ERROR_IN_SAVING_GERMPLASMLIST_DATA_SORTING
     ,ERROR_IN_GERMPLASM_STUDY_INFORMATION_BY_GERMPLASM_ID
     ,ERROR_TEXT
     ,ERROR_INVALID_DIRECTORY
     ,ERROR_INVALID_DESTINATION_FOLDER_TEXT
     ,ERROR_PLEASE_INPUT_FILE_NAME_TEXT
     
     ,ERROR_DAY_WITHOUT_MONTH_YEAR
     ,ERROR_MONTH_WITHOUT_YEAR
     ,DATE_YEAR_FIELD_DESCRIPTION
     ,ERROR_YEAR_MUST_BE_NUMBER
     ,ERROR_YEAR_FORMAT
     ,ERROR_MONTH_MUST_BE_NUMBER
     ,ERROR_MONTH_FORMAT
     ,ERROR_DAY_MUST_BE_NUMBER
     ,ERROR_DAY_FORMAT
     ,ERROR_DAY_OUT_OF_RANGE
     ,ERROR_MONTH_OUT_OF_RANGE
     ,NO_STUDIES_FOUND
     ,STUDY_NAME
     ,INCLUDE_DERIVATIVE_LINES
     ,ERROR_LIST_ENTRIES_MUST_BE_SELECTED
     ,ERROR_GERMPLASM_MUST_BE_SELECTED

     // NOTIFICATION MESSAGES
     ,SUCCESS
     ,UNSUCCESSFUL
     ,SAVE_GERMPLASMLIST_DATA_SORTING_SUCCESS
     ,INVALID_DELETING_LIST_ENTRIES
     ,INVALID_USER_DELETING_LIST_ENTRIES
     ,SAVE_GERMPLASMLIST_DATA_COPY_TO_NEW_LIST_SUCCESS
     ,SAVE_GERMPLASMLIST_DATA_COPY_TO_NEW_LIST_FAILED
     ,SAVE_GERMPLASMLIST_DATA_COPY_TO_EXISTING_LIST_FAILED
     ,SAVE_GERMPLASMS_TO_NEW_LIST_SUCCESS
     
     
     //TEST MESSAGES
     ,TEST_BUTTON
     
     //Head to Head Main Cross Study
     ,SPECIFY_ENTRIES
     ,SELECT_TRAITS
     ,SELECT_ENVIRONMENTS
     ,DISPLAY_RESULTS
     
     ,SELECT_TEST_STANDARD_COMPARE
     ,SPECIFY_SINGLE_TEST_ENTRY
     ,SPECIFY_SINGLE_STANDARD_ENTRY
     ,SPECIFY_TEST_GERMPLASM_LIST_ENTRY
     ,SPECIFY_STANDARD_GERMPLASM_LIST_ENTRY
     
     ,HEAD_TO_HEAD_SEARCH_GERMPLASM
     ,HEAD_TO_HEAD_BROWSE_LIST
     ,SELECTED_LIST_LABEL
     ,LIST_ENTRIES_LABEL
     ,CLOSE_SCREEN_LABEL
     ,ADD_LIST_ENTRY

     //Table Viewer
     ,TABLE_VIEWER_CAPTION
     ,CONFIRM_DIALOG_MESSAGE_OPEN_TABLE_VIEWER
     ,TABLE_VIEWER_OK_LABEL
     
     ,HEAD_TO_HEAD_TAG
     ,HEAD_TO_HEAD_TRAIT
     ,HEAD_TO_HEAD_NO_OF_ENVS
     ,HEAD_TO_HEAD_DIRECTION
     ,HEAD_TO_HEAD_INCREASING
     ,HEAD_TO_HEAD_DECREASING
     ,HEAD_TO_HEAD_SELECT_TRAITS
     ,HEAD_TO_HEAD_SELECT_TRAITS_REMINDER
     ,CONDITION_HEADER
     ,NUMBER_OF_ENVIRONMENTS_HEADER
     ,DONE
     ,ADD_ENVT_CONDITION_COLUMNS_LABEL
     ,SELECTED_ENVT_CONDITIONS_WILL_BE_ADDED
     
     //Query for Adapted Germplasm
     ,SPECIFY_WEIGH_ENVIRONMENT
     ,SETUP_TRAIT_FILTER
     ,ENVIRONMENT_FILTER
     ,ENVIRONMENT_FILTER_VAL
     ,FILTER_BY_LOCATION
     ,FILTER_BY_STUDY
     ,ADD_ENV_CONDITION
     ,CHOOSE_ENVIRONMENTS
     ,NO_OF_SELECTED_ENVIRONMENT
     ,NEXT
     ,LINE_BY_LOCATION_FOR_NUMERIC_VARIATE
     ,LINE_BY_LOCATION_FOR_TRAIT
     ,OBSERVATION_NO
     ,LINE_NO
     ,LINE_GID
     ,LINE_DESIGNATION
     ,LOCATION_1
     ,LOCATION_2
}