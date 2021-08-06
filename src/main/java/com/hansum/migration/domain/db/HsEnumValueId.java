package com.hansum.migration.domain.db;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
//@Embeddable
public class HsEnumValueId implements Serializable {

    public HsEnumValueId(){}

    public HsEnumValueId(String _code)
    {
        this.code = _code;
    }

    public HsEnumValueId(String _code, String _enumCode)
    {
        this.code = _code;
        this.enumCode = _enumCode;
    }

    @Column
    String code;

    @Column
    String enumCode;
}
