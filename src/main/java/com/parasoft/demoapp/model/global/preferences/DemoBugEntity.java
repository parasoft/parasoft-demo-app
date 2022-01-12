package com.parasoft.demoapp.model.global.preferences;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString(exclude = "globalPreferences")
@Table(name="tbl_demo_bug")
@NoArgsConstructor
@EqualsAndHashCode(exclude = "globalPreferences")
public class DemoBugEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "demo_bugs_type")
    private DemoBugsType demoBugsType;

    @ManyToOne
    @JoinColumn(name = "global_preferences_id")
    @JsonIgnore
    private GlobalPreferencesEntity globalPreferences;

    public DemoBugEntity(DemoBugsType demoBugsType) {
        this.demoBugsType = demoBugsType;
    }

}
