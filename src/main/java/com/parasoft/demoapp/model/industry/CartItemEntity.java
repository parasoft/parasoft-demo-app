package com.parasoft.demoapp.model.industry;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name="tbl_cart")
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CartItemEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "user_id")
    private Long userId;

    @Setter
    @Column(name = "item_id")
    private Long itemId;

    @Setter
    private String name;

    @Setter
    private String description;

    @Setter
    private String image;

    @Setter
    @Transient
    private Integer realInStock;

    @Setter
    private Integer quantity;

    public CartItemEntity(Long userId, ItemEntity item, Integer quantity) {
        this.userId = userId;
        this.itemId = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.image = item.getImage();
        this.quantity = quantity;
    }
}
