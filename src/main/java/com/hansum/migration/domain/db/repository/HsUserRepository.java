package com.hansum.migration.domain.db.repository;

import com.hansum.migration.domain.db.HsUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HsUserRepository extends JpaRepository<HsUser, String> {
}
