package com.hansum.migration.domain.db.repository;


import com.hansum.migration.domain.db.HsEnumValue;
import com.hansum.migration.domain.db.HsEnumValueId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HsEnumValuesRepository extends JpaRepository<HsEnumValue, String> {

//    List<HsEnumValue> findByHsEnumValueId(HsEnumValueId code);

//    @Query(
//            "SELECT * FROM hs_enum_value e " +
//                    " WHERE e.code = :code "
//    )
    public List<HsEnumValue> findByCode(String code);


}
