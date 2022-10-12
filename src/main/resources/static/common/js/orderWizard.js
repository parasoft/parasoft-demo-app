var app = angular.module('orderWizardPageApp', ['pascalprecht.translate']);

setLocale(app);
//initialize controllers of import page
initImportPageControllers(app);

initToastr();

app.controller('orderWizardController', function($scope, $rootScope, $http, $filter) {
	$rootScope.isShowRequisitionRequestButton = false;
	connectAndSubscribeMQ(CURRENT_ROLE,$http,$rootScope,$filter);
	getUnreviewedAmount($http,$rootScope,$filter);

	//Initialize wizard
	$scope.isDevelopmentLocation = true;
	$scope.isAreaInfoNotReady = true;
	$scope.getLocationButton = true;
	$scope.isAssignCampaignInfoNotReady = true;
	$scope.getPositionInfo = false;

    $scope.shippingTypes = {
        shippingService1: 'STANDARD_SHIPPING',
        shippingService2: 'RUSH_SHIPPING',
        shippingService3: 'NEXT_DAY_SHIPPING'
    };

    $scope.shippingService = {
        // Set shippingType1 as the initial value for better demo about Selenium
        shippingType: $scope.shippingTypes.shippingService1,
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
	    	$scope.regions = regions;
	    }).catch(function(result) {
	        console.info(result);
	        displayLoadError(result,$rootScope,$filter,$http,true,"locations");
	    });
	}, 500);

	//Whether the related link shows in the breadcrumb bar
	$scope.isActive =function(flag,isClickable) {
		return flag ? 'active' : (isClickable ? 'clickable non-active' : 'non-active');
	}

	//Change to the front step when clicking the related link
	$scope.switchProcess = function(currentProcess,isClickable) {
		if(currentProcess === 'Deployment Location' && isClickable){
			$scope.isDevelopmentLocation = true;
			$scope.isAssignCampaign = false
			$scope.isReview = false;
			$scope.isCampaignClickable = false;
		}else if(currentProcess === 'Assign Campaign' && isClickable){
			$scope.isDevelopmentLocation = false;
			$scope.isAssignCampaign = true;
			$scope.isReview = false;
		}
	}

	//Change to next step when clicking button
	$scope.nextProcess = function(currentProcess) {
		if(currentProcess === 'Deployment Location'){
			$scope.isDevelopmentLocation = false;
			$scope.isAssignCampaign = true;
			$scope.isReview = false;
			$scope.isDevelopmentClickable = true;
			$scope.isCampaignClickable = false;

		}else if(currentProcess === 'Assign Campaign'){
			$scope.isDevelopmentLocation = false;
			$scope.isAssignCampaign = false;
			$scope.isReview = true;
			$scope.isDevelopmentClickable = true;
			$scope.isCampaignClickable = true;

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

				$scope.cartItems = cartItems;
				$scope.totalAmount = totalAmount;
		    }).catch(function(result) {
		        console.info(result);
		        displayLoadError(result,$rootScope,$filter,$http,true,'cart');
		    });
		}
	}

	//Show order delivery position
	$scope.showPosition = function(){
		var platoonId = angular.element("#platoon_id_input").val();
		$scope.platoonId = platoonId;
		$scope.getPositionInfo = true;

		if($scope.positionId !== null && $scope.positionId !== ''){
            $scope.isAreaInfoNotReady = false;
        }
	}

	//Show submitted status
	$scope.isSubmitted = false;
	$scope.submitForApproval = function(){
	    const shipping = {
            shippingType: $scope.shippingService.shippingType,
            receiverId: $scope.shippingService.positionId
        }
		$http({
	        method: 'POST',
	        url: '/proxy/v1/orders',
	        data: {region: $scope.region, location: $scope.locationInfo, shipping: shipping, eventId: $scope.campaignID, eventNumber: $scope.campaignNumber},
	        headers: { 'Content-Type': 'application/json' }
	    }).then(function(result) {
	    	$scope.isSubmitted = true;

	    	var response = result.data.data;
	        $scope.orderNumber = response.orderNumber;
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
	    	$scope.isSubmitted = false;
	    });
	}

	//Clear landmark
	$scope.changeArea = function() {
		$scope.positionInfo = false;
		$scope.isAreaInfoNotReady = true;
		$scope.getPositionInfo = false;

        var region_select = angular.element("#region_select option:selected").val().replace("string:","");
        $scope.region = region_select;
		if(region_select !== '' && region_select !== '-'){
            $http({
                method: 'GET',
        	    url: '/proxy/v1/locations/location',
        	    params:{"region": region_select}
            }).then(function(result) {
        	    $scope.positionInfo = true;
        	    var location = result.data.data;
        	    $scope.locationInfo = location.locationInfo;
        	    $scope.locationImage = location.locationImage;
            }).catch(function(result) {
        	    console.info(result);
        	    displayLoadError(result,$rootScope,$filter,$http,true,"locations");
            })
        }

		//Control for get location button
        if($scope.selectedArea !== null && $scope.selectedArea !== '' && $scope.shippingService.positionId !== undefined && $scope.shippingService.positionId !== ''
           && $scope.shippingService.shippingType !== undefined && $scope.shippingService.shippingType !== null && $scope.shippingService.shippingType !== ''){
			$scope.getLocationButton = false;
		}else{
			$scope.getLocationButton = true;
		}
	}

	//Whether the area info is not be null when change the value of the id (Platoon ID)
	$scope.checkAreaInfo = function(){
		//Control for process button
        if($scope.selectedArea && $scope.positionId && $scope.getPositionInfo && $scope.shippingService.shippingType){
			$scope.isAreaInfoNotReady = false;
		}else{
			$scope.isAreaInfoNotReady = true;
		}

		//Control for get location button
        if($scope.selectedArea && $scope.shippingService.positionId && $scope.shippingService.shippingType){
			$scope.getLocationButton = false;
		}else{
			$scope.getLocationButton = true;
		}
	}

	//Whether the campaign info is not be null when change the value of campaign data
	$scope.checkCampaignInfo = function() {
		if($scope.campaignID === undefined || $scope.campaignID === '' || $scope.campaignNumber === undefined || $scope.campaignNumber === ''){
			$scope.isAssignCampaignInfoNotReady = true;
		}else {
			$scope.isAssignCampaignInfoNotReady = false;
		}
	}

	$("body").css("visibility","visible");
});