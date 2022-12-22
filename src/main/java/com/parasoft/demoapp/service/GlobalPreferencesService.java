package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.ImplementedIndustries;
import com.parasoft.demoapp.config.MQConfig;
import com.parasoft.demoapp.config.activemq.ActiveMQConfig;
import com.parasoft.demoapp.config.activemq.ActiveMQInventoryRequestQueueListener;
import com.parasoft.demoapp.config.activemq.ActiveMQInventoryResponseQueueListener;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.config.kafka.KafkaConfig;
import com.parasoft.demoapp.config.kafka.KafkaInventoryRequestTopicListener;
import com.parasoft.demoapp.config.kafka.KafkaInventoryResponseTopicListener;
import com.parasoft.demoapp.config.rabbitmq.RabbitMQConfig;
import com.parasoft.demoapp.defaultdata.ClearEntrance;
import com.parasoft.demoapp.defaultdata.ResetEntrance;
import com.parasoft.demoapp.dto.GlobalPreferencesDTO;
import com.parasoft.demoapp.dto.MQPropertiesResponseDTO;
import com.parasoft.demoapp.exception.*;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.model.global.preferences.*;
import com.parasoft.demoapp.repository.global.GlobalPreferencesRepository;
import com.parasoft.demoapp.util.BugsTypeSortOfDemoBugs;
import com.parasoft.demoapp.util.RouteIdSortOfRestEndpoint;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
    private EndpointService endpointService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ParasoftJDBCProxyService parasoftJDBCProxyService;

    @Autowired
    private ActiveMQInventoryResponseQueueListener activeMQInventoryResponseQueueListener;

    @Autowired
    private ActiveMQInventoryRequestQueueListener activeMQInventoryRequestQueueListener;

    @Autowired
    private KafkaInventoryRequestTopicListener kafkaInventoryRequestTopicListener;

    @Autowired
    private KafkaInventoryResponseTopicListener kafkaInventoryResponseTopicListener;

    @Autowired
    private GlobalPreferencesDefaultSettingsService defaultGlobalPreferencesSettingsService;

    @Autowired
    private ActiveMQConfig activeMQConfig;

    @Autowired
    private KafkaConfig kafkaConfig;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    /**
     * A flag indicating whether the destinations of the mq need to be updated when the global preference is updated.
    * */
    private boolean mqStatusChanged = false;


    /**
    * This method is used only when PDA startup first time(it means no db files are created),
     * do not use it for other purposes since there are no validations for parameters.
    */
    public GlobalPreferencesEntity addNewGlobalPreferences(DataAccessMode dataAccessMode, String soapEndPoint,
                                                           Set<RestEndpointEntity> restEndpoints,
                                                           IndustryType industryType, WebServiceMode webServiceMode,
                                                           String graphQLEndpoint, Set<DemoBugEntity> demoBugs,
                                                           Boolean advertisingEnabled, Boolean useParasoftJDBCProxy,
                                                           String parasoftVirtualizeServerUrl, String parasoftVirtualizeServerPath,
                                                           String parasoftVirtualizeGroupId,
                                                           MqType mqType,
                                                           String orderServiceActiveMqRequestQueue,
                                                           String orderServiceActiveMqResponseQueue,
                                                           String orderServiceKafkaRequestTopic,
                                                           String orderServiceKafkaResponseTopic,
                                                           String orderServiceRabbitMqRequestQueue,
                                                           String orderServiceRabbitMqResponseQueue) throws ParameterException {

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
                                                            webServiceMode,
                                                            graphQLEndpoint,
                                                            demoBugs,
                                                            advertisingEnabled,
                                                            useParasoftJDBCProxy,
                                                            parasoftVirtualizeServerUrl,
                                                            parasoftVirtualizeServerPath,
                                                            parasoftVirtualizeGroupId,
                                                            mqType,
                                                            orderServiceActiveMqRequestQueue,
                                                            orderServiceActiveMqResponseQueue,
                                                            orderServiceKafkaRequestTopic,
                                                            orderServiceKafkaResponseTopic,
                                                            orderServiceRabbitMqRequestQueue,
                                                            orderServiceRabbitMqResponseQueue);

        for(DemoBugEntity demoBug : demoBugs){
            demoBug.setGlobalPreferences(newGlobalPreferences);
        }

        return globalPreferencesRepository.save(newGlobalPreferences);
    }

    @Transactional(transactionManager = "globalTransactionManager",
            rollbackFor = {ParameterException.class, VirtualizeServerUrlException.class})
    public synchronized GlobalPreferencesEntity updateGlobalPreferences(GlobalPreferencesDTO globalPreferencesDto)
            throws GlobalPreferencesNotFoundException, GlobalPreferencesMoreThanOneException, ParameterException,
            EndpointInvalidException, VirtualizeServerUrlException, KafkaServerIsNotAvailableException {

        IndustryType industry = globalPreferencesDto.getIndustryType();
        validateIndustry(industry);

        Boolean advertisingEnabled =
                globalPreferencesDto.getAdvertisingEnabled() != null && globalPreferencesDto.getAdvertisingEnabled();

        GlobalPreferencesEntity currentPreferences = getCurrentGlobalPreferences();
        currentPreferences.setIndustryType(industry);
        currentPreferences.setAdvertisingEnabled(advertisingEnabled);

        handleDemoBugs(currentPreferences, globalPreferencesDto);

        handleEndpoints(currentPreferences, globalPreferencesDto);

        handleParasoftJDBCProxy(currentPreferences, globalPreferencesDto);

        handleMq(currentPreferences, globalPreferencesDto);

        currentPreferences = updateGlobalPreferences(currentPreferences);

        afterUpdateGlobalPreferences(currentPreferences);

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

    private void afterUpdateGlobalPreferences(GlobalPreferencesEntity preferences) {
        switchIndustry(preferences);

        endpointService.refreshEndpoint();

        refreshProxyDataSource(preferences);

        if(mqStatusChanged) {
            refreshInventoryDestinations(preferences);
        }

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

    private void refreshInventoryDestinations(GlobalPreferencesEntity currentPreferences){
        stopMQListenersExcept(currentPreferences.getMqType());
        if(MqType.ACTIVE_MQ == currentPreferences.getMqType()) {
            MQConfig.currentMQType = MqType.ACTIVE_MQ;
            ActiveMQConfig.setOrderServiceSendToQueue(new ActiveMQQueue(currentPreferences.getOrderServiceActiveMqRequestQueue()));
            activeMQInventoryResponseQueueListener.refreshDestination(currentPreferences.getOrderServiceActiveMqResponseQueue());
            activeMQInventoryRequestQueueListener.refreshDestination(ActiveMQConfig.getInventoryServiceListenToQueue());
        } else if(MqType.KAFKA == currentPreferences.getMqType()) {
            MQConfig.currentMQType = MqType.KAFKA;
            KafkaConfig.setOrderServiceSendToTopic(currentPreferences.getOrderServiceKafkaRequestTopic());
            kafkaInventoryRequestTopicListener.refreshDestination(KafkaConfig.DEFAULT_ORDER_SERVICE_REQUEST_TOPIC);
            kafkaInventoryResponseTopicListener.refreshDestination(currentPreferences.getOrderServiceKafkaResponseTopic());
        } else if(MqType.RABBIT_MQ == currentPreferences.getMqType()) {
            MQConfig.currentMQType = MqType.RABBIT_MQ;
            // TODO
        } else {
            throw new UnsupportedOperationException("Unsupported MQ type: " + currentPreferences.getMqType());
        }
    }

    /**
     * This method should only be used on startup.
     *
     * @throws ParameterException
     */
    public void initializeMqOnStartup(GlobalPreferencesEntity globalPreferences)
            throws ParameterException {
        MQConfig.currentMQType = globalPreferences.getMqType();
        GlobalPreferencesDTO globalPreferencesDto = new GlobalPreferencesDTO();
        globalPreferencesDto.setMqType(globalPreferences.getMqType());
        if (globalPreferences.getMqType() == MqType.ACTIVE_MQ) {
            globalPreferencesDto.setOrderServiceSendTo(globalPreferences.getOrderServiceActiveMqRequestQueue());
            globalPreferencesDto.setOrderServiceListenOn(globalPreferences.getOrderServiceActiveMqResponseQueue());
            validateMqConfig(globalPreferencesDto);
            ActiveMQConfig.setOrderServiceSendToQueue(new ActiveMQQueue(
                    globalPreferences.getOrderServiceActiveMqRequestQueue()));
            ActiveMQConfig.setOrderServiceListenToQueue(globalPreferences.getOrderServiceActiveMqResponseQueue());
        } else if (globalPreferences.getMqType() == MqType.KAFKA) {
            globalPreferencesDto.setOrderServiceSendTo(globalPreferences.getOrderServiceKafkaRequestTopic());
            globalPreferencesDto.setOrderServiceListenOn(globalPreferences.getOrderServiceKafkaResponseTopic());
            validateMqConfig(globalPreferencesDto);
            KafkaConfig.setOrderServiceSendToTopic(globalPreferences.getOrderServiceKafkaRequestTopic());
            KafkaConfig.setOrderServiceListenToTopic(globalPreferences.getOrderServiceKafkaResponseTopic());
        }
    }

    private void handleParasoftJDBCProxy(GlobalPreferencesEntity currentPreferences,
                                         GlobalPreferencesDTO globalPreferencesDto)
                                            throws VirtualizeServerUrlException, ParameterException {

        boolean useParasoftJDBCProxy = globalPreferencesDto.getUseParasoftJDBCProxy() != null && globalPreferencesDto.getUseParasoftJDBCProxy();
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

    private void handleEndpoints(GlobalPreferencesEntity currentPreferences,
                                     GlobalPreferencesDTO globalPreferencesDto) throws EndpointInvalidException, ParameterException {
        WebServiceMode webServiceMode = globalPreferencesDto.getWebServiceMode();
        ParameterValidator.requireNonNull(webServiceMode, GlobalPreferencesMessages.WEBSERVICEMODE_MUST_NOT_BE_NULL);

        currentPreferences.setWebServiceMode(webServiceMode);
        if (WebServiceMode.GRAPHQL.equals(webServiceMode)){
            String graphQLEndpoint = globalPreferencesDto.getGraphQLEndpoint();
            if (!StringUtils.isBlank(graphQLEndpoint)) {
                endpointService.validateUrl(graphQLEndpoint, GlobalPreferencesMessages.INVALID_GRAPHQL_URL);
            } else {
                graphQLEndpoint = "";
            }
            currentPreferences.setGraphQLEndpoint(graphQLEndpoint);
            return;
        }
        // handle endpoints
        restEndpointService.removeAllEndpoints(); // remove existed endpoints

        Set<RestEndpointEntity> endpoints = new TreeSet<>(new RouteIdSortOfRestEndpoint()); // add new endpoints
        String categoriesRestEndpoint = globalPreferencesDto.getCategoriesRestEndpoint();
        if(!StringUtils.isBlank(categoriesRestEndpoint)){
            endpointService.validateUrl(categoriesRestEndpoint, GlobalPreferencesMessages.INVALID_CATEGORIES_URL);
            endpoints.add(new RestEndpointEntity(CATEGORIES_ENDPOINT_ID, CATEGORIES_ENDPOINT_PATH,
                    categoriesRestEndpoint, currentPreferences));
        }

        String itemsRestEndpoint = globalPreferencesDto.getItemsRestEndpoint();
        if(!StringUtils.isBlank(itemsRestEndpoint)){
            endpointService.validateUrl(itemsRestEndpoint, GlobalPreferencesMessages.INVALID_ITEMS_URL);
            endpoints.add(new RestEndpointEntity(ITEMS_ENDPOINT_ID, ITEMS_ENDPOINT_PATH,
                    itemsRestEndpoint, currentPreferences));
        }

        String cartItemsRestEndpoint = globalPreferencesDto.getCartItemsRestEndpoint();
        if(!StringUtils.isBlank(cartItemsRestEndpoint)){
            endpointService.validateUrl(cartItemsRestEndpoint, GlobalPreferencesMessages.INVALID_CART_ITEMS_URL);
            endpoints.add(new RestEndpointEntity(CART_ENDPOINT_ID, CART_ENDPOINT_PATH,
                    cartItemsRestEndpoint, currentPreferences));
        }

        String ordersRestEndpoint = globalPreferencesDto.getOrdersRestEndpoint();
        if(!StringUtils.isBlank(ordersRestEndpoint)){
            endpointService.validateUrl(ordersRestEndpoint, GlobalPreferencesMessages.INVALID_ORDERS_URL);
            endpoints.add(new RestEndpointEntity(ORDERS_ENDPOINT_ID, ORDERS_ENDPOINT_PATH,
                    ordersRestEndpoint, currentPreferences));
        }

        String locationsRestEndpoint = globalPreferencesDto.getLocationsRestEndpoint();
        if(!StringUtils.isBlank(locationsRestEndpoint)){
            endpointService.validateUrl(locationsRestEndpoint, GlobalPreferencesMessages.INVALID_LOCATIONS_URL);
            endpoints.add(new RestEndpointEntity(LOCATIONS_ENDPOINT_ID, LOCATIONS_ENDPOINT_PATH,
                    locationsRestEndpoint, currentPreferences));
        }

        currentPreferences.setRestEndPoints(endpoints);
    }

    private void handleMq(GlobalPreferencesEntity currentPreferences,
                               GlobalPreferencesDTO globalPreferencesDto) throws ParameterException, KafkaServerIsNotAvailableException {
        validateMqConfig(globalPreferencesDto);
        if(globalPreferencesDto.getMqType() == MqType.KAFKA) {
            validateKafkaBrokerUrl();
        }
        checkMqStatusHasChanged(currentPreferences, globalPreferencesDto);
        currentPreferences.setMqType(globalPreferencesDto.getMqType());
        String orderServiceSendTo = globalPreferencesDto.getOrderServiceSendTo();
        String orderServiceListenOn = globalPreferencesDto.getOrderServiceListenOn();
        if (globalPreferencesDto.getMqType() == MqType.ACTIVE_MQ) {
            currentPreferences.setOrderServiceActiveMqRequestQueue(orderServiceSendTo);
            currentPreferences.setOrderServiceActiveMqResponseQueue(orderServiceListenOn);
        } else if (globalPreferencesDto.getMqType() == MqType.KAFKA) {
            currentPreferences.setOrderServiceKafkaRequestTopic(orderServiceSendTo);
            currentPreferences.setOrderServiceKafkaResponseTopic(orderServiceListenOn);
        } else if (globalPreferencesDto.getMqType() == MqType.RABBIT_MQ) {
            currentPreferences.setOrderServiceRabbitMqRequestQueue(orderServiceSendTo);
            currentPreferences.setOrderServiceRabbitMqResponseQueue(orderServiceListenOn);
        }
    }

    public void checkMqStatusHasChanged(GlobalPreferencesEntity currentPreferences, GlobalPreferencesDTO globalPreferencesDto) {
        String orderServiceSendTo = globalPreferencesDto.getOrderServiceSendTo();
        String orderServiceListenOn = globalPreferencesDto.getOrderServiceListenOn();
        if (globalPreferencesDto.getMqType() == MqType.ACTIVE_MQ){
            mqStatusChanged = !(globalPreferencesDto.getMqType() == currentPreferences.getMqType()) ||
                    !(orderServiceSendTo.equals(currentPreferences.getOrderServiceActiveMqRequestQueue())) ||
                    !(orderServiceListenOn.equals(currentPreferences.getOrderServiceActiveMqResponseQueue()));
        } else if (globalPreferencesDto.getMqType() == MqType.KAFKA) {
            mqStatusChanged = !(globalPreferencesDto.getMqType() == currentPreferences.getMqType()) ||
                    !(orderServiceSendTo.equals(currentPreferences.getOrderServiceKafkaRequestTopic())) ||
                    !(orderServiceListenOn.equals(currentPreferences.getOrderServiceKafkaResponseTopic()));
        } else if (globalPreferencesDto.getMqType() == MqType.RABBIT_MQ) {
            mqStatusChanged = !(globalPreferencesDto.getMqType() == currentPreferences.getMqType()) ||
                    !(orderServiceSendTo.equals(currentPreferences.getOrderServiceRabbitMqRequestQueue())) ||
                    !(orderServiceListenOn.equals(currentPreferences.getOrderServiceRabbitMqResponseQueue()));
        }
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

    private void validateMqConfig(GlobalPreferencesDTO globalPreferencesDto) throws ParameterException {
        ParameterValidator.requireNonNull(globalPreferencesDto.getMqType(), GlobalPreferencesMessages.MQTYPE_MUST_NOT_BE_NULL);
        ParameterValidator.requireNonBlank(globalPreferencesDto.getOrderServiceSendTo(), GlobalPreferencesMessages.ORDER_SERVICE_SEND_TO_CANNOT_BE_NULL);
        ParameterValidator.requireNonBlank(globalPreferencesDto.getOrderServiceListenOn(), GlobalPreferencesMessages.ORDER_SERVICE_LISTEN_ON_CANNOT_BE_NULL);
    }

    public void shutdownJMSService() {
        // TODO shutdown JMS service
    }

    private void stopMQListenersExcept(MqType mqType) {
        if(MqType.ACTIVE_MQ == mqType) {
            // Stop listeners on Kafka
            kafkaInventoryRequestTopicListener.stopAllListenedDestinations();
            kafkaInventoryResponseTopicListener.stopAllListenedDestinations();
        } else if(MqType.KAFKA == mqType) {
            // Stop listeners on ActiveMQ
            activeMQInventoryResponseQueueListener.stopAllListenedDestinations();
            activeMQInventoryRequestQueueListener.stopAllListenedDestinations();
        }
    }

    public MQPropertiesResponseDTO getMQProperties() {
        MQPropertiesResponseDTO.ActiveMQConfigResponse activeMQConfigResponse =
                new MQPropertiesResponseDTO.ActiveMQConfigResponse(
                        activeMQConfig.getBrokerUrl(),
                        activeMQConfig.getUsername(),
                        activeMQConfig.getPassword());
        MQPropertiesResponseDTO.KafkaConfigResponse kafkaConfigResponse =
                new MQPropertiesResponseDTO.KafkaConfigResponse(
                        kafkaConfig.getBootstrapServers(),
                        kafkaConfig.getGroupId()
                );
        MQPropertiesResponseDTO.RabbitMQConfigResponse rabbitMQConfigResponse =
                new MQPropertiesResponseDTO.RabbitMQConfigResponse(
                        rabbitMQConfig.getRabbitMqHost(),
                        rabbitMQConfig.getRabbitMqPort(),
                        rabbitMQConfig.getUsername(),
                        rabbitMQConfig.getPassword());

        return new MQPropertiesResponseDTO(activeMQConfigResponse, kafkaConfigResponse, rabbitMQConfigResponse);
    }

    public void validateKafkaBrokerUrl() throws KafkaServerIsNotAvailableException {
        AdminClient client = AdminClient.create(kafkaConfig.adminClientConfigs());
        try {
            client.listTopics(new ListTopicsOptions().timeoutMs(KafkaConfig.ADMIN_CLIENT_TIMEOUT_MS))
                    .names()
                    .get();
        } catch (Exception e) {
            throw new KafkaServerIsNotAvailableException(
                    MessageFormat.format(GlobalPreferencesMessages.KAFKA_SERVER_IS_NOT_AVAILABLE,
                            kafkaConfig.getBootstrapServers()), e);
        } finally {
            // To avoid trying to connect all the time
            client.close();
        }
    }

    public void validateRabbitMQServerUrl() throws RabbitMQServerIsNotAvailableException {
        Connection connection = null;
        try {
            connection = rabbitMQConfig.factory().createConnection();
        } catch (Exception e) {
            throw new RabbitMQServerIsNotAvailableException(
                    MessageFormat.format(GlobalPreferencesMessages.RABBITMQ_SERVER_IS_NOT_AVAILABLE,
                            rabbitMQConfig.getRabbitMqHost() + ":" + rabbitMQConfig.getRabbitMqPort()), e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
