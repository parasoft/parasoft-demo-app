package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.ImplementedIndustries;
import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.config.activemq.InventoryRequestQueueListener;
import com.parasoft.demoapp.config.activemq.InventoryResponseQueueListener;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.defaultdata.ClearEntrance;
import com.parasoft.demoapp.defaultdata.ResetEntrance;
import com.parasoft.demoapp.dto.GlobalPreferencesDTO;
import com.parasoft.demoapp.exception.*;
import com.parasoft.demoapp.exception.EndpointInvalidException;
import com.parasoft.demoapp.exception.GlobalPreferencesMoreThanOneException;
import com.parasoft.demoapp.exception.GlobalPreferencesNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.model.global.preferences.*;
import com.parasoft.demoapp.repository.global.GlobalPreferencesRepository;
import com.parasoft.demoapp.util.BugsTypeSortOfDemoBugs;
import com.parasoft.demoapp.util.RouteIdSortOfRestEndpoint;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;

import static com.parasoft.demoapp.config.ParasoftJDBCProxyConfig.*;
import static com.parasoft.demoapp.service.GlobalPreferencesDefaultSettingsService.*;

@Service
public class GlobalPreferencesService {

    @Autowired
    private GlobalPreferencesRepository globalPreferencesRepository;

    @Autowired
    private DemoBugService demoBugService;

    @Autowired
    private ResetEntrance resetEntrance;

    @Autowired
    private ClearEntrance clearEntrance;

    @Autowired
    private RestEndpointService restEndpointService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ParasoftJDBCProxyService parasoftJDBCProxyService;

    @Autowired
    private InventoryRequestQueueListener inventoryRequestQueueListener;

    @Autowired
    private InventoryResponseQueueListener inventoryResponseQueueListener;

    @Autowired
    private GlobalPreferencesDefaultSettingsService defaultGlobalPreferencesSettingsService;


    /**
    * This method is used only when PDA startup first time(it means no db files are created),
     * do not use it for other purposes since there are no validations for parameters.
    */
    public GlobalPreferencesEntity addNewGlobalPreferences(DataAccessMode dataAccessMode, String soapEndPoint,
                                                           Set<RestEndpointEntity> restEndpoints,
                                                           IndustryType industryType, Set<DemoBugEntity> demoBugs,
                                                           Boolean advertisingEnabled, Boolean useParasoftJDBCProxy,
                                                           String parasoftVirtualizeServerUrl, String parasoftVirtualizeServerPath,
                                                           String parasoftVirtualizeGroupId,
                                                           Boolean mqProxyEnabled,
                                                           MqType mqType,
                                                           String orderServiceDestinationQueue,
                                                           String orderServiceReplyToQueue,
                                                           String inventoryServiceDestinationQueue,
                                                           String inventoryServiceReplyToQueue ) throws ParameterException {

        validateIndustry(industryType);
        ParameterValidator.requireNonNull(advertisingEnabled, GlobalPreferencesMessages.ADVERTISING_ENABLED_CANNOT_BE_NULL);

        if(demoBugs == null){
            demoBugs = new TreeSet<>();
        }

        GlobalPreferencesEntity newGlobalPreferences = new GlobalPreferencesEntity(
                                                            dataAccessMode,
                                                            soapEndPoint,
                                                            restEndpoints,
                                                            industryType,
                                                            demoBugs,
                                                            advertisingEnabled,
                                                            useParasoftJDBCProxy,
                                                            parasoftVirtualizeServerUrl,
                                                            parasoftVirtualizeServerPath,
                                                            parasoftVirtualizeGroupId,
                                                            mqProxyEnabled,
                                                            mqType,
                                                            orderServiceDestinationQueue,
                                                            orderServiceReplyToQueue,
                                                            inventoryServiceDestinationQueue,
                                                            inventoryServiceReplyToQueue);

        for(DemoBugEntity demoBug : demoBugs){
            demoBug.setGlobalPreferences(newGlobalPreferences);
        }

        return globalPreferencesRepository.save(newGlobalPreferences);
    }

    @Transactional(transactionManager = "globalTransactionManager",
            rollbackFor = {ParameterException.class, VirtualizeServerUrlException.class})
    public GlobalPreferencesEntity updateGlobalPreferences(GlobalPreferencesDTO globalPreferencesDto)
            throws GlobalPreferencesNotFoundException, GlobalPreferencesMoreThanOneException, ParameterException,
            EndpointInvalidException, VirtualizeServerUrlException {

        IndustryType industry = globalPreferencesDto.getIndustryType();
        validateIndustry(industry);

        Boolean advertisingEnabled =
                globalPreferencesDto.getAdvertisingEnabled() == null ? false : globalPreferencesDto.getAdvertisingEnabled();

        GlobalPreferencesEntity currentPreferences = getCurrentGlobalPreferences();
        currentPreferences.setIndustryType(industry);
        currentPreferences.setAdvertisingEnabled(advertisingEnabled);

        handleDemoBugs(currentPreferences, globalPreferencesDto);

        handleRestEndpoints(currentPreferences, globalPreferencesDto);

        handleParasoftJDBCProxy(currentPreferences, globalPreferencesDto);

        currentPreferences = updateGlobalPreferences(currentPreferences);

        afterUpdateGlobalPreferences(currentPreferences, globalPreferencesDto);

        return currentPreferences;
    }

    private void validateIndustry(IndustryType industry) throws ParameterException {
        ParameterValidator.requireNonNull(industry, GlobalPreferencesMessages.INDUSTRY_CANNOT_BE_NULL);

        if(!ImplementedIndustries.isIndustryImplemented(industry)){
            throw new ParameterException(MessageFormat.format(GlobalPreferencesMessages.INDUSTRY_HAS_NOT_IMPLEMENTED, industry));
        }
    }

    public GlobalPreferencesEntity updateLabelOverridedStatus(Boolean labelsOverrided) throws ParameterException {
        ParameterValidator.requireNonNull(labelsOverrided, GlobalPreferencesMessages.LABEL_OVERRIDED_CANNOT_BE_NULL);

        GlobalPreferencesEntity globalPreferencesEntity = null;
        try {
            globalPreferencesEntity = getCurrentGlobalPreferences();
            globalPreferencesEntity.setLabelsOverrided(labelsOverrided);
        } catch (GlobalPreferencesNotFoundException | GlobalPreferencesMoreThanOneException e) {
            // Generally, can not reach here.
            e.printStackTrace();
        }

        return updateGlobalPreferences(globalPreferencesEntity);
    }

    public boolean getLabelOverridedStatus(){
        boolean labelsOverrided = false;
        try {
            labelsOverrided = getCurrentGlobalPreferences().isLabelsOverrided();
        } catch (GlobalPreferencesNotFoundException | GlobalPreferencesMoreThanOneException e) {
            // Generally, can not reach here.
            e.printStackTrace();
        }

        return labelsOverrided;
    }

    private GlobalPreferencesEntity updateGlobalPreferences(GlobalPreferencesEntity preferences) {
        preferences = globalPreferencesRepository.save(preferences);

        return preferences;
    }

    private void afterUpdateGlobalPreferences(GlobalPreferencesEntity preferences, GlobalPreferencesDTO globalPreferencesDTO) throws ParameterException {
        switchIndustry(preferences);

        restEndpointService.refreshEndpoint();

        refreshProxyDataSource(preferences);

        refreshInventoryQueueDestinations(preferences, globalPreferencesDTO);

        // TODO switch other settings
    }

    private void refreshProxyDataSource(GlobalPreferencesEntity currentPreferences){
        String virtualizeServerUrl = currentPreferences.getParasoftVirtualizeServerUrl();
        String virtualizeServerPath = currentPreferences.getParasoftVirtualizeServerPath();
        String virtualizeGroupId = currentPreferences.getParasoftVirtualizeGroupId();
        if(currentPreferences.getUseParasoftJDBCProxy()){
            if(StringUtils.isBlank(virtualizeServerUrl)){
                virtualizeServerUrl = PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_DEFAULT_VALUE;
            }

            if(StringUtils.isBlank(virtualizeServerPath)){
                virtualizeServerPath = PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_PATH_DEFAULT_VALUE;
            }

            if(StringUtils.isBlank(virtualizeGroupId)){
                virtualizeGroupId = PARASOFT_JDBC_PROXY_VIRTUALIZE_GROUP_ID_DEFAULT_VALUE;
            }

            IndustryRoutingDataSource.useParasoftJDBCProxy = true;
            IndustryRoutingDataSource.isParasoftVirtualizeServerUrlConnected = true;
        }else{
            IndustryRoutingDataSource.useParasoftJDBCProxy = false;
            IndustryRoutingDataSource.isParasoftVirtualizeServerUrlConnected = false;
        }

        IndustryRoutingDataSource.parasoftVirtualizeServerUrl = virtualizeServerUrl;
        IndustryRoutingDataSource.parasoftVirtualizeServerPath = virtualizeServerPath;
        IndustryRoutingDataSource.parasoftVirtualizeGroupId = virtualizeGroupId;

        parasoftJDBCProxyService.refreshParasoftJDBCProxyDataSource();
    }

    private void refreshInventoryQueueDestinations(GlobalPreferencesEntity currentPreferences, GlobalPreferencesDTO globalPreferencesDto) throws ParameterException {
        Boolean mqProxyEnabled = globalPreferencesDto.getMqProxyEnabled();
        ParameterValidator.requireNonNull(mqProxyEnabled, "Mq enabled must not be null");

        MqType mqType = globalPreferencesDto.getMqType();
        String orderServiceDestinationQueue = globalPreferencesDto.getOrderServiceDestinationQueue();
        String orderServiceReplyToQueue = globalPreferencesDto.getOrderServiceReplyToQueue();
        String inventoryServiceReplyToQueue = globalPreferencesDto.getInventoryServiceReplyToQueue();
        String inventoryServiceDestinationQueue = globalPreferencesDto.getInventoryServiceDestinationQueue();

        if(mqProxyEnabled) {
            ParameterValidator.requireNonNull(mqType, "Mq type must not be null");
            ParameterValidator.requireNonBlank(orderServiceDestinationQueue, GlobalPreferencesMessages.ORDER_SERVICE_DESTINATION_QUEUE_CANNOT_BE_NULL);
            ParameterValidator.requireNonBlank(orderServiceReplyToQueue, GlobalPreferencesMessages.ORDER_SERVICE_REPLY_TO_QUEUE_CANNOT_BE_NULL);
            ParameterValidator.requireNonBlank(inventoryServiceReplyToQueue, GlobalPreferencesMessages.INVENTORY_SERVICE_REPLY_TO_QUEUE_CANNOT_BE_NULL);
            ParameterValidator.requireNonBlank(inventoryServiceDestinationQueue, GlobalPreferencesMessages.INVENTORY_SERVICE_DESTINATION_QUEUE_CANNOT_BE_NULL);
        }

        if(mqType == null) {
            mqType = MqType.ACTIVE_MQ;
        }

        currentPreferences.setMqProxyEnabled(mqProxyEnabled);
        currentPreferences.setMqType(mqType);
        currentPreferences.setOrderServiceDestinationQueue(orderServiceDestinationQueue);
        currentPreferences.setOrderServiceReplyToQueue(orderServiceReplyToQueue);
        currentPreferences.setInventoryServiceReplyToQueue(inventoryServiceReplyToQueue);
        currentPreferences.setInventoryServiceDestinationQueue(inventoryServiceDestinationQueue);

        if(!mqProxyEnabled) {
            ActiveMQConfig.resetInventoryActiveMqQueues();
            inventoryRequestQueueListener.refreshDestination(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_REQUEST);
            inventoryResponseQueueListener.refreshDestination(ActiveMQConfig.DEFAULT_QUEUE_INVENTORY_RESPONSE);
            return;
        }

        ActiveMQConfig.setInventoryRequestActiveMqQueue(new ActiveMQQueue(orderServiceDestinationQueue));
        inventoryResponseQueueListener.refreshDestination(orderServiceReplyToQueue);

        ActiveMQConfig.setInventoryResponseActiveMqQueue(new ActiveMQQueue(inventoryServiceReplyToQueue));
        inventoryRequestQueueListener.refreshDestination(inventoryServiceDestinationQueue);
    }

    private void handleParasoftJDBCProxy(GlobalPreferencesEntity currentPreferences,
                                         GlobalPreferencesDTO globalPreferencesDto)
                                            throws VirtualizeServerUrlException, ParameterException {

        boolean useParasoftJDBCProxy = globalPreferencesDto.getUseParasoftJDBCProxy()
                                                    == null ? false : globalPreferencesDto.getUseParasoftJDBCProxy();
        currentPreferences.setUseParasoftJDBCProxy(useParasoftJDBCProxy);

        String virtualizeServerUrl = globalPreferencesDto.getParasoftVirtualizeServerUrl();
        String virtualizeServerPath = globalPreferencesDto.getParasoftVirtualizeServerPath();
        String virtualizeGroupId = globalPreferencesDto.getParasoftVirtualizeGroupId();

        if(useParasoftJDBCProxy){
            if(StringUtils.isBlank(virtualizeServerUrl)){
                virtualizeServerUrl = PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_DEFAULT_VALUE;
            }

            if(StringUtils.isBlank(virtualizeServerPath)){
                virtualizeServerPath = PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_PATH_DEFAULT_VALUE;
            }

            if(StringUtils.isBlank(virtualizeGroupId)){
                virtualizeGroupId = PARASOFT_JDBC_PROXY_VIRTUALIZE_GROUP_ID_DEFAULT_VALUE;
            }

            parasoftJDBCProxyService.validateVirtualizeServerUrl(virtualizeServerUrl);

            parasoftJDBCProxyService.validateVirtualizeServerPath(virtualizeServerPath);

            parasoftJDBCProxyService.validateVirtualizeGroupId(virtualizeGroupId);
        }

        currentPreferences.setParasoftVirtualizeServerUrl(globalPreferencesDto.getParasoftVirtualizeServerUrl());
        currentPreferences.setParasoftVirtualizeServerPath(globalPreferencesDto.getParasoftVirtualizeServerPath());
        currentPreferences.setParasoftVirtualizeGroupId(globalPreferencesDto.getParasoftVirtualizeGroupId());
    }

    private void handleDemoBugs(GlobalPreferencesEntity currentPreferences, GlobalPreferencesDTO globalPreferencesDto){
        // handle bugs
        demoBugService.removeByGlobalPreferencesId(currentPreferences.getId()); // remove existed endpoints

        Set<DemoBugEntity> demoBugs = new TreeSet<>(new BugsTypeSortOfDemoBugs()); // add new bugs
        if(globalPreferencesDto.getDemoBugs() != null){
            for(DemoBugsType demoBugsType : globalPreferencesDto.getDemoBugs()){
                DemoBugEntity demoBug = new DemoBugEntity(demoBugsType);
                demoBug.setGlobalPreferences(currentPreferences);
                demoBugs.add(demoBug);
            }
        }

        currentPreferences.setDemoBugs(demoBugs);
    }

    private void handleRestEndpoints(GlobalPreferencesEntity currentPreferences,
                    GlobalPreferencesDTO globalPreferencesDto) throws EndpointInvalidException, ParameterException {

        // handle endpoints
        restEndpointService.removeAllEndpoints(); // remove existed endpoints

        Set<RestEndpointEntity> endpoints = new TreeSet<>(new RouteIdSortOfRestEndpoint()); // add new endpoints
        String categoriesRestEndpoint = globalPreferencesDto.getCategoriesRestEndpoint();
        if(!StringUtils.isBlank(categoriesRestEndpoint)){
            restEndpointService.validateUrl(categoriesRestEndpoint, GlobalPreferencesMessages.INVALID_CATEGORIES_URL);
            endpoints.add(new RestEndpointEntity(CATEGORIES_ENDPOINT_ID, CATEGORIES_ENDPOINT_PATH,
                    categoriesRestEndpoint, currentPreferences));
        }

        String itemsRestEndpoint = globalPreferencesDto.getItemsRestEndpoint();
        if(!StringUtils.isBlank(itemsRestEndpoint)){
            restEndpointService.validateUrl(itemsRestEndpoint, GlobalPreferencesMessages.INVALID_ITEMS_URL);
            endpoints.add(new RestEndpointEntity(ITEMS_ENDPOINT_ID, ITEMS_ENDPOINT_PATH,
                    itemsRestEndpoint, currentPreferences));
        }

        String cartItemsRestEndpoint = globalPreferencesDto.getCartItemsRestEndpoint();
        if(!StringUtils.isBlank(cartItemsRestEndpoint)){
            restEndpointService.validateUrl(cartItemsRestEndpoint, GlobalPreferencesMessages.INVALID_CART_ITEMS_URL);
            endpoints.add(new RestEndpointEntity(CART_ENDPOINT_ID, CART_ENDPOINT_PATH,
                    cartItemsRestEndpoint, currentPreferences));
        }

        String ordersRestEndpoint = globalPreferencesDto.getOrdersRestEndpoint();
        if(!StringUtils.isBlank(ordersRestEndpoint)){
            restEndpointService.validateUrl(ordersRestEndpoint, GlobalPreferencesMessages.INVALID_ORDERS_URL);
            endpoints.add(new RestEndpointEntity(ORDERS_ENDPOINT_ID, ORDERS_ENDPOINT_PATH,
                    ordersRestEndpoint, currentPreferences));
        }

        String locationsRestEndpoint = globalPreferencesDto.getLocationsRestEndpoint();
        if(!StringUtils.isBlank(locationsRestEndpoint)){
            restEndpointService.validateUrl(locationsRestEndpoint, GlobalPreferencesMessages.INVALID_LOCATIONS_URL);
            endpoints.add(new RestEndpointEntity(LOCATIONS_ENDPOINT_ID, LOCATIONS_ENDPOINT_PATH,
                    locationsRestEndpoint, currentPreferences));
        }

        currentPreferences.setRestEndPoints(endpoints);
    }

    private void switchIndustry(GlobalPreferencesEntity currentPreferences) {
    	IndustryRoutingDataSource.currentIndustry = currentPreferences.getIndustryType();
    }

    public IndustryType getDefaultIndustry() {
    	return defaultGlobalPreferencesSettingsService.defaultIndustry();
    }

    public GlobalPreferencesEntity getCurrentGlobalPreferences()
            throws GlobalPreferencesNotFoundException, GlobalPreferencesMoreThanOneException {

        List<GlobalPreferencesEntity> currentIndustries =
                globalPreferencesRepository.findAll();

        int size = currentIndustries.size();
        if(size == 0){
            throw new GlobalPreferencesNotFoundException(
                    GlobalPreferencesMessages.THERE_IS_NO_CURRENT_GLOBAL_PREFERENCES);
        }
        if(size > 1){
            throw new GlobalPreferencesMoreThanOneException(
                    MessageFormat.format(GlobalPreferencesMessages.THERE_ARE_MORE_THAN_ONE_PREFERENCES, size));
        }
        return currentIndustries.get(0);
    }

    public IndustryType getCurrentIndustry()
            throws GlobalPreferencesNotFoundException, GlobalPreferencesMoreThanOneException {

    	return getCurrentGlobalPreferences().getIndustryType();
    }

    public void initializeDatabase() {
        // TODO initialize database
    }

    /**
     * to reset all industries database (excluding global preferences)
     */
    public void resetAllIndustriesDatabase() {
        resetEntrance.run();

        imageService.removeAllIndustriesUploadedImages();
    }

    /**
     * to clear current industry database (excluding global preferences)
     */
	public void clearCurrentIndustryDatabase() {
		clearEntrance.run();

        imageService.removeSpecificIndustryUploadedImages(IndustryRoutingDataSource.currentIndustry);
	}

    public void shutdownJMSService() {
        // TODO shutdown JMS service
    }

}
