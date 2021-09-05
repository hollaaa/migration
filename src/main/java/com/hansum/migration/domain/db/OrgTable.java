package com.hansum.migration.domain.db;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity(name = "orgTables")
public class OrgTable {

    @Id
    private String tabName;

    @Column
    private String tabComment;

    @Column (length = 3000)
    private String modelName;

    @Column
    private String isHybrisTable;

    @Column
    private String typeGroup;

    // 데이터 존재 유무
    @Column
    private String isDataExists;

    @Column
    private String hybrisTypeGubun;

    @Column
    private String jndiName;

    @Column
    private String systemName;


//    @OneToMany(mappedBy="orgTableMaster", targetEntity=OrgTable.class)
//    private List<OrgTable> orgTables = new ArrayList<OrgTable>();

    @Column //(name = "regDt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP )
    private Date regDt;

}
