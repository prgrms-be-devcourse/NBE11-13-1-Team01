package com.composebean.web.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductForm {

    private String name;
    private Long price;
    private String description;
    private MultipartFile imageFile;
    private boolean deleteImage;
    private Integer stockQuantity;
}
