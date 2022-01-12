package com.parasoft.demoapp.model.industry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name="tbl_location")
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class LocationEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private RegionType region;

    @Setter
    @Column(name = "location_info")
    private String locationInfo;

    @Setter
    @Column(name = "location_image")
    private String locationImage;

}
