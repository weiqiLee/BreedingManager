package org.generationcp.breeding.manager.action;

import java.util.List;

import org.generationcp.middleware.pojos.GermplasmListData;

public interface SaveGermplasmListActionSource {
	public void updateListDataTable(List<GermplasmListData> listDataEntries);
}
