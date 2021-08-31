package com.hansum.migration.service;

import com.hansum.migration.domain.db.repository.HsOrgTableRepository;
import com.hansum.migration.domain.db.OrgTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class HsDbService {

    @Resource
    private HsOrgTableRepository hsOrgTableRepository;

    public int create(List<Map<String, Object>> list)
    {
        OrgTable orgTable;

        log.warn("HsDbService.create START!!!");

        int i = 0;
        for (Map map : list)
        {
            orgTable = new OrgTable();

            orgTable.setIdx(++i);
            orgTable.setTabName(String.valueOf(map.get("TAB_NAME")));
            orgTable.setTabComment((String) map.get("TAB_COMMENT"));
            orgTable.setColId(((BigInteger)map.get("COL_ID")).intValue());
            orgTable.setColName(String.valueOf(map.get("COL_NAME")));
            orgTable.setColComment((String) map.get("COL_COMMENT"));
            orgTable.setDataType(String.valueOf(map.get("DATA_TYPE")));
            orgTable.setColType((String) map.get("COL_TYPE"));
            orgTable.setColNullable(String.valueOf(map.get("NULLABLE")));
            orgTable.setColKey(String.valueOf(map.get("COL_KEY")));
            orgTable.setRegDt(new Date());

            hsOrgTableRepository.save(orgTable);
        }

        log.warn("HsDbService.create END:{}", i);

        return i;
    }

    public long getCount()
    {
        return hsOrgTableRepository.count();
    }

}
