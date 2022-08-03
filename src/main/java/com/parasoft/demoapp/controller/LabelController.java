package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.dto.LabelsRequestDTO;
import com.parasoft.demoapp.dto.LabelsResponseDTO;
import com.parasoft.demoapp.exception.LocalizationException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import com.parasoft.demoapp.model.industry.LabelEntity;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.LabelService;
import com.parasoft.demoapp.service.LocalizationService;
import com.parasoft.demoapp.util.PropertiesMapUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "labels")
@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class LabelController {

    @Autowired
    private LabelService labelService;

    @Autowired
    private LocalizationService localizationService;

    @Autowired
    private GlobalPreferencesService globalPreferencesService;

    @Operation(description = "Update labels.")
    @ApiResponse(responseCode = "200", description = "Labels updated successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request payload.",
            content = {@Content(schema = @Schema(hidden = true))})
    @ApiResponse(responseCode = "403", description = "The user does not have permission to update the item.",
            content = {@Content(schema = @Schema(hidden = true)) })
    @PutMapping("/v1/labels")
    @ResponseBody
    public ResponseResult<LabelsResponseDTO> updateLabels(@RequestBody LabelsRequestDTO labelsRequestDTO)
            throws ParameterException, LocalizationException {

    	ResponseResult<LabelsResponseDTO> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
                ResponseResult.MESSAGE_OK);

        List<LabelEntity> labelEntities = labelService.updateLabelsInDB(labelsRequestDTO.toEntities(), labelsRequestDTO.getLanguageType());

        GlobalPreferencesEntity globalPreferencesEntity =
                globalPreferencesService.updateLabelOverridedStatus(labelsRequestDTO.getLabelsOverrided());

        LabelsResponseDTO labelsResponseDTO = LabelsResponseDTO.convertFrom(labelEntities, labelsRequestDTO.getLanguageType());
        labelsResponseDTO.setLabelsOverrided(globalPreferencesEntity.isLabelsOverrided());

        response.setData(labelsResponseDTO);

        return response;
    }

    @Operation(description = "Obtain overrided labels.")
    @ApiResponse(responseCode = "200", description = "Overrided labels were returned.")
    @GetMapping("/v1/labels/overrided")
    @ResponseBody
    public ResponseResult<LabelsResponseDTO> getOverridedLabels(@RequestParam(value = "language") LocalizationLanguageType languageType)
            throws ParameterException {

        ResponseResult<LabelsResponseDTO> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
                ResponseResult.MESSAGE_OK);

        LabelsResponseDTO labelsResponseDTO = new LabelsResponseDTO();
        labelsResponseDTO.setLanguageType(languageType);
        labelsResponseDTO.setLabelPairs(PropertiesMapUtil.sortByKey(localizationService.loadPropertiesFromDB(languageType)));

        labelsResponseDTO.setLabelsOverrided(globalPreferencesService.getLabelOverridedStatus());

        response.setData(labelsResponseDTO);

        return response;
    }

    @Operation(description = "Obtain default labels.")
    @ApiResponse(responseCode = "200", description = "Default labels were returned.")
    @GetMapping("/v1/labels/default")
    @ResponseBody
    public ResponseResult<LabelsResponseDTO> getDefaultLabels(@RequestParam(value = "language") LocalizationLanguageType languageType)
            throws LocalizationException, ParameterException {

        ResponseResult<LabelsResponseDTO> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
                ResponseResult.MESSAGE_OK);

        LabelsResponseDTO labelsResponseDTO = new LabelsResponseDTO();
        labelsResponseDTO.setLanguageType(languageType);
        labelsResponseDTO.setLabelPairs(PropertiesMapUtil.sortByKey(localizationService.loadPropertiesFromFile(languageType)));

        response.setData(labelsResponseDTO);

        return response;
    }

}
