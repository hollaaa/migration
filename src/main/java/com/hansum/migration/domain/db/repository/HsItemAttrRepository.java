package com.hansum.migration.domain.db.repository;


import com.hansum.migration.domain.db.HsItemAttr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HsItemAttrRepository extends JpaRepository<HsItemAttr, String> {

//    List<HsEnumValue> findByHsEnumValueId(HsEnumValueId code);

//    @Query(
//            "SELECT * FROM hs_enum_value e " +
//                    " WHERE e.code = :code "
//    )
    public List<HsItemAttr> findByCode(String code);


}
