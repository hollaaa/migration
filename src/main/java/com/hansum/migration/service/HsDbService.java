package com.hansum.migration.service;

import com.hansum.migration.common.HsUtils;
import com.hansum.migration.domain.db.OrgTableMaster;
import com.hansum.migration.domain.db.repository.HsOrgTableMasterRepository;
import com.hansum.migration.domain.db.repository.HsOrgTableRepository;
import com.hansum.migration.domain.db.OrgTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;

/**
 * 오리지널 DB 관련 작업
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class HsDbService {

    @Resource
    private HsOrgTableRepository hsOrgTableRepository;

    @Resource
    private HsOrgTableMasterRepository hsOrgTableMasterRepository;

    @Autowired
    @Qualifier("sqlSessionMain")
    protected SqlSession sqlSessionH2;

    @Autowired @Qualifier("sqlSessionSub")
    protected SqlSession sqlSessionMysql;

    public int create()
    {
        List<Map<String, Object>> list = getTableInfos();
        OrgTable orgTable;
        log.warn("HsDbService.create START!!!");

        List<OrgTableMaster> masterList = new ArrayList<>();
        List<OrgTable> orgTableList = new ArrayList<>();

        String lastTabName = "";
        int i = 0;
        for (Map map : list)
        {
            // 마이그 관련 테이블일 경우 skip
            if (HsUtils.getStringFromObject(map.get("TAB_NAME")).contains("hs_") || HsUtils.getStringFromObject(map.get("TAB_NAME")).contains("org_"))
            {
                log.warn("skip:{}", HsUtils.getStringFromObject(map.get("TAB_NAME")));
                continue;
            }

            orgTable = new OrgTable();
            String tabName = HsUtils.getStringFromObject(map.get("TAB_NAME"));

            orgTable.setIdx(++i);
            orgTable.setTabName(tabName);
            orgTable.setColId(((BigInteger)map.get("COL_ID")).intValue());
            orgTable.setColName(HsUtils.getStringFromObject(map.get("COL_NAME")));
            orgTable.setColComment(HsUtils.getStringFromObject(map.get("COL_COMMENT")));
            orgTable.setDataType(HsUtils.getStringFromObject(map.get("DATA_TYPE")));
            orgTable.setColType(HsUtils.getStringFromObject(map.get("COL_TYPE")));
            orgTable.setColNullable(HsUtils.getStringFromObject(map.get("NULLABLE")));
            orgTable.setColKey(HsUtils.getStringFromObject(map.get("COL_KEY")));
            orgTable.setRegDt(new Date());

            // mater 정보 저장
            if (!StringUtils.equals(lastTabName, tabName))
            {
                int isDataExists = getDataExists(tabName);

                OrgTableMaster orgTableMaster = new OrgTableMaster();
                orgTableMaster.setTabName(tabName);
                orgTableMaster.setTabComment(HsUtils.getStringFromObject(map.get("TAB_COMMENT")));
                if (isDataExists == 1)
                {
                    orgTableMaster.setIsDataExists("Y");
                }
                else
                {
                    orgTableMaster.setIsDataExists("N");
                }
                orgTableMaster.setRegDt(new Date());

//                hsOrgTableMasterRepository.save(orgTableMaster);
                masterList.add(orgTableMaster);
            }

            // detail 정보 저장
//            hsOrgTableRepository.save(orgTable);
            orgTableList.add(orgTable);
        }

        hsOrgTableMasterRepository.saveAll(masterList);
        hsOrgTableRepository.saveAll(orgTableList);

        log.warn("HsDbService.create END:{}", i);

        return i;
    }

    /**
     * 원본 Mysql 데이터베이스의 모든 테이블 정보 조회
     * @return List<Map<String, Object>> map
     */
    public List<Map<String, Object>> getTableInfos()
    {
        List<Map<String, Object>> map = sqlSessionMysql.selectList("mysql.getTableInfos");

        return map;
    }

    /**
     * 특정 테이블에 데이터가 존재하는지 여부 조회
     * @param tabName
     * @return int
     */
    public int getDataExists(String tabName)
    {
        Map<String, String> param = new HashMap<>();
        param.put("tabName", tabName);

        log.warn("tabName={}", tabName);

        return sqlSessionMysql.selectOne("mysql.getDataExists", param);
    }

    public long getCount()
    {
        return hsOrgTableRepository.count();
    }

}
