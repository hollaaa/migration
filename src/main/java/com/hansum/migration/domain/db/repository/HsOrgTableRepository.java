package com.hansum.migration.domain.db.repository;

import com.hansum.migration.domain.db.HsType;
import com.hansum.migration.domain.db.OrgTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HsOrgTableRepository extends JpaRepository<OrgTable, String> {

    public List<OrgTable> findByTabName(String tabName);

    public OrgTable findByTabNameAndColName(String tabName, String colName);


}
