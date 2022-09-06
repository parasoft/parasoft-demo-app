package com.parasoft.demoapp.model.industry;

import lombok.*;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ShippingEntity {
    @Setter
    private String shippingType;

    @Setter
    private String receiverId;
}