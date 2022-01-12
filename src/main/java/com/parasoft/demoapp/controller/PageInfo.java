package com.parasoft.demoapp.controller;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PageInfo<T>  {

	@Schema(description = "The total amount of elements.")
    private long totalElements;
	@Schema(description = "The number of total pages.")
    private long totalPages;
	@Schema(description = "The size of the slice.")
    private long size;
	@Schema(description = "The number of the current slice.")
    private long number;
	@Schema(description = "The number of elements currently on this slice.")
    private long numberOfElements;
	@Schema(description = "The sorting parameters for the slice.")
    private String sort;
	@Schema(description = "The page content as list.")
    private List<T> content;

    public PageInfo(Page<T> page){
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.size = page.getSize();
        this.number = page.getNumber();
        this.numberOfElements = page.getNumberOfElements();
        this.sort = page.getSort().toString();
        this.content = page.getContent();
    }
}