package com.hansum.migration.service;

import com.hansum.migration.common.HsUtils;
import com.hansum.migration.domain.db.OrgTable;
import com.hansum.migration.domain.db.repository.HsOrgTableRepository;
import com.hansum.migration.domain.db.repository.HsOrgColRepository;
import com.hansum.migration.domain.db.OrgCol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
    private HsOrgColRepository hsOrgColRepository;

    @Resource
    private HsOrgTableRepository hsOrgTableRepository;

    @Autowired
    @Qualifier("sqlSessionMain")
    protected SqlSession sqlSessionH2;

    @Autowired @Qualifier("sqlSessionSub")
    protected SqlSession sqlSessionMysql;

    public int create()
    {
        List<Map<String, Object>> list = getTableInfos();
        OrgCol orgCol;
        log.warn("HsDbService.create START!!!");

        // Table 의 deploy 정보 조회
        List<Map<String, String>> deployList = sqlSessionMysql.selectList("mysql.getTableDeployInfos");
        Map<String, String> modelMap = new HashMap<>();
        Map<String, String> jndiMap = new HashMap<>();
        for(Map<String, String> depMap : deployList)
        {
            String _tabName = depMap.get("TAB_NAME");
            String _jndiName = depMap.get("JNDI_NAME");
            String _modelNames = depMap.get("MODEL_NAMES");

            modelMap.put(_tabName, _modelNames);
            jndiMap.put(_tabName, _jndiName);
        }


        List<OrgTable> masterList = new ArrayList<>();
        List<OrgCol> orgColList = new ArrayList<>();

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

            orgCol = new OrgCol();
            String tabName = HsUtils.getStringFromObject(map.get("TAB_NAME"));

            orgCol.setIdx(++i);
            orgCol.setTabName(tabName);
            orgCol.setColId(((BigInteger)map.get("COL_ID")).intValue());
            orgCol.setColName(HsUtils.getStringFromObject(map.get("COL_NAME")));
            orgCol.setColComment(HsUtils.getStringFromObject(map.get("COL_COMMENT")));
            orgCol.setDataType(HsUtils.getStringFromObject(map.get("DATA_TYPE")));
            orgCol.setColType(HsUtils.getStringFromObject(map.get("COL_TYPE")));
            orgCol.setColNullable(HsUtils.getStringFromObject(map.get("NULLABLE")));
            orgCol.setColKey(HsUtils.getStringFromObject(map.get("COL_KEY")));
            orgCol.setRegDt(new Date());

            // 하이브리스 기초 정보 저장
            if ("PK".equals(orgCol.getColName()))
            {
                orgCol.setColComment("Hybris PK");
                orgCol.setAttrName("pk");
            }

            if ("aCLTS".equals(orgCol.getColName()))
            {
                orgCol.setColComment("Hybris 지정컬럼");
            }

            if ("createdTS".equals(orgCol.getColName()))
            {
                orgCol.setColComment("생성일시");
                orgCol.setAttrName("creationtime");
            }

            if ("hjmpTS".equals(orgCol.getColName()))
            {
                orgCol.setColComment("Hybris 지정컬럼");
            }

            if ("modifiedTS".equals(orgCol.getColName()))
            {
                orgCol.setColComment("최종수정일시");
                orgCol.setAttrName("modifiedtime");

            }

            if ("OwnerPkString".equals(orgCol.getColName()))
            {
                orgCol.setColComment("Hybris 지정컬럼");
            }

            if ("propTS".equals(orgCol.getColName()))
            {
                orgCol.setColComment("Hybris 지정컬럼");
            }

            if ("TypePkString".equals(orgCol.getColName()))
            {
                orgCol.setColComment("Hybris 지정컬럼");
            }

            // master 정보 저장
            if (!StringUtils.equals(lastTabName, tabName))
            {
                int isDataExists = getDataExists(tabName);

                OrgTable orgTable = new OrgTable();
                orgTable.setTabName(tabName);
                orgTable.setTabComment(HsUtils.getStringFromObject(map.get("TAB_COMMENT")));
                if (isDataExists == 1)
                {
                    orgTable.setIsDataExists("Y");
                }
                else
                {
                    orgTable.setIsDataExists("N");
                }
                orgTable.setRegDt(new Date());

                // deploy 정보 저장
                if (modelMap.get(map.get("TAB_NAME")) != null)
                {
                    orgTable.setModelName(modelMap.get(map.get("TAB_NAME")));
                    orgTable.setJndiName(jndiMap.get(map.get("TAB_NAME")));
                    orgTable.setIsHybrisTable("Y");
                    if (jndiMap.get(map.get("TAB_NAME")).contains(".link."))
                    {
                        orgTable.setHybrisTypeGubun("Relation");
                    }
                    else if (jndiMap.get(map.get("TAB_NAME")).contains(".type."))
                    {
                        orgTable.setHybrisTypeGubun("Hybris primitive type");
                    }
                    else
                    {
                        orgTable.setHybrisTypeGubun("Item Type");
                    }

                    if (StringUtils.isNotBlank(orgTable.getJndiName()))
                    {
                        orgTable.setSystemName(getSystemName(orgTable.getJndiName()));
                    }

                }
                else
                {
                    orgTable.setIsHybrisTable("N");
                }


//                hsOrgTableMasterRepository.save(orgTableMaster);
                masterList.add(orgTable);
            }

            // detail 정보 저장
//            hsOrgTableRepository.save(orgTable);
            orgColList.add(orgCol);
        }

        hsOrgTableRepository.saveAll(masterList);
        hsOrgColRepository.saveAll(orgColList);

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
        return hsOrgColRepository.count();
    }

    /**
     * JNDI 명에서 system 명 추출
     * @param jndiName
     * @return String
     */
    private String getSystemName(String jndiName) {
        String systemName = "";

        systemName = StringUtils.replace(jndiName, "de.hybris.platform.persistence.", "");
        systemName = StringUtils.replace(systemName, "link.", "");
        systemName = StringUtils.replace(systemName, "type.", "");

        if (systemName.contains("_"))
        {
            return systemName.split("_")[0];
        }
        else {
            return "core";
        }
    }

}
