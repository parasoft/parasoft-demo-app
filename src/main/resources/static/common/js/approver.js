var app = angular.module('pdaApp', ['pascalprecht.translate']);

setLocale(app);
initHeaderController(app);
initProductBuildInfo(app);
initAuthorizationHeader(app);
initToastr();

app.controller('approverHomePageController', function($rootScope, $http, $filter, graphQLService, $window) {
    var approver = this;

    var mqConsumeCallback = function mqConsumeCallback(){
        if(approver.totalPages === 0){
            approver.totalPages = 1;
        }
        if(approver.currentPage === 0){
            approver.currentPage = 1;
        }
        approver.selectPage(approver.currentPage);
    }

    approver.scrollToQueueConfiguration = function () {
        if (localStorage.getItem("displayKafkaError") === 'true') {
            $window.location.href="/demoAdmin#parasoft_queue_configuration";
        } else {
            $window.location.href="/demoAdmin";
        }
    }

    connectAndSubscribeMQ(CURRENT_ROLE,$http,$rootScope,$filter,mqConsumeCallback);

    //To control whether the order detail is visible or hidden
    approver.showOrderDetail = {'show':false}
    approver.openOrderDetail = function(orderNum) {
        let success = (data) => {
            handleOrderDetail(data);
        }
        let error = (data) => {
            console.log(data);
        };
        let params = {"orderNumber": orderNum};
        if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            let selectionSet = "{" +
                "orderNumber,requestedBy,status,reviewedByAPV," +
                "orderItems" +
                "{" +
                "name,description,image,quantity" +
                "}," +
                "region,location,orderImage,receiverId,eventId,eventNumber,comments" +
                "}";
            graphQLService.getOrderByOrderNumber(params, success, (data) => {
                error(data, "graphQL");
            }, selectionSet);
        } else {
            $http({
                method: 'GET',
                url: '/proxy/v1/orders/'+orderNum,
            }).then(function(result) {
                success(result.data.data);
            }).catch(function(result) {
                error(result, "approver");
            });
        }
    }

    function handleOrderDetail(orderDetail){
        approver.orderDetail = orderDetail;
        var orderStatus = approver.orderDetail.status;

        if(!approver.orderDetail.reviewedByAPV){
            approver.changeReviewStatus(approver.orderDetail);
        }

        approver.response = {
            'showResponseSelect' : false,
            'showResponseResult' : false,
            'showResponseCommentsLabel' : true,
            'showResponseCommentsTextarea' : false,
            'showResponseCommentsResult' : false,
            'showResponseSaveAndCancel' : false,
            'responseSelectValue' : '',
            'responseCommentsTextarea' : '',
            'saveButtonDisabled' : true,
            'toggleElements' : function(){

                if(approver.response.responseSelectValue === ''){
                    approver.response.saveButtonDisabled = true;
                    approver.response.responseCommentsTextarea = '';
                }else{
                    approver.response.saveButtonDisabled = false;
                }
            },
        };

        if(orderStatus === 'PROCESSED'){
            approver.response.showResponseSelect = true;
            approver.response.showResponseCommentsTextarea = true;
            approver.response.showResponseSaveAndCancel = true;
            approver.response.showResponseSaveAndCancel = true;
        } else {
            approver.response.showResponseResult = true;
            approver.response.showResponseCommentsResult = true;
        }

        approver.showOrderDetail['show'] = true;
    }

    approver.closeOrderDetail = function(){
        approver.showOrderDetail['show'] = false;
    }

    approver.changeReviewStatus = function(orderDetail){

        var orderNumber = orderDetail.orderNumber;
        var orderStatus = orderDetail.status;
        var reviewedByAPV = true;

        let success = (data) => {
            //Update icon
            approver.showNewLabel[orderNumber] = false;
        }

        let error = (data) => {
            console.log(data);
        }

        let orderStatusData = {"status":orderStatus,"reviewedByAPV":reviewedByAPV};
        let variables = {
            "orderNumber": orderNumber,
            "orderStatusDTO": orderStatusData
        };

        if (CURRENT_WEB_SERVICE_MODE === "GraphQL"){
            let selectionSet = "{orderNumber}"
            graphQLService.updateOrderByOrderNumber(variables, success, (data) => {error(data)}, selectionSet);
        } else {
            $http({
                method: 'PUT',
                url: '/proxy/v1/orders/'+orderNumber,
                data: orderStatusData,
                headers: { 'Content-Type': 'application/json' }
            }).then(function(result) {
                success(result.data.data);
            }).catch(function(result) {
                error(result);
            });
        }
    }

    approver.loadStyles = function(index){
        return {
            "top":64*index+"px",
            "position":"absolute"
        };
    }

    approver.calItemsAmount = function(items){
        var total = 0;

        $.each(items,function(i,item){
            total += item.quantity;
        });

        return total;
    }

    approver.sendResponse = function(orderNumber){
        let success = (data) => {
            handleOrderDetail(data);
            approver.selectPage(approver.currentPage);
            approver.showOrderDetail['show'] = false;
        };

        let error = (data) => {
            approver.response.saveButtonDisabled = false;
            let errCode = data.status;
            let errMsg;
            let errTitle;
            switch (errCode) {
                case 400:
                    errMsg = $filter('translate')('UPDATING_REQUEST_ERROR');
                    break;
                case 401:
                    errMsg = $filter('translate')('NO_AUTHORIZATION_TO_UPDATE_ORDER');
                    break;
                case 403:
                    errMsg = $filter('translate')('NO_PERMISSION_TO_UPDATE_ORDER');
                    break;
                case 404:
                    errMsg = $filter('translate')('ORDER_NOT_FOUND');
                    break;
                case 500:
                    if(data.data.message.indexOf("Can not send message to Kafka broker.") > -1) {
                        errTitle = $filter('translate')('UPDATING_ERROR_TITLE')
                        errMsg = $filter('translate')('KAFKA_BROKER_NOT_AVAILABLE');
                    } else {
                        errMsg = $filter('translate')('UPDATING_ERROR');
                    }
                    break;
                default:
                    errMsg = $filter('translate')('UPDATING_ERROR');
            }
            toastrService().error(errMsg, errTitle);
        };

        approver.response.saveButtonDisabled = true;
        let orderStatusData = angular.element('#order_response_form').serializeJSON();
        let variables = {
            "orderNumber": orderNumber,
            "orderStatusDTO": orderStatusData
        };
        if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            let selectionSet = "{status,reviewedByAPV,orderNumber}"
            graphQLService.updateOrderByOrderNumber(variables, success, (data) => {error(data)}, selectionSet);
        } else {
            $http({
                method: 'PUT',
                url: '/proxy/v1/orders/'+orderNumber,
                data: orderStatusData,
                headers: { 'Content-Type': 'application/json' }
            }).then(function(result) {
                success(result.data.data);
            }).catch(function(result) {
                error(result);
            });
        }
    }

    /**For pagination**/
    // The item number for per page
    approver.pageSize = 10;

    // Load data for previous page
    approver.prev = function(currentPage){
        approver.selectPage(currentPage-1);
    }

    // Load data for next page
    approver.next = function(currentPage){
        approver.selectPage(currentPage+1);
    }

    // Load data for given page number
    approver.selectPage = function(page) {

        // Situation for page number is more than legal range
        if((approver.totalPages !== 0 || approver.totalPages !== undefined) && (page < 1 || page > approver.totalPages)){
            return ;
        }

        let success = (data) => {
            var orders = data.content;
            var totalOrders = data.totalElements;
            orders = $filter('orderBy')(orders,"orderNumber",true);

            if(totalOrders < 1){
                $rootScope.emptyContentError = true;
            }

            approver.totalPages = Math.ceil(totalOrders / approver.pageSize);
            approver.currentPage = approver.totalPages === 0 ? 0 : page;

            var begin; //The index of the first order
            var end; //The index of the last order
            begin = 10 * (page - 1);
            end = begin + 10;

            // Situation for the last index is more than legal range
            if(end > totalOrders){
                end = totalOrders;
            }

            //Obtain the orders' list shown on the page
            var ordersList = new Array();
            for(var i = begin; i < end; i++){
                ordersList.push(orders[i]);
            }

            //Fill in with blank lines if the total amount of orders less than 10
            if(ordersList.length < approver.pageSize && ordersList.length != 0){
                var blank = approver.pageSize - ordersList.length
                for(var i = 0; i < blank; i++){
                    ordersList.push("");
                }
            }

            approver.showNewLabel = {};
            $.each(ordersList,function(i,order){
                approver.showNewLabel[order.orderNumber] = !order.reviewedByAPV;
                // Convert the time to local time
                order.submissionDate = moment.utc(order.submissionDate).local().format('YYYY-MM-DDTHH:mm:ss.SSSZ');
            });

            if(ordersList.length < 1){
                approver.emptyContentsMessage = true;
            }

            // Update the new data on web
            approver.orders = ordersList;
        }

        let error = (data, endpointType) => {
            console.log(data);
            approver.totalPages = 0;
            approver.currentPage = 0;
            displayLoadError(data,$rootScope,$filter,$http,false,endpointType);
            approver.ordersLoadError = true;
        }

        if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            let selectionSet = "{totalElements,content{orderNumber,requestedBy,status,reviewedByAPV,submissionDate}}";
            graphQLService.getOrders(success, (data) => {error(data, "graphQL")}, selectionSet);
        } else {
            $http({
                method: 'GET',
                url: '/proxy/v1/orders',
                params: {'sort':'orderNumber,desc'}
            }).then(function(result) {
                success(result.data.data);
            }).catch(function(result) {
                error(result, 'orders');
            });
        }
    }

    approver.parseOrderStatus = function(status) {
        switch (status) {
            case 'PROCESSED':
                return $filter('translate')('OPEN_STATUS');
            case 'APPROVED':
                return $filter('translate')('APPROVED_STATUS');
            case 'DECLINED':
                return $filter('translate')('DECLINED_STATUS');
            default:
                return '';
        }
    }

    // Set time out for avoiding to get the key when using $filter('translate') filter.
    setTimeout(function(){
        // Initialize pagination
        approver.selectPage(1);
    }, 500);

    // To avoid displaying page without styles due to the slow loading of CSS files
    setTimeout(function(){ $("body").css("visibility","visible") }, 500);
});