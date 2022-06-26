package com.hansum.migration.service;

import com.hansum.migration.common.HsUtils;
import com.hansum.migration.domain.db.OrgCol;
import com.hansum.migration.domain.db.OrgTable;
import com.hansum.migration.domain.db.repository.HsOrgColRepository;
import com.hansum.migration.domain.db.repository.HsOrgTableRepository;
import com.hansum.migration.vo.GridConfigVo;
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
 * Grid DB 관련 작업
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GridDbService {

    @Autowired
    @Qualifier("sqlSessionSub")
    protected SqlSession sqlSessionMysql;

    /**
     * 설정 정보 조회
     * @param saveName
     * @return List<GridConfigVo>
     */
    public List<Map> getGridConfigs(String saveName)
    {

        Map<String, String> param = new HashMap<>();
        if (StringUtils.isBlank(saveName))
        {
            param.put("saveName", "default");
        }
        else
        {
            param.put("saveName", saveName);
        }


        List<Map> result = sqlSessionMysql.selectList("mysql.getGridConfigs", param);

        return result;
    }

    public List<Map> getGridSort(String saveName)
    {

        List<Map> result = sqlSessionMysql.selectList("mysql.getGridSort", saveName);

        return result;
    }

    /**
     * 설정 이름 조회
     * @return
     */
    public List<String> getSaveNames()
    {
        return sqlSessionMysql.selectList("mysql.getSaveNames");
    }

    /**
     * 설정 저장
     * @param params
     */
    public void saveGridConfig(List<Map<String, Object>> params) {

        // 먼저 지움.
        String delName = (String)params.get(0).get("saveName");
        log.info("delName={}", delName);
        sqlSessionMysql.delete("mysql.deleteGridConfig", delName);



        for(Map<String, Object> map : params) {
            sqlSessionMysql.insert("mysql.saveGridConfig", map);
        }

    }

    public void deleteGridConfig(String delName) {
        sqlSessionMysql.delete("mysql.deleteGridConfig", delName);
    }

    public void saveGridSort(List<Map<String, Object>> params) {
        log.info("sort.size={}", params.size());

        String delName = (String)params.get(0).get("saveName");
        sqlSessionMysql.delete("mysql.deleteGridSort", delName);
        log.info("delete sort:{}", delName);
        for(Map<String, Object> map : params) {
            log.info("insert:{}", map);
            sqlSessionMysql.insert("mysql.saveGridSort", map);
        }
    }
}
