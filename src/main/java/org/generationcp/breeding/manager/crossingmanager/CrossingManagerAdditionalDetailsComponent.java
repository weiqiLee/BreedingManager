package org.generationcp.breeding.manager.crossingmanager;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.crossingmanager.listeners.CrossingManagerImportButtonClickListener;
import org.generationcp.breeding.manager.crossingmanager.pojos.CrossesMade;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

@Configurable
public class CrossingManagerAdditionalDetailsComponent extends AbsoluteLayout 
        implements InitializingBean, InternationalizableComponent, CrossesMadeContainer{
    
    public static final String NEXT_BUTTON_ID = "next button";
    public static final String BACK_BUTTON_ID = "back button";
    
    private static final long serialVersionUID = 9097810121003895303L;
    @SuppressWarnings("unused")
	private final static Logger LOG = LoggerFactory.getLogger(CrossingManagerAdditionalDetailsComponent.class);
    
    private final CrossingManagerMain source;
    private final Accordion accordion;
    private CrossesMade crossesMade;
    
    //Used Form to make use of fieldset HTML element to render section border
    private CssLayout crossingMethodForm;
    private CssLayout crossNameForm;
    private CssLayout crossInfoForm;
    
    private AdditionalDetailsBreedingMethodComponent crossingMethodComponent;
    private AdditionalDetailsCrossNameComponent crossNameComponent;
    private AdditionalDetailsCrossInfoComponent crossInfoComponent;
    
    private Button backButton;
    private Button nextButton;

    private final CrossesMadeContainerUpdateListener[] updateListeners = new CrossesMadeContainerUpdateListener[3];
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private Component nextScreen;
    private Component previousScreen;
    
    public CrossingManagerAdditionalDetailsComponent(CrossingManagerMain source, Accordion accordion){
        this.source = source;
        this.accordion = accordion;
    }
    
    public void setNextScreen(Component nextScreen) {
        this.nextScreen = nextScreen;
    }

    public AdditionalDetailsBreedingMethodComponent getBreedingMethodComponent() {
        return crossingMethodComponent;
    }

    public AdditionalDetailsCrossNameComponent getCrossNameComponent() {
        return crossNameComponent;
    }

    public AdditionalDetailsCrossInfoComponent getCrossInfoComponent() {
        return crossInfoComponent;
    }

    public CrossingManagerMain getSource() {
        return source;
    }

    public Accordion getAccordion() {
        return accordion;
    }
    
    @Override
    public void setCrossesMade(CrossesMade crossesMade) {
        this.crossesMade = crossesMade;
    }
    
    @Override
    public CrossesMade getCrossesMade() {
        return this.crossesMade;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        setHeight("640px");
        setWidth("800px");
        
        Label crossingMethodLabel = new Label("<b>"+ messageSource.getMessage(Message.CROSSING_METHOD) +"</b>");
        crossingMethodLabel.setContentMode(Label.CONTENT_XHTML);
        
        crossingMethodComponent = new AdditionalDetailsBreedingMethodComponent();
        crossingMethodForm = new CssLayout();
        crossingMethodForm.addComponent(crossingMethodLabel);
        crossingMethodForm.addComponent(crossingMethodComponent);

        Label crossNameLabel = new Label("<b>"+ messageSource.getMessage(Message.CROSS_NAME) +"</b>");
        crossNameLabel.setContentMode(Label.CONTENT_XHTML);

        crossNameComponent = new AdditionalDetailsCrossNameComponent();
        crossNameForm = new CssLayout();
        crossNameForm.addComponent(crossNameLabel);
        crossNameForm.addComponent(crossNameComponent);
        
        Label crossInfoLabel = new Label("<b>"+ messageSource.getMessage(Message.CROSS_INFO) +"</b>");
        crossInfoLabel.setContentMode(Label.CONTENT_XHTML);
        
        crossInfoComponent = new AdditionalDetailsCrossInfoComponent();
        crossInfoForm = new CssLayout();
        crossInfoForm.addComponent(crossInfoLabel);
        crossInfoForm.addComponent(crossInfoComponent);
        
        CrossingManagerImportButtonClickListener listener = new CrossingManagerImportButtonClickListener(this);
        
        backButton = new Button();
        backButton.setData(BACK_BUTTON_ID);
        backButton.addListener(listener);
        
        nextButton = new Button();
        nextButton.setData(NEXT_BUTTON_ID);
        nextButton.addListener(listener);
        nextButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        
        // Layout Components
        addComponent(crossingMethodForm, "top:40px;left:100px");
        addComponent(crossNameForm, "top:230px;left:100px");
        addComponent(crossInfoForm, "top:460px;left:100px");
        addComponent(backButton, "top:570px;left:320px");
        addComponent(nextButton, "top:570px;left:390px");
        
        setUpdateListeners();
    }
    
    @Override
    public void attach() {
        super.attach();
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        messageSource.setCaption(backButton, Message.BACK);
        messageSource.setCaption(nextButton, Message.NEXT);
        //messageSource.setCaption(crossingMethodForm, Message.CROSSING_METHOD);
        //messageSource.setCaption(crossNameForm, Message.CROSS_CODE);
        //messageSource.setCaption(crossInfoForm, Message.CROSS_INFO);
    }
    
    private void setUpdateListeners(){
//        crossingMethodComponent.setCrossesMadeContainer(this);
//        crossNameComponent.setCrossesMadeContainer(this);
//        crossInfoComponent.setCrossesMadeContainer(this);
        
        updateListeners[0] = crossingMethodComponent;
        updateListeners[1] = crossNameComponent;
        updateListeners[2] = crossInfoComponent;
    }
    
    public void setPreviousScreen(Component backScreen){
        this.previousScreen = backScreen;
    }    
    
    public Component getPreviousScreen(){
        return this.previousScreen;
    }
    
    public void nextButtonClickAction(){
        boolean allValidationsPassed = true;
        //perform validations and update CrossesMade instance
        for (CrossesMadeContainerUpdateListener listener : updateListeners){
            if (listener != null){
                if (!listener.updateCrossesMadeContainer(this)){
                    allValidationsPassed = false;
                    break;
                }
            }
        }  
        
        nextScreen = source.getWizardScreenFour();
//        source.getWizardScreenFour().setPreviousScreen(this);
        
        if (this.nextScreen != null && allValidationsPassed){
            source.enableWizardTabs();
            assert this.nextScreen instanceof CrossesMadeContainer;
            ((CrossesMadeContainer) this.nextScreen).setCrossesMade(getCrossesMade());
            source.setCrossesMade(getCrossesMade());
            
            this.accordion.setSelectedTab(nextScreen);
            if(this.getPreviousScreen() instanceof CrossingManagerMakeCrossesComponent){
                source.enableWizardTabs();
            } else{
                source.enableOnlyWizardTabFour();
                source.enableWizardTabThree();
                source.enableWizardTabOne();
            }
        }
    }
    
    public void backButtonClickAction(){
        if (this.previousScreen != null){
            source.enableWizardTabs();
            this.accordion.setSelectedTab(this.previousScreen);
        }
        
    }

}