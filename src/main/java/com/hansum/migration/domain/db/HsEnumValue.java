package com.hansum.migration.domain.db;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity(name = "hsEnumValue")
@IdClass(HsEnumValueId.class)
public class HsEnumValue {

    public HsEnumValue(){};

    public HsEnumValue(HsEnumValueId _hsEnumValueId)
    {
        this.code = _hsEnumValueId.getCode();
        this.enumCode = _hsEnumValueId.getEnumCode();
        this.regDt = new Date();
    }

    @Id
    String code;

    @Id
    String enumCode;

//    @EmbeddedId
//    HsEnumValueId hsEnumValueId;

    @Column
    String enumValueXml;

    @Column
    String enumValueKr;

    @Column
    String enumValueCn;

    @Column
    String enumValueEn;

    @Column //(name = "insTm", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP )
    private Date regDt;

}
