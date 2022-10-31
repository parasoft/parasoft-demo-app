var DEFENSE = "defense";
var AEROSPACE = "aerospace";
var ROLE_APPROVER = "ROLE_APPROVER";
var ROLE_PURCHASER = "ROLE_PURCHASER";
var CURRENT_ROLE = angular.element("#current_rolename").val();
var CURRENT_USERNAME = angular.element("#current_username").val();
function symbol(s, e){
    return s + 'dmi' + e;
}

function getDefaultLang() {
    var lang = navigator.language;
    lang = lang.substr(0, 2).toUpperCase();

    // Need update this condition when we localize PDA to other languages.
    if(lang !== "EN" && lang !== "ZH"){
        lang = "EN";
    }

    return lang;
}

function setLocale(appName) {

    appName.config(function($translateProvider) {
        var lang;
        //to configure sanitization strategy. This can have serious security implications if without this configuration.
        $translateProvider.useSanitizeValueStrategy(['escapeParameters']);

        $translateProvider.useUrlLoader("/localize");
        $translateProvider.preferredLanguage(getDefaultLang()); //Angular-translate will then make a call to /localize?lang=lang
    });
}

function initImportPageControllers(app){
    initHeaderController(app);
    initRequisitionBarController(app);
    initProductBuildInfo(app);
    initAuthorizationHeader(app);
}

function initHeaderController(app){

    //Use angularJS filter to deal with the text that is more than the specified length
    //The excess parts are followed by an ellipsis
    app.filter('textLengthSet', function() {
        return function(value, wordwise, max, tail) {
            if (!value) return '';

            max = parseInt(max, 10);
            if (!max) return value;
            var realLength = 0;

            //Calculate the char code length of the specified text
            //A English character is 1 and a Chinese character is 2
            for (var i = 0; i < value.length; i++) {
                charCode = value.charCodeAt(i);
                if (charCode >= 0 && charCode <= 128){
                    realLength += 1;
                }else{
                    realLength += 2;
                }
            }

            if (realLength <= max) return value;

            value = value.substr(0, max);
            if (wordwise) {
                var lastspace = value.lastIndexOf(' ');
                if (lastspace != -1) {
                    value = value.substr(0, lastspace);
                }
            }

            return value + (tail);
        };
    });

    app.controller('headerController', function($rootScope, $http, $filter) {
        //Get current industry
        $http({
            method: 'GET',
            url: '/v1/demoAdmin/currentPreferences'
        }).then(function successCallback(response) {
            var preferenceData = response.data.data;
            var industry = preferenceData.industryType;
            $rootScope.INDUSTRY = industry;
            industry = industry.toLowerCase();
            $rootScope.industry = industry;
            $rootScope.isDefense = angular.equals(industry, DEFENSE);
            $rootScope.isAerospace = angular.equals(industry, AEROSPACE);
            $rootScope.isShowAdvertising = preferenceData.advertisingEnabled;
            $rootScope.deBugs = preferenceData.demoBugs;

            if(preferenceData.useParasoftJDBCProxy){
                $http({
                    method: 'GET',
                    url: '/v1/parasoftJDBCProxy/status'
                }).then(function successCallback(response) {
                }, function errorCallback(response) {
                    console.log(response);
                    parasoftVirtualizeServerUrl = response.data.data;
                    displayLoadError(response,$rootScope,$filter,$http);
                    //toastr.error($filter('translate')('CAN_NOT_CONNECT_TO_PARASOFT_JDBC_PROXY') + " " + parasoftVirtualizeServerUrl, '', {timeOut: 0});
                });
            }
        }, function errorCallback(response) {
            console.log(response);
        });

        //Get current role name
        $rootScope.isPurchaser = angular.equals(CURRENT_ROLE, ROLE_PURCHASER);
        $rootScope.isApprover = angular.equals(CURRENT_ROLE, ROLE_APPROVER);
        $rootScope.username = CURRENT_USERNAME;

        $rootScope.isShowSettingButton = true;
        $rootScope.isShowRequisitionButton = true;
        $rootScope.isShowRequisitionRequestButton = true;
        $rootScope.isShowAccount = true;
    });
}

function initRequisitionBarController(app){
    app.controller('requisitionBarController', function($rootScope, $http, $filter, $location) {
        var req = this;
        //Get current industry
        var industry = $rootScope.industry;
        //Get current itemId
        var currentItemId = $location.absUrl().substr($location.absUrl().lastIndexOf("/")+1);
        //Get requisition data from database
        loadShoppingCartData($rootScope,$http,$filter);

        //Get related assets data from database TODO
        var testNums = [0,1,2];
        req.relatedItems = testNums;

        req.collapse = "collapse_out";
        req.isAerospace = angular.equals(industry, AEROSPACE);
        $rootScope.totalAmount = 0;

        req.minusRequisition = function(requisitionNums,index,cartItem){
            var itemId = cartItem.itemId;
            var inventory = cartItem.realInStock;

            if(requisitionNums[index] > inventory){
                requisitionNums.splice(index, 1, inventory);
                //$rootScope.totalAmount = sum(requisitionNums);
                updateShoppingCart(itemId,inventory);
                if(inventory === 1){
                    setMinusDisabled(index);
                }
                return;
            }

            clearPlusDisabled(index);
            if(requisitionNums[index] - 1 < 2){
                setMinusDisabled(index);
                //$rootScope.totalAmount =  sum(requisitionNums) - requisitionNums[index] + 1;
                updateShoppingCart(itemId,1);
                //return 1
            }else{
                //$rootScope.totalAmount = sum(requisitionNums) - 1;
                updateShoppingCart(itemId,requisitionNums[index] - 1);
                //return requisitionNums[index] - 1;
            }
        }

        req.plusRequisition = function(requisitionNums,index,cartItem){
            var itemId = cartItem.itemId;
            var total_amount = sum(requisitionNums);
            var inventory = cartItem.realInStock;

            if(requisitionNums[index] < inventory){
                clearMinusDisabled(index);
                //$rootScope.totalAmount = total_amount + 1;
                if(requisitionNums[index] + 1 === inventory){
                    setPlusDisabled(index);
                    $rootScope.totalAmount = total_amount + 1;
                }
                updateShoppingCart(itemId,requisitionNums[index] + 1);
                //return requisitionNums[index] + 1;
            }else{
                setPlusDisabled(index);
                $rootScope.totalAmount = total_amount;
                updateShoppingCart(itemId,requisitionNums[index]);
                //return requisitionNums[index];
            }
        }

        req.inputItemNum = function(index,cartItem){
            var itemId = cartItem.itemId;
            var inventory = cartItem.realInStock;
            var quantity = cartItem.quantity;

            if(Number(inventory) === 0 || inventory === null){
                //return cartItem.quantity;
            }else if(quantity < 2){
                setMinusDisabled(index);
                clearPlusDisabled(index);
                updateShoppingCart(itemId,1);
                //return 1;
            }else if(quantity < inventory){
                clearPlusDisabled(index);
                clearMinusDisabled(index);
                updateShoppingCart(itemId,quantity);
                //return requisitionNums[index];
            }else{
                setPlusDisabled(index);
                clearMinusDisabled(index);
                updateShoppingCart(itemId,inventory);
                //return inventory;
            }
        }

        req.relatedLineExp = function(index){
            var line = index + 1;
            return line;
        };

        req.requisitionRowExp = function(index){
            var row = index + 1;
            return row;
        };

        req.closeRequisition = function(){
            req.rightNavigationBar = {
                "visibility" : "collapse"
            }
            //Class 'in' is generated by bootstrap, so it cannot be controlled by angularJs
            angular.element("#requisition_bar").removeClass("in");
        }

        req.removeCartItem = function(itemId){
            $http({
                method: 'DELETE',
                url: '/proxy/v1/cartItems/'+itemId,
            }).then(function(result) {
                loadShoppingCartData($rootScope,$http,$filter);
                $rootScope.inRequisition = 0;
                toastr.success($filter('translate')('REMOVE_ITEM_SUCCESS'));
            }).catch(function(result) {
                console.info(result);
            });

            //Update the data in item detail page
            if(Number(itemId) === Number(currentItemId)){
                //Get the number of item in stock
                $http({
                    method: 'GET',
                    url: '/proxy/v1/assets/items/' + itemId,
                }).then(function(result) {
                    var item = result.data.data;
                    var quantity = 0;
                    var inventory = item.inStock;
                    $rootScope.itemInventory = inventory;
                    checkInventoryInItemDetail(inventory,quantity);
                }).catch(function(result) {
                    console.info(result);
                });
            }
        }

        $rootScope.switchBar = function(){
            req.rightNavigationBar = {
                "visibility" : "visible"
            }

            var status = angular.element("#requisition_bar").attr("class");
            if(angular.equals(status,"collapse_out collapse") || angular.equals(status,"collapse_in collapse")){
                //Get requisition data from database
                loadShoppingCartData($rootScope,$http,$filter);
                req.collapse = "collapse_out";
            }else{
                req.collapse = "collapse_in";
            }
        }

        //Check whether there are items in the requisition request
        req.checkItems = function(){
            //Get the newest cart items from database
            $http({
                method: 'GET',
                url: '/proxy/v1/cartItems',
            }).then(function(result) {
                var cartItems = result.data.data;
                if(cartItems.length < 1){
                    toastrService().error($filter('translate')('NO_SUBMIT_ITEM_ERROR'));
                }else{
                    for(var i = 0; i < cartItems.length; i++){
                        var inventory = cartItems[i].realInStock;
                        var quantity = cartItems[i].quantity;

                        if(inventory === 0 || quantity > inventory){
                            toastrService().error($filter('translate')('SUBMIT_ITEM_FAIL'));
                            return;
                        }
                    }
                    window.location.href= "/orderWizard";
                }
            }).catch(function(result) {
                console.info(result);
            });
        }

        req.isInventoryEnough = function(inventory,quantity,index){
            if(quantity > inventory || inventory === 0 || inventory === null){
                angular.element(".input"+(index+1)).addClass("error_input");
                return true;
            }else{
                angular.element(".input"+(index+1)).removeClass("error_input");
                return false;
            }
        }

        //Update the cartItem data to database
        function updateShoppingCart(requireItemId,requireItemQty){
            $http({
                method: 'PUT',
                url: '/proxy/v1/cartItems/'+requireItemId,
                params: {"itemId":requireItemId,"itemQty":requireItemQty}
            }).then(function(result) {
                var cartItem = result.data.data;
                if(Number(requireItemId) === Number(currentItemId)){
                    var quantity = cartItem.quantity;
                    var inventory = cartItem.realInStock;
                    $rootScope.inRequisition = quantity;
                    $rootScope.itemInventory = inventory;
                    checkInventoryInItemDetail(inventory,quantity);
                }
                loadShoppingCartData($rootScope,$http,$filter);
            }).catch(function(result) {
                console.info(result);
            });
        }

        function checkInventoryInItemDetail(inventory,quantity){
            //Get number in input box in item detail page
            var inputNum = angular.element("#item_number_input").val();

            if(inventory === 0 || inventory - quantity <= 0){
                setPlusDisabledInItemDetail();
                setMinusDisabledInItemDetail();
                setAddToBtnDisabledInItemDetail();
                angular.element("#item_number_input").attr("readonly",true);
            }else if(inventory - quantity === 1){
                setMinusDisabledInItemDetail();
                setPlusDisabledInItemDetail();
                clearAddToBtnDisabledInItemDetail();
                angular.element("#item_number_input").attr("readonly",true);
            }else if(Number(inputNum) + quantity >= inventory){
                setPlusDisabledInItemDetail();
                clearMinusDisabledInItemDetail();
                clearAddToBtnDisabledInItemDetail();
            }else if(Number(inputNum) + quantity < inventory && Number(inputNum) > 1){
                clearMinusDisabledInItemDetail();
                clearPlusDisabledInItemDetail();
                clearAddToBtnDisabledInItemDetail();
            }else{
                setMinusDisabledInItemDetail();
                clearAddToBtnDisabledInItemDetail();
                clearPlusDisabledInItemDetail();
                angular.element("#item_number_input").attr("readonly",false);
            }
        }

        //Make the plus button disabled in detail page
        function setPlusDisabledInItemDetail(){
            $rootScope.plusDisabled = {
                "opacity" : ".35",
                "cursor" : "not-allowed"
            };
            $rootScope.plusBtnDisabled = true;
        }

        //Make the minus button disabled in detail page
        function setMinusDisabledInItemDetail(){
            $rootScope.minusDisabled = {
                "opacity" : ".35",
                "cursor" : "not-allowed"
            };
            $rootScope.minusBtnDisabled = true;
        }

        //Make 'add to requisition' button disabled in detail page
        function setAddToBtnDisabledInItemDetail(){
            $rootScope.btnDisabled = {
                "opacity" : ".35",
                "cursor" : "not-allowed"
            };
            $rootScope.addToBtnDisabled = true;
        }

        //Clear the plus button disabled in detail page
        function clearPlusDisabledInItemDetail(){
            $rootScope.plusDisabled = {};
            $rootScope.plusBtnDisabled = false;
        }

        //Clear the minus button disabled in detail page
        function clearMinusDisabledInItemDetail(){
            $rootScope.minusDisabled = {};
            $rootScope.minusBtnDisabled = false;
        }

        //Clear 'add to requisition' button disabled in detail page
        function clearAddToBtnDisabledInItemDetail(){
            $rootScope.btnDisabled = {};
            $rootScope.addToBtnDisabled = false;
        }
    });
}

function initProductBuildInfo(app){
    app.run(function($rootScope, $http) {
        //get build information.
        $http({
            method: 'GET',
            url: '/v1/build-info'
        }).then(function successCallback(response) {
            var buildInfoData = response.data.data;
            var buildInfo = 'v' + buildInfoData.buildVersion + '_' + buildInfoData.buildId;
            $rootScope.productBuildInfo = buildInfo;
            //sessionStorage.setItem("buildInfo",buildInfo);
        }, function errorCallback(response) {});

    });
}

function initAuthorizationHeader(app){
    app.run(function($window, $http) {
        // It's not necessary to check against the back-end for now,
        // as the user need to be login first (where the userToken is stored in the localStorage)
        // before accessing other web pages.
        var token = $window.localStorage.getItem('userToken');
        if (token) {
            $http.defaults.headers.common['Authorization'] = 'Basic ' + token;
        }
    });
}

function initToastr(){
    toastr.options = {
        closeButton: true,
        debug: false,
        progressBar: true,
        positionClass: "toast-bottom-right",
        onclick: null,
        showDuration: "300",
        hideDuration: "1000",
        timeOut: "4000",
        extendedTimeOut: "1000",
        showEasing: "swing",
        hideEasing: "linear",
        showMethod: "fadeIn",
        hideMethod: "fadeOut"
    };
}

function toastrService(){
    var error = function(content, title) {
        return toastr.error(content, title, {timeOut: 0,extendedTimeOut: 0});
    }
    var service = {
        error: error,
    };
    return service;

}

function sum(requisitionNums){
    var sum = 0;
    for(var i = 0; i<requisitionNums.length; i++){
        sum = sum + requisitionNums[i];
    }
    return sum;
}

function loadShoppingCartData($rootScope,$http,$filter){
    // Set time out for avoiding to get the key when using $filter('translate') fliter.
    setTimeout(function(){
        $http({
            method: 'GET',
            url: '/proxy/v1/cartItems',
        }).then(function(result) {
            var data = result.data.data;
            $rootScope.cartItems = data;
            var requisitionNums = new Array();
            var errorFlag = false;

            for(var i = 0; i < data.length; i++){
                var cartItem = data[i];
                var inventory = cartItem.realInStock;
                var quantity = cartItem.quantity;

                //record quantity for every items
                //Make the minus button or plus button disabled if the number of item is 1 or equals to inventory
                if(Number(inventory) > Number(quantity)){
                    requisitionNums.push(quantity);
                    clearPlusDisabled(i);
                }else if(Number(quantity) === Number(inventory)){
                    requisitionNums.push(quantity);
                    setPlusDisabled(i);
                }else{//When the inventory is insufficient
                    errorFlag = true;
                    requisitionNums.push(quantity);
                    setPlusDisabled(i);
                }

                //Make the minus button disabled if the number of item is 1
                if(Number(quantity) > 1){
                    clearMinusDisabled(i);
                }else if(Number(quantity) === 1){
                    setMinusDisabled(i);
                }

                //When inventory is 0 or the item is deleted
                if(Number(inventory) === 0 || inventory === null){
                    errorFlag = true;
                    setPlusDisabled(i);
                    setMinusDisabled(i);
                    setInputDisabled(i);
                    angular.element(".input"+(i+1)).addClass("error_input");
                }else{
                    angular.element(".input"+(i+1)).removeClass("error_input");
                }
            }

            $rootScope.totalAmount = sum(requisitionNums);
            $rootScope.requisitionNums = requisitionNums;
            $rootScope.isRelatedItemsShow = false;
            $rootScope.shoppingCart = {
                "visibility" : "visible"
            };

            //Make the submit button disabled if there are no items in requisition request
            if(data.length < 1 || errorFlag){
                $rootScope.disableSubmitBtn = true;
            }else{
                $rootScope.disableSubmitBtn = false;
            }
        }).catch(function(result) {
            console.info(result);
            $rootScope.shoppingCart = {
                "visibility" : "hidden"
            };
            displayLoadError(result,$rootScope,$filter,$http,true,'cart');
            $rootScope.cartItemLoadError = true;
        });
    }, 500);
}

//Make the plus button disabled in requisition request
function setPlusDisabled(index){
    var row = index + 1;
    angular.element(".requisition_plus_row"+row+">button").css("opacity",".35");
    angular.element(".requisition_plus_row"+row+">button").css("cursor","not-allowed");
    angular.element(".requisition_plus_row"+row+">button").attr("disabled",true);
}

//Make the minus button disabled in requisition request
function setMinusDisabled(index){
    var row = index + 1;
    angular.element(".requisition_minus_row"+row+">button").css("opacity",".35");
    angular.element(".requisition_minus_row"+row+">button").css("cursor","not-allowed");
    angular.element(".requisition_minus_row"+row+">button").attr("disabled",true);
}

//Make the input box disabled in requisition request
function setInputDisabled(index){
    var row = index + 1;
    angular.element(".input"+row).attr("readonly",true);
}

function clearPlusDisabled(index){
    var row = index + 1;
    angular.element(".requisition_plus_row"+row+">button").css("opacity","");
    angular.element(".requisition_plus_row"+row+">button").css("cursor","");
    angular.element(".requisition_plus_row"+row+">button").attr("disabled",false);
}

function clearMinusDisabled(index){
    var row = index + 1;
    angular.element(".requisition_minus_row"+row+">button").css("opacity","");
    angular.element(".requisition_minus_row"+row+">button").css("cursor","");
    angular.element(".requisition_minus_row"+row+">button").attr("disabled",false);
}

function clearInputDisabled(index){
    var row = index + 1;
    angular.element(".input"+row).attr("readonly",false);
}

function getUnreviewedAmount($http,$rootScope,$filter){
    // Set time out for avoiding to get the key when using $filter('translate') fliter.
    $rootScope.unreviewedNumByPRCH = 0;
    setTimeout(function(){
        $http({
            method: 'GET',
            url: '/proxy/v1/orders/unreviewedNumber'
        }).then(function(result) {
            const numbers = result.data.data;
            $rootScope.unreviewedNumByPRCH = numbers.unreviewedByPurchaser;
            $rootScope.unreviewedNumByAPV = numbers.unreviewedByApprover;
        }).catch(function(result) {
            console.log(result);
            $rootScope.unreviewedNumByPRCH = 0;
            displayLoadError(result,$rootScope,$filter,$http,true,'orders');
        });
    }, 500);
}

function connectAndSubscribeMQ(role, $http, $rootScope, $filter, mqConsumeCallback, mqProduceCallback){
    $http({
        method: 'GET',
        url: '/v1/MQConnectorUrl',
    }).then(function(result) {
        var connectorUrl = result.data.data;
        var ws = new WebSocket(connectorUrl, 'stomp');
        mqClient = Stomp.over(ws);
        mqClient.debug = null;

        // heartbeat，ms
        mqClient.heartbeat.outgoing = 10000;
        mqClient.heartbeat.incoming = 10000;

        var on_connect = function(x) {
            var requisition = $filter('translate')('ORDER');

             // subscribe approver topic
            mqClient.subscribe("/topic/order.approver", function(data) {
                var msgString = data.body;
                var msgObject = $.parseJSON(msgString);

                var status = translatedStatus(msgObject.status, $filter);

                if(role === ROLE_PURCHASER){    // purchaser produced this message to this topic
                     if (msgObject.requestedBy === CURRENT_USERNAME) {
                         toastr.success(requisition+' '+msgObject.orderNumber+' '+status+'!', '', {timeOut: 4000});
                         if(mqProduceCallback){mqProduceCallback();}
                         // update the number on the icon
                         getUnreviewedAmount($http,$rootScope,$filter);
                         $rootScope.totalAmount = 0;
                     }
                }else if(role === ROLE_APPROVER){ // approver consume this message from this topic
                     toastr.info(requisition+' '+msgObject.orderNumber+' '+status+'!', '', {timeOut: 0});
                     if(mqConsumeCallback){mqConsumeCallback();}
                }
                $rootScope.emptyContentError = false;
            });

             // subscribe purchaser topic
            mqClient.subscribe("/topic/order.purchaser", function(data) {
                 var msgString = data.body;
                 var msgObject = $.parseJSON(msgString);

                 var status = translatedStatus(msgObject.status, $filter);

                 if(role === ROLE_APPROVER){        // approver produced this message to this topic
                     toastr.success(requisition+' '+msgObject.orderNumber+' '+status+'!', '', {timeOut: 4000});
                     if(mqProduceCallback){mqProduceCallback();}
                 }else if(role === ROLE_PURCHASER){    // purchaser consume this message from this topic
                     if (msgObject.requestedBy === CURRENT_USERNAME){
                         toastr.info(requisition+' '+msgObject.orderNumber+' '+status+'!', '', {timeOut: 0});
                         if(mqConsumeCallback){mqConsumeCallback();}
                         // update the number on the icon
                         getUnreviewedAmount($http,$rootScope,$filter);
                     }
                 }
            });

             // subscribe industry change topic
            mqClient.subscribe("/topic/globalPreferences.industryChange", function(data) {
                 var msgString = data.body;
                 var msgObject = $.parseJSON(msgString);
                 var currentIndustryOnPage = $rootScope.industry.toLowerCase();
                 var currentIndustryOnServer = msgObject.currentIndustry.toLowerCase();

                 if(msgObject.industryChanged === true && currentIndustryOnPage !== currentIndustryOnServer){
                    localStorage.setItem("removeRegionFilterInCookie",true);
                    mqClient.disconnect(function() {
                        var industryIsChangedInfo = $filter('translate')('INDUSTRY_IS_CHANGED');
                        toastr.info(industryIsChangedInfo, '', {timeOut: 0});
                    });
                 }
            });
        };

        var on_error =  function(e) {
            console.log('connection error');
            // TODO need reconnect or not?
        };

        mqClient.connect(symbol('a', 'n'), symbol('a', 'n'), on_connect, on_error, '/');
    });
}

function translatedStatus(status, $filter) {
    switch (status) {
        case 'SUBMITTED':
            status = 'IS_SUBMITTED';
            break;
        case 'PROCESSED':
            if (angular.equals(CURRENT_ROLE, ROLE_APPROVER)) {
                status = 'IS_SUBMITTED';
            } else {
                status = 'IS_PROCESSED';
            }
            break;
        case 'CANCELED':
            status = 'IS_CANCELLED';
            break;
        case 'APPROVED':
            status = 'IS_APPROVED';
            break;
        case 'DECLINED':
            status = 'IS_DECLINED';
    }
    return $filter('translate')(status);
}

/*
 * formElement: js element like document.getElementById("xx");
 */
function uploadImage($http, formElement, callBackAfterUploadSuccess, callBackAfterUploadFail) {
    var formdata = new FormData(formElement);
    $http({
        method:'post',
        url:'/v1/images',
        data: formdata,
        headers: {'Content-Type':undefined},
        transformRequest: angular.identity
    }).then(function(response) {
        callBackAfterUploadSuccess(response);
    }, function error(response) {
        callBackAfterUploadFail(response);
    }).catch(function(response) {
        callBackAfterUploadFail(response);
    });
}

function validateFileFormat(fileName){
    if(fileName === undefined){
        return;
    }
    var index = fileName.lastIndexOf(".");
    var suffixName = fileName.substring(index);

    var isSupportedFormat = false;
    switch(suffixName){
        case ".jpg":
        case ".jpeg":
        case ".bmp":
           case ".gif":
        case ".png":
            isSupportedFormat = true;
            break;
        default:
            isSupportedFormat = false;
    }

    return isSupportedFormat;
}

function validateFileSize(fileSize){
    if(fileSize === undefined){
        return;
    }
    var oneMB = 1048576
    var isCorrectFileSize = true;
    if(fileSize >= oneMB){
        isCorrectFileSize = false;
    }

    return isCorrectFileSize;
}

var errorMessages = new Array();
function displayLoadError(result,$rootScope,$filter,$http,showToastr,errorType){
    doDisplayLoadError(result,$rootScope,$filter,$http,showToastr,errorType);
}

function doDisplayLoadError(result,$rootScope,$filter,$http,showToastr,errorType){
    var data = result.data;
    var url = result.config.url;
    var dataString = JSON.stringify(data).toLowerCase();

    if(dataString.indexOf("can not establish connection with virtualize server") > -1){
        handleJDBCErrorMessage($rootScope,$filter,data.data,errorType,true);
        return;
    }

    if(dataString.indexOf("could not execute query") > -1){
        handleJDBCErrorMessage($rootScope,$filter,data.data,errorType,true);
        return;
    }

    if(dataString.indexOf("com.netflix.zuul.exception.zuulException") > -1){
        handleRestpointErrorMessage($rootScope,$http,$filter,errorType,url,showToastr,true);
        return;
    }

    if(dataString.indexOf("connect timed out") > -1){
        handleRestpointErrorMessage($rootScope,$http,$filter,errorType,url,showToastr, true);
        return;
    }

    handleRestpointErrorMessage($rootScope,$http,$filter,errorType,url,showToastr,false,data);
}

function handleRestpointErrorMessage($rootScope,$http,$filter,errorType,url,showToastr,isconnectionError,data){
    $http({
        method: 'GET',
        url: '/v1/demoAdmin/currentPreferences'
    }).then(function successCallback(response) {
        var preferenceData = response.data.data;
        var errorUrl;
        var endpointError = $filter('translate')('REST_ENDPOINT_ERROR');
        var unavailableToConnect = $filter('translate')('UNAVAILABLE_TO_CONNECT')

        $.each(preferenceData.restEndPoints,function(i,endpoint){
            if(endpoint.routeId.indexOf(errorType) > -1){
                url = endpoint.url
                return false;
            }
        });

        if(isconnectionError){
            var errorMessage = endpointError + url + ' ' + unavailableToConnect;
        }else{
            var handledString = JSON.stringify(data).substring(0, 1000);
            handledString = handledString + (handledString.length >= 1000 ? "..." : "");
            handledString = handledString.replace(/(\\r\\n)+/gi, ". ");
            handledString = handledString.replace(/(\\r)+/gi, ". ");
            handledString = handledString.replace(/(\\n)+/gi, ". ");
            handledString = handledString.replace(/(\\t)+/gi, " ");

            var errorMessage = endpointError + url + ' ' + handledString;
        }

        handleMessageAccordingToType($rootScope,$filter,errorMessage,errorType,showToastr,true);
    }, function errorCallback(response) {});
}

function handleJDBCErrorMessage($rootScope,$filter,url,errorType,showToastr){
    var JDBCError = $filter('translate')('JDBC_PARASOFT_JDBC_PROXY_ERROR');
    var tostrErrorMessage = $filter('translate')('CAN_NOT_CONNECT_TO_PARASOFT_JDBC_PROXY') + url;
    var errorMessage;

    if(url !== null){
        errorMessage = JDBCError + url + ' ' + $filter('translate')('UNAVAILABLE_TO_CONNECT');
        if(showToastr && $.inArray(tostrErrorMessage,errorMessages) < 0){
            errorMessages.push(tostrErrorMessage);
            toastrService().error(tostrErrorMessage);
        }
    }

    if(url === null){
        errorMessage = JDBCError + $filter('translate')('COULD_NOT_EXECUTE_QUERY');
        if(showToastr && $.inArray(errorMessage,errorMessages) < 0){
            errorMessages.push(errorMessage);
            toastrService().error(errorMessage);
        }
    }

    handleMessageAccordingToType($rootScope,$filter,errorMessage,errorType,false,false);
}

function handleMessageAccordingToType($rootScope,$filter,errorMessage,errorType,showToastr,isEndpointError){
        switch (errorType) {
            case "categories":
                if(isEndpointError) {errorMessage = $filter('translate')('CATEGORIES') + ' ' + errorMessage;}
                $rootScope.categoriesErrorMessage = errorMessage;
                break;
            case "items":
                if(isEndpointError) {errorMessage = $filter('translate')('ITEMS') + ' ' + errorMessage;}
                $rootScope.itemsErrorMessage = errorMessage;
                break;
            case "cart":
                if(isEndpointError) {errorMessage = $filter('translate')('CART_ITEMS') + ' ' + errorMessage;}
                $rootScope.cartIemsErrorMessage = errorMessage;
                break;
            case "orders":
                if(isEndpointError) {errorMessage = $filter('translate')('ORDERS') + ' ' + errorMessage;}
                $rootScope.ordersErrorMessage = errorMessage;
                break;
            case "locations":
                if(isEndpointError) {errorMessage = $filter('translate')('LOCATIONS') + ' ' + errorMessage;}
                $rootScope.locationsErrorMessage = errorMessage;
                break;
            default:
        }

        if(showToastr && $.inArray(errorMessage,errorMessages) < 0){
            errorMessages.push(errorMessage);
            toastrService().error(errorMessage);
        }
}

function getArrIndex(arr, obj) {
    var index = null;
    var key = Object.keys(obj)[0];
    arr.every(function(value, i) {
        if (value[key] === obj[key]) {
            index = i;
            return false;
        }
        return true;
    });
    return index;
}
