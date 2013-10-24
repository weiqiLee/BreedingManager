package org.generationcp.browser.cross.study.adapted.main.pojos;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.ObservationKey;

public class ObservationList {
	ObservationKey key;
	List<Observation> observationList = new ArrayList<Observation>();
	
	public ObservationList(ObservationKey key) {
		super();
		this.key = key;
	}
	public ObservationKey getKey() {
		return key;
	}
	public void setKey(ObservationKey key) {
		this.key = key;
	}
	public List<Observation> getObservationList() {
		return observationList;
	}
	public void setObservationList(List<Observation> observationList) {
		this.observationList = observationList;
	}
	
	public void add(Observation obs){
		observationList.add(obs);
	}
}
