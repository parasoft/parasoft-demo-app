var app = angular.module('orderWizardPageApp', ['pascalprecht.translate']);

setLocale(app);
//initialize controllers of import page
initImportPageControllers(app);

initToastr();

app.controller('orderWizardController', function($rootScope, $http, $filter) {
    var orderWizard = this;
    $rootScope.isShowRequisitionRequestButton = false;
    connectAndSubscribeMQ(CURRENT_ROLE,$http,$rootScope,$filter);
    getUnreviewedAmount($http,$rootScope,$filter);

    //Initialize wizard
    orderWizard.isDelopmentLocation = true;
    orderWizard.isAreaInfoNotReady = true;
    orderWizard.getLocationButton = true;
    orderWizard.isAssignCampaignInfoNotReady = true;
    orderWizard.getPositionInfo = false;

    orderWizard.shippingTypes = {
        shippingService1: 'STANDARD_SHIPPING',
        shippingService2: 'RUSH_SHIPPING',
        shippingService3: 'NEXT_DAY_SHIPPING'
    };

    orderWizard.shippingService = {
        shippingType: undefined,
        positionId: undefined
    };

    // Set time out for avoiding to get the key when using $filter('translate') filter.
    setTimeout(function(){
        //Get regions
        $http({
            method: 'GET',
            url: '/proxy/v1/locations/regions',
        }).then(function(result) {
            var regions = result.data.data;
            orderWizard.regions = regions;
        }).catch(function(result) {
            console.info(result);
            displayLoadError(result,$rootScope,$filter,$http,true,"locations");
        });
    }, 500);

    //Whether the related link shows in the breadcrumb bar
    orderWizard.isActive =function(flag,isClickable) {
        return flag ? 'active' : (isClickable ? 'clickable non-active' : 'non-active');
    }

    //Change to the front step when clicking the related link
    orderWizard.switchProcess = function(currentProcess,isClickable) {
        if(currentProcess === 'Deployment Location' && isClickable){
            orderWizard.isDelopmentLocation = true;
            orderWizard.isAssignCampaign = false
            orderWizard.isReview = false;
            orderWizard.isCampaignClickable = false;
        }else if(currentProcess === 'Assign Campaign' && isClickable){
            orderWizard.isDelopmentLocation = false;
            orderWizard.isAssignCampaign = true;
            orderWizard.isReview = false;
        }
    }

    //Change to next step when clicking button
    orderWizard.nextProcess = function(currentProcess) {
        if(currentProcess === 'Deployment Location'){
            orderWizard.isDelopmentLocation = false;
            orderWizard.isAssignCampaign = true;
            orderWizard.isReview = false;
            orderWizard.isDevelopmentClickable = true;
            orderWizard.isCampaignClickable = false;

        }else if(currentProcess === 'Assign Campaign'){
            orderWizard.isDelopmentLocation = false;
            orderWizard.isAssignCampaign = false;
            orderWizard.isReview = true;
            orderWizard.isDevelopmentClickable = true;
            orderWizard.isCampaignClickable = true;

            //Get cart items from database to avoid the changes of items in other pages
            $http({
                method: 'GET',
                url: '/proxy/v1/cartItems',
            }).then(function(result) {
                var cartItems = result.data.data;
                var totalAmount = 0;

                for(var i = 0; i < cartItems.length; i++){
                    //Calculate the total amount of cart items
                    totalAmount += cartItems[i].quantity;
                }

                orderWizard.cartItems = cartItems;
                orderWizard.totalAmount = totalAmount;
            }).catch(function(result) {
                console.info(result);
                displayLoadError(result,$rootScope,$filter,$http,true,'cart');
            });
        }
    }

    //Show order delivery position
    orderWizard.showPosition = function(positionId){
        var platoonId = angular.element("#platoon_id_input").val();
        orderWizard.platoonId = platoonId;
        orderWizard.getPositionInfo = true;

        if(positionId !== null && positionId !== ''){
            orderWizard.isAreaInfoNotReady = false;
        }
    }

    //Show submitted status
    orderWizard.isSubmitted = false;
    orderWizard.submitForApproval = function(region, location, shippingType, receiverId, eventId, eventNumber){
        const shipping = {
            shippingType: shippingType,
            receiverId: receiverId
        }
        $http({
            method: 'POST',
            url: '/proxy/v1/orders',
            data: {region: region, location: location, shipping: shipping, eventId: eventId, eventNumber: eventNumber},
            headers: { 'Content-Type': 'application/json' }
        }).then(function(result) {
            orderWizard.isSubmitted = true;

            var response = result.data.data;
            orderWizard.orderNumber = response.orderNumber;
        }, function error(response) {
            if(response.status === 400){
                toastr.error($filter('translate')('INSUFFICIENT_INVENTORY'));
            }else if(response.status === 404){
                if(response.data.message.indexOf("No message available") > -1){
                    displayLoadError(response,$rootScope,$filter,$http,true,"orders");
                }else{
                    toastr.error($filter('translate')('NO_SUCH_ITEMS'));
                }
            }
        }).catch(function(result) {
            orderWizard.isSubmitted = false;
        });
    }

    //Clear landmark
    orderWizard.changeArea = function(area,positionId){
        orderWizard.positionInfo = false;
        orderWizard.isAreaInfoNotReady = true;
        orderWizard.getPositionInfo = false;

        var region_select = angular.element("#region_select option:selected").val().replace("string:","");
        orderWizard.region = region_select;
        if(region_select !== '' && region_select !== '-'){
            $http({
                method: 'GET',
                url: '/proxy/v1/locations/location',
                params:{"region": region_select}
            }).then(function(result) {
                orderWizard.positionInfo = true;
                var location = result.data.data;
                orderWizard.locationInfo = location.locationInfo;
                orderWizard.locationImage = location.locationImage;
            }).catch(function(result) {
                console.info(result);
                displayLoadError(result,$rootScope,$filter,$http,true,"locations");
            })
        }

        //Control for get location button
        if(area !== null && area !== '' && positionId !== undefined && positionId !== ''){
            orderWizard.getLocationButton = false;
        }else{
            orderWizard.getLocationButton = true;
        }
    }

    //Whether the area info is not be null when change the value of the id (Platoon ID)
    orderWizard.checkAreaInfo = function(area,positionId,shippingType,landmark){
        //Control for process button
        if(area === undefined || area === null || area === '' || positionId === undefined || positionId === null
            || positionId === '' || shippingType === undefined || shippingType === null || shippingType === ''
            || landmark === false){
            orderWizard.isAreaInfoNotReady = true;
        }else{
            orderWizard.isAreaInfoNotReady = false;
        }

        //Control for get location button
        if(area !== undefined && area !== null && area !== '' && positionId !== undefined && positionId !== ''
            && positionId !== null && shippingType !== undefined && shippingType !== null && shippingType !== ''){
            orderWizard.getLocationButton = false;
        }else{
            orderWizard.getLocationButton = true;
        }
    }

    //Whether the campaign info is not be null when change the value of campaign data
    orderWizard.checkCampaignInfo = function(campaignID,campaignNumber){
        if(campaignID === undefined || campaignID === '' || campaignNumber === undefined || campaignNumber === ''){
            orderWizard.isAssignCampaignInfoNotReady = true;
        }else {
            orderWizard.isAssignCampaignInfoNotReady = false;
        }
    }

    $("body").css("visibility","visible");
});