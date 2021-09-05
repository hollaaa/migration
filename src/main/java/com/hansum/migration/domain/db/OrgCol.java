package com.hansum.migration.domain.db;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity(name = "orgCols")
@IdClass(OrgColId.class)
public class OrgCol {

    @Id
    private String tabName;

    @Id
    private String colName;

//    @Column
//    private String tabComment;

    @Column
    private Integer idx;

    @Column
    private Integer colId;

    @Column
    private String colComment;

    @Column
    private String dataType;

    @Column
    private String dataLen;

    @Column
    private String colNullable;

    @Column
    private String colType;

    @Column
    private String colKey;

//    @Column
//    private String modelName;

    @Column
    private String attrName;

    // 하이브리스 모델의 (컬럼)타입
    @Column
    private String modelType;

    @Column
    private String defaultValue;

    @Column
    private String modelName;

//    @Column
//    private String isHybrisTable;
//
//    @Column
//    private String typeGroup;

//    @ManyToOne
//    @JoinColumn(name="orgTableMaster")
//    private OrgTableMaster orgTableMaster;

    @Column //(name = "regDt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP )
    private Date regDt;

}
