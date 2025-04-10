package com.parasoft.demoapp.model.industry;

import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Entity
@Table(name = "tbl_order")
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class OrderEntity {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The format is like #23-456-001, according to the value of id.
     * <pre>
     *     example:
     *     id       number
     *     1        #23-456-001
     *     2        #23-456-002
     *     1234567  #24-690-567
     * </pre>
     */
    @Setter
    @Column(name = "order_number")
    private String orderNumber;

    @Setter
    @Column(name = "requested_by")
    private String requestedBy;

    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Setter
    @Column(name = "reviewed_by_apv")
    private Boolean reviewedByAPV;

    @Setter
    @Column(name = "reviewed_by_prch")
    private Boolean reviewedByPRCH;

    @Setter
    @Column(name = "responded_by")
    private String respondedBy;  // approver's username

    @Setter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<OrderItemEntity> orderItems; // parasoft-suppress UC.AURCO "expected"

    @Setter
    @Enumerated(EnumType.STRING)
    private RegionType region;

    @Setter
    private String location;

    @Setter
    @Column(name = "shipping")
    private String shippingType;

    @Setter
    @Column(name = "order_image")
    private String orderImage;

    @Setter
    @Column(name = "receiver_id")
    private String receiverId;

    @Setter
    @Column(name = "event_id")
    private String eventId;

    @Setter
    @Column(name = "event_number")
    private String eventNumber;

    @Setter
    @Column(name = "submission_date")
    private Date submissionDate;

    @Setter
    @Column(name = "approver_reply_date")
    private Date approverReplyDate;

    @Setter
    @Column(name = "comments")
    private String comments;

    public OrderEntity(String requestedBy, RegionType region, String location, ShippingEntity shipping, String eventId,
                       String eventNumber) {
        this.requestedBy = requestedBy;
        this.region = region;
        this.location = location;
        this.shippingType = shipping.getShippingType();
        this.receiverId = shipping.getReceiverId();
        this.eventId = eventId;
        this.eventNumber = eventNumber;
    }

    public OrderEntity copy(){
        OrderEntity newOrder = new OrderEntity();

        newOrder.id = this.id;
        newOrder.orderNumber = this.orderNumber;
        newOrder.requestedBy = this.requestedBy;
        newOrder.status = this.status;
        newOrder.reviewedByAPV = this.reviewedByAPV;
        newOrder.reviewedByPRCH = this.reviewedByPRCH;
        newOrder.respondedBy = this.respondedBy;
        newOrder.orderItems = this.orderItems;
        newOrder.region = this.region;
        newOrder.location = this.location;
        newOrder.shippingType = this.shippingType;
        newOrder.orderImage = this.orderImage;
        newOrder.receiverId = this.receiverId;
        newOrder.eventId = this.eventId;
        newOrder.eventNumber = this.eventNumber;
        newOrder.submissionDate = this.submissionDate;
        newOrder.approverReplyDate = this.approverReplyDate;
        newOrder.comments = this.comments;

        return newOrder;
    }
}
