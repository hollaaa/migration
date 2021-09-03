package com.hansum.migration.domain.db;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity(name = "hsType")
public class HsType implements Serializable {
    @Id
    private String code;

    @Column
    private Integer idx;

    @Column
    private String typeName;

    @Column
    private String elementType;

    @Column
    private String autoCreate;

    @Column
    private String generate;

    @Column
    private String pType;

    @Column
    private String dynamic;

    @Column(length = 1000)
    private String description;

    @Column
    private String localized;

    // 실제 테이블명
    @Column
    private String pTable;

    @Column
    private String typeCode;

    @Column
    private String itemExtends;

    @Column(length = 10000)
    private String descriptionDetail;

    @Column
    private String typeGroup;

    @Column(columnDefinition = "boolean default false")
    private Boolean orgMapped;

    @Column //(name = "insTm", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP )
    private Date regDt;




}
