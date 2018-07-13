package org.generationcp.breeding.manager.crossingmanager;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.crossingmanager.constants.CrossType;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@RunWith(MockitoJUnitRunner.class)
public class CrossingMethodComponentTest {

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	Window window;

	@Mock
	CrossingManagerMakeCrossesComponent makeCrossesMain;

	private CrossingMethodComponent component;

	@Before
	public void setUp() throws Exception {
		this.component = new CrossingMethodComponent(this.makeCrossesMain);
		this.component.setMessageSource(this.messageSource);
		this.component.setParent(this.makeCrossesMain);
		Mockito.when(this.makeCrossesMain.getWindow()).thenReturn(this.window);
		this.component.instantiateComponents();
		this.component.initializeValues();
		this.component.addListeners();
	}

	@Test
	public void testGenerateCrossWithNoSelectedCrossingMethod() {
		Mockito.when(this.messageSource.getMessage(Message.WARNING)).thenReturn("Warning");
		Mockito.when(this.messageSource.getMessage(Message.PLEASE_CHOOSE_CROSSING_METHOD))
				.thenReturn("Please choose crossing method.");

		this.component.getGenerateCrossButton().click();
		Mockito.verify(this.makeCrossesMain).getWindow();
		Mockito.verify(this.messageSource).getMessage(Message.WARNING);
		Mockito.verify(this.messageSource).getMessage(Message.PLEASE_CHOOSE_CROSSING_METHOD);
	}

	@Test
	public void testGenerateCrossWith() {
		final MakeCrossesParentsComponent parentsComponent = Mockito.mock(MakeCrossesParentsComponent.class);
		Mockito.when(this.makeCrossesMain.getParentsComponent()).thenReturn(parentsComponent);
		final Table femaleTable = Mockito.mock(Table.class);
		final Table maleTable = Mockito.mock(Table.class);
		Mockito.when(parentsComponent.getFemaleTable()).thenReturn(femaleTable);
		Mockito.when(parentsComponent.getMaleTable()).thenReturn(maleTable);
		this.component.getCrossingMethodComboBox().setValue(CrossType.MULTIPLY);

		this.component.getGenerateCrossButton().click();
		Mockito.verify(this.makeCrossesMain).getParentsComponent();
		Mockito.verify(parentsComponent).getFemaleTable();
		Mockito.verify(parentsComponent).getMaleTable();
		Mockito.verify(parentsComponent).getCorrectSortedValue(femaleTable);
		Mockito.verify(parentsComponent).getCorrectSortedValue(maleTable);
		Mockito.verify(parentsComponent).updateFemaleListNameForCrosses();
		Mockito.verify(parentsComponent).updateMaleListNameForCrosses();
		Mockito.verify(this.makeCrossesMain).makeCrossButtonAction(Matchers.anyList(), Matchers.anyList(),
				Matchers.anyString(), Matchers.anyString(), Matchers.eq(CrossType.MULTIPLY), Matchers.anyBoolean(),
				Matchers.anyBoolean());
	}
}