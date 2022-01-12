package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.exception.*;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.repository.industry.CategoryRepository;
import com.parasoft.demoapp.repository.industry.ItemRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageService imageService;

    public CategoryEntity addNewCategory(String name, String description, String imagePath)
            throws CategoryNameExistsAlreadyException, ParameterException {

        ParameterValidator.requireNonBlank(name, AssetMessages.CATEGORY_NAME_CANNOT_BE_BLANK);
        ParameterValidator.requireNonBlank(description, AssetMessages.DESCRIPTION_CANNOT_BE_BLANK);

        if(categoryRepository.existsByName(name)){
            throw new CategoryNameExistsAlreadyException(
                    MessageFormat.format(AssetMessages.CATEGORY_NAME_EXISTS_ALREADY, name));
        }

        if (StringUtils.isBlank(imagePath)) {
            imagePath = imageService.getDefaultImage();
        }

        CategoryEntity category = new CategoryEntity(name, description, imagePath);
        return categoryRepository.save(category);
    }

    public void removeCategory(Long categoryId)
            throws CategoryNotFoundException, ParameterException, CategoryHasAtLeastOneItemException {

        ParameterValidator.requireNonNull(categoryId, AssetMessages.CATEGORY_ID_CANNOT_BE_NULL);

        if(!categoryRepository.existsById(categoryId)){
            throw new CategoryNotFoundException(MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, categoryId));
        }
        if(itemRepository.countByCategoryId(categoryId) > 0){
            throw new CategoryHasAtLeastOneItemException(
                    MessageFormat.format(AssetMessages.THERE_IS_AT_LEAST_ONE_ITEM_IN_THE_CATEGORY, categoryId));
        }

        CategoryEntity category = getByCategoryId(categoryId);

        try{ // it is ok if image failed to remove.
            String image = (null == category.getImage() ? " " : category.getImage());
            // Only delete uploaded image and not used by other item or category
            if (image.startsWith(WebConfig.UPLOADED_IMAGES_SUB_LOCATION) && imageService.numberOfImageUsed(image) <= 1)
                imageService.deleteUploadedImageByPath(category.getImage());
        }catch (Exception e){
            e.printStackTrace();
        }

        categoryRepository.deleteById(categoryId);
    }

    public CategoryEntity updateCategory(Long categoryId, String newName, String newDescription, String newImagePath)
            throws CategoryNotFoundException, ParameterException, CategoryNameExistsAlreadyException {

        ParameterValidator.requireNonBlank(newName, AssetMessages.CATEGORY_NAME_CANNOT_BE_BLANK);
        ParameterValidator.requireNonBlank(newDescription, AssetMessages.DESCRIPTION_CANNOT_BE_BLANK);

        CategoryEntity categoryEntity = getByCategoryId(categoryId);
        if(!newName.equals(categoryEntity.getName())){
            if(categoryRepository.existsByName(newName)){
                throw new CategoryNameExistsAlreadyException(
                        MessageFormat.format(AssetMessages.CATEGORY_NAME_EXISTS_ALREADY, newName));
            }
        }

        if (StringUtils.isBlank(newImagePath)) {
            newImagePath = imageService.getDefaultImage();
        }

        categoryEntity.setName(newName);
        categoryEntity.setDescription(newDescription);
        categoryEntity.setImage(newImagePath);
        
        categoryEntity = categoryRepository.save(categoryEntity);

        return categoryEntity;
    }

    /**
     * Obtain all items under current conditions.
     * @param searchString filter by name, if it is empty("" or null), this filter is ignored.
     * @param pageable page option
     * @return paged result
     */
    public Page<CategoryEntity> getCategories(String searchString, Pageable pageable) {

        Specification<CategoryEntity> spec = new CategorySpecification(searchString);

        return categoryRepository.findAll(spec, pageable);
    }

    private class CategorySpecification implements Specification<CategoryEntity>{
        private static final long serialVersionUID = -3671211277363076560L;

        private static final String NAME = "name";
        private static final String WILDCARD = "%";

        private String searchString;

        public CategorySpecification(String searchString) {
            this.searchString = searchString;
        }

        @Override
        public Predicate toPredicate(Root<CategoryEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Predicate> predicateList = new ArrayList<>();

            if (!StringUtils.isEmpty(searchString)) {
                Predicate searchPredicate = cb.like(
                        cb.lower(root.get(NAME)), WILDCARD + searchString.toLowerCase() + WILDCARD);
                predicateList.add(searchPredicate);
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        }
    }

    public CategoryEntity getByCategoryId(Long id) throws CategoryNotFoundException, ParameterException {
        ParameterValidator.requireNonNull(id, AssetMessages.CATEGORY_ID_CANNOT_BE_NULL);

        Optional<CategoryEntity> optional = categoryRepository.findById(id);
        if(!optional.isPresent()){
            throw new CategoryNotFoundException(MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, id));
        }

        return optional.get();
    }

    public CategoryEntity getByCategoryName(String name) throws CategoryNotFoundException, ParameterException {
        ParameterValidator.requireNonBlank(name, AssetMessages.CATEGORY_NAME_CANNOT_BE_BLANK);

        CategoryEntity category = categoryRepository.findByName(name);
        if(category == null){
            throw new CategoryNotFoundException(MessageFormat.format(AssetMessages.CATEGORY_NAME_NOT_FOUND, name));
        }

        return category;
    }

    public Page<CategoryEntity> searchCategoriesByNameOrDescription(String key, Pageable pageable)
            throws ParameterException {
        ParameterValidator.requireNonBlank(key, AssetMessages.SEARCH_FIELD_CANNOT_BE_BLANK);

        return categoryRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(key, key, pageable);
    }

    public boolean existsByCategoryId(Long categoryId){
        return categoryRepository.existsById(categoryId);
    }

    public long numberOfImageUsedInCategories(String imagePathOfCategory){
        if(imagePathOfCategory == null){
            return 0;
        }
        return categoryRepository.countByImage(imagePathOfCategory);
    }
}
