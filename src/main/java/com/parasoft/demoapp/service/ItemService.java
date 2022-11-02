package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.exception.*;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.repository.industry.ItemRepository;
import com.parasoft.demoapp.util.SqlStringEscapeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ItemInventoryService itemInventoryService;

    @Transactional(value = "industryTransactionManager")
    public ItemEntity addNewItem(
            String name, String description, Long categoryId, Integer inStock, String imagePath, RegionType region)
            throws ItemNameExistsAlreadyException, CategoryNotFoundException, ParameterException, GlobalPreferencesNotFoundException,
            GlobalPreferencesMoreThanOneException, UnsupportedOperationInCurrentIndustryException {

        ParameterValidator.requireNonBlank(name, AssetMessages.ITEM_NAME_CANNOT_BE_BLANK);
        ParameterValidator.requireNonBlank(description, AssetMessages.DESCRIPTION_CANNOT_BE_BLANK);
        ParameterValidator.requireNonNull(categoryId, AssetMessages.CATEGORY_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(inStock, AssetMessages.IN_STOCK_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(region, AssetMessages.REGION_CANNOT_BE_NULL);
        ParameterValidator.requireNonNegative(inStock,
                MessageFormat.format(AssetMessages.IN_STOCK_CANNOT_BE_A_NEGATIVE_NUMBER, inStock));

        if(!categoryService.existsByCategoryId(categoryId)){
            throw new CategoryNotFoundException(MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, categoryId));
        }

        if (itemRepository.existsByName(name)) {
            throw new ItemNameExistsAlreadyException(
                    MessageFormat.format(AssetMessages.ITEM_NAME_EXISTS_ALREADY, name));
        }

        if (StringUtils.isBlank(imagePath)) {
            imagePath = imageService.getDefaultImage();
        }

        if(!locationService.isCorrectRegionInCurrentIndustry(region)) {
        	throw new UnsupportedOperationInCurrentIndustryException(AssetMessages.INCORRECT_REGION_IN_CURRENT_INDUSTRY);
        }

        ItemEntity item = new ItemEntity(name, description, categoryId, imagePath, region, new Date());
        ItemEntity result = itemRepository.save(item);
        Integer itemInStock = itemInventoryService.saveItemInStock(result.getId(), inStock).getInStock();
        result.setInStock(itemInStock);

        return result;
    }

    @Transactional(value = "industryTransactionManager")
    public void removeItemById(Long itemId) throws ItemNotFoundException, ParameterException {
        ParameterValidator.requireNonNull(itemId, AssetMessages.ITEM_ID_CANNOT_BE_NULL);

        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(
                    MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, itemId));
        }

        ItemEntity item = getItemById(itemId);

        try{ // it is ok if image failed to remove.
            String image = (null == item.getImage() ? "" : item.getImage());
            // Only delete uploaded image and not used by other item or category
            if (image.startsWith(WebConfig.UPLOADED_IMAGES_SUB_LOCATION) && imageService.numberOfImageUsed(image) <= 1)
                imageService.deleteUploadedImageByPath(image);
        }catch (Exception e){
            e.printStackTrace();
        }

        itemInventoryService.removeItemInventoryByItemId(itemId);
        itemRepository.deleteById(itemId);
    }

    public void removeItemByName(String itemName) throws ItemNotFoundException, ParameterException {
        ParameterValidator.requireNonBlank(itemName, AssetMessages.ITEM_NAME_CANNOT_BE_BLANK);

        if (!itemRepository.existsByName(itemName)) {
            throw new ItemNotFoundException(
                    MessageFormat.format(AssetMessages.ITEM_NAME_NOT_FOUND, itemName));
        }

        ItemEntity item =  getItemByName(itemName);

        removeItemById(item.getId());
    }

    @Transactional(value = "industryTransactionManager")
    public ItemEntity updateItem(Long itemId, String name, String description, Long categoryId,
                                 Integer inStock, String imagePath, RegionType region)
            throws ItemNameExistsAlreadyException, CategoryNotFoundException, ItemNotFoundException,
            ParameterException, UnsupportedOperationInCurrentIndustryException {

        ParameterValidator.requireNonNull(itemId, AssetMessages.ITEM_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonBlank(name, AssetMessages.ITEM_NAME_CANNOT_BE_BLANK);
        ParameterValidator.requireNonBlank(description, AssetMessages.DESCRIPTION_CANNOT_BE_BLANK);
        ParameterValidator.requireNonNull(categoryId, AssetMessages.CATEGORY_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(inStock, AssetMessages.IN_STOCK_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(region, AssetMessages.REGION_CANNOT_BE_NULL);
        ParameterValidator.requireNonNegative(inStock,
                MessageFormat.format(AssetMessages.IN_STOCK_CANNOT_BE_A_NEGATIVE_NUMBER, inStock));

        if(!categoryService.existsByCategoryId(categoryId)){
            throw new CategoryNotFoundException(MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, categoryId));
        }

        ItemEntity itemEntity = getItemById(itemId);

        if (!itemEntity.getName().equals(name)) {
            if (itemRepository.existsByName(name)){
                throw new ItemNameExistsAlreadyException(
                        MessageFormat.format(AssetMessages.ITEM_NAME_EXISTS_ALREADY, name));
            } else {
                itemEntity.setName(name);
            }
        }

        if (StringUtils.isBlank(imagePath)) {
            imagePath = imageService.getDefaultImage();
        }

        if(!locationService.isCorrectRegionInCurrentIndustry(region)) {
        	throw new UnsupportedOperationInCurrentIndustryException(AssetMessages.INCORRECT_REGION_IN_CURRENT_INDUSTRY);
        }

        itemEntity.setDescription(description);
        itemEntity.setCategoryId(categoryId);
        itemEntity.setImage(imagePath);
        itemEntity.setRegion(region);
        itemEntity.setLastAccessedDate(new Date());

        ItemEntity item = itemRepository.save(itemEntity);
        Integer itemInStock = itemInventoryService.saveItemInStock(itemId, inStock).getInStock();
        item.setInStock(itemInStock);

        return item;
    }

    @Transactional(value = "industryTransactionManager")
    public ItemEntity updateItemInStock(Long itemId, Integer newInStock)
            throws ParameterException, ItemNotFoundException {
        ParameterValidator.requireNonNull(newInStock, AssetMessages.IN_STOCK_CANNOT_BE_NULL);
        ParameterValidator.requireNonNegative(newInStock, AssetMessages.IN_STOCK_CANNOT_BE_A_NEGATIVE_NUMBER);

        ItemEntity item = getItemById(itemId);
        item.setInStock(itemInventoryService.saveItemInStock(itemId, newInStock).getInStock());

        return item;
    }

    public List<ItemEntity> getAllItems() throws ItemNotFoundException{
        List<ItemEntity> items = itemRepository.findAll();

        if (items.size() == 0) {
            throw new ItemNotFoundException(AssetMessages.NO_ITEMS);
        }

        items.forEach(item -> {
            try {
                item.setInStock(itemInventoryService.getInStockByItemId(item.getId()));
            } catch (ParameterException e) {
                e.printStackTrace();
            }
        });

        return items;
    }

    /**
     * Obtain all items under current conditions.
     * @param categoryId filter by category id, if it is null, this filter is ignored.
     * @param regions filter by region type, if it is null, this filter is ignored.
     * @param searchString filter by name, if it is empty("" or null), this filter is ignored.
     * @param pageable page option
     * @return paged result
     * @throws ItemNotFoundException
     */
    public Page<ItemEntity> getItems(Long categoryId, RegionType[] regions, String searchString, Pageable pageable)
            throws ItemNotFoundException {

        Specification<ItemEntity> spec = new ItemSpecification(categoryId, regions, searchString);

        Page<ItemEntity> items = itemRepository.findAll(spec, pageable);

        if (items.getSize() == 0) {
            throw new ItemNotFoundException(AssetMessages.NO_ITEMS);
        }

        items.forEach(item -> {
            try {
                item.setInStock(itemInventoryService.getInStockByItemId(item.getId()));
            } catch (ParameterException e) {
                e.printStackTrace();
            }
        });

        return items;
    }

    private class ItemSpecification implements Specification<ItemEntity>{
        private static final long serialVersionUID = -3831816113290085890L;
        private static final String CATEGORY_ID = "categoryId";

        private static final String DESCRIPTION = "description";
        private static final String REGION = "region";
        private static final String NAME = "name";
        private static final String WILDCARD = "%";
        Long categoryId;

        RegionType[] regions;

        String searchString;

        public ItemSpecification(Long categoryId, RegionType[] regions, String searchString) {
            this.categoryId = categoryId;
            this.regions = regions;
            this.searchString = searchString;
        }

        @Override
        public Predicate toPredicate(Root<ItemEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Predicate> predicateList = new ArrayList<>();

            if (categoryId != null){
                Predicate categoryPredicate = cb.equal(root.get(CATEGORY_ID), categoryId);
                predicateList.add(categoryPredicate);
            }

            if (regions != null && regions.length != 0) {
                Predicate regionPredicate = cb.and(root.get(REGION).in((Object[]) regions));
                predicateList.add(regionPredicate);
            }

            if (!StringUtils.isEmpty(searchString)) {
                searchString = SqlStringEscapeUtil.escapeLikeString(searchString);
                searchString = WILDCARD + searchString.toLowerCase() + WILDCARD;
                Predicate searchPredicate = cb.or(
                    cb.like(cb.lower(root.get(NAME)), searchString, SqlStringEscapeUtil.escapeChar),
                    cb.like(cb.lower(root.get(DESCRIPTION)), searchString, SqlStringEscapeUtil.escapeChar));
                predicateList.add(searchPredicate);
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        }

    }

    public ItemEntity getItemById(Long id) throws ItemNotFoundException, ParameterException {
        ParameterValidator.requireNonNull(id, AssetMessages.ITEM_ID_CANNOT_BE_NULL);

        Optional<ItemEntity> optional = itemRepository.findById(id);
        if(!optional.isPresent()){
            throw new ItemNotFoundException(MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, id));
        }

        ItemEntity item = optional.get();
        item.setInStock(itemInventoryService.getInStockByItemId(item.getId()));

        return item;
    }

    public ItemEntity getItemByName(String name) throws ItemNotFoundException, ParameterException {
        ParameterValidator.requireNonBlank(name, AssetMessages.ITEM_NAME_CANNOT_BE_BLANK);

        ItemEntity item = itemRepository.findByName(name);
        if (item == null) {
            throw new ItemNotFoundException(MessageFormat.format(AssetMessages.ITEM_NAME_NOT_FOUND, name));
        }
        item.setInStock(itemInventoryService.getInStockByItemId(item.getId()));
        return item;
    }

    public Page<ItemEntity> searchItemsByNameOrDescription(String key, Pageable pageable) throws ParameterException {
        ParameterValidator.requireNonBlank(key, AssetMessages.SEARCH_FIELD_CANNOT_BE_BLANK);

        Page<ItemEntity> items = itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(key, key, pageable);
        items.forEach(item -> {
            try {
                item.setInStock(itemInventoryService.getInStockByItemId(item.getId()));
            } catch (ParameterException e) {
                e.printStackTrace();
            }
        });

        return items;
    }

    public long numberOfImageUsedInItems(String imagePathOfItem){
        if(imagePathOfItem == null){
            return 0;
        }

        return itemRepository.countByImage(imagePathOfItem);
    }

    public boolean existsByItemId(Long itemId){
        return itemRepository.existsById(itemId);
    }
}
