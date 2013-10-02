package org.generationcp.browser.cross.study.h2h.main.pojos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.browser.cross.study.constants.EnvironmentWeight;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.TraitInfo;

import com.vaadin.ui.ComboBox;


public class EnvironmentForComparison implements Serializable{

    private static final long serialVersionUID = -879684249019712493L;
    
    private Integer environmentNumber;
    private String locationName;
    private String countryName;
    private String studyName;
    private ComboBox weightComboBox;
    private Double weight;
    private LinkedHashMap<TraitForComparison, List<ObservationList>> traitAndObservationMap = new LinkedHashMap();
    
    public EnvironmentForComparison(Integer environmentNumber, String locationName, String countryName, String studyName, ComboBox weightComboBox) {
        super();
        this.environmentNumber = environmentNumber;
        this.locationName = locationName;
        this.countryName = countryName;
        this.studyName = studyName;
        this.weightComboBox = weightComboBox;
    //    this.traitAndNumberOfPairsComparableMap = traitAndNumberOfPairsComparableMap;
    }

	public ComboBox getWeightComboBox() {
		return weightComboBox;
	}

	public void setWeightComboBox(ComboBox weightComboBox) {
		this.weightComboBox = weightComboBox;
	}

	public Integer getEnvironmentNumber() {
        return environmentNumber;
    }
    
    public void setEnvironmentNumber(Integer environmentNumber) {
        this.environmentNumber = environmentNumber;
    }
    
    public String getLocationName() {
        return locationName;
    }
    
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    
    public String getCountryName() {
        return countryName;
    }
    
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    
    public String getStudyName() {
        return studyName;
    }
    
    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

	public LinkedHashMap<TraitForComparison, List<ObservationList>> getTraitAndObservationMap() {
		return traitAndObservationMap;
	}

	public void setTraitAndObservationMap(
			LinkedHashMap<TraitForComparison, List<ObservationList>> traitAndObservationMap) {
		this.traitAndObservationMap = traitAndObservationMap;
	}

	public Double getWeight() {
		return this.weight;
	}


	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	public void computeWeight(int total){
		if (total > 0 && this.weightComboBox != null){
			EnvironmentWeight envtWeight = (EnvironmentWeight) weightComboBox.getValue();
			this.weight = (double) envtWeight.getWeight() / total;
		}
	}
    
    
}
