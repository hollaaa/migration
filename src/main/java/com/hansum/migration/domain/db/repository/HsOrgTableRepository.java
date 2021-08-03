package com.hansum.migration.domain.db.repository;

import com.hansum.migration.domain.db.OrgTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HsOrgTableRepository extends JpaRepository<OrgTable, String> {
}
