package com.parasoft.demoapp.model.industry;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name="tbl_shipping")
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ShippingEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "shipping_type")
    private String shippingType;

    @Setter
    private String receiverId;
}
