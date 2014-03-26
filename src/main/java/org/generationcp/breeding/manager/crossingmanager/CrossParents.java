package org.generationcp.breeding.manager.crossingmanager;

import org.generationcp.breeding.manager.crossingmanager.pojos.GermplasmListEntry;

public class CrossParents {
    
    private GermplasmListEntry femaleParent;
    
    private GermplasmListEntry maleParent;
    
    private String seedSource;
    
    public CrossParents(GermplasmListEntry femaleParent, GermplasmListEntry maleParent){
        this.femaleParent = femaleParent;
        this.maleParent = maleParent;
    }
    
    public GermplasmListEntry getFemaleParent() {
        return femaleParent;
    }
    
    public GermplasmListEntry getMaleParent() {
        return maleParent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CrossParents other = (CrossParents) obj;
        if (femaleParent == null) {
            if (other.femaleParent != null)
                return false;
            } else if (!femaleParent.equals(other.femaleParent))
                return false;
        if (maleParent == null) {
            if (other.maleParent != null)
                return false;
         } else if (!maleParent.equals(other.maleParent))
            return false;
        
        return true;
    }

	public String getSeedSource() {
		return seedSource;
	}

	public void setSeedSource(String seedSource) {
		this.seedSource = seedSource;
	}

}
