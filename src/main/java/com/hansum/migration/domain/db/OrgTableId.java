package com.hansum.migration.domain.db;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class OrgTableId implements Serializable {

    public OrgTableId(){}

    public OrgTableId(String _tabName)
    {
        this.tabName = _tabName;
    }

    public OrgTableId(String _tabName, String _colName)
    {
        this.tabName = _tabName;
        this.colName = _colName;
    }
    @Column
    private String tabName;

    @Column
    private String colName;
}
