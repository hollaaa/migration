package com.hansum.migration.domain.db;

import javax.persistence.Column;
import java.io.Serializable;

public class HsItemAttrId implements Serializable {

    public HsItemAttrId(){}

    public HsItemAttrId(String _code)
    {
        this.code = _code;
    }

    public HsItemAttrId(String _code, String _qualifier)
    {
        this.code = _code;
        this.qualifier = _qualifier;
    }

    @Column
    String code;

    @Column
    String qualifier;
}
