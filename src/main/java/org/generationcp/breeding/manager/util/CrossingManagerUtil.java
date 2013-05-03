package org.generationcp.breeding.manager.util;

import java.util.List;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.UserDefinedField;

import com.vaadin.ui.Window;


public class CrossingManagerUtil{
	
	public static final String[] USER_DEF_FIELD_CROSS_NAME = {"CROSS NAME", "CROSSING NAME"};

    private GermplasmDataManager germplasmDataManager;


    public CrossingManagerUtil(GermplasmDataManager germplasmDataManager) {
	this.germplasmDataManager = germplasmDataManager;
    }


    public Germplasm setCrossingBreedingMethod(Germplasm gc,Integer femaleGid, Integer maleGid) throws MiddlewareQueryException{

	Germplasm gf = germplasmDataManager.getGermplasmByGID(femaleGid); // germplasm female
	Germplasm gm = germplasmDataManager.getGermplasmByGID(maleGid); // germplasm male
	Germplasm gff = germplasmDataManager.getGermplasmByGID(gf.getGpid2()); // maternal male grand parent (daddy of female parent)
	Germplasm gfm =  germplasmDataManager.getGermplasmByGID(gf.getGpid1()); // maternal female grand parent (mommy of female parent)
	Germplasm gmf = germplasmDataManager.getGermplasmByGID(gm.getGpid1()); //  paternal female grand parent (mommy of male parent)
	Germplasm gmm =  germplasmDataManager.getGermplasmByGID(gm.getGpid2()); // paternal male grand parent (daddy of male parent)

	if(gf.getGnpgs()<0)
	{
	    if(gm.getGnpgs()<0)
	    {
		gc.setMethodId(101);
	    }
	    else
	    {
		if(gm.getGnpgs()==1)
		{
		    gc.setMethodId(101);
		}
		else if(gm.getGnpgs()==2)
		{
		    if(gmf.getGid()==gf.getGid() || gmm.getGid()==gf.getGid())
		    {
			gc.setMethodId(107);
		    }
		    else
		    {
			gc.setMethodId(102);
		    }
		}
		else
		{
		    gc.setMethodId(106);
		}
	    }
	}
	else
	{
	    if(gm.getGnpgs()<0)
	    {
		if(gf.getGnpgs()==1)
		{
		    gc.setMethodId(101);
		}
		else if(gf.getGnpgs()==2)
		{
		    if(gff.getGid()==gm.getGid() || gfm.getGid()==gm.getGid())
		    {
			gc.setMethodId(107);
		    }
		    else
		    {
			gc.setMethodId(102);
		    }
		}
		else
		{
		    gc.setMethodId(106);
		}
	    }
	    else
	    {
		if(gf.getMethodId()==101 && gm.getMethodId()==101)
		{
		    gc.setMethodId(103);
		}
		else
		{
		    gc.setMethodId(106);
		}
	    }
	}

	return gc;

    }
    
    public static String generateFemaleandMaleCrossName(String femaleName, String maleName){
    	return femaleName + "/" + maleName;
    }
    
    /**
     * Get the id for UserDefinedField of Germplasm Name type for Crossing Name
	 * (matches upper case of UserDefinedField either fCode or fName). Query is:
	 * <b>
	 *	SELECT fldno
     *	  FROM udflds
     *   WHERE UPPER(fname) IN ('CROSSING NAME', 'CROSS NAME')
     *      OR UPPER(fcode) IN ('CROSSING NAME', 'CROSS NAME');
	 * </b>
	 * 
     * @param germplasmListManager
     * @return
     * @throws MiddlewareQueryException 
     */
	public static Integer getIDForUserDefinedFieldCrossingName(GermplasmListManager germplasmListManager) throws MiddlewareQueryException  {
	    	
		List<UserDefinedField> nameTypes = germplasmListManager.getGermplasmNameTypes();
		for (UserDefinedField type : nameTypes){
			for (String crossNameValue : USER_DEF_FIELD_CROSS_NAME){
				if (crossNameValue.equals(type.getFcode().toUpperCase()) || 
						crossNameValue.equals(type.getFname().toUpperCase())){
					return type.getFldno();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Get the id for UserDefinedField of Germplasm Name type for Crossing Name
	 * (matches upper case of UserDefinedField either fCode or fName). Query is:
	 * <b>
	 *	SELECT fldno
     *	  FROM udflds
     *   WHERE UPPER(fname) IN ('CROSSING NAME', 'CROSS NAME')
     *      OR UPPER(fcode) IN ('CROSSING NAME', 'CROSS NAME');
	 * </b>
	 * If any error occurs, shows error message in passed in Window instance
	 * @param germplasmListManager - instance of GermplasmListManager
	 * @param window - window where error message will be shown
	 * @param messageSource - resource bundle where the error message will be retrieved from
	 * @return
	 */
	public static Integer getIDForUserDefinedFieldCrossingName(GermplasmListManager germplasmListManager, 
			Window window, SimpleResourceBundleMessageSource messageSource){
		
		try {
			
			return getIDForUserDefinedFieldCrossingName(germplasmListManager);
		
		} catch (MiddlewareQueryException e) {
            e.printStackTrace();
            
            if (window != null && messageSource != null){
                MessageNotifier.showWarning(window, 
                		messageSource.getMessage(Message.ERROR_DATABASE),
                		messageSource.getMessage(Message.ERROR_IN_GETTING_CROSSING_NAME_TYPE));
            }
		}
		
		return null;
	}
	
	


}
