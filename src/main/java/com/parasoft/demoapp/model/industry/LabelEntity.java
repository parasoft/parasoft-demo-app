package com.parasoft.demoapp.model.industry;

import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name="tbl_label")
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class LabelEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @Setter
    private String value;

    @Setter
    @Column(name = "language_type")
    @Enumerated(EnumType.STRING)
    private LocalizationLanguageType languageType;

    public LabelEntity(String name, String value, LocalizationLanguageType languageType) {
        this.name = name;
        this.value = value;
        this.languageType = languageType;
    }
}
