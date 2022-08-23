package com.parasoft.demoapp.service;

import java.text.MessageFormat;
import java.util.*;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.parasoft.demoapp.exception.DemoBugsIntroduceFailedException;
import com.parasoft.demoapp.exception.GlobalPreferencesMoreThanOneException;
import com.parasoft.demoapp.exception.GlobalPreferencesNotFoundException;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.messages.OrderMessages;
import com.parasoft.demoapp.model.global.preferences.DemoBugEntity;
import com.parasoft.demoapp.model.global.preferences.DemoBugsType;
import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.LocationEntity;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.OrderStatus;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.repository.global.DemoBugRepository;

@Service
@DependsOn("globalPreferencesService")
public class DemoBugService {

    @Autowired
    private DemoBugRepository demoBugRepository;

    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private IndustryRoutingDataSource industryRoutingDataSource;

    public void removeByGlobalPreferencesId(Long id) {
        Objects.requireNonNull(id, GlobalPreferencesMessages.GLOBAL_PREFERENCES_ID_CANNOT_BE_NULL);

        demoBugRepository.deleteByGlobalPreferencesId(id);
    }

    public void introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(OrderEntity order)
            throws GlobalPreferencesMoreThanOneException, GlobalPreferencesNotFoundException, DemoBugsIntroduceFailedException {

        if(order == null || order.getStatus() != OrderStatus.APPROVED){
            return;
        }

        GlobalPreferencesEntity globalPreferencesEntity = globalPreferencesService.getCurrentGlobalPreferences();
        Set<DemoBugEntity> demoBugs = globalPreferencesEntity.getDemoBugs();

        boolean needBug = false;
        for(DemoBugEntity demoBugEntity : demoBugs){
            if(demoBugEntity.getDemoBugsType() == DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS){
                needBug = true;
                break;
            }
        }

        if(!needBug){
            return;
        }

        IndustryType currentIndustry = globalPreferencesEntity.getIndustryType();
        LocationEntity incorrectLocation = null;
        RegionType realRegion = null;
        try{
            if(currentIndustry == IndustryType.DEFENSE) {
                if(order.getRegion() == RegionType.AUSTRALIA){
                    realRegion = RegionType.UNITED_KINGDOM;
                }else{
                    realRegion = RegionType.AUSTRALIA;
                }
            }else if(currentIndustry == IndustryType.AEROSPACE){
                if(order.getRegion() == RegionType.NEPTUNE){
                    realRegion = RegionType.VENUS;
                }else{
                    realRegion = RegionType.NEPTUNE;
                }
            }else if(currentIndustry == IndustryType.OUTDOOR){
                if(order.getRegion() == RegionType.LOCATION_8){
                    realRegion = RegionType.LOCATION_2;
                }else{
                    realRegion = RegionType.LOCATION_8;
                }
            }

            if(realRegion == null){
                // TODO handle incorrect location for other industry when they are needed
                throw new RuntimeException(
                        MessageFormat.format(OrderMessages.HAVE_NOT_IMPLEMENTED_BUG_FOR_CURRENT_INDUSTRY,
                                DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS.getValue()));
            }

            incorrectLocation = locationService.getLocationByRegion(realRegion);

        }catch (Exception e){
            e.printStackTrace();
            throw new DemoBugsIntroduceFailedException(
                    MessageFormat.format(OrderMessages.FAILED_TO_INTRODUCES_INCORRECT_LOCATION_BUG,
                                            DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS.getValue()), e);
        }

        order.setOrderImage(incorrectLocation.getLocationImage());
        order.setLocation(incorrectLocation.getLocationInfo());
        order.setRegion(incorrectLocation.getRegion());
    }

    public Pageable introduceBugWithReverseOrdersIfNeeded(Pageable pageable)
    		throws GlobalPreferencesNotFoundException, GlobalPreferencesMoreThanOneException {

         GlobalPreferencesEntity globalPreferencesEntity = globalPreferencesService.getCurrentGlobalPreferences();
         Set<DemoBugEntity> demoBugs = globalPreferencesEntity.getDemoBugs();
         Sort sort = pageable.getSort();

         for(DemoBugEntity demoBugEntity : demoBugs){
             if(demoBugEntity.getDemoBugsType() == DemoBugsType.REVERSE_ORDER_OF_ORDERS){
            	Iterator<Sort.Order> iterator = sort.iterator();
            	List<Sort.Order> orders = new ArrayList<>();

            	while(iterator.hasNext()) {
            		Sort.Order originOrder = iterator.next();
            		String property = originOrder.getProperty();
            		Sort.Order newOrder;
            		if(originOrder.getDirection() == Sort.Direction.ASC) {
            			newOrder = Order.desc(property);
            		}else {
            			newOrder = Order.asc(property);
            		}
            		orders.add(newOrder);
            	}

            	Sort newSort = Sort.by(orders);
                return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
             }
         }

        return pageable;
    }

    public void introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(Collection<OrderEntity> orders)
            throws GlobalPreferencesMoreThanOneException, GlobalPreferencesNotFoundException,
                    DemoBugsIntroduceFailedException {

        if(orders == null){
            return;
        }

        for(OrderEntity order : orders){
            introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);
        }
    }

    public void introduceBugWithCannotDetermineTargetDatasourceIfNeeded() {
        if(needBug(DemoBugsType.REINITIALIZE_DATASOURCE_FOR_EACH_HTTP_REQUEST)) {
            // Call afterPropertiesSet() method will case a concurrency problem
            industryRoutingDataSource.afterPropertiesSet();
        }
    }

    private boolean needBug(DemoBugsType bugType) {
        boolean needBug = false;
        try {
            GlobalPreferencesEntity globalPreferencesEntity = globalPreferencesService.getCurrentGlobalPreferences();
            for(DemoBugEntity demoBugEntity : globalPreferencesEntity.getDemoBugs()){
                if(demoBugEntity.getDemoBugsType() == bugType){
                    needBug = true;
                    break;
                }
            }
        } catch (GlobalPreferencesNotFoundException | GlobalPreferencesMoreThanOneException e) {
            // Will not reach here if project is started up successfully
            throw new RuntimeException(e);
        }

        return needBug;
    }
}
