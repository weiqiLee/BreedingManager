
package org.generationcp.breeding.manager.listmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.breeding.manager.listmanager.api.FillColumnSource;
import org.generationcp.breeding.manager.listmanager.util.FillWithOption;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class GermplasmColumnValuesGeneratorTest {

	private static final List<Integer> ITEMS_LIST = Arrays.asList(1, 2, 3, 4, 5);

	private static final List<Integer> GID_LIST = Arrays.asList(101, 102, 103, 104, 105);

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private PedigreeService pedigreeService;

	@Mock
	private FillColumnSource fillColumnSource;

	@InjectMocks
	private GermplasmColumnValuesGenerator valuesGenerator;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.valuesGenerator.setGermplasmDataManager(this.germplasmDataManager);
		this.valuesGenerator.setPedigreeService(this.pedigreeService);

		Mockito.doReturn(GermplasmColumnValuesGeneratorTest.ITEMS_LIST).when(this.fillColumnSource)
				.getItemIdsToProcess();
		Mockito.doReturn(GermplasmColumnValuesGeneratorTest.GID_LIST).when(this.fillColumnSource).getGidsToProcess();
		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			Mockito.doReturn(GermplasmColumnValuesGeneratorTest.GID_LIST.get(i)).when(this.fillColumnSource)
					.getGidForItemId(GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i));
		}
	}

	private Map<Integer, String> generateGIDStringMap(final String prefix) {
		return this.generateGIDStringMap(prefix, GermplasmColumnValuesGeneratorTest.GID_LIST);
	}

	private Map<Integer, String> generateGIDStringMap(final String prefix, final List<Integer> ids) {
		final Map<Integer, String> namesMap = new HashMap<>();
		for (final Integer gid : ids) {
			namesMap.put(gid, prefix + " " + gid);
		}
		return namesMap;
	}

	private Map<Integer, Integer> generateGIDIntegerMap() {
		final Map<Integer, Integer> map = new HashMap<>();
		for (final Integer gid : GermplasmColumnValuesGeneratorTest.GID_LIST) {
			map.put(gid, Integer.valueOf("2017" + gid));
		}
		return map;
	}

	private Map<Integer, Object> generateMethodsMap() {
		final Map<Integer, Object> map = new HashMap<>();
		for (final Integer gid : GermplasmColumnValuesGeneratorTest.GID_LIST) {
			final Method method = new Method();
			method.setMname("HYBRID METHOD " + gid);
			method.setMid(gid);
			method.setMcode("HYB " + gid);
			method.setMgrp("GRP " + gid);
			map.put(gid, method);
		}
		return map;
	}

	private List<Germplasm> generateListofGermplasm(final boolean isDerivative) {
		final List<Germplasm> list = new ArrayList<>();
		for (final Integer gid : GermplasmColumnValuesGeneratorTest.GID_LIST) {
			final Germplasm germplasm = new Germplasm();
			germplasm.setGid(gid);
			if (isDerivative) {
				germplasm.setGnpgs(-1);
				germplasm.setGpid1(0);
				germplasm.setGpid2(0);
			} else {
				germplasm.setGnpgs(2);
				germplasm.setGpid1(gid - 90);
				germplasm.setGpid2(gid - 100);
			}
			list.add(germplasm);
		}
		return list;
	}

	@Test
	public void testSetPreferredIdColumnValues() {
		final Map<Integer, String> namesMap = this.generateGIDStringMap("QWERTY");
		Mockito.doReturn(namesMap).when(this.germplasmDataManager)
				.getPreferredIdsByGIDs(GermplasmColumnValuesGeneratorTest.GID_LIST);

		final String columnName = ColumnLabels.ENTRY_CODE.getName();
		this.valuesGenerator.setPreferredIdColumnValues(columnName);

		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, namesMap.get(gid));
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetPreferredNameColumnValues() {
		final Map<Integer, String> namesMap = this.generateGIDStringMap("ABCD");
		Mockito.doReturn(namesMap).when(this.germplasmDataManager)
				.getPreferredNamesByGids(GermplasmColumnValuesGeneratorTest.GID_LIST);

		final String columnName = ColumnLabels.PREFERRED_NAME.getName();
		this.valuesGenerator.setPreferredNameColumnValues(columnName);

		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, namesMap.get(gid));
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetGermplasmDateColumnValues() {
		final Map<Integer, Integer> datesMap = this.generateGIDIntegerMap();
		Mockito.doReturn(datesMap).when(this.germplasmDataManager)
				.getGermplasmDatesByGids(GermplasmColumnValuesGeneratorTest.GID_LIST);

		final String columnName = ColumnLabels.GERMPLASM_DATE.getName();
		this.valuesGenerator.setGermplasmDateColumnValues(columnName);

		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, datesMap.get(gid));
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetLocationNameColumnValues() {
		final Map<Integer, String> locationsMap = this.generateGIDStringMap("Default Seed Storage");
		Mockito.doReturn(locationsMap).when(this.germplasmDataManager)
				.getLocationNamesByGids(GermplasmColumnValuesGeneratorTest.GID_LIST);

		final String columnName = ColumnLabels.GERMPLASM_LOCATION.getName();
		this.valuesGenerator.setLocationNameColumnValues(columnName);

		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, locationsMap.get(gid));
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetBreedingMethodNameColumnValues() {
		final Map<Integer, Object> locationsMap = this.generateMethodsMap();
		Mockito.doReturn(locationsMap).when(this.germplasmDataManager)
				.getMethodsByGids(GermplasmColumnValuesGeneratorTest.GID_LIST);

		final String columnName = ColumnLabels.BREEDING_METHOD_NAME.getName();
		this.valuesGenerator.setMethodInfoColumnValues(columnName, FillWithOption.FILL_WITH_BREEDING_METHOD_NAME);

		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			final Method method = (Method) locationsMap.get(gid);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, method.getMname());
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetBreedingMethodAbbreviationColumnValues() {
		final Map<Integer, Object> locationsMap = this.generateMethodsMap();
		Mockito.doReturn(locationsMap).when(this.germplasmDataManager)
				.getMethodsByGids(GermplasmColumnValuesGeneratorTest.GID_LIST);

		final String columnName = ColumnLabels.SEED_SOURCE.getName();
		this.valuesGenerator.setMethodInfoColumnValues(columnName, FillWithOption.FILL_WITH_BREEDING_METHOD_ABBREV);

		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			final Method method = (Method) locationsMap.get(gid);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, method.getMcode());
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetBreedingMethodNumberColumnValues() {
		final Map<Integer, Object> locationsMap = this.generateMethodsMap();
		Mockito.doReturn(locationsMap).when(this.germplasmDataManager)
				.getMethodsByGids(GermplasmColumnValuesGeneratorTest.GID_LIST);

		final String columnName = ColumnLabels.ENTRY_CODE.getName();
		this.valuesGenerator.setMethodInfoColumnValues(columnName, FillWithOption.FILL_WITH_BREEDING_METHOD_NUMBER);

		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			final Method method = (Method) locationsMap.get(gid);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, method.getMid().toString());
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetBreedingMethodGroupColumnValues() {
		final Map<Integer, Object> locationsMap = this.generateMethodsMap();
		Mockito.doReturn(locationsMap).when(this.germplasmDataManager)
				.getMethodsByGids(GermplasmColumnValuesGeneratorTest.GID_LIST);

		final String columnName = ColumnLabels.BREEDING_METHOD_GROUP.getName();
		this.valuesGenerator.setMethodInfoColumnValues(columnName, FillWithOption.FILL_WITH_BREEDING_METHOD_GROUP);

		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			final Method method = (Method) locationsMap.get(gid);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, method.getMgrp());
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testFillWithEmpty() {
		final String columnName = ColumnLabels.ENTRY_CODE.getName();
		this.valuesGenerator.fillWithEmpty(columnName);

		for (final Object itemId : GermplasmColumnValuesGeneratorTest.ITEMS_LIST) {
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(itemId, columnName, "");
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testFillWithAttribute() {
		final Integer attributeTypeId = 1001;
		final Map<Integer, String> attributesMap = this.generateGIDStringMap("NOTES ");
		Mockito.doReturn(attributesMap).when(this.germplasmDataManager)
				.getAttributeValuesByTypeAndGIDList(attributeTypeId, GermplasmColumnValuesGeneratorTest.GID_LIST);

		final String columnName = ColumnLabels.ENTRY_CODE.getName();
		this.valuesGenerator.fillWithAttribute(attributeTypeId, columnName);

		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, attributesMap.get(gid));
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}
	
	@Test
	public void testFillWithGermplasmName() {
		final Integer nameTypeId = 1001;
		final Map<Integer, String> namesMap = this.generateGIDStringMap("DRVNM ");
		Mockito.doReturn(namesMap).when(this.germplasmDataManager)
				.getNamesByTypeAndGIDList(nameTypeId, GermplasmColumnValuesGeneratorTest.GID_LIST);

		final String columnName = ColumnLabels.ENTRY_CODE.getName();
		this.valuesGenerator.fillWithGermplasmName(nameTypeId, columnName);

		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, namesMap.get(gid));
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testFillWIthSequenceWithSpaceBetweenPrefixAndCode() {
		final String columnName = ColumnLabels.DESIGNATION.getName();
		final String prefix = "LEAFYNODE";
		final String suffix = "4EV";
		final Integer startNumber = 100;
		final Integer numberofZeros = 5;
		final boolean withSpaceBetweenPrefixAndCode = true;
		final boolean withSpaceBetweenSuffixAndCode = false;

		this.valuesGenerator.fillWithSequence(columnName, prefix, suffix, startNumber, numberofZeros,
				withSpaceBetweenPrefixAndCode, withSpaceBetweenSuffixAndCode);

		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final StringBuilder sb = new StringBuilder(prefix);
			sb.append(" 00");
			sb.append(startNumber + i);
			sb.append(suffix);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, sb.toString());
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testFillWIthSequenceWithSpaceBetweenSuffixAndCode() {
		final String columnName = ColumnLabels.DESIGNATION.getName();
		final String prefix = "LEAFYNODE";
		final String suffix = "4EV";
		final Integer startNumber = 100;
		final Integer numberofZeros = 0;
		final boolean withSpaceBetweenPrefixAndCode = false;
		final boolean withSpaceBetweenSuffixAndCode = true;

		this.valuesGenerator.fillWithSequence(columnName, prefix, suffix, startNumber, numberofZeros,
				withSpaceBetweenPrefixAndCode, withSpaceBetweenSuffixAndCode);

		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final StringBuilder sb = new StringBuilder(prefix);
			sb.append(startNumber + i);
			sb.append(" ");
			sb.append(suffix);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, sb.toString());
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testFillWithCrossExpansion() {
		final int crossExpansionLevel = 1;
		final Map<Integer, String> pedigreeNames = this.generateGIDStringMap("CRUZ");
		final HashSet<Integer> gidsSet = new HashSet<>(GermplasmColumnValuesGeneratorTest.GID_LIST);
		Mockito.doReturn(pedigreeNames).when(this.pedigreeService).getCrossExpansions(ArgumentMatchers.eq(gidsSet),
				ArgumentMatchers.eq(crossExpansionLevel), ArgumentMatchers.<CrossExpansionProperties>isNull());

		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.fillWithCrossExpansion(crossExpansionLevel, columnName);

		Mockito.verify(this.pedigreeService).getCrossExpansions(ArgumentMatchers.eq(gidsSet), ArgumentMatchers.eq(crossExpansionLevel),
				ArgumentMatchers.<CrossExpansionProperties>isNull());
		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, pedigreeNames.get(gid));
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testFillWithCrossExpansionWithNullExpansionLevel() {
		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.fillWithCrossExpansion(null, columnName);

		Mockito.verify(this.pedigreeService, Mockito.never()).getCrossExpansion(ArgumentMatchers.anyInt(),
				ArgumentMatchers.any(CrossExpansionProperties.class));
		Mockito.verify(this.fillColumnSource, Mockito.never()).setColumnValueForItem(ArgumentMatchers.anyInt(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
		Mockito.verify(this.fillColumnSource, Mockito.never()).propagateUIChanges();
	}

	@Test
	public void testSetCrossMaleGIDColumnValuesForDerivativeGermplasm() {
		Mockito.doReturn(this.generateListofGermplasm(true, null, null)).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);
		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossMaleGIDColumnValues(columnName);
		// Expecting "-" to be set as all germplasm are derived
		for (final Object itemId : GermplasmColumnValuesGeneratorTest.ITEMS_LIST) {
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(itemId, columnName, "-");
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetCrossMaleGIDColumnValuesForGenerativeGermplasm() {
		final List<Germplasm> germplasmList = this.generateListofGermplasm(false);
		Mockito.doReturn(germplasmList).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);

		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossMaleGIDColumnValues(columnName);
		// Expecting gpid2 to be set as all germplasm have gnpgs = 2
		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName,
					germplasmList.get(i).getGpid2().toString());
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}
	
	@Test
	public void testSetCrossMaleGIDColumnValuesForUnknownMaleParent() {
		final List<Germplasm> germplasmList = this.generateListofGermplasm(false);
		// Set unknown male parent for the first germplasm
		germplasmList.get(0).setGpid2(0);
		Mockito.doReturn(germplasmList).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);

		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossMaleGIDColumnValues(columnName);
		// For first germplasm, expect "UNKNOWN" to be set
		Mockito.verify(this.fillColumnSource).setColumnValueForItem(
				GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(0), columnName,
				Name.UNKNOWN);
		// Expecting gpid2 to be set as all germplasm have gnpgs = 2
		for (int i = 1; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName,
					germplasmList.get(i).getGpid2().toString());
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetCrossMalePrefNameColumnValuesForDerivativeGermplasm() {
		Mockito.doReturn(this.generateListofGermplasm(true, null, null)).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);
		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossMalePrefNameColumnValues(columnName);
		// Expecting "-" to be set as all germplasm are derived
		for (final Object itemId : GermplasmColumnValuesGeneratorTest.ITEMS_LIST) {
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(itemId, columnName, "-");
		}
		Mockito.verify(this.germplasmDataManager, Mockito.never())
				.getPreferredNamesByGids(ArgumentMatchers.anyListOf(Integer.class));
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetCrossMalePrefNameColumnValuesForGenerativeGermplasm() {
		final List<Germplasm> germplasmList = this.generateListofGermplasm(false);
		Mockito.doReturn(germplasmList).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);
		final Map<Integer, Integer> maleParentsMap = new HashMap<>();
		for (final Germplasm germplasm : germplasmList) {
			maleParentsMap.put(germplasm.getGid(), germplasm.getGpid2());
		}
		final Map<Integer, String> namesMap = this.generateGIDStringMap("ABCDEFG",
				new ArrayList<>(maleParentsMap.values()));
		Mockito.doReturn(namesMap).when(this.germplasmDataManager)
				.getPreferredNamesByGids(ArgumentMatchers.anyListOf(Integer.class));

		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossMalePrefNameColumnValues(columnName);

		// Expecting preferred names of gpid2 to be set as all germplasm have
		// gnpgs = 2
		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			final Integer maleParentId2 = maleParentsMap.get(gid);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, namesMap.get(maleParentId2));
		}
		Mockito.verify(this.germplasmDataManager).getPreferredNamesByGids(ArgumentMatchers.anyListOf(Integer.class));
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}
	
	@Test
	public void testSetCrossMalePrefNameColumnValuesForUnknownMaleParent() {
		final List<Germplasm> germplasmList = this.generateListofGermplasm(false);
		// Set unknown male parent for the first germplasm
		germplasmList.get(0).setGpid2(0);
		Mockito.doReturn(germplasmList).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);
		final Map<Integer, Integer> maleParentsMap = new HashMap<>();
		for (final Germplasm germplasm : germplasmList) {
			maleParentsMap.put(germplasm.getGid(), germplasm.getGpid2());
		}
		final Map<Integer, String> namesMap = this.generateGIDStringMap("ABCDEFG",
				new ArrayList<>(maleParentsMap.values()));
		final String unknownStringFromMiddleware = RandomStringUtils.randomAlphabetic(20);
		namesMap.put(0, unknownStringFromMiddleware);
		Mockito.doReturn(namesMap).when(this.germplasmDataManager)
				.getPreferredNamesByGids(ArgumentMatchers.anyListOf(Integer.class));

		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossMalePrefNameColumnValues(columnName);

		// For first germplasm, expect "UNKNOWN" string from Middleware to be set
		Mockito.verify(this.fillColumnSource).setColumnValueForItem(
				GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(0), columnName, unknownStringFromMiddleware);
		for (int i = 1; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			final Integer maleParentId2 = maleParentsMap.get(gid);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, namesMap.get(maleParentId2));
		}
		Mockito.verify(this.germplasmDataManager).getPreferredNamesByGids(ArgumentMatchers.anyListOf(Integer.class));
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetCrossFemaleGIDColumnValuesForDerivativeGermplasm() {
		Mockito.doReturn(this.generateListofGermplasm(true)).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);
		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossFemaleInfoColumnValues(columnName, FillWithOption.FILL_WITH_CROSS_FEMALE_GID);
		// Expecting "-" to be set as all germplasm are derived
		for (final Object itemId : GermplasmColumnValuesGeneratorTest.ITEMS_LIST) {
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(itemId, columnName, "-");
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetCrossFemaleGIDColumnValuesForGenerativeGermplasm() {
		final List<Germplasm> germplasmList = this.generateListofGermplasm(false);
		Mockito.doReturn(germplasmList).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);

		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossFemaleInfoColumnValues(columnName, FillWithOption.FILL_WITH_CROSS_FEMALE_GID);
		// Expecting gpid1 to be set as all germplasm have gnpgs = 2
		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName,
					germplasmList.get(i).getGpid1().toString());
		}
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetCrossFemalePrefNameColumnValuesForDerivativeGermplasm() {
		Mockito.doReturn(this.generateListofGermplasm(true)).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);
		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossFemaleInfoColumnValues(columnName, FillWithOption.FILL_WITH_CROSS_FEMALE_NAME);
		// Expecting "-" to be set as all germplasm are derived
		for (final Object itemId : GermplasmColumnValuesGeneratorTest.ITEMS_LIST) {
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(itemId, columnName, "-");
		}
		Mockito.verify(this.germplasmDataManager, Mockito.never())
				.getPreferredNamesByGids(ArgumentMatchers.anyListOf(Integer.class));
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetCrossFemalePrefNameColumnValuesForGenerativeGermplasm() {
		final List<Germplasm> germplasmList = this.generateListofGermplasm(false);
		Mockito.doReturn(germplasmList).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);
		final Map<Integer, Integer> femaleParentsMap = new HashMap<>();
		for (final Germplasm germplasm : germplasmList) {
			femaleParentsMap.put(germplasm.getGid(), germplasm.getGpid1());
		}
		final Map<Integer, String> namesMap = this.generateGIDStringMap("ABCDEFG",
				new ArrayList<>(femaleParentsMap.values()));
		Mockito.doReturn(namesMap).when(this.germplasmDataManager)
				.getPreferredNamesByGids(Mockito.anyListOf(Integer.class));

		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossFemaleInfoColumnValues(columnName, FillWithOption.FILL_WITH_CROSS_FEMALE_NAME);

		// Expecting preferred names of gpid1 to be set as all germplasm have
		// gnpgs = 2
		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			final Integer maleParentId2 = femaleParentsMap.get(gid);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, namesMap.get(maleParentId2));
		}
		Mockito.verify(this.germplasmDataManager).getPreferredNamesByGids(ArgumentMatchers.anyListOf(Integer.class));
		Mockito.verify(this.fillColumnSource).propagateUIChanges();
	}

	@Test
	public void testSetImmediateSourcePreferredNameColumnValues() {
		final String columnName = ColumnLabels.IMMEDIATE_SOURCE_PREFERRED_NAME.getName();
		final Map<Integer, String> namesMap = this.generateGIDStringMap("ABCDEFG",
				new ArrayList<>(GermplasmColumnValuesGeneratorTest.GID_LIST));
		Mockito.doReturn(namesMap).when(this.germplasmDataManager)
				.getImmediateSourcePreferredNamesByGids(ArgumentMatchers.anyListOf(Integer.class));
		this.valuesGenerator.setImmediateSourcePreferredNameColumnValues(columnName);
		for (int i = 0; i < GermplasmColumnValuesGeneratorTest.ITEMS_LIST.size(); i++) {
			final Integer gid = GermplasmColumnValuesGeneratorTest.GID_LIST.get(i);
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(
					GermplasmColumnValuesGeneratorTest.ITEMS_LIST.get(i), columnName, namesMap.get(gid));
		}
		Mockito.verify(this.germplasmDataManager)
				.getImmediateSourcePreferredNamesByGids(ArgumentMatchers.anyListOf(Integer.class));
		Mockito.verify(this.fillColumnSource, Mockito.times(5)).propagateUIChanges();
	}

	@Test
	public void testSetCrossFemalePrefNameColumnValuesForDerivativeGermplasmWithParentGID() {
		Mockito.doReturn(this.generateListofGermplasm(true, 1, 2)).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);
		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossFemaleInfoColumnValues(columnName, FillWithOption.FILL_WITH_CROSS_FEMALE_GID);
		for (final Object itemId : GermplasmColumnValuesGeneratorTest.ITEMS_LIST) {
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(itemId, columnName, "1");
		}
	}

	@Test
	public void testSetCrossMaleGIDColumnValuesWithParentGID() {
		Mockito.doReturn(this.generateListofGermplasm(true, 1, 2)).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);
		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossMaleGIDColumnValues(columnName);
		for (final Object itemId : GermplasmColumnValuesGeneratorTest.ITEMS_LIST) {
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(itemId, columnName, "2");
		}
	}

	@Test
	public void testSetCrossMaleInfoColumnValuesWithParentNAME() {
		List<Germplasm> germplasms = this.generateListofGermplasm(true, 1, 2);
		ArrayList<Integer> parent = new ArrayList<>();
		parent.add(2);

		final Map<Integer, String> namesMap = this.generateGIDStringMap("ABCDEFG",parent);
		Mockito.when(this.germplasmDataManager.getPreferredNamesByGids(ArgumentMatchers.anyListOf(Integer.class))).thenReturn(namesMap);
		Mockito.doReturn(germplasms).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);
		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossMalePrefNameColumnValues(columnName);
		for(final Object itemId : parent)  {
			this.fillColumnSource.setColumnValueForItem(itemId, columnName, "ABCDEFG 1");
		}
	}

	@Test
	public void testSetCrossFemaleInfoColumnValuesWithParentNAME() {
		List<Germplasm> germplasms = this.generateListofGermplasm(true, 1, 2);
		ArrayList<Integer> parent = new ArrayList<>();
		parent.add(1);

		final Map<Integer, String> namesMap = this.generateGIDStringMap("ABCDEFG",parent);
		Mockito.when(this.germplasmDataManager.getPreferredNamesByGids(ArgumentMatchers.anyListOf(Integer.class))).thenReturn(namesMap);
		Mockito.doReturn(germplasms).when(this.germplasmDataManager)
				.getGermplasms(GermplasmColumnValuesGeneratorTest.GID_LIST);
		final String columnName = ColumnLabels.PARENTAGE.getName();
		this.valuesGenerator.setCrossFemaleInfoColumnValues(columnName, FillWithOption.FILL_WITH_CROSS_FEMALE_NAME);
		for (final Object itemId : GermplasmColumnValuesGeneratorTest.ITEMS_LIST) {
			Mockito.verify(this.fillColumnSource).setColumnValueForItem(itemId, columnName, "-");
		}

		for(final Object itemId : parent)  {
			this.fillColumnSource.setColumnValueForItem(itemId, columnName, "ABCDEFG 2");
		}
	}

	private List<Germplasm> generateListofGermplasm(final boolean isDerivative, Integer femaleParent, Integer maleParent) {
		final List<Germplasm> list = new ArrayList<>();
		for (final Integer gid : GermplasmColumnValuesGeneratorTest.GID_LIST) {
			final Germplasm germplasm = new Germplasm();
			germplasm.setGid(gid);
			germplasm.setGnpgs(isDerivative ? -1 : 2);
			germplasm.setGpid1(femaleParent);
			germplasm.setGpid2(maleParent);
			germplasm.setFemaleParentPreferredName(String.valueOf(femaleParent));
			germplasm.setMaleParentPreferredName(String.valueOf(maleParent));
			list.add(germplasm);
		}
		return list;
	}

}
