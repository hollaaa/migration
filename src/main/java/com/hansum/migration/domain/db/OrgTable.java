package com.hansum.migration.domain.db;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity(name = "orgTable")
public class OrgTable {

    @Id
    private Integer idx;

    @Column
    private String tabName;

    @Column
    private String tabComment;

    @Column
    private Integer colId;

    @Column
    private String colName;

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

    @Column //(name = "regDt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP )
    private Date regDt;

}
