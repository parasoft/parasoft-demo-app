package com.parasoft.demoapp.model.industry;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ShippingEntity {
    @Setter
    @NotBlank
    private String shippingType;

    @Setter
    @NotBlank
    private String receiverId;
}