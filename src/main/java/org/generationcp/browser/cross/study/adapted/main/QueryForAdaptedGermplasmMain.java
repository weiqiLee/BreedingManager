package org.generationcp.browser.cross.study.adapted.main;

import org.generationcp.browser.application.Message;
import org.generationcp.browser.cross.study.commons.EnvironmentFilter;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Autowired;


import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class QueryForAdaptedGermplasmMain extends VerticalLayout implements InitializingBean, InternationalizableComponent {
	private static final long serialVersionUID = -3488805933508882321L;
    private static final String VERSION = "1.0.0-BETA";

    private Accordion accordion;

    private HorizontalLayout titleLayout;

    private Label mainTitle;


    private WelcomeScreen welcomeScreen;
    private EnvironmentFilter screenOne;
    private SetUpTraitFilter screenTwo;
    private DisplayResults screenThree;

    private Tab welcomeTab;
    private Tab firstTab;
    private Tab secondTab;
    private Tab thirdTab;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private static final String GUIDE_MESSAGE = "Find germplasms suitable for specific environmental conditions. " +
    					"(Filter trial environments by the required environmental conditions, and find the germplasm " + 
    					"with the best performance of some important trait(s) in those environments). ";
    
    @Override
    public void afterPropertiesSet() throws Exception {
	    setMargin(false);
	    setSpacing(true);

	    titleLayout = new HorizontalLayout();
        titleLayout.setSpacing(true);
        setTitleContent(GUIDE_MESSAGE);
        addComponent(titleLayout);

        accordion = new Accordion();
        accordion.setWidth("1000px");

        screenThree = new DisplayResults(this);
        screenTwo = new SetUpTraitFilter(this, screenThree);
        screenOne = new EnvironmentFilter(this, screenTwo);
        welcomeScreen = new WelcomeScreen(this, screenOne);

        welcomeTab = accordion.addTab(welcomeScreen, messageSource.getMessage(Message.INTRODUCTION));
        firstTab = accordion.addTab(screenOne, messageSource.getMessage(Message.SPECIFY_WEIGH_ENVIRONMENT));
        secondTab = accordion.addTab(screenTwo,  messageSource.getMessage(Message.SETUP_TRAIT_FILTER));
        thirdTab = accordion.addTab(screenThree, messageSource.getMessage(Message.DISPLAY_RESULTS));
        
        welcomeTab.setEnabled(true);	        	        
        firstTab.setEnabled(true);
        secondTab.setEnabled(false);
        thirdTab.setEnabled(false);

        accordion.addListener(new SelectedTabChangeListener() {
            private static final long serialVersionUID = 7580598154898239027L;

			@Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                Component selected =accordion.getSelectedTab();
                Tab tab = accordion.getTab(selected);

                if(tab!=null && tab.equals(welcomeTab)){
                	welcomeTab.setEnabled(true);
                    firstTab.setEnabled(true);
                    secondTab.setEnabled(false);
                } else if(tab!=null && tab.equals(firstTab)){
                	welcomeTab.setEnabled(true);
                    secondTab.setEnabled(true);
                    thirdTab.setEnabled(false);
                } else if(tab!=null && tab.equals(secondTab)){
                	welcomeTab.setEnabled(false);
                	firstTab.setEnabled(true);
                	thirdTab.setEnabled(true);
                } else if(tab!=null && tab.equals(thirdTab)){
                	welcomeTab.setEnabled(false);
                	firstTab.setEnabled(false);
                	thirdTab.setEnabled(true);
                }
            }
        });
		
        addComponent(accordion);
    }
    
    private void setTitleContent(String guideMessage){
        titleLayout.removeAllComponents();

        String title =  "Adapted Germplasm Query  <h2>" + VERSION + "</h2>";
        mainTitle = new Label();
        mainTitle.setStyleName(Bootstrap.Typography.H1.styleName());
        mainTitle.setWidth("380px");
        mainTitle.setContentMode(Label.CONTENT_XHTML);
        mainTitle.setValue(title);
        titleLayout.addComponent(mainTitle);


        Label descLbl = new Label(guideMessage);
        descLbl.setWidth("300px");

        PopupView popup = new PopupView("?",descLbl);
        popup.setStyleName("gcp-popup-view");
        titleLayout.addComponent(popup);

        titleLayout.setComponentAlignment(popup, Alignment.MIDDLE_LEFT);

    }

	@Override
	public void updateLabels() {
	}
	
	public void selectFirstTabAndReset(){
    	firstTab.setEnabled(true);
        this.accordion.setSelectedTab(screenOne);
        secondTab.setEnabled(true);
        thirdTab.setEnabled(false);
    }
	
    public void selectWelcomeTab(){
    	welcomeTab.setEnabled(true);
    	firstTab.setEnabled(true);
        this.accordion.setSelectedTab(welcomeScreen);
        secondTab.setEnabled(false);
        thirdTab.setEnabled(false);
    }		
    
    public void selectFirstTab(){
    	welcomeTab.setEnabled(true);
    	firstTab.setEnabled(true);
        this.accordion.setSelectedTab(screenOne);
        secondTab.setEnabled(true);
        thirdTab.setEnabled(false);
    }

    public void selectSecondTab(){
    	welcomeTab.setEnabled(false);
    	firstTab.setEnabled(true);
    	secondTab.setEnabled(true);
        this.accordion.setSelectedTab(screenTwo);
        thirdTab.setEnabled(true);
    }

    public void selectThirdTab(){
    	welcomeTab.setEnabled(false);
        firstTab.setEnabled(false);
        secondTab.setEnabled(true);
        thirdTab.setEnabled(true);
        this.accordion.setSelectedTab(screenThree);
    }
}