package com.parasoft.demoapp.service;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.MessageFormat;

import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.exception.UploadedImageCanNotDeleteException;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.messages.ImageMessages;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class ImageService {

    public static final String JPG_SUFFIX = ".jpg";
    public static final String JPEG_SUFFIX = ".jpeg";
    public static final String GIF_SUFFIX = ".gif";
    public static final String PNG_SUFFIX = ".png";
    public static final String BMP_SUFFIX = ".bmp";

	@Autowired
	private WebConfig webConfig;

	@Autowired
    private ItemService itemService;

	@Autowired
    private CategoryService categoryService;

    /**
     * Handle MultipartFile object. <br/>
     * <br/>
     *  1.Validate format of this object.<br/>
     *  2.If this object is an image, store this image in specific path(static resource path). <br/>
     *  3.If the name of this image is exist, this image will overwrite the existing one.
     *  4.Return static resource path of this image.
     * @param image {@link MultipartFile}
     * @return Static resource path of this image
     * @throws IllegalStateException
     * @throws IOException
     * @throws ParameterException
     */
	public String handleImageAndReturnStaticLocation(MultipartFile image)
                                                        throws IllegalStateException, IOException, ParameterException {

		if (image == null || image.isEmpty()) {
            throw new ParameterException(ImageMessages.IMAGE_FILE_IS_EMPTY_OR_NOT_EXISTING);
        }

        String fileName = image.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        String suffixName = null;
        if(index != -1){
            suffixName = fileName.substring(index);
        }

        validateImageFormat(suffixName);

        String imagesStorePath = getUploadedImagesStorePath(IndustryRoutingDataSource.currentIndustry);

        File destinationFile = new File(imagesStorePath + fileName);

        if (!destinationFile.getParentFile().exists()) {
            destinationFile.getParentFile().mkdirs();
        }

        // if the existing image has the same name with current uploading image, delete the existing one and rewrite it.
        if(destinationFile.exists()){
            destinationFile.delete();
        }

        image.transferTo(destinationFile);

        String industrySubDirName = IndustryRoutingDataSource.currentIndustry.getValue();

        return WebConfig.UPLOADED_IMAGES_SUB_LOCATION + industrySubDirName + "/" + fileName;
	}

	private void validateImageFormat(String suffixName) throws ParameterException {
        ParameterValidator.requireNonNull(suffixName, ImageMessages.IMAGE_WITH_NO_SUFFIX_NAME);

        boolean isSupported = false;
        suffixName = suffixName.toLowerCase();

        switch (suffixName){
            case JPG_SUFFIX:
            case JPEG_SUFFIX:
            case GIF_SUFFIX:
            case PNG_SUFFIX:
            case BMP_SUFFIX:
                isSupported = true;
                break;
            default:
                isSupported = false;
        }

        if(!isSupported){
            throw new ParameterException(
                    MessageFormat.format(ImageMessages.IMAGE_SUFFIX_NAME_IS_NOT_SUPPORTED, suffixName));
        }
    }

    /**
     * Delete images by static resource path of image, but it is only for uploaded images.
     * This method will convert the static resource path to real path of image on server, and then delete it.
     * @param imagePath static resource path of image. Like /uploaded_images/defense/image.jpg
     * @throws ParameterException
     * @throws UploadedImageCanNotDeleteException Failed to delete image.
     */
    public void deleteUploadedImageByPath(String imagePath)
            throws ParameterException, UploadedImageCanNotDeleteException {

        if(imagePath == null || !imagePath.startsWith(WebConfig.UPLOADED_IMAGES_SUB_LOCATION)){
            throw new ParameterException(MessageFormat.format(ImageMessages.IMAGE_NOT_FOUND, imagePath));
        }

        String imageRealSubPath = imagePath.replace(WebConfig.UPLOADED_IMAGES_SUB_LOCATION, "");

        String realImagePath = Paths.get(webConfig.getUploadedImagesStorePath(), imageRealSubPath).toString();

        File imageFile = new File(realImagePath);
        if(!imageFile.delete()){
            throw new UploadedImageCanNotDeleteException(MessageFormat.format(ImageMessages.IMAGE_FAILED_TO_DELETE, imagePath));
        }
    }

    /**
     * Remove uploaded images of all industries.
     * @return true if all images are removed, or false if some images are not removed.
     */
    public boolean removeAllIndustriesUploadedImages(){

        // Remove all industry uploaded images from users
        boolean isAllImagesRemoved = true;
        for(IndustryType industry : IndustryType.values()){
            boolean flag = removeSpecificIndustryUploadedImages(industry);
            if(!flag){
                isAllImagesRemoved = false;
            }
        }

        return isAllImagesRemoved;
    }

    /**
     * Remove uploaded images of specific industry.
     * @param industry {@link IndustryType}
     * @return true if all images are removed, or false if some images are not removed.
     */
    public boolean removeSpecificIndustryUploadedImages(IndustryType industry){
        boolean isAllImagesRemoved = true;

        File industryUploadedImagesDirFile = new File(getUploadedImagesStorePath(industry));
        if(!industryUploadedImagesDirFile.exists()){
            return isAllImagesRemoved;
        }

        // Only remove files(not dirs) of specific dir.
        for(File image : industryUploadedImagesDirFile.listFiles()){
            boolean flag = image.delete();
            if(!flag){
                isAllImagesRemoved = false;
            }
        }

        if(!isAllImagesRemoved) {
            log.warn(MessageFormat.format(GlobalPreferencesMessages.SOME_UPLOADED_IMAGES_FAILED_TO_REMOVE, industry));
        }

        return isAllImagesRemoved;
    }

    /**
     * Get images of store Path, and this path is under specific industry.<br/>
     * @param industry Specific industry
     * @return Result is like: C:/Parasoft/demoapp/pda-files/uploaded_images/defense/
     */
    private String getUploadedImagesStorePath(IndustryType industry){
        String industrySubDirName = industry.getValue();
        return webConfig.getUploadedImagesStorePath() + File.separator + industrySubDirName + File.separator;
    }

    public String getDefaultImage() {

        IndustryType currentIndustry = IndustryRoutingDataSource.currentIndustry;
        String defaultImage = "/"+ currentIndustry.getValue().toLowerCase() +"/images/defaultImage.png";

        return defaultImage;
    }

    public long numberOfImageUsed(String imagePath){

        if(imagePath == null){
            return 0;
        }

        return itemService.numberOfImageUsedInItems(imagePath) +
                categoryService.numberOfImageUsedInCategories(imagePath);
    }
}
