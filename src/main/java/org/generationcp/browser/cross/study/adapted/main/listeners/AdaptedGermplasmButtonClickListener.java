package org.generationcp.browser.cross.study.adapted.main.listeners;

import java.util.List;

import org.generationcp.browser.cross.study.adapted.main.SetUpTraitFilter;
import org.generationcp.browser.cross.study.adapted.main.WelcomeScreen;
import org.generationcp.browser.cross.study.commons.trait.filter.CategoricalVariatesSection;
import org.generationcp.browser.cross.study.commons.trait.filter.NumericTraitsSection;
import org.generationcp.browser.cross.study.commons.trait.filter.CharacterTraitsSection;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;

public class AdaptedGermplasmButtonClickListener implements ClickListener {
	
	private static final long serialVersionUID = 1L;

	private Component source;
	private Integer traitId;
	private String traitName;
	private String variateType;
	private List<Integer> envIds;

	public AdaptedGermplasmButtonClickListener(Component source) {
		super();
		this.source = source;
	}

	public AdaptedGermplasmButtonClickListener(Component source, Integer traitId, String traitName, String variateType, List<Integer> envIds){
		super();
		this.source = source;
		this.traitId = traitId;
		this.traitName = traitName;
		this.variateType = variateType;
		this.envIds = envIds;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		Object data = event.getButton().getData();

		if (source instanceof WelcomeScreen){
			WelcomeScreen screen = (WelcomeScreen) source;
			if (WelcomeScreen.NEXT_BUTTON_ID.equals(data)){
				screen.nextButtonClickAction();
			}
		} else if (source instanceof NumericTraitsSection){
			NumericTraitsSection screen = (NumericTraitsSection) source;
			
			if (NumericTraitsSection.TRAIT_BUTTON_ID.equals(data)){
				screen.showNumericVariateClickAction(traitId, traitName, envIds);
			}
		} else if (source instanceof SetUpTraitFilter){
			SetUpTraitFilter screen = (SetUpTraitFilter) source;
			
			if (SetUpTraitFilter.NEXT_BUTTON_ID.equals(data)){
				screen.nextButtonClickAction();
			}
			
		}
		else if (source instanceof CharacterTraitsSection){
			CharacterTraitsSection screen = (CharacterTraitsSection) source;
			if (CharacterTraitsSection.TRAIT_BUTTON_ID.equals(data)){
				screen.showTraitObservationClickAction(traitId, variateType, traitName, envIds);
			}
		}
		else if (source instanceof CategoricalVariatesSection){
			CategoricalVariatesSection screen = (CategoricalVariatesSection) source;
			if (CategoricalVariatesSection.TRAIT_BUTTON_ID.equals(data)){
				screen.showTraitObservationClickAction(traitId, variateType, traitName, envIds);
			}
		}
	}

}