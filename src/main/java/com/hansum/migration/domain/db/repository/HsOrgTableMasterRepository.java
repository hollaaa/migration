package com.hansum.migration.domain.db.repository;

import com.hansum.migration.domain.db.OrgTableMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HsOrgTableMasterRepository extends JpaRepository<OrgTableMaster, String> {

    public OrgTableMaster findByTabName(String tabName);

}
