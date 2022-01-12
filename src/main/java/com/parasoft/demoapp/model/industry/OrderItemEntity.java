package com.parasoft.demoapp.model.industry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name="tbl_order_item")
@NoArgsConstructor
@EqualsAndHashCode(exclude = "order")
@ToString(exclude = "order")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String image;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private OrderEntity order;

    @Column(name = "item_id")
    private Long itemId;

    private Integer quantity;

    public OrderItemEntity(String name, String description, String image, Integer quantity) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.quantity = quantity;
    }
}
