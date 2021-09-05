package com.hansum.migration.domain.db;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class OrgColId implements Serializable {

    public OrgColId(){}

    public OrgColId(String _tabName)
    {
        this.tabName = _tabName;
    }

    public OrgColId(String _tabName, String _colName)
    {
        this.tabName = _tabName;
        this.colName = _colName;
    }
    @Column
    private String tabName;

    @Column
    private String colName;
}
