package com.hansum.migration.controller;

import com.hansum.migration.service.HsCommonService;
import com.hansum.migration.service.HsDbService;
import com.hansum.migration.service.HsItemReadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class HsRestController {

    @Resource
    private HsItemReadService hsItemReadService;

    @Resource
    private HsDbService hsDbService;

    @Resource
    private HsCommonService hsCommonService;

    @Autowired @Qualifier("sqlSessionMain")
    protected SqlSession sqlSessionH2;

    @Autowired @Qualifier("sqlSessionSub")
    protected SqlSession sqlSessionMysql;

    @GetMapping("/readItem")
    public Map<String, Object> readItem() {
        return hsItemReadService.readItems();
    }

    @RequestMapping("/getName")
    public String getName()
    {

        return sqlSessionH2.selectOne("h2.getValueFromDatabase");
    }

    @Deprecated
    @RequestMapping("/getTableName")
    public String getTableName()
    {

        return sqlSessionMysql.selectOne("mysql.getTableName");
    }

    /**
     * 원본 Mysql 데이터베이스의 모든 테이블 정보 조회
     * @return List<Map<String, Object>> map
     */
    @RequestMapping("/getTableInfos")
    public List<Map<String, Object>> getTableInfos()
    {
        List<Map<String, Object>> map = sqlSessionMysql.selectList("mysql.getTableInfos");

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

    /**
     * Collection Type 을 읽어 DB에 저장한다.
     * @return
     */
    @RequestMapping("/saveCollectionTypes")
    public String saveCollectionTypes()
    {
        return hsItemReadService.saveCollectionTypes() + " 건 저장 완료";
    }

    /**
     * xml로부터 enumType을 읽어 DB에 저장한다.
     * @return
     */
    @RequestMapping("/saveEnumTypes")
    public String saveEnumTypes()
    {
        return hsItemReadService.saveEnumTypes("handsomecore-items.xml") + " 건 저장 완료";
    }

    /**
     * xml로부터 ItemType을 읽어 DB에 저장한다.
     * @return String
     */
    @RequestMapping("/saveItemTypes")
    public String saveItemTypes()
    {
        return hsItemReadService.saveItemTypes("handsomecore-items.xml") + " 건 저장 완료";
    }

    /**
     * 프로퍼티에 정의된 모든 item.xml 파일을 읽어 Map 형태의 json 으로 반환한다.
     * @return map
     */
    @RequestMapping("/printJson")
    public Map printJson()
    {
//        Map map = new HashMap();
//        map.put("shilladfscore-items.xml", hsItemReadService.readItems());
        Map map = hsItemReadService.readItems();
        return map;
    }

    /**
     * 프로퍼티에 정의된 모든 item.xml 파일 List 를 반환한다.
     * @return List
     */
    @RequestMapping("/getItemFiles")
    public List HsCommonService()
    {
        return hsCommonService.getItemFileList();
    }

    /**
     * 프로퍼티에 정의된 모든 item.xml 파일이 존재하는지 확인한다.
     * @return boolean
     */
    @RequestMapping("/isItemFilesExists")
    public boolean isItemFilesExists()
    {
        return hsCommonService.isItemFilesExists();
    }

}
