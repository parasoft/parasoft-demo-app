package com.parasoft.demoapp.model.industry;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ShippingEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String shippingType;

    @Setter
    private String receiverId;
}
