package com.hansum.migration.controller;

import com.hansum.migration.service.HsDbService;
import com.hansum.migration.service.HsItemReadService;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HsRestController {

    @Resource
    private HsItemReadService hsItemReadService;

    @Resource
    private HsDbService hsDbService;

    @Autowired @Qualifier("sqlSessionMain")
    protected SqlSession sqlSessionH2;

    @Autowired @Qualifier("sqlSessionSub")
    protected SqlSession sqlSessionOrcl;

    @GetMapping("/readItem")
    public Map<String, Object> readItem() {
        return hsItemReadService.readItems();
    }

    @RequestMapping("/getName")
    public String getName()
    {

        return sqlSessionH2.selectOne("h2.getValueFromDatabase");
    }

    @RequestMapping("/getTableName")
    public String getTableName()
    {

        return sqlSessionOrcl.selectOne("oracle.getTableName");
    }

    @RequestMapping("/getTableInfos")
    public List<Map<String, Object>> getTableInfos()
    {
        List<Map<String, Object>> map = sqlSessionOrcl.selectList("oracle.getTableInfos");

        return map;
    }

    /**
     * 원본 테이블 스키마 입력
     * @return String
     */
    @RequestMapping("/createOrgTables")
    public String createOrgTables()
    {
        List<Map<String, Object>> map = getTableInfos();
        int cnt = hsDbService.create(map);
        return cnt + " 건 입력완료.";
    }


    @RequestMapping("/getItemJson")
    public String getItemJson()
    {
        return hsItemReadService.convertXmlToJsonString();
    }

    @RequestMapping("/getItemJsonObject")
    public JSONObject getItemJsonObject()
    {
        return hsItemReadService.convertXmlToJsonObject();
    }

    @RequestMapping("/getCollectionTypesMap")
    public Object getCollectionTypesMap()
    {
        return hsItemReadService.getCollectionTypesMap();
    }


    @RequestMapping("/getEnumTypesMap")
    public Object getEnumTypesMap()
    {
        return hsItemReadService.getEnumTypesMap();
    }


    @RequestMapping("/getRelationTypesMap")
    public Object getRelationTypesMap()
    {
        return hsItemReadService.getRelationTypesMap();
    }


    @RequestMapping("/saveCollectionTypes")
    public String saveCollectionTypes()
    {
        return hsItemReadService.saveCollectionTypes() + " 건 저장 완료";
    }


    @RequestMapping("/saveEnumTypes")
    public String saveEnumTypes()
    {
        return hsItemReadService.saveEnumTypes() + " 건 저장 완료";
    }

    @RequestMapping("/printJson")
    public Map printJson()
    {
//        Map map = new HashMap();
//        map.put("shilladfscore-items.xml", hsItemReadService.readItems());
        Map map = hsItemReadService.readItems();
        return map;
    }



}
