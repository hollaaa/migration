package com.hansum.migration.domain.db.repository;

import com.hansum.migration.domain.db.OrgCol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HsOrgColRepository extends JpaRepository<OrgCol, String> {

    public List<OrgCol> findByTabName(String tabName);

    public OrgCol findByTabNameAndColName(String tabName, String colName);


}
