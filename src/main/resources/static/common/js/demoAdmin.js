var mod = angular.module('pdaApp', ['pascalprecht.translate']);

setLocale(mod);
//initialize controllers of import page
initHeaderController(mod);
initProductBuildInfo(mod);
initAuthorizationHeader(mod);

initToastr();

var industry = angular.element("input[name='industryType']:checked").next().text().toUpperCase();
var bug_messesUp_value = "AGGREGATOR_API_MESSES_UP_ERROR_HANDLING";
var bug_extraOrdered_value = "AGGREGATOR_API_ADDS_EXTRA_TO_THE_QUANTITY_ORDERED";
var bug_javascriptError_value = "JAVASCRIPT_ERROR_WHEN_SORTING_ITEMS";
var bug_missingProvider_value = "PURCHASER_WEBSITE_IS_MISSING_ONE_OF_THE_EXTERNAL_PROVIDERS";
var bug_Incorrect_location_value = "INCORRECT_LOCATION_FOR_APPROVED_ORDERS";
var bug_Incorrect_number_value = "INCORRECT_NUMBER_OF_ITEMS_IN_SUMMARY_OF_PENDING_ORDER";
var bug_Reverse_orders_value = "REVERSE_ORDER_OF_ORDERS";
var bug_Reinitialize_datasource_for_each_http_request_value = "REINITIALIZE_DATASOURCE_FOR_EACH_HTTP_REQUEST";

mod.controller('demo_admin_controller', function($rootScope, $scope, $http, $filter, $window, $timeout, graphQLService) {
    var demo = this;
    demo.end_point_for_categories = "/proxy/v1/assets/categories/**";
    demo.end_point_for_items = "/proxy/v1/assets/items/**";
    demo.end_point_for_cartItems = "/proxy/v1/cartItems/**";
    demo.end_point_for_orders = "/proxy/v1/orders/**";
    demo.end_point_for_locations = "/proxy/v1/locations/**";
    demo.end_point_for_graphql = "/proxy/graphql";

    $rootScope.isShowSettingButton = false;
    $rootScope.isShowRequisitionButton = false;
    $rootScope.isShowRequisitionRequestButton = false;

    var flag = localStorage.getItem("status");
    var databaseResetFlag = localStorage.getItem("databaseResetStatus");
    connectAndSubscribeMQ(CURRENT_ROLE, $http, $rootScope, $filter, null, null, graphQLService);

    demo.GENERAL = "active";
    //Get regions
    getAllRegions();

    demo.changeToGeneral = function(){
        if(demo.GENERAL !== "active"){
            demo.GENERAL = "active";
            demo.CATEGORIES = "";
            demo.ITEMS = "";
            demo.LABELS = "";
            demo.isShowImgModal = false;
        }
    }

    demo.userAccountsInfo = [
        "purchaser / password; approver / password",
        "purchaser2 / password; approver2 / password",
        "...",
        "purchaser50 / password; approver50 / password"
    ];

    demo.changeToCategories = function(){
        if(demo.CATEGORIES !== "active"){
            demo.CATEGORIES = "active";
            demo.GENERAL = "";
            demo.ITEMS = "";
            demo.LABELS = "";
            demo.isShowImgModal = false;

            // get all categories
            getAllCategories();
        }
    }

    demo.changeToItems = function(){
        if(demo.ITEMS !== "active"){
            demo.ITEMS = "active";
            demo.CATEGORIES = "";
            demo.GENERAL = "";
            demo.LABELS = "";
            demo.isShowImgModal = false;

            //Get all items
            getAllItems();
        }
    }

    demo.changeToLabels = function(){
        if(demo.INDUSTRY !== "active"){
            demo.ITEMS = "";
            demo.CATEGORIES = "";
            demo.GENERAL = "";
            demo.LABELS = "active";
            demo.isShowImgModal = false;

            //Get all overrided labels
            getOverridedLabels();
            //Get all default labels
            getDefaultLabels();
        }
    }

    function getAllItems() {
        let getCategoriesSuccess = (data) => {
            demo.modalCategories = data.content;
            $.each(demo.modalCategories,function(i, category) {
                if (!demo.categoryMap) {
                    demo.categoryMap = new Map();
                }
                demo.categoryMap.set(category.id,category.name);
            }.bind(this));
        };

        let getItemsError = (data, endpointType) => {
            console.info(data);
            displayLoadError(data, $rootScope, $filter, $http, true, endpointType);
        };

        let getItemsSuccess = (dataItems, dataCategories, isGraphQL) => {
            demo.items = dataItems.content;

            //Get category name and store in a map
            if (isGraphQL) {
                getCategoriesSuccess(dataCategories);
            } else {
                $http({
                    method: 'GET',
                    url: '/proxy/v1/assets/categories',
                }).then(function(result) {
                    getCategoriesSuccess(result.data.data);
                }).catch(function(result) {
                    getItemsError(result, 'categories');
                });
            }
        };

        if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            graphQLService.makeGraphQLCall(
                {"query": "query GetItemsCombinedCategories{getCategories{content{id,name}},getItems{content{id,name,description,inStock,image,region,categoryId}}}"},
                function(response) {
                    getItemsSuccess(response.data.data.getItems, response.data.data.getCategories, true);
                },
                function(response) {
                    getItemsError(response, "graphQL")
                }
            );
        } else {
            $http({
                method: 'GET',
                url: '/proxy/v1/assets/items',
            }).then(function(response) {
                getItemsSuccess(response.data.data);
            }).catch(function (response) {
                getItemsError(response, "items");
            });
        }
    }

    function getAllCategories(){
        let success = (data) => {
            categories = data.content;
            demo.categories = categories;
        };
        let error = (data, endpointType) => {
            console.info(data);
            displayLoadError(data,$rootScope,$filter,$http,true,endpointType);
        };
        if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            graphQLService.getCategories(success, (data) => {error(data, "graphQL")});
        } else {
            $http({
                method: 'GET',
                url: '/proxy/v1/assets/categories',
            }).then(function(result) {
                success(result.data.data);
            }).catch(function(result) {
                error(result, "categories");
            });
        }
    }

    function getAllRegions(){
        let getAllRegionsSuccess = (data) => {
            demo.regions = data;
        };
        let getAllRegionsError = (result, endpointType) => {
            console.info(result);
            displayLoadError(result,$rootScope,$filter,$http,true,endpointType);
        };
        if(CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            graphQLService.getAllRegionTypesOfCurrentIndustry(getAllRegionsSuccess, (data) => {getAllRegionsError(data, "graphQL")})
        } else {
            $http({
                method: 'GET',
                url: '/proxy/v1/locations/regions',
            }).then(function(result) {
                getAllRegionsSuccess(result.data.data);
            }).catch(function(result) {
                getAllRegionsError(result, "locations");
            });
        }
    }

    function getOverridedLabels(){
        $http({
            method: 'GET',
            url: '/v1/labels/overrided?language='+$rootScope.lang,
        }).then(function(result) {
            demo.overridedLabels = result.data.data;
            demo.useLabelsOverrided = demo.overridedLabels.labelsOverrided;
        }).catch(function(result) {
            console.info(result);
        });
    }

    function getDefaultLabels(){
        $http({
            method: 'GET',
            url: '/v1/labels/default?language='+$rootScope.lang,
        }).then(function(result) {
            var defaultLabels = result.data.data;
            demo.defaultLabels = defaultLabels;
        }).catch(function(result) {
            console.info(result);
        });
    }

    demo.resetLabel = function (labelName){
        demo.overridedLabels.labelPairs[labelName] = demo.defaultLabels.labelPairs[labelName];
    }

    if(flag === "true"){
        localStorage.setItem("status", "false");
        toastr.success(localStorage.getItem("save_succeeded"));
    }

    demo.validateEndpointUrlWellForm = function(url, type) {
        clearMessage(type);

        if(url === null || url === ""){
            return;
        }

        if(!validateUrlWellForm(url)){
            switch(type) {
                case "categories":
                    demo.endpointError_categories = true;
                    break;
                case "items":
                    demo.endpointError_items = true;
                    break;
                case "cart":
                    demo.endpointError_cart = true;
                    break;
                case "orders":
                    demo.endpointError_orders = true;
                    break;
                case "locations":
                    demo.endpointError_locations = true;
                    break;
                case "graphql":
                    demo.endpointError_graphql = true;
                    break;
                default:
            }
        }
    };

    validateUrlWellForm = function(url) {
        if(url === null || url === ""){
            return;
        }
        var regUrl = new RegExp();
        regUrl.compile("^(https?)://([a-zA-Z0-9-_]+.?)*[a-zA-Z0-9-_]+(((/[\\S]+))?/?)$");

        return regUrl.test(url);
    }

    clearMessage = function(type) {
        switch(type) {
        case "categories":
            demo.endpointError_categories = false;
            break;
        case "items":
            demo.endpointError_items = false;
            break;
        case "cart":
            demo.endpointError_cart = false;
            break;
        case "orders":
            demo.endpointError_orders = false;
            break;
        case "locations":
            demo.endpointError_locations = false;
            break;
        case "graphql":
            demo.endpointError_graphql = false;
            break;
        default:
        }
    }

    demo.validateKafkaBrokerUrl = function () {
        demo.isTestingKafkaBrokerUrl = true;
        $http({
            method: 'GET',
            url: '/v1/demoAdmin/kafkaBrokerUrlValidation'
        }).then(function success() {
            localStorage.setItem("displayKafkaError", "false");
            toastr.success($filter('translate')('CONNECT_KAFKA_BROKER_SUCCESS'));
        }, function error() {
            localStorage.setItem("displayKafkaError", "true");
            toastrService().error($filter('translate')('INVALID_KAFKA_SERVER_URL'));
        }).finally(function () {
            demo.isTestingKafkaBrokerUrl = false;
            $rootScope.displayKafkaError = localStorage.getItem("displayKafkaError");
        });
    }

    demo.validateVirtualizeServerUrl = function(url){
        demo.clearVirtualizeServerUrlTestMessage();
        demo.isVirtualizeServerUrlTesting = true;

        $http({
            method: 'GET',
            url: '/v1/demoAdmin/parasoftVirtualizeServerUrlValidation',
            params: {url : url},
        }).then(function(result) {
            demo.clearVirtualizeServerUrlTestMessage();
            demo.validVirtualizeServerUrl = true;
        }, function error(response) {
            console.info(response);
            demo.clearVirtualizeServerUrlTestMessage();
            demo.cannotConnectToVirtualizeServerUrl = true;
        }).catch(function(result) {
            console.info(result);
        });
    }

    demo.clearVirtualizeServerUrlTestMessage = function() {
        demo.isVirtualizeServerUrlTesting = false;
        demo.invalidVirtualizeServerUrl = false;
        demo.validVirtualizeServerUrl = false;
        demo.cannotConnectToVirtualizeServerUrl = false;
    }

    demo.validateVirtualizeServerUrlWellForm = function(virtualizeServerUrl){
        demo.clearVirtualizeServerUrlTestMessage();
        if(virtualizeServerUrl === null || virtualizeServerUrl === ""){
            return true;
        }

        if(!validateUrlWellForm(virtualizeServerUrl)){
            demo.invalidVirtualizeServerUrl = true;
        }
    }

    validatePathWellForm = function(virtualizeServerPath){

        if(virtualizeServerPath === null || virtualizeServerPath === ""){
            return true;
        }
        var reg = new RegExp();
        reg.compile("^/[a-zA-Z0-9-_]+$");

        return reg.test(virtualizeServerPath);
    }

    demo.clearVirtualizeServerPathTestMessage = function(){
        demo.invalidVirtualizeServerPath = false;
    }

    demo.validateVirtualizeServerPathWellForm = function(virtualizeServerPath){
        demo.clearVirtualizeServerPathTestMessage();

        if(!validatePathWellForm(virtualizeServerPath)){
            demo.invalidVirtualizeServerPath = true;
        }
    }

    validateGroupIdWellForm = function(virtualizegroupId){

        if(virtualizegroupId === null || virtualizegroupId === ""){
            return true;
        }
        var reg = new RegExp();
        reg.compile("^([a-zA-Z0-9-_]+)$");

        return reg.test(virtualizegroupId);
    }

    demo.clearVirtualizeGroupIdTestMessage = function(){
        demo.invalidVirtualizeGroupId = false;
    }

    demo.validateVirtualizeGroupIdWellForm = function(virtualizegroupId){
        demo.clearVirtualizeGroupIdTestMessage();

        if(!validateGroupIdWellForm(virtualizegroupId)){
            demo.invalidVirtualizeGroupId = true;
        }
    }

    demo.triggerParasoftJdbcProxy = function(check, virtualizeServerUrl, virtualizeServerpath, virtualizegroupId) {
        if(check){
            demo.validateVirtualizeServerUrlWellForm(virtualizeServerUrl);
            demo.validateVirtualizeServerPathWellForm(virtualizeServerpath);
            demo.validateVirtualizeGroupIdWellForm(virtualizegroupId);
        }else{
            demo.clearVirtualizeServerUrlTestMessage();
            demo.clearVirtualizeServerPathTestMessage();
            demo.clearVirtualizeGroupIdTestMessage();
        }
    }

    demo.disableSaveChangesButton = function(options) {
        if (options.webServiceMode === "GRAPHQL" && demo.endpointError_graphql) {
            return true;
        } else if(options.webServiceMode === "REST_API") {
            if(demo.endpointError_categories || demo.endpointError_categories || demo.endpointError_items ||
                demo.endpointError_cart || demo.endpointError_orders || demo.endpointError_locations) {
                return true;
            }
        }
        if (demo.invalidVirtualizeServerUrl || demo.cannotConnectToVirtualizeServerUrl ||
            demo.invalidVirtualizeServerPath || demo.invalidVirtualizeGroupId){
            return true;
        }

        return false;
    }

    demo.saveAll = function() {
        let data = angular.element('#options_form').serializeJSON()
        if (data.mqType === 'ACTIVE_MQ') {
            data.orderServiceSendTo = data.inventoryServiceActiveMqRequestQueue;
            data.orderServiceListenOn = data.inventoryServiceActiveMqResponseQueue;
        } else if (data.mqType === 'KAFKA') {
            data.orderServiceSendTo = data.inventoryServiceKafkaRequestTopic;
            data.orderServiceListenOn = data.inventoryServiceKafkaResponseTopic;
        } else if (data.mqType === 'RABBIT_MQ') {
            data.orderServiceSendTo = data.inventoryServiceRabbitMqRequestQueue;
            data.orderServiceListenOn = data.inventoryServiceRabbitMqResponseQueue;
        }
        $http({
            method: 'PUT',
            url: '/v1/demoAdmin/preferences',
            data: data,
            headers : { 'Content-Type': 'application/json' }
        }).then(function(result) {
            localStorage.setItem("status", "true");
            localStorage.setItem("save_succeeded", $filter('translate')('SAVING_SUCCEEDS'));
            localStorage.setItem("displayKafkaError", "false");
            $window.location.reload();
            $('#saving_modal').modal('hide');
        }).catch(function(response) {
            console.info(response);

            let responseMessage = response.data.message.toLowerCase();
            let errorMessage;
            if(responseMessage.indexOf("invalid categories url") > -1) {
                errorMessage = $filter('translate')('INVALID_CATEGORIES_URL');
            } else if (responseMessage.indexOf("invalid items url") > -1) {
                errorMessage = $filter('translate')('INVALID_ITEMS_URL');
            } else if (responseMessage.indexOf("invalid cart items url") > -1) {
                errorMessage = $filter('translate')('INVALID_CART_ITEMS_URL');
            } else if (responseMessage.indexOf("invalid orders url") > -1) {
                errorMessage = $filter('translate')('INVALID_ORDERS_URL');
            } else if (responseMessage.indexOf("invalid locations url") > -1) {
                errorMessage = $filter('translate')('INVALID_LOCATIONS_URL');
            } else if (responseMessage.indexOf("can not establish connection with virtualize server") > -1){
                errorMessage = $filter('translate')('INVALID_PARASOFT_VIRTUALIZE_SERVER_URL_1');
            } else if(responseMessage.indexOf('Invalid virtualize server path') > -1){
                errorMessage =  $filter('translate')('INVALID_PARASOFT_VIRTUALIZE_SERVER_PATH');
            } else if(responseMessage.indexOf('Invalid virtualize group id') > -1){
                errorMessage =  $filter('translate')('INVALID_PARASOFT_VIRTUALIZE_GROUP_ID');
            } else if(responseMessage.indexOf('can not establish connection with kafka broker') > -1){
                localStorage.setItem("displayKafkaError", "true");
                errorMessage =  $filter('translate')('INVALID_KAFKA_SERVER_URL');
            } else {
                errorMessage = responseMessage;
            }

            $('#saving_modal').modal('hide');
            localStorage.setItem("status", "false");
            toastrService().error($filter('translate')('SAVING_FAILS') + '<br/>' + errorMessage);
        }).finally(function() {
            $rootScope.displayKafkaError = localStorage.getItem("displayKafkaError");
        });
    };

    $rootScope.lang = getDefaultLang();

    demo.saveOverridedLabels = function() {
        $http({
            method: 'PUT',
            url: '/v1/labels',
            data: angular.element('#labels_form').serializeJSON(),
            headers : { 'Content-Type': 'application/json' }
        }).then(function(result) {
            localStorage.setItem("status", "true");
            localStorage.setItem("save_succeeded", $filter('translate')('SAVING_SUCCEEDS'));
            $window.location.reload();
        }, function error(response) {
            console.info(response);
            const errorCode = response.status;
            let errMsg;
            switch (errorCode) {
                case 400:
                    errMsg = $filter('translate')('UPDATE_LABEL_REQUEST_ERROR');
                    break;
                case 401:
                    errMsg = $filter('translate')('NO_AUTHORIZATION_TO_UPDATE_LABEL');
                    break;
                default:
                    errMsg = $filter('translate')('UPDATE_LABEL_ERROR');
            }
            toastrService().error(errMsg, $filter('translate')('SAVING_FAILS'));
        }).catch(function(result) {
            console.info(result);
        });
    }

    if(databaseResetFlag === "true"){
        localStorage.setItem("databaseResetStatus", "false");
        toastr.success(localStorage.getItem("database_reset_succeeded"));
    }
    demo.resetDatabase = function() {
        $http({
            method: 'PUT',
            url: '/v1/demoAdmin/databaseReset',
        }).then(function(result) {
            localStorage.setItem("databaseResetStatus", "true");
            localStorage.setItem("database_reset_succeeded", $filter('translate')('DATABASE_RESET_SUCCEEDS'));
            $window.location.reload();
        }, function error(response) {
            console.info(response);
            localStorage.setItem("databaseResetStatus", "false");
            toastrService().error($filter('translate')('DATABASE_RESET_FAILS'));
        }).catch(function(result) {
            console.info(result);
        });
    };

    demo.clearDatabase = function() {
        $http({
            method: 'PUT',
            url: '/v1/demoAdmin/databaseClear',
        }).then(function(result) {
            localStorage.setItem("databaseResetStatus", "true");
            localStorage.setItem("database_reset_succeeded", $filter('translate')('DATABASE_CLEAR_SUCCEEDS'));
            $window.location.reload();
        }, function error(response) {
            console.info(response);
            localStorage.setItem("databaseResetStatus", "false");
            toastrService().error($filter('translate')('DATABASE_CLEAR_FAILS'));
        }).catch(function(result) {
            console.info(result);
        });
    };

    demo.showDatabaseOperationConfirmModal = function(operation){
        if(operation === "reset"){
            demo.currentDatabaseOperation = "reset";
        }else if(operation === "clear"){
            demo.currentDatabaseOperation = "clear";
        }
    }

    demo.showImgModal = function(index,imageUrl){
        if(demo.isShowImgModal !== true){
            demo.isShowImgModal = true;
            demo.itemImg = imageUrl;
        }else if(Number(demo.index) !== Number(index)){
            demo.itemImg = imageUrl;
        }else{
            demo.isShowImgModal = false;
        }

        demo.index = index;
        demo.modalStyle = {
            "top": index * 42 + 327 + "px",
            "left": "440px"
        };
    }

    demo.closeImgModal = function(){
        demo.isShowImgModal = false;
    }

    demo.showRemoveConfirmModal = function(selected_item){
        if(selected_item.categoryId === undefined){
            demo.isCategory = true;
        }else{
            demo.isCategory = false;
        }
        demo.selected_item = selected_item;
    }

    demo.showItemModal = function(item){

        demo.itemModal = {
            showFormErrorBox : false,
            formError : '',
            showUploadErrorBox : false,
            uploadError : '',
            isAddNewItem : false,
            isEditItem : false,
            showUploadBox : false,
            showTmpImage : false,
            showErrorBox : false,
            disableSaveButton : true,
            disableUploadButton : true,
            tmpImage : '',
            chosenFile : '',
            form : {
                currentValue : {},
                defaultValue : {}
            },
            fieldsOnChange : function(){},
            fileChosenOnChange : function(element){},
            removeTmpImage : function(){},
            saveItem : function(itemId){},
            uploadImage : function(){}
        }

        document.getElementById("item_form").reset();
        document.getElementById("image_form").reset();

        if(item === undefined){
            demo.editItem = false
            demo.currentRegion = undefined;
            demo.currentCategory = undefined;
        }else{
            demo.editItem = true;
            demo.currentRegion = item.region.toUpperCase();
            demo.currentCategoryId = item.categoryId;
        }

        if(demo.editItem){
            demo.itemModal.isEditItem = true;
            demo.itemModal.isAddNewItem = false;
            demo.itemInModal = item;
        }else{
            demo.itemModal.isAddNewItem = true;
            demo.itemModal.isEditItem = false;
            demo.itemInModal = {
                'id' : '',
                'name' : '',
                'region' : '',
                'inStock' : '',
                'categoryId' : '',
                'image' : '',
                'description' : ''
            };
        }

        demo.itemModal.form = {
            currentValue : {
                'id' : demo.itemInModal.id,
                'name' : demo.itemInModal.name,
                'region' : demo.itemInModal.region,
                'inStock' : demo.itemInModal.inStock,
                'categoryId' : demo.itemInModal.categoryId + "",
                'image' : demo.itemInModal.image,
                'description' : demo.itemInModal.description
            },
            defaultValue : {
                'id' : demo.itemInModal.id,
                'name' : demo.itemInModal.name,
                'region' : demo.itemInModal.region,
                'inStock' : demo.itemInModal.inStock,
                'categoryId' : demo.itemInModal.categoryId + "",
                'image' : demo.itemInModal.image,
                'description' : demo.itemInModal.description
            }
        };

        if(demo.itemModal.isAddNewItem){
            demo.itemModal.showUploadBox = true;
            demo.itemModal.showTmpImage = false;
        }

        if(demo.itemModal.isEditItem){
            var currentImage = demo.itemModal.form.currentValue.image;
            demo.itemModal.tmpImage = currentImage
            if(currentImage !== ''){ // image in item
                demo.itemModal.showUploadBox = false;
                demo.itemModal.showTmpImage = true;
            }else{ // no image in item
                demo.itemModal.showUploadBox = true;
                demo.itemModal.showTmpImage = false;
            }
        }

        demo.itemModal.fileChosenOnChange = function(fileElement){
            demo.itemModal.form.currentValue.image = '';
            demo.itemModal.disableUploadButton= true;
            demo.itemModal.showUploadErrorBox = true;
            demo.itemModal.fieldsOnChange();

            var fileInfo = fileElement.files[0];
            if(fileInfo === undefined){
                demo.itemModal.showUploadErrorBox = false;
                demo.itemModal.fieldsOnChange();
                $scope.$apply();
                return;
            }

            var isCorrectFileSize = validateFileSize(fileInfo.size);
            var isSupportedFormat = validateFileFormat(fileInfo.name);

            if(!isCorrectFileSize){
                demo.itemModal.uploadError = $filter('translate')("INVALID_FILE_SIZE");
                $scope.$apply();
                return;
            }

            if(!isSupportedFormat){
                demo.itemModal.uploadError = $filter('translate')("UNSUPPORTED_FORMAT");
                $scope.$apply();
                return;
            }

            demo.itemModal.uploadError = '';
            demo.itemModal.disableUploadButton= false;
            demo.itemModal.showUploadErrorBox = false;
            demo.itemModal.fieldsOnChange();
            $scope.$apply();
        }

        demo.itemModal.removeTmpImage = function(){
            demo.itemModal.tmpImage = "";
            demo.itemModal.showTmpImage = false;
            demo.itemModal.showUploadBox = true;

            demo.itemModal.form.currentValue.image = '';
            demo.itemModal.fieldsOnChange();
        }

        demo.itemModal.fieldsOnChange = function(){
            var currentValue = demo.itemModal.form.currentValue;
            var defaultValue = demo.itemModal.form.defaultValue;

            var isFormChanged = angular.equals(currentValue, defaultValue);
            if(isFormChanged){
                demo.itemModal.disableSaveButton = true;
            }else{
                if(currentValue.name === '' || currentValue.name === null){
                    demo.itemModal.disableSaveButton = true;
                    return;
                }
                if(currentValue.region === '' || currentValue.region === null){
                    demo.itemModal.disableSaveButton = true;
                    return;
                }
                if(currentValue.inStock === '' || currentValue.inStock === null){
                    demo.itemModal.disableSaveButton = true;
                    return;
                }
                if(currentValue.categoryId === '' || currentValue.categoryId === null){
                    demo.itemModal.disableSaveButton = true;
                    return;
                }
                if(demo.itemModal.showUploadErrorBox){
                    demo.itemModal.disableSaveButton = true;
                    return;
                }
                if(currentValue.description === '' || currentValue.description === null){
                    demo.itemModal.disableSaveButton = true;
                    return;
                }

                demo.itemModal.disableSaveButton = false;
            }
        }

        demo.itemModal.saveItem = function(itemId){
            if(document.getElementById("file_choose").files.length > 0){
                saveItemInfoWithImage(itemId);
            }else{
                saveItemInfoWithoutImage(itemId);
            }
        }

        function addNewItem(){
            let params = angular.element('#item_form').serializeJSON();
            let success = (data) => {
                toastr.success($filter('translate')("ADD_ITEM_SUCCESS"));
                demo.itemModal.showErrorBox = false;
                getAllItems();
                $('#item_modal').modal('hide');
            }

            if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
                graphQLService.addNewItem(params, success, (data) => {handleErrorMessageForItemEdit(data)}, "{id}");
            } else {
                $http({
                    method: 'POST',
                    url: '/proxy/v1/assets/items/',
                    data: params,
                    headers : {'Content-Type': 'application/json'}
                }).then(function(response) {
                    success(response);
                }, function error(response) {
                    handleErrorMessageForItemEdit(response);
                }).catch(function(response) {
                    handleErrorMessageForItemEdit(response);
                });
            }
        }

        function updateItem(itemId){
            let updateItemSuccess = (data) => {
                toastr.success($filter('translate')("UPDATE_ITEM_SUCCESS"));
                demo.itemModal.showErrorBox = false;
                getAllItems();
                $('#item_modal').modal('hide');
            }
            let updateItemError = (data) => {
                handleErrorMessageForItemEdit(data);
            }

            let updateItemFormData = angular.element('#item_form').serializeJSON();
            let updateItemParams = {"itemId": itemId, "itemsDTO": updateItemFormData};
            if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
                graphQLService.updateItemByItemId(updateItemParams, updateItemSuccess, (data) => {updateItemError(data)}, "{id}");
            } else {
                $http({
                    method: 'PUT',
                    url: '/proxy/v1/assets/items/' + itemId,
                    data: updateItemFormData,
                    headers : {'Content-Type': 'application/json'}
                }).then(function(response) {
                    updateItemSuccess(response);
                }).catch(function(response) {
                    updateItemError(response);
                });
            }
        }

        function saveItemInfoWithImage(itemId){
            var callBackAfterUploadSuccess = function(response) {
                var imagePath = response.data.data;
                demo.itemModal.form.currentValue.image = imagePath;

                setTimeout(function(){
                    if(demo.itemModal.isAddNewItem){
                        addNewItem();
                    }else{
                        updateItem(itemId);
                    }
                }, 100);
            }

            var callBackAfterUploadFail = function(response) {
                demo.itemModal.uploadError = $filter('translate')("IMAGE_UPLOAD_FAILED");
                demo.itemModal.showUploadErrorBox = true;
                demo.itemModal.fieldsOnChange();
                handleErrorMessageForItemEdit(response);
            }

            var formElement = document.getElementById("image_form");
            uploadImage($http, formElement, callBackAfterUploadSuccess, callBackAfterUploadFail);
        }

        function saveItemInfoWithoutImage(itemId){
            if(demo.itemModal.isAddNewItem){
                addNewItem();
            }else{
                updateItem(itemId);
            }
        }

        function handleErrorMessageForItemEdit(response){
            var messageNotFound = false;
            demo.itemModal.showErrorBox = true;

            if(response.data === null || response.data === undefined){
                messageNotFound = true;
            }else{
                console.log(response);
                var message = response.data.message.toLowerCase();
                var status = response.status;

                if(status === 404){
                    if(message.indexOf("category") !== -1){
                        demo.itemModal.formError = "CATEGORY_NOT_FOUND";
                    }else{
                        messageNotFound = true;
                    }
                }else if(status === 400){
                    if(message.indexOf("in stock") !== -1){
                        demo.itemModal.formError = "IN_STOCK_NUMBER_IS_INCORRECT";
                    }else if(message.indexOf("exists") !== -1){
                        demo.itemModal.formError = "ITEM_NAME_ALREADY_EXISTS";
                    }else if(message.indexOf("item name") !== -1 && message.indexOf("empty") !== -1){
                        demo.itemModal.formError = "ITEM_NAME_CAN_NOT_BE_EMPTY";
                    }else if(message.indexOf("description") !== -1){
                        demo.itemModal.formError = "DESCRIPTION_VALUE_IS_INCORRECT";
                    }else if(message.indexOf("image file") !== -1 && message.indexOf("empty") !== -1){
                        demo.itemModal.formError = "IMAGE_PATH_CAN_NOT_BE_EMPTY";
                    }else if(message.indexOf("category") !== -1 && message.indexOf("null") !== -1){
                        demo.itemModal.formError = "CATEGORY_CAN_NOT_BE_EMPTY";
                    }else if(message.indexOf("image") !== -1 && message.indexOf("supported") !== -1){
                        demo.itemModal.formError = "UNSUPPORTED_FORMAT";
                    }else{
                        messageNotFound = true;
                    }
                }else if(status === 500){
                    if(message.indexOf("regiontype") !== -1){
                        demo.itemModal.formError = "LOCATION_VALUE_IS_INCORRECT";
                    }else if(message.indexOf("java.lang.integer") !== -1){
                        demo.itemModal.formError = "IN_STOCK_NUMBER_IS_INCORRECT";
                    }else if(message.indexOf("maximum upload size exceeded") !== -1){
                        demo.itemModal.formError = "INVALID_FILE_SIZE";
                    }else{
                        messageNotFound = true;
                    }
                } else {
                    messageNotFound = true;
                }
            }

            if(messageNotFound){
                if(demo.itemModal.isAddNewItem){
                    demo.itemModal.formError = "ADD_ITEM_FAILED";
                }else if(demo.itemModal.isEditItem){
                    demo.itemModal.formError = "UPDATE_ITEM_FAILED";
                }
            }
        }
    }

    demo.removeItem = function(item){
        var items = demo.items;
        let success = () => {
            var arrIndex = getArrIndex(items,item);
            demo.items.splice(arrIndex,1);
            toastr.success($filter('translate')('ITEMS_REMOVED_SUCCESSFULLY'));
        }
        let errorhandler = (data) => {
            console.info(data);
            toastrService().error($filter('translate')('ITEMS_FAILED_TO_REMOVE'));
        }
        let params = {"itemId": item.id};

    if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
        graphQLService.deleteItemByItemId(params, success, (data) => {errorhandler(data)});
    } else {
        $http({
            method: 'DELETE',
            url: '/proxy/v1/assets/items/'+item.id,
        }).then(function(result) {
            success(result);
        }, function error(result) {
            errorhandler(result);
        }).catch(function(result) {
            console.info(result);
        });
    }

        $('#remove_confirm_modal').modal('hide');
    }

    demo.showCategoryModal = function(category){

        demo.categoryModal = {
                showFormErrorBox : false,
                formError : '',
                showUploadErrorBox : false,
                uploadError : '',
                isAddNewCategory : false,
                isEditCategory : false,
                showUploadBox : true,
                showTmpImage : false,
                showErrorBox : false,
                disableSaveButton : true,
                disableUploadButton : true,
                tmpImage : '',
                chosenFile : '',
                form : {
                    currentValue : {},
                    defaultValue : {}
                },
                fieldsOnChange : function(){},
                fileChosenOnChange : function(element){},
                removeTmpImage : function(){},
                saveCategory : function(categoryId){},
                uploadImage : function(){}
            }

        document.getElementById("category_form").reset();
        document.getElementById("image_form").reset();

        if(category === undefined){
            demo.editCategory = false;
        }else{
            demo.editCategory = true;
        }

        if(demo.editCategory){
            demo.categoryModal.isEditCategory = true;
            demo.categoryModal.isAddNewCategory = false;
            demo.categoryInModal = category;
        }else{
            demo.categoryModal.isEditCategory = false;
            demo.categoryModal.isAddNewCategory = true;
            demo.categoryInModal = {
                'id' : '',
                'name' : '',
                'image' : '',
                'description' : ''
            };
        }

        demo.categoryModal.form = {
            currentValue : {
                'id' : demo.categoryInModal.id,
                'name' : demo.categoryInModal.name,
                'image' : demo.categoryInModal.image,
                'description' : demo.categoryInModal.description
            },
            defaultValue : {
                'id' : demo.categoryInModal.id,
                'name' : demo.categoryInModal.name,
                'image' : demo.categoryInModal.image,
                'description' : demo.categoryInModal.description
            }
        };

        if(demo.categoryInModal.isAddNewCategory){
            demo.categoryModal.showUploadBox = true;
            demo.categoryModal.showTmpImage = false;
        }

        if(demo.categoryModal.isEditCategory){
            var currentImage = demo.categoryModal.form.currentValue.image;
            demo.categoryModal.tmpImage = currentImage
            if(currentImage !== ''){ // image in category
                demo.categoryModal.showUploadBox = false;
                demo.categoryModal.showTmpImage = true;
            }else{ // no image in category
                demo.categoryModal.showUploadBox = true;
                demo.categoryModal.showTmpImage = false;
            }
        }

        demo.categoryModal.fileChosenOnChange = function(fileElement){
            demo.categoryModal.form.currentValue.image = '';
            demo.categoryModal.disableUploadButton= true;
            demo.categoryModal.showUploadErrorBox = true;
            demo.categoryModal.fieldsOnChange();

            var fileInfo = fileElement.files[0];
            if(fileInfo === undefined){
                demo.categoryModal.showUploadErrorBox = false;
                demo.categoryModal.fieldsOnChange();
                $scope.$apply();
                return;
            }

            var isCorrectFileSize = validateFileSize(fileInfo.size);
            var isSupportedFormat = validateFileFormat(fileInfo.name);

            if(!isCorrectFileSize){
                demo.categoryModal.uploadError = $filter('translate')("INVALID_FILE_SIZE");
                $scope.$apply();
                return;
            }

            if(!isSupportedFormat){
                demo.categoryModal.uploadError = $filter('translate')("UNSUPPORTED_FORMAT");
                $scope.$apply();
                return;
            }

            demo.categoryModal.uploadError = '';
            demo.categoryModal.disableUploadButton= false;
            demo.categoryModal.showUploadErrorBox = false;
            demo.categoryModal.fieldsOnChange();
            $scope.$apply();
        }

        demo.categoryModal.removeTmpImage = function(){
            demo.categoryModal.tmpImage = "";
            demo.categoryModal.showTmpImage = false;
            demo.categoryModal.showUploadBox = true;

            demo.categoryModal.form.currentValue.image = '';
            demo.categoryModal.fieldsOnChange();
        }

        demo.categoryModal.fieldsOnChange = function(){
            var currentValue = demo.categoryModal.form.currentValue;
            var defaultValue = demo.categoryModal.form.defaultValue;
            var isFormChanged = angular.equals(currentValue, defaultValue);

            if(isFormChanged){
                demo.categoryModal.disableSaveButton = true;
            }else{
                if(currentValue.name === '' || currentValue.name === null){
                    demo.categoryModal.disableSaveButton = true;
                    return;
                }
                if(demo.categoryModal.showUploadErrorBox){
                    demo.categoryModal.disableSaveButton = true;
                    return;
                }
                if(currentValue.description === '' || currentValue.description === null){
                    demo.categoryModal.disableSaveButton = true;
                    return;
                }

                demo.categoryModal.disableSaveButton = false;
            }
        }

        demo.categoryModal.saveCategory = function(categoryId){
            if(document.getElementById("file_choose").files.length > 0){
                saveCategoryInfoWithImage(categoryId);
            }else{
                saveCategoryInfoWithoutImage(categoryId);
            }
        }

        function addNewCategory(){
            let success = (data) => {
                toastr.success($filter('translate')("ADD_CATEGORY_SUCCESS"));
                demo.categoryModal.showErrorBox = false;
                getAllCategories(); // refresh all categories
                $('#category_modal').modal('hide');
            }
            let error = (data) => {
                handleErrorMessageForCategoryEdit(data);
            }
            let formData = angular.element('#category_form').serializeJSON();
            if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
                graphQLService.addCategory(formData, success, (data) => {error(data)}, '{id}');
            } else {
                $http({
                    method: 'POST',
                    url: '/proxy/v1/assets/categories/',
                    data: formData,
                    headers : {'Content-Type': 'application/json'}
                }).then(function(response) {
                    success(response.data.data);
                }, function(response) {
                    error(response);
                }).catch(function(response) {
                    error(response);
                });
            }
        }

        function updateCategory(categoryId){
            let updateCategorySuccess = (data) => {
                toastr.success($filter('translate')("UPDATE_CATEGORY_SUCCESS"));
                demo.categoryModal.showErrorBox = false;
                getAllCategories(); // refresh all categories
                $('#category_modal').modal('hide');
            }
            let updateCategoryError = (data) => {
                handleErrorMessageForCategoryEdit(data);
            }

            let updateCategoryFormData = angular.element('#category_form').serializeJSON();
            let updateCategoryParams = {"categoryId": categoryId, "categoryDto": updateCategoryFormData};
            if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
                graphQLService.updateCategory(updateCategoryParams, updateCategorySuccess, (data) => {updateCategoryError(data)}, "{id}");
            } else {
                $http({
                    method: 'PUT',
                    url: '/proxy/v1/assets/categories/' + categoryId,
                    data: updateCategoryFormData,
                    headers: {'Content-Type': 'application/json'}
                }).then(function (response) {
                    updateCategorySuccess(response.data.data);
                }, function error(response) {
                    updateCategoryError(response);
                }).catch(function (response) {
                    updateCategoryError(response);
                });
            }
        }

        function saveCategoryInfoWithImage(categoryId){
            var callBackAfterUploadSuccess = function(response) {
                var imagePath = response.data.data;
                demo.categoryModal.form.currentValue.image = imagePath;

                setTimeout(function(){
                    if(demo.categoryModal.isAddNewCategory){
                        addNewCategory();
                    }else{
                        updateCategory(categoryId);
                    }
                }, 100);
            }

            var callBackAfterUploadFail = function(response) {
                demo.categoryModal.fieldsOnChange();
                handleErrorMessageForCategoryEdit(response);
            }

            var formElement = document.getElementById("image_form");
            uploadImage($http, formElement, callBackAfterUploadSuccess, callBackAfterUploadFail);
        }

        function saveCategoryInfoWithoutImage(categoryId){
            if(demo.categoryModal.isAddNewCategory){
                addNewCategory();
            }else{
                updateCategory(categoryId);
            }
        }

        function handleErrorMessageForCategoryEdit(response){
            demo.imgBoxStyle = {
                "top" : "89px"
            }
            demo.imgUploadBoxStyle = {
                "top" : "161px"
            }
            demo.categoryModal.showErrorBox = true;
            var messageNotFound = false;

            if(response.data === null || response.data === undefined){
                messageNotFound = true;
            }else{
                console.log(response);
                var message = response.data.message.toLowerCase();
                var status = response.status;
                if(status === 404){
                    if(message.indexOf("Category") !== -1){
                        demo.categoryModal.formError = "CATEGORY_NOT_FOUND";
                    }else{
                        messageNotFound = true;
                    }
                }else if(status === 400){
                    if(message.indexOf("exists") !== -1){
                        demo.categoryModal.formError = "CATEGORY_NAME_ALREADY_EXISTS";
                    }else if(message.indexOf("category name") !== -1 && message.indexOf("empty") !== -1){
                        demo.categoryModal.formError = "CATEGORY_NAME_CAN_NOT_BE_EMPTY";
                    }else if(message.indexOf("description") !== -1){
                        demo.categoryModal.formError = "DESCRIPTION_VALUE_IS_INCORRECT";
                    }else if(message.indexOf("image file") !== -1 && message.indexOf("empty") !== -1){
                        demo.categoryModal.formError = "IMAGE_PATH_CAN_NOT_BE_EMPTY";
                    }else if(message.indexOf("image") !== -1 && message.indexOf("supported") !== -1){
                        demo.categoryModal.formError = "UNSUPPORTED_FORMAT";
                    }else{
                        messageNotFound = true;
                    }
                }else if(status === 500){
                    if(message.indexOf("maximum upload size exceeded") !== -1){
                        demo.categoryModal.formError = "INVALID_FILE_SIZE";
                    }else{
                        messageNotFound = true;
                    }
                }
            }

            if(messageNotFound){
                if(demo.categoryModal.isAddNewCategory){
                    demo.categoryModal.formError = "ADD_CATEGORY_FAILED";
                }else if(demo.categoryModal.isEditCategory){
                    demo.categoryModal.formError = "UPDATE_CATEGORY_FAILED";
                }
            }
        }
    }

    demo.removeCategory = function(category) {
        var categories = demo.categories;
        let success = (data) => {
            var arrIndex = getArrIndex(categories,category);
            categories.splice(arrIndex,1);
            toastr.success($filter('translate')('CATEGORY_SUCCESSFULLY_REMOVED'));
        };
        let error = (data, endpointType) => {
            console.info(data);
            let errMsg = "";
            switch (data.status) {
                case 400:
                    errMsg = $filter('translate')('CATEGORY_FAILED_TO_REMOVE_WITH_CONFLICT');
                    break;
                case 404:
                    errMsg = $filter('translate')('CATEGORY_NOT_FOUND');
                    break;
                default:
                    errMsg = $filter('translate')('CATEGORY_FAILED_TO_REMOVE');
            }
            toastrService().error(errMsg);
        };
        if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            graphQLService.deleteCategoryById({"categoryId": category.id }, success, (data) => {error(data, "graphQL")});
        } else {
            $http({
                method: 'DELETE',
                url: '/proxy/v1/assets/categories/'+category.id,
            }).then(function(result) {
               success(result.data.data);
           }).catch(function(result) {
               error(result, "categories");
           });
        }
        $('#remove_confirm_modal').modal('hide');
    }

    //To avoid displaying page without styles due to the slow loading of CSS files
    setTimeout(function(){ angular.element("body").css("visibility","visible") }, 500);
});

/* with CSRF
mod.config(['$httpProvider', function($httpProvider) {
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
}]);*/

mod.controller('optionsForm', function($scope, $rootScope, $http, $filter) {
    var options = this;

    $http({
        method: 'GET',
        url: '/v1/demoAdmin/currentPreferences',
    }).then(function(result) {
        var data = result.data.data;
        var status = result.data.status;
        if(status == null){
            window.location.href="/loginPage";
        }

        options.dataAccessMode = data.dataAccessMode;
        options.soapEndPoint = data.soapEndPoint;

        var checkedEndpoints = handleRestEndpointsFromServer(data.restEndPoints);
        options.restEndpoints = checkedEndpoints;

        var checkedBugs = handleDemoBugsFromServer(data.demoBugs);
        options.demoBugs = checkedBugs;

        options.industryType = data.industryType;
        options.advertisingEnabled = data.advertisingEnabled;

        options.useParasoftJDBCProxy = data.useParasoftJDBCProxy;
        options.parasoftVirtualizeServerUrl = data.parasoftVirtualizeServerUrl;
        options.parasoftVirtualizeServerPath = data.parasoftVirtualizeServerPath;
        options.parasoftVirtualizeGroupId = data.parasoftVirtualizeGroupId;

        options.mqType = data.mqType;
        options.inventoryServiceActiveMqRequestQueue = data.activeMqConfig.orderServiceSendTo;
        options.inventoryServiceActiveMqResponseQueue = data.activeMqConfig.orderServiceListenOn;
        options.inventoryServiceKafkaRequestTopic = data.kafkaConfig.orderServiceSendTo;
        options.inventoryServiceKafkaResponseTopic = data.kafkaConfig.orderServiceListenOn;
        options.inventoryServiceRabbitMqRequestQueue = data.rabbitMqConfig.orderServiceSendTo;
        options.inventoryServiceRabbitMqResponseQueue = data.rabbitMqConfig.orderServiceListenOn;

        options.webServiceMode = data.webServiceMode;
        options.graphQLEndpoint = data.graphQLEndpoint;

    }).catch(function(result) {
        toastrService().error($filter('translate')('LOADING_DATA_FAILS'));
        console.log(result);
    });

    options.resetGraphQLEndpoint = function() {
        resetValuesTemplate(function(defaultOptions){
            options.graphQLEndpoint = defaultOptions.graphQLEndpoint;
            clearMessage("graphql");
        })
    }

    options.resetEndpoint = function(endpoint){

        resetValuesTemplate(function(defaultOptions){
            var restDefaultEndpoints = defaultOptions.restEndPoints;

           for(i = 0; i < restDefaultEndpoints.length; i++){
               if(restDefaultEndpoints[i].routeId === endpoint){
                   switch(endpoint) {
                        case "categories":
                           options.restEndpoints.categoriesRestEndpointUrl = restDefaultEndpoints[i].url;
                           clearMessage("categories");
                           break;
                        case "items":
                            options.restEndpoints.itemsRestEndpointUrl = restDefaultEndpoints[i].url;
                            clearMessage("items");
                           break;
                       case "cart":
                           options.restEndpoints.cartItemsRestEndpointUrl = restDefaultEndpoints[i].url;
                           clearMessage("cart");
                           break;
                       case "orders":
                           options.restEndpoints.ordersRestEndpointUrl = restDefaultEndpoints[i].url;
                           clearMessage("orders");
                           break;
                       case "locations":
                           options.restEndpoints.locationsRestEndpointUrl = restDefaultEndpoints[i].url;
                           clearMessage("locations");
                           break;
                       default:
                   }
                   return false;
               }
           }
        });
    }

    options.resetOrderServiceActiveMqRequestQueue = function(){
        resetValuesTemplate(function(defaultOptions){
            options.inventoryServiceActiveMqRequestQueue = defaultOptions.activeMqConfig.orderServiceSendTo;
        });
    }

    options.resetOrderServiceActiveMqResponseQueue = function(){
        resetValuesTemplate(function(defaultOptions){
            options.inventoryServiceActiveMqResponseQueue = defaultOptions.activeMqConfig.orderServiceListenOn;
        });
    }

    options.restInventoryServiceKafkaRequestTopic = function(){
        resetValuesTemplate(function(defaultOptions){
            options.inventoryServiceKafkaRequestTopic = defaultOptions.kafkaConfig.orderServiceSendTo;
        });
    }

    options.restInventoryServiceKafkaResponseTopic = function() {
        resetValuesTemplate(function(defaultOptions){
            options.inventoryServiceKafkaResponseTopic = defaultOptions.kafkaConfig.orderServiceListenOn;
        });
    }

    options.resetOrderServiceRabbitMqRequestQueue = function(){
        resetValuesTemplate(function(defaultOptions){
            options.inventoryServiceRabbitMqRequestQueue = defaultOptions.rabbitMqConfig.orderServiceSendTo;
        });
    }

    options.resetOrderServiceRabbitMqResponseQueue = function(){
        resetValuesTemplate(function(defaultOptions){
            options.inventoryServiceRabbitMqResponseQueue = defaultOptions.rabbitMqConfig.orderServiceListenOn;
        });
    }

    options.resetVirtualizeServerUrl = function(){
        resetValuesTemplate(function(defaultOptions){
            options.parasoftVirtualizeServerUrl = defaultOptions.parasoftVirtualizeServerUrl;
        });
    }

    options.resetVirtualizeServerPath = function(){
        resetValuesTemplate(function(defaultOptions){
            options.parasoftVirtualizeServerPath = defaultOptions.parasoftVirtualizeServerPath;
        });
    }

    options.resetVirtualizeGroupId = function(){
        resetValuesTemplate(function(defaultOptions){
            options.parasoftVirtualizeGroupId = defaultOptions.parasoftVirtualizeGroupId;
        });
    }

    function resetValuesTemplate(doResetFunction){
        if(options.defaultOptions === undefined){
            $http({
                method: 'GET',
                url: '/v1/demoAdmin/defaultPreferences',
            }).then(function(result) {
                options.defaultOptions = result.data.data;
                doResetFunction(options.defaultOptions);
            }).catch(function(result) {
                console.log(result);
            });
        }else{
            doResetFunction(options.defaultOptions);
        }
    }

    options.fullfilPath = function(){
        var path = options.parasoftVirtualizeServerPath;

        if(path === null || path === ""){
            return
        }

        if(path.substring(0, 1) !== '/'){
            options.parasoftVirtualizeServerPath = '/' + options.parasoftVirtualizeServerPath;
        }
    }

    options.openConfigurationDetailsModal = function() {
        $http({
            method: 'GET',
            url: '/v1/demoAdmin/mqProperties',
        }).then(function(result) {
            const data = result.data.data
            if(options.mqType === "ACTIVE_MQ") {
                options.configurationDetails = [
                    {
                        label: $filter('translate')('PROVIDER_URL_LABEL'),
                        value: data.activeMqConfig.brokerUrl.replace("0.0.0.0", location.hostname)
                    }, {
                        label: $filter('translate')('INITIAL_CONTEXT_CLASS_LABEL'),
                        value: data.activeMqConfig.initialContextClass
                    }, {
                        label: $filter('translate')('CONNECTION_FACTORY_LABEL'),
                        value: data.activeMqConfig.connectionFactory
                    }, {
                        label: $filter('translate')('USER_NAME_LABEL'),
                        value: data.activeMqConfig.username
                    }, {
                        label: $filter('translate')('PASSWORD_LABEL'),
                        value: data.activeMqConfig.password
                    }
                ];
            } else if(options.mqType === "KAFKA") {
                options.configurationDetails = [
                    {
                        label: $filter('translate')('BROKER_URL'),
                        value: data.kafkaConfig.bootstrapServers
                    }, {
                        label: $filter('translate')('GROUP_ID'),
                        value: data.kafkaConfig.groupId
                    }
                ];
            } else if(options.mqType === "RABBIT_MQ") {
                options.configurationDetails = [
                     {
                         label: $filter('translate')('RABBITMQ_HOST'),
                         value: data.rabbitMQConfig.rabbitMqHost
                    }, {
                         label: $filter('translate')('RABBITMQ_PORT'),
                         value: data.rabbitMQConfig.rabbitMqPort
                    }, {
                         label: $filter('translate')('USER'),
                         value: data.rabbitMQConfig.username
                    }, {
                         label: $filter('translate')('PASSWORD'),
                         value: data.rabbitMQConfig.password
                    }
                ];
             };
        }).catch(function(result) {
            console.error(result);
        });
    }
});

function handleRestEndpointsFromServer(restEndpoints){

    var checkedEndpoints = {};
    for(i = 0; i < restEndpoints.length; i++){
        switch(restEndpoints[i].routeId) {
             case "categories":
                 checkedEndpoints.categoriesRestEndpointUrl = restEndpoints[i].url;
                break;
             case "items":
                 checkedEndpoints.itemsRestEndpointUrl = restEndpoints[i].url;
                break;
            case "cart":
                checkedEndpoints.cartItemsRestEndpointUrl = restEndpoints[i].url;
                break;
            case "orders":
                checkedEndpoints.ordersRestEndpointUrl = restEndpoints[i].url;
                break;
            case "locations":
                checkedEndpoints.locationsRestEndpointUrl = restEndpoints[i].url;
                break;
            default:
        }
    }

    return checkedEndpoints;
}

/* traverse the enabled demo bugs form server, and bind data for angular uses  */
function handleDemoBugsFromServer(demoBugs){
    var checkedBugs = {};
    if(demoBugs != null){
        for(i = 0; i < demoBugs.length; i++){
            switch(demoBugs[i].demoBugsType) {
                 case bug_messesUp_value:
                    checkedBugs.messesUp = true;
                    break;
                 case bug_extraOrdered_value:
                    checkedBugs.extraOrdered = true;
                    break;
                case bug_javascriptError_value:
                    checkedBugs.javascriptError = true;
                    break;
                case bug_missingProvider_value:
                    checkedBugs.missingProvider = true;
                    break;
                case bug_Incorrect_location_value:
                    checkedBugs.incorrect_location = true;
                    break;
                case bug_Incorrect_number_value:
                    checkedBugs.incorrect_number = true;
                    break;
                case bug_Reverse_orders_value:
                    checkedBugs.reverse_order_of_orders = true;
                    break;
                case bug_Reinitialize_datasource_for_each_http_request_value:
                    checkedBugs.reinitialize_datasource_for_each_http_request = true;
                    break;
                default:
                    checkedBugs.unkonwn = true;
            }
        }
    }

    return checkedBugs;
}