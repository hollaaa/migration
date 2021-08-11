package com.hansum.migration.domain.db;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity(name = "hsItemAttr")
@IdClass(HsItemAttrId.class)
public class HsItemAttr {

    @Id
    String code;

    @Id
    String qualifier;

    @Column
    String pType;

    @Column
    String description;

    @Column
    String pDefaultValue;

    @Column
    String pOptional;

    @Column
    String pUnique;


    @Column
    String pInitial;


    @Column //(name = "insTm", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP )
    private Date regDt;

}
