package com.parasoft.demoapp.model.global;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.SpringSecurityCoreVersion;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="tbl_role")
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class RoleEntity implements Serializable {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

	@Setter
    @Column(nullable = false, unique = true)
    private String name;

    public RoleEntity(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
