
package org.generationcp.breeding.manager.listmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.generationcp.breeding.manager.listmanager.api.FillColumnSource;
import org.generationcp.breeding.manager.listmanager.util.FillWithOption;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AddedColumnsMapperTest {

	private static final int ATTRIBUTE_TYPE_ID2 = 2;
	private static final int ATTRIBUTE_TYPE_ID1 = 1;
	private static final String ATTRIBUTE_TYPE_NAME2 = "New Passport Type";
	private static final String ATTRIBUTE_TYPE_NAME1 = "Ipstat";
	private static final String ATTRIBUTE_TYPE_CODE2 = "NEW_PAZZPORT";
	private static final String ATTRIBUTE_TYPE_CODE1 = "Ipstat";

	private static final String[] STANDARD_COLUMNS =
			{ColumnLabels.GID.getName(), ColumnLabels.DESIGNATION.getName(), ColumnLabels.SEED_SOURCE.getName(),
					ColumnLabels.ENTRY_CODE.getName(), ColumnLabels.GROUP_ID.getName(), ColumnLabels.STOCKID.getName()};

	private static final List<String> ADDED_COLUMNS = Arrays.asList(ColumnLabels.PREFERRED_NAME.getName(),
			ColumnLabels.PREFERRED_ID.getName(), ColumnLabels.GERMPLASM_DATE.getName(), ColumnLabels.GERMPLASM_LOCATION.getName(),
			ColumnLabels.BREEDING_METHOD_NAME.getName(), ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName(),
			ColumnLabels.BREEDING_METHOD_NUMBER.getName(), ColumnLabels.BREEDING_METHOD_GROUP.getName(),
			ColumnLabels.CROSS_FEMALE_GID.getName(), ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName(),
			ColumnLabels.CROSS_MALE_GID.getName(), ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName());

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private GermplasmColumnValuesGenerator valuesGenerator;

	@Mock
	private FillColumnSource fillColumnSource;

	@InjectMocks
	private AddedColumnsMapper addedColumnsMapper;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.addedColumnsMapper.setValuesGenerator(this.valuesGenerator);
		this.addedColumnsMapper.setGermplasmDataManager(this.germplasmDataManager);
		Mockito.doReturn(this.getAttributeTypes()).when(this.germplasmDataManager).getAllAttributesTypes();
	}

	@Test
	public void testGenerateValuesForAddedColumnsWhenNoAddedColumns() {
		this.addedColumnsMapper.generateValuesForAddedColumns(STANDARD_COLUMNS);

		Mockito.verifyZeroInteractions(this.valuesGenerator);
		Mockito.verifyZeroInteractions(this.germplasmDataManager);
	}

	@Test
	public void testGenerateValuesForAddedColumnsWhenColumnsAdded() {
		final List<String> columns = new ArrayList<>(Arrays.asList(STANDARD_COLUMNS));
		columns.addAll(ADDED_COLUMNS);
		this.addedColumnsMapper.generateValuesForAddedColumns(columns.toArray());
		
		Mockito.verify(this.valuesGenerator).setPreferredNameColumnValues(ColumnLabels.PREFERRED_NAME.getName());
		Mockito.verify(this.valuesGenerator).setPreferredIdColumnValues(ColumnLabels.PREFERRED_ID.getName());
		Mockito.verify(this.valuesGenerator).setGermplasmDateColumnValues(ColumnLabels.GERMPLASM_DATE.getName());
		Mockito.verify(this.valuesGenerator).setLocationNameColumnValues(ColumnLabels.GERMPLASM_LOCATION.getName());
		Mockito.verify(this.valuesGenerator).setMethodInfoColumnValues(ColumnLabels.BREEDING_METHOD_NAME.getName(),
				FillWithOption.FILL_WITH_BREEDING_METHOD_NAME);
		Mockito.verify(this.valuesGenerator).setMethodInfoColumnValues(ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName(),
				FillWithOption.FILL_WITH_BREEDING_METHOD_ABBREV);
		Mockito.verify(this.valuesGenerator).setMethodInfoColumnValues(ColumnLabels.BREEDING_METHOD_NUMBER.getName(),
				FillWithOption.FILL_WITH_BREEDING_METHOD_NUMBER);
		Mockito.verify(this.valuesGenerator).setMethodInfoColumnValues(ColumnLabels.BREEDING_METHOD_GROUP.getName(),
				FillWithOption.FILL_WITH_BREEDING_METHOD_GROUP);
		Mockito.verify(this.valuesGenerator).setCrossFemaleInfoColumnValues(ColumnLabels.CROSS_FEMALE_GID.getName(),
				FillWithOption.FILL_WITH_CROSS_FEMALE_GID);
		Mockito.verify(this.valuesGenerator).setCrossFemaleInfoColumnValues(ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName(),
				FillWithOption.FILL_WITH_CROSS_FEMALE_NAME);
		Mockito.verify(this.valuesGenerator).setCrossMaleGIDColumnValues(ColumnLabels.CROSS_MALE_GID.getName());
		Mockito.verify(this.valuesGenerator).setCrossMalePrefNameColumnValues(ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName());
		Mockito.verifyZeroInteractions(this.germplasmDataManager);
	}
	
	@Test
	public void testGenerateValuesForAddedColumnsWhenAtributeColumnsAdded() {
		final List<String> columns = new ArrayList<>(Arrays.asList(STANDARD_COLUMNS));
		columns.add(ATTRIBUTE_TYPE_CODE1);
		columns.add(ATTRIBUTE_TYPE_CODE2);
		
		this.addedColumnsMapper.generateValuesForAddedColumns(columns.toArray());
		
		// Check that attribute type columns are capitalized
		Mockito.verify(this.valuesGenerator).fillWithAttribute(ATTRIBUTE_TYPE_ID1, ATTRIBUTE_TYPE_CODE1.toUpperCase());
		Mockito.verify(this.valuesGenerator).fillWithAttribute(ATTRIBUTE_TYPE_ID2, ATTRIBUTE_TYPE_CODE2.toUpperCase());
	}
	
	@Test
	public void testGetAttributeTypesMap() {
		final List<UserDefinedField> expectedAttributeTypes = this.getAttributeTypes();
		final Map<String, Integer> attributeTypesMap = this.addedColumnsMapper.getAllAttributeTypesMap();
		for (final UserDefinedField attributeType : expectedAttributeTypes) {
			final String fieldCode = attributeType.getFcode().toUpperCase();
			Assert.assertTrue(attributeTypesMap.containsKey(fieldCode));
			Assert.assertEquals(attributeType.getFldno(), attributeTypesMap.get(fieldCode));
		}
	}
	
	private List<UserDefinedField> getAttributeTypes() {
		final UserDefinedField attributeType1 = new UserDefinedField(AddedColumnsMapperTest.ATTRIBUTE_TYPE_ID1);
		attributeType1.setFname(AddedColumnsMapperTest.ATTRIBUTE_TYPE_NAME1);
		attributeType1.setFcode(AddedColumnsMapperTest.ATTRIBUTE_TYPE_CODE1);
		final UserDefinedField attributeType2 = new UserDefinedField(AddedColumnsMapperTest.ATTRIBUTE_TYPE_ID2);
		attributeType2.setFname(AddedColumnsMapperTest.ATTRIBUTE_TYPE_NAME2);
		attributeType2.setFcode(AddedColumnsMapperTest.ATTRIBUTE_TYPE_CODE2);
		final UserDefinedField attributeType3 = new UserDefinedField(3);
		attributeType3.setFname("Grower");
		attributeType3.setFcode("Grow");
		return Arrays.asList(attributeType1, attributeType2, attributeType3);
	}

}
