package com.hansum.migration.domain.db;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity(name = "user")
public class HsUser {

    @Id
    private String id;

    @Column
    private String name;

    @Column
    private Integer age;

    @Column //(name = "ins_tm", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP )
    private Date insTm;

}
