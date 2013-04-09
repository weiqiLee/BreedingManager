package org.generationcp.breeding.manager.application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.generationcp.breeding.manager.listimport.GermplasmImportMain;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.hibernate.util.HttpRequestAwareUtil;
import org.generationcp.commons.vaadin.actions.UpdateComponentLabelsAction;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.vaadin.terminal.Terminal;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


public class BreedingManagerApplication extends SpringContextApplication implements ApplicationContextAware{
    private final static Logger LOG = LoggerFactory.getLogger(BreedingManagerApplication.class);

    private static final long serialVersionUID = 1L;
    
    public static final String GERMPLASM_IMPORT_WINDOW_NAME = "germplasm-import"; 
    
    private Window window;
    
    private VerticalLayout rootLayoutForImportGermplasmList;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private UpdateComponentLabelsAction messageSourceListener;

    private ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void initSpringApplication(ConfigurableWebApplicationContext arg0) {
        
        messageSourceListener = new UpdateComponentLabelsAction(this);
        messageSource.addListener(messageSourceListener);
        
        this.rootLayoutForImportGermplasmList = new VerticalLayout();
        rootLayoutForImportGermplasmList.setSizeFull();
        
        window = new Window(messageSource.getMessage(Message.MAIN_WINDOW_CAPTION)); // "Breeding Manager"
        setMainWindow(window);
        setTheme("gcp-default");
        window.setSizeUndefined();

        TabSheet tabSheet = new TabSheet();
        
        VerticalLayout layouts[] = new VerticalLayout[1];
        layouts[0] = this.rootLayoutForImportGermplasmList;
        
        WelcomeTab welcomeTab = new WelcomeTab(tabSheet, layouts);
        
        tabSheet.addTab(welcomeTab, messageSource.getMessage(Message.WELCOME_TAB_LABEL)); // "Welcome"
        tabSheet.addTab(rootLayoutForImportGermplasmList, messageSource.getMessage(Message.IMPORT_GERMPLASM_LIST_TAB_LABEL)); // "Import Germlasm List"
        tabSheet.addListener(new MainApplicationSelectedTabChangeListener(this));
        
        window.addComponent(tabSheet);
        
        // Override the existing error handler that shows the stack trace
        setErrorHandler(this);
    }

    @Override
    public Window getWindow(String name) {
        // dynamically create other application-level windows which is associated with specific URLs
        // these windows are the jumping on points to parts of the application
        if(super.getWindow(name) == null){
            if(name.equals(GERMPLASM_IMPORT_WINDOW_NAME)){
                Window germplasmImportWindow = new Window(messageSource.getMessage(Message.IMPORT_GERMPLASM_LIST_TAB_LABEL));
                germplasmImportWindow.setName(GERMPLASM_IMPORT_WINDOW_NAME);
                germplasmImportWindow.setSizeUndefined();
                germplasmImportWindow.addComponent(new GermplasmImportMain());
                this.addWindow(germplasmImportWindow);
                return germplasmImportWindow;
            }
        }
        
        return super.getWindow(name);
    }
    
    public void tabSheetSelectedTabChangeAction(TabSheet source) throws InternationalizableException {

        if (source.getSelectedTab() == this.rootLayoutForImportGermplasmList) {
            if (this.rootLayoutForImportGermplasmList.getComponentCount() == 0) {
                rootLayoutForImportGermplasmList.addComponent(new GermplasmImportMain());
            }
        } 
    }


    /** 
     * Override terminalError() to handle terminal errors, to avoid showing the stack trace in the application 
     */
    @Override
    public void terminalError(Terminal.ErrorEvent event) {
        LOG.error("An unchecked exception occurred: ", event.getThrowable());
        event.getThrowable().printStackTrace();
        // Some custom behaviour.
        if (getMainWindow() != null) {
            MessageNotifier.showError(getMainWindow(), messageSource.getMessage(Message.ERROR_INTERNAL),  // TESTED
                    messageSource.getMessage(Message.ERROR_PLEASE_CONTACT_ADMINISTRATOR)
                            + (event.getThrowable().getLocalizedMessage() == null ? "" : "</br>"
                                    + event.getThrowable().getLocalizedMessage()));
        }
    }

    @Override
    public void close() {
        super.close();

        // implement this when we need to do something on session timeout
        messageSource.removeListener(messageSourceListener);

        LOG.debug("Application closed");
    }
    
    public static BreedingManagerApplication get() {
        return get(BreedingManagerApplication.class);
    }

    @Override
    protected void doOnRequestStart(HttpServletRequest request, HttpServletResponse response) {
        super.doOnRequestStart(request, response);
        
        LOG.trace("Request started " + request.getRequestURI() + "?" + request.getQueryString());
        
        synchronized (this) {
            HttpRequestAwareUtil.onRequestEnd(applicationContext, request, response);
        }
    }
    
    @Override
    protected void doOnRequestEnd(HttpServletRequest request, HttpServletResponse response) {
        super.doOnRequestEnd(request, response);
        
        LOG.trace("Request ended " + request.getRequestURI() + "?" + request.getQueryString());
        
        synchronized (this) {
            HttpRequestAwareUtil.onRequestEnd(applicationContext, request, response);
        }
    }

}
