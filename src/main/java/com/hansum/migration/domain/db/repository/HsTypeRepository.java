package com.hansum.migration.domain.db.repository;


import com.hansum.migration.domain.db.HsType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HsTypeRepository extends JpaRepository<HsType, String> {

    List<HsType> findByTypeName(String typeName);
}
