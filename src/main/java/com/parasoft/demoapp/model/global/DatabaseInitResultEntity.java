package com.parasoft.demoapp.model.global;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * This entity saves the result of all default creators initialization, the result status is a boolean type.
 * If all creators initialization are success, the result status is true, false if not.
 */
@Getter
@Entity
@Table(name="tbl_db_init_result")
@NoArgsConstructor
@EqualsAndHashCode
public class DatabaseInitResultEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Setter
    private boolean created;

    @Setter
    @Column(name = "created_time")
    private Date createdTime;

    @Setter
    @Column(name = "latest_recreated_time")
    private Date latestRecreatedTime;

    public DatabaseInitResultEntity(boolean created, Date time, Date latestRecreatedTime) {
        this.created = created;
        this.createdTime = time;
        this.latestRecreatedTime = latestRecreatedTime;
    }
}
