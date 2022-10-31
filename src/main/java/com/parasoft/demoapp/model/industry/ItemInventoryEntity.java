package com.parasoft.demoapp.model.industry;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name="tbl_item_inventory")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ItemInventoryEntity {
    @Id
    @Column(name = "item_id")
    private Long itemId;

    @Setter
    @Column(name = "in_stock")
    private Integer inStock;
}
