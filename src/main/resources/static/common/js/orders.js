var app = angular.module('pdaApp', ['pascalprecht.translate']);

setLocale(app);
//initialize controllers of import page
initImportPageControllers(app);

initToastr();

app.controller('orderHistoryController', function($rootScope, $http, $filter, graphQLService) {
    var history = this;
    connectAndSubscribeMQ(CURRENT_ROLE,$http,$rootScope,$filter);
    getUnreviewedAmount($http,$rootScope,$filter);

    history.showOrderItems = function(items){
        var processedItems = "";
        $.each(items,function(i,item){
            var itemInfo = (i+1)+". "+item.name+" ("+item.quantity+") - ";
            if((i + 1) % 3 === 0){
                processedItems += itemInfo +"\n";
            }else{
                processedItems += itemInfo;
            }
        });
        return processedItems.substring(0,processedItems.lastIndexOf(" - "));
    }

    history.orderItems = function(items){
        var processedItems = "";
        $.each(items,function(i,item){
            var itemInfo = (i+1)+". "+item.name+" ("+item.quantity+") - ";
            processedItems += itemInfo;
        });

        return processedItems.substring(0,processedItems.lastIndexOf(" - "));
    }

    history.parseOrderStatus = function(status) {
        switch (status) {
            case 'SUBMITTED':
                return $filter('translate')('SUBMITTED_STATUS');
            case 'PROCESSED':
                return $filter('translate')('PROCESSED_STATUS');
            case 'CANCELED':
                return $filter('translate')('CANCELLED_STATUS');
            case 'APPROVED':
                return $filter('translate')('APPROVED_STATUS');
            case 'DECLINED':
                return $filter('translate')('DECLINED_STATUS');
            default:
                return '';
        }
    }

    history.showOrderDetail = {'show':false}

    history.openOrderDetail = function(index, orderNumber){
        let success = (data) => {
            history.order = data;

            if(!history.order.reviewedByPRCH){
                $http({
                    method: 'PUT',
                    url: '/proxy/v1/orders/'+history.order.orderNumber,
                    data: {
                        "status": history.order.status,
                        "reviewedByPRCH": true,
                        "reviewedByAPV": history.order.reviewedByAPV
                    },
                    headers: { 'Content-Type': 'application/json' }
                }).then(function(result) {
                    angular.element(".new_label" + index).css("visibility", "hidden");
                    //Update icon
                    getUnreviewedAmount($http,$rootScope,$filter);
                }).catch(function(result) {
                    console.log(result);
                });
            }

            var introduceIncorrectNumberBug = false;
            $.each($rootScope.deBugs,function(i,debug){
                if(debug.demoBugsType === "INCORRECT_NUMBER_OF_ITEMS_IN_SUMMARY_OF_PENDING_ORDER"){
                    introduceIncorrectNumberBug = true;
                    return false;
                }
            });

            if(introduceIncorrectNumberBug && history.order.status === "PROCESSED"){
                history.totalItemQuantity = 0;
            }else{
                var totalAmount = 0;
                $.each(history.order.orderItems,function(i,item){
                    totalAmount += item.quantity;
                });

                history.totalItemQuantity = totalAmount;
            }

            if(history.order.status === "CANCELED") {
                if(history.order.comments.indexOf("out of stock") !== -1) {
                    history.order.comments = $filter('translate')('INVENTORY_ITEM_OUT_OF_STOCK');
                } else if (history.order.comments.indexOf("doesn't exist") !== -1) {
                    history.order.comments = $filter('translate')('INVENTORY_ITEM_NOT_EXIST');
                }
            }
        };
        let error = (data) => {
            console.log(data);
        };
        let params = {"orderNumber": orderNumber};
        if (CURRENT_WEB_SERVICE_MODE === 'GraphQL') {
            let queryField = "reviewedByPRCH";
            graphQLService.getOrderByOrderNumber(params, success, (data) => {
                error(data, "graphQL");
            }, queryField);
        } else {
            $http({
                method: 'GET',
                url: '/proxy/v1/orders/'+orderNumber
            }).then(function(result) {
                success(result.data.data);
            }).catch(function(result) {
                error(result, "order");
            });
        }


        history.showOrderDetail['show'] = true;
    }

    history.closeOrderDetail = function(){
        history.showOrderDetail['show'] = false;
        history.order = undefined;
        history.totalItemQuantity = undefined;
    }

    history.loadStyles = function(index){
        return{
            "top": 85 * index + "px",
            "position": "absolute"
        };
    }

    /**For pagination**/
    // The item number for per page
    history.pageSize = 10;

    // Load data for previous page
    history.prev = function(currentPage){
        history.selectPage(currentPage-1);
    }

    // Load data for next page
    history.next = function(currentPage){
        history.selectPage(currentPage+1);
    }

    // Load data for given page number
    history.selectPage = function(page) {

        // Situation for page number is more than legal range
        if((history.totalPages !== 0 || history.totalPages !== undefined) && (page < 1 || page > history.totalPages)){
            return ;
        }

        $http({
            method: 'GET',
            url: '/proxy/v1/orders',
            params: {'sort':'orderNumber,desc'}
        }).then(function(result) {
            var orders = result.data.data.content;
            var totalOrders = result.data.data.totalElements;
            orders = $filter('orderBy')(orders,"orderNumber",true);

            if(totalOrders < 1){
                $rootScope.emptyContentError = true;
            }

            history.totalPages = Math.ceil(totalOrders / history.pageSize);
            history.currentPage = history.totalPages === 0 ? 0 : page;

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

            if(ordersList.length < 1){
                history.emptyContentsMessage = true;
            }

            // Update the new data on web
            history.orders = ordersList;
        }).catch(function(result) {
            console.log(result);
            history.totalPages = 0;
            history.currentPage = 0;
            displayLoadError(result,$rootScope,$filter,$http,false,"orders");
            history.ordersLoadError = true;
        });
    }

    // Set time out for avoiding to get the key when using $filter('translate') filter.
    setTimeout(function(){
        // Initialize pagination
        history.selectPage(1);
    }, 500);

    setTimeout(function(){ $("body").css("visibility","visible") }, 500);
});