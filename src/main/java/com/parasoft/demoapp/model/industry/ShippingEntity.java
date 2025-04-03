package com.parasoft.demoapp.model.industry;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

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