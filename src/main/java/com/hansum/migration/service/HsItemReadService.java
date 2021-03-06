package com.hansum.migration.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hansum.migration.common.HsConstants;
import com.hansum.migration.common.HsUtils;
import com.hansum.migration.dao.HsMigrateDao;
import com.hansum.migration.domain.db.*;
import com.hansum.migration.domain.db.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class HsItemReadService {

    @Autowired
    private HsCommonService hsCommonService;

    @Autowired
    private HsMigrateDao hsMigrateDao;

    @Autowired
    private HsTypeRepository hsTypeRepository;

    @Autowired
    private HsItemAttrRepository hsItemAttrRepository;

    @Autowired
    private HsEnumValuesRepository hsEnumValuesRepository;

    @Autowired
    private HsOrgColRepository hsOrgColRepository;

    @Autowired
    private HsOrgTableRepository hsOrgTableRepository;

    @Autowired @Qualifier("sqlSessionMain")
    protected SqlSession sqlSessionH2;

//    private String shilla = "C:\\SDF-SDS\\4.Hybris\\hybris\\bin\\custom\\multisite\\shilladfscore\\resources\\shilladfscore-items.xml";
//    private String hansome = "C:\\handsome\\workspace\\hybris\\bin\\custom\\handsome\\handsomecore\\resources\\handsomecore-items.xml";
//    private String core = "C:\\handsome\\workspace\\hybris\\bin\\platform\\ext\\core\\resources\\core-items.xml";

    /**
     * 프로퍼티에 선언된 item.xml 파일들의 모든 정보를 읽어서 Map 으로 반환한다.
     * @return Map<String, Object>
     */
    public Map<String, Object> readItems()
    {

        if (!hsCommonService.isItemFilesExists())
        {
            log.error("ERROR:Item Files not Exists!!!");
            return null;
        }

        List<String> fileList = hsCommonService.getItemFileList();

        Map<String, Object> retMap = new HashMap<String, Object>();

        String fileName = "";

        for (String filePath : fileList)
        {

            File file = new File(filePath);
            fileName = file.getName();

            log.debug("fileName=" + fileName);

            retMap.put(fileName, readItem(filePath)) ;

        }
        return retMap;
    }

    /**
     * item.xml 경로를 받아와서 xml에서 컬렉션,Enum,relation,itemtype 정보를 Map으로 반환한다.
     * @param filePath
     * @return Map<String, Object>
     */
    public Map<String, Object> readItem(String filePath)
    {
        Map<String, Object> returnMap = new HashMap<String, Object>();

        try {


            InputStream inputStream = new FileInputStream(filePath);
            String xml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            int INDENT_FACTOR = 2;

            JSONObject jObject = XML.toJSONObject(xml);
            String strJson = jObject.toString(INDENT_FACTOR);

            Gson gson = new Gson();

            // 1.collectiontypes 처리
            JSONArray collectiontypeArray = jObject.getJSONObject("items").getJSONObject("collectiontypes").getJSONArray("collectiontype");
            List<Map<String, String>> collectionTypeList = null;
            collectionTypeList = gson.fromJson(collectiontypeArray.toString(), new TypeToken<List<Map<String, Object>>>() {
            }.getType());

//            log.debug(collectionTypeList);


            // 2.enumtypes 처리
            JSONArray enumtypeArray = jObject.getJSONObject("items").getJSONObject("enumtypes").getJSONArray(HsConstants.ENUM_TYPE_NAME);
            List<Map<String, String>> enumtypeList = null;
            enumtypeList = gson.fromJson(enumtypeArray.toString(), new TypeToken<List<Map<String, Object>>>() {
            }.getType());


//            log.debug(enumtypeList);
//            log.debug(enumtypeList.size());

            // 3.relations 처리
            JSONArray relationArray = jObject.getJSONObject("items").getJSONObject("relations").getJSONArray("relation");
            List<Map<String, String>> relationList = null;
            relationList = gson.fromJson(relationArray.toString(), new TypeToken<List<Map<String, Object>>>() {
            }.getType());


            // 4.itemtypes 처리
            JSONArray itemtypeArray = new JSONArray();
            List<Map<String, Object>> itemtypeList = new ArrayList<Map<String, Object>>();

            if (jObject.getJSONObject("items").getJSONObject("itemtypes").isNull("typegroup")) {
                itemtypeArray = jObject.getJSONObject("items").getJSONObject("itemtypes").getJSONArray("itemtype");
            } else {
                if ((jObject.getJSONObject("items").getJSONObject("itemtypes").get("typegroup")) instanceof  JSONArray) {
                    for( Object _obj: jObject.getJSONObject("items").getJSONObject("itemtypes").getJSONArray("typegroup")) {
                        JSONObject _jobj = (JSONObject)_obj;

                        if (_jobj.get("itemtype") instanceof JSONArray) {
                            for (Object _obj2 : _jobj.getJSONArray("itemtype")) {
                                itemtypeArray.put((JSONObject)_obj2);
                            }
                        } else {
                            itemtypeArray.put(((JSONObject)_jobj).getJSONObject("itemtype"));
                        }

                    }
                } else {
                    itemtypeArray = jObject.getJSONObject("items").getJSONObject("itemtypes").getJSONObject("typegroup").getJSONArray("itemtype");
                }

            }

            itemtypeList = gson.fromJson(itemtypeArray.toString(), new TypeToken<List<Map<String, Object>>>() {
            }.getType());


            // 5.maptypes 처리
            Object mapJObj=null;
            JSONArray maptypeArray = new JSONArray();
            if (!jObject.getJSONObject("items").isNull("maptypes")) {
                mapJObj = jObject.getJSONObject("items").getJSONObject("maptypes").get("maptype");
                if (mapJObj instanceof JSONArray) {
                    maptypeArray = jObject.getJSONObject("items").getJSONObject("maptypes").getJSONArray("maptype");
                } else {
                    maptypeArray.put(jObject.getJSONObject("items").getJSONObject("maptypes").getJSONObject("maptype"));
                }
            } else {
            }
            List<Map<String, Object>> maptypeList = null;
            maptypeList = gson.fromJson(maptypeArray.toString(), new TypeToken<List<Map<String, Object>>>() {
            }.getType());


//            log.debug(itemtypeList);
//            log.debug(itemtypeList.size());

            returnMap.put("collectiontypes", collectionTypeList);
            returnMap.put("enumtypes", enumtypeList);
            returnMap.put("relations", relationList);
            returnMap.put("itemtypes", itemtypeList);
            returnMap.put("maptypes", maptypeList);
//            return retMap;


        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return returnMap;
    }


    /**
     * item.xml 을 json으로 변환하여 JsonString 으로 return 한다.
     * item files 중에서 0번째만 반환함. (테스트 목적이므로)
     * @return String
     */
    public String convertXmlToJsonString()
    {
        String strJson = "";
        String hansome = hsCommonService.getItemFileList().get(0);

        try {
            InputStream inputStream = new FileInputStream(hansome);
            String xml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            int INDENT_FACTOR = 2;

            JSONObject jObject = XML.toJSONObject(xml);
            strJson = jObject.toString(INDENT_FACTOR);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return strJson;

    }

    /**
     * item.xml 을 json으로 변환하여 JSONObject 로 return 한다.
     * item files 중에서 0번째만 반환함. (테스트 목적이므로)
     * @return JSONObject
     */
    public JSONObject convertXmlToJsonObject()
    {
        JSONObject jObject = null;
        String strJson = "";
        String hansome = hsCommonService.getItemFileList().get(0);

        try {
            InputStream inputStream = new FileInputStream(hansome);
            String xml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            jObject = XML.toJSONObject(xml);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

//        log.debug(jObject.toString(2));

        return jObject;

    }

    /**
     * Collection type 을 DB에 입력한다.
     * @return
     */
    public int saveCollectionTypes()
    {
        Map<String, Object> returnMap = readItems();
        List<Map<String, Object>> list =  (List)returnMap.get("collectiontypes");

        HsType hsType = null;

        int i = getMaxHsTypeIdx();
        List<HsType> typeList = new ArrayList<>();
        for(Map<String, Object> map:list)
        {
            hsType = new HsType();
            hsType.setIdx(++i);
            hsType.setTypeName("CollectionType");
            hsType.setCode((String)map.get("code"));
            hsType.setElementType((String)map.get("elementtype"));
            hsType.setAutoCreate(HsUtils.getStringFromObject(map.get("autocreate")));
            hsType.setPType((String)map.get("type"));
            hsType.setGenerate(HsUtils.getStringFromObject(map.get("generate")));
            hsType.setRegDt(new Date());
            typeList.add(hsType);
        }
        hsTypeRepository.saveAll(typeList);
        return typeList.size();
    }

    /**
     * xml 파일을 전달받아 enumType을 DB에 저장한다.
     * @param fileName
     * @return  int
     */
    public int saveEnumTypes(String fileName)
    {
        log.info("saveEnumTypes START====================================");

        Map<String, Object> returnMap = readItems();

        Map<String, Object> sitemap = (Map<String, Object>)returnMap.get(fileName);

        List<Map<String, Object>> list =  (List)sitemap.get("enumtypes");

        HsType hsType = null;

        int i = getMaxHsTypeIdx();
        for(Map<String, Object> map:list)
        {
            log.info("enum:{}", map.get("code"));
            hsType = new HsType();
            hsType.setIdx(++i);
            hsType.setTypeName(HsConstants.ENUM_TYPE_NAME);
            hsType.setCode((String)map.get("code"));
            hsType.setDynamic(HsUtils.getStringFromObject(map.get("dynamic")));
            hsType.setAutoCreate(HsUtils.getStringFromObject(map.get("autocreate")));
            hsType.setGenerate(HsUtils.getStringFromObject(map.get("generate")));
            hsType.setDescription(getDescriptionTitle(map.get("description")));
            hsType.setDescriptionDetail((String)map.get("description"));
            hsType.setRegDt(new Date());

            hsTypeRepository.save(hsType);

            if (map.get("value") != null)
            {

                if (map.get("value") instanceof  List)
                {
                    List valList = (List)map.get("value");

                    List<HsEnumValue> enumValues = new ArrayList<HsEnumValue>();
                    for(Object obj:valList)
                    {
                        HsEnumValueId hsEnumValueId = new HsEnumValueId((String)map.get("code"), (String)((Map)obj).get("code"));

                        HsEnumValue hsEnumValue = new HsEnumValue(hsEnumValueId);
                        enumValues.add(hsEnumValue);
                    }
                    hsEnumValuesRepository.saveAll(enumValues);


                }
                else
                {
                    HsEnumValueId hsEnumValueId = new HsEnumValueId();
                    hsEnumValueId.setCode((String)map.get("code"));
                    hsEnumValueId.setEnumCode(HsUtils.getStringFromObject(((Map)map.get("value")).get("code")));

                    HsEnumValue hsEnumValue = new HsEnumValue(hsEnumValueId);

                    hsEnumValuesRepository.save(hsEnumValue);
                }

            }


        }

        log.info("saveEnumTypes END====================================:{}", list.size());

        return list.size();
    }




    /**
     * xml 파일을 전달받아 itemType을 DB에 저장한다.
     * @param fileName
     * @return  int
     */
    public int saveItemTypes(String fileName)
    {
        log.info("saveItemTypes START--------------------------");
        Map<String, Object> returnMap = readItems();

        Map<String, Object> sitemap = (Map<String, Object>)returnMap.get(fileName);

        List<Map<String, Object>> list =  (List)sitemap.get("itemtypes");

        log.debug(""+list);

        HsType hsType = null;

        List<HsType> typeList = new ArrayList<>();
        List<HsItemAttr> attrList = new ArrayList<>();
        int i = getMaxHsTypeIdx();
        for(Map<String, Object> map:list)
        {
            hsType = new HsType();
            hsType.setIdx(++i);
            hsType.setTypeName("itemtype");
            hsType.setCode((String)map.get("code"));
            hsType.setItemExtends(HsUtils.getStringFromObject(map.get("extends")));
            hsType.setAutoCreate(HsUtils.getStringFromObject(map.get("autocreate")));
            hsType.setDescription(HsUtils.getStringFromObject(map.get("description")));
            hsType.setRegDt(new Date());
            if (map.get("deployment") != null)
            {
                Map<String, String> deployMap = (Map<String, String>) map.get("deployment");

                hsType.setPTable(deployMap.get("table"));
                hsType.setTypeCode(HsUtils.getStringFromObject(deployMap.get("typecode")));
            }
            typeList.add(hsType);


            log.warn("=======================================================");

            // Attribute 추가
            Object attrObj = map.get("attributes");
            if (attrObj != null)
            {
                HsItemAttr attr;

                Map attrMap = (Map)attrObj;

                log.warn("code:{}", hsType.getCode());
                if (attrMap.get("attribute") instanceof  List)
                {

                    log.warn("att is LIST");
                    List valList = (List)attrMap.get("attribute");

//                    List<HsEnumValue> enumValues = new ArrayList<HsEnumValue>();
                    for(Object obj:valList)
                    {
                        attr = new HsItemAttr();
                        log.warn("qualifier:{}",(String)((Map)obj).get("qualifier"));

                        Map _map = (Map)obj;

                        attr.setCode(hsType.getCode());
                        attr.setQualifier((String)_map.get("qualifier"));
                        attr.setDescription(HsUtils.getStringFromObject(_map.get("description")));
                        attr.setPType((String)_map.get("type"));
                        attr.setPDefaultValue(HsUtils.getStringFromObject(_map.get("defaultvalue")));
                        attr.setOrgTableName(hsType.getPTable());
                        attr.setRegDt(new Date());

                        if (_map.get("modifiers") != null && _map.get("modifiers") instanceof Map)
                        {
                            Map modMap = (Map)_map.get("modifiers");
                            attr.setPUnique(HsUtils.getStringFromObject(modMap.get("unique")));
                            attr.setPOptional(HsUtils.getStringFromObject(modMap.get("optional")));
                        }
                        attrList.add(attr);
                    }
                }
                else
                {
                    attr = new HsItemAttr();
                    Map _map = (Map)attrMap.get("attribute");

                    log.warn("qualifier:{}", (String)_map.get("qualifier"));
                    attr.setCode(hsType.getCode());
                    attr.setQualifier((String)_map.get("qualifier"));
                    attr.setDescription((String)_map.get("description"));
                    attr.setPType((String)_map.get("type"));
                    attr.setPDefaultValue(HsUtils.getStringFromObject(_map.get("defaultvalue")));
                    attr.setRegDt(new Date());

                    if (_map.get("modifiers") != null && _map.get("modifiers") instanceof Map)
                    {
                        Map modMap = (Map)(_map.get("modifiers"));
                        attr.setPUnique(HsUtils.getStringFromObject(modMap.get("unique")));
                        attr.setPOptional(HsUtils.getStringFromObject(modMap.get("optional")));
                    }

                    attrList.add(attr);
                }
            }
        }
        hsTypeRepository.saveAll(typeList);
        hsItemAttrRepository.saveAll(attrList);
        log.info("saveItemTypes END--------------------------");

        return typeList.size();
    }

    /**
     * HsType 의 Max seq 값을 가져옴
     * @return int
     */
    public int getMaxHsTypeIdx()
    {
        return sqlSessionH2.selectOne("getHsTypeMaxIdx");
    }

    /**
     * Enum 에서 Description 의 첫번째 라인만 추출
     * @param description
     * @return String
     */
    private String getDescriptionTitle(Object description) {
        String str = (String)description;
//        log.debug("str:{}", str);
        if (StringUtils.isBlank(str))
        {
//            log.debug("blank");
            return "";
        }

        if (!str.contains(System.lineSeparator()))
        {
//            log.debug("no line");
            return str;
        }

        String[] result = str.split(System.lineSeparator());

        log.debug("result.length={}", result.length);
        for(String val : result)
        {
            if (!StringUtils.isBlank(val))
            {
                return StringUtils.trim(val);
            }
        }
        return "";

    }

    /**
     * item.xml 에서 code value 값을 읽어와서 enum 의 value 값 들을 세팅함
     * (한섬 item.xml 에 description 기입한 방법에만 적용 가능)
     */
    public String setHsEnumValuesAll()
    {
        List<HsType> enumList = hsTypeRepository.findByTypeName(HsConstants.ENUM_TYPE_NAME);

        log.info("enumList.size:{}", enumList.size());

        int cnt = 0;
        for (HsType hsType : enumList)
        {
            List<HsEnumValue> valueList = hsEnumValuesRepository.findByCode(hsType.getCode());
//            log.info("valueList.size:{}", valueList.size());
            for (HsEnumValue enumValue : valueList)
            {
                String _value = getValueFromFullDescription(hsType, enumValue);
                if (!StringUtils.isBlank(_value))
                {
                    log.info("_value:{}", _value);
                    enumValue.setEnumValueXml(_value);
                    hsEnumValuesRepository.save(enumValue);

                    cnt++;
                }
            }
        }
        return cnt + " 건 저장 완료";
    }

    /**
     * item.xml 의 enum 상세설명에서 enumCode 의 value 값을 추출한다.
     * @param hsType
     * @param enumValue
     * @return String
     */
    public String getValueFromFullDescription(HsType hsType, HsEnumValue enumValue)
    {
        String enumCode = enumValue.getEnumCode();
        String desc = (String)hsType.getDescriptionDetail();
        if (StringUtils.isBlank(desc))
        {
            return "";
        }

        if (!desc.contains(System.lineSeparator()))
        {
            return desc;
        }

        String[] result = desc.split(System.lineSeparator());

        log.debug("result.length={}", result.length);
        for(String lineStr : result)
        {
            if (StringUtils.contains(lineStr, enumCode))
            {
                String val = StringUtils.replace(lineStr, enumCode, "");
                return StringUtils.trim(val);
            }
        }
        return "";

    }

    /**
     * 이미 입력한 모델 정보에 typegroup 정보를 매핑한다.
     */
    public void mappingTypeGroup() {
        // 프로퍼티에 정의된 item.xml 파일 목록으로 작업
        List<String> fileList = hsCommonService.getItemFileList();
        for (String filePath:fileList)
        {
            mappingTypeGroup(filePath);
        }

    }


    /**
     * 이미 입력한 모델 정보에 typegroup 정보를 매핑한다.
     * @param filePath
     */
    public void mappingTypeGroup(String filePath) {

        try {
            InputStream inputStream = new FileInputStream(filePath);
            String xml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            int INDENT_FACTOR = 2;

            JSONObject jObject = XML.toJSONObject(xml);
            String strJson = jObject.toString(INDENT_FACTOR);

            Gson gson = new Gson();

            JSONArray itemtypeArray = new JSONArray();
            List<Map<String, Object>> itemtypeList = new ArrayList<Map<String, Object>>();

            if (!jObject.getJSONObject("items").getJSONObject("itemtypes").isNull("typegroup")) {
                if ((jObject.getJSONObject("items").getJSONObject("itemtypes").get("typegroup")) instanceof  JSONArray) {
                    for( Object _obj: jObject.getJSONObject("items").getJSONObject("itemtypes").getJSONArray("typegroup")) {
                        JSONObject _jobj = (JSONObject)_obj;

                        String typegroupName = ((JSONObject) _obj).getString("name");
                        log.warn("typegroup:{}", typegroupName);

                        if (_jobj.get("itemtype") instanceof JSONArray) {
                            for (Object _obj2 : _jobj.getJSONArray("itemtype")) {
                                String code = ((JSONObject) _obj2).getString("code");
                                log.warn("------{}", code);

                                // typegroup 을 itemtype 에 매핑하고 저장
                                HsType hsType = hsTypeRepository.findByCode(code);
                                hsType.setTypeGroup(typegroupName);
                                hsTypeRepository.save(hsType);

                            }
                        } else {

                            String code = ((JSONObject)_jobj).getJSONObject("itemtype").getString("code");
                            log.warn("------{}", code);

                            // typegroup 을 itemtype 에 매핑하고 저장
                            HsType hsType = hsTypeRepository.findByCode(code);
                            hsType.setTypeGroup(typegroupName);
                            hsTypeRepository.save(hsType);
                        }

                    }
                } else {
                    JSONObject _obj = jObject.getJSONObject("items").getJSONObject("itemtypes").getJSONObject("typegroup");
                    String typegroupName =  _obj.getString("name");

                    log.warn("+++++++++++++++++++++++typegroup:{}", typegroupName);
                    // TODO item.xml 에 단건짜리는 없어서 로직 비워놓음
                }

            }
        } catch (Exception e)
        {
            log.error("mappingTypeGroup ERR:", e);
        }

    }

    /**
     * org 테이블 정보에 모델 정보를 매핑한다.
     * @return String
     */
    public String mappingModelandTable() {

        // 전체 테이블 조회
        List<OrgTable> allTables = hsOrgTableRepository.findAll();
        // 전체 type 조회
        List<HsType> hsTypes = hsTypeRepository.findAll();
        Map<String, HsType> typesMap = new HashMap<>();
//        List<String> typeNames = new ArrayList<>();
        for (HsType hsType : hsTypes)
        {
            typesMap.put(hsType.getCode(), hsType);
//            typeNames.add(hsType.getCode());
        }


        // save 용 전체 테이블
//        List<OrgTableMaster> saveOrgTableMaster = new ArrayList<>();
        // save 용 Model
//        List<HsType> saveHsType = new ArrayList<>();
        // save 용 Attribute
        List<HsItemAttr> saveHsItemAttrs = new ArrayList<>();
        // save 용 orgCols
        List<OrgCol> saveOrgCols = new ArrayList<>();

        // 테이블명만 리스트로 보관 - lp테이블 판단하기 위함.
        List<String> tabNames = new ArrayList<>();
        for (OrgTable orgTab : allTables)
        {
            tabNames.add(orgTab.getTabName());
        }

        // 모든 테이블 looping
        int i_table = 0;
        int i_total = 0;
        for (OrgTable orgTab : allTables) {

            log.warn("{}.table name:{}", ++i_table, orgTab.getTabName());
            // lp 테이블여부를 판단한다.
            if (orgTab.getTabName().contains("lp"))
            {
                // 다른테이블명 + lp 이면 language 테이블
                if (tabNames.contains(StringUtils.replace(orgTab.getTabName(), "lp", "")))
                {
                    orgTab.setIsHybrisTable("Y");
                    orgTab.setHybrisTypeGubun("다국어 테이블");
                    orgTab.setTabComment(StringUtils.replace(orgTab.getTabName(), "lp", "") + " 다국어 테이블");
                    continue;
                }
            }

            // 해당 테이블의 모델명이 존재할 경우, looping
            if (StringUtils.isNotBlank(orgTab.getModelName()))
            {
                String[] modelNames = StringUtils.split(orgTab.getModelName(), ",");
                log.warn("modelName:{}", orgTab.getModelName());
                log.warn("modelNames.length:{}", modelNames.length);

                // 해당 테이블의 모든 col 목록 조회
                List<OrgCol> orgCols = null;


                // 테이블과 관련된 모든 모델 looping
                int i_model = 0;
                for (String code : modelNames)
                {

                    ++i_model;
                    HsType hsType = typesMap.get(code);
                    log.warn("({}.{}){}.model:{}", i_table, orgTab.getTabName(), i_model, code);

                    // item.xml에 모델이 존재하면 (매칭되는 모델이 있으면)
                    if(hsType != null){
                        if (orgCols == null)
                        {
                            log.warn("{}.findByTabName({})", i_model, orgTab.getTabName());
                            // 해당 테이블의 모든 col 목록 조회
                            orgCols = hsOrgColRepository.findByTabName(orgTab.getTabName());
                            // save 대상에 컬럼정보 포함
                            saveOrgCols.addAll(orgCols);
                        }

                        // 테이블 마스터 정보 세팅
                        orgTab.setTabComment(hsType.getDescription());
                        orgTab.setTypeGroup(hsType.getTypeGroup());
                        // Type 정보 세팅
                        hsType.setOrgMapped("Y");
                        hsType.setPTable(orgTab.getTabName());

                        // attr 목록 조회
                        List<HsItemAttr> itemAttrs = hsItemAttrRepository.findByCode(code);
                        // save 대상 attr 에 추가
                        saveHsItemAttrs.addAll(itemAttrs);

                        for (HsItemAttr attr : itemAttrs)
                        {
                            for (OrgCol orgCol : orgCols)
                            {
                                // 같은 컬럼일때 컬럼 정보를 update 함
                                if (HsUtils.isSameAttr(attr.getQualifier(), orgCol.getColName()))
                                {
                                    log.warn("{} update", attr.getQualifier());

                                    attr.setOrgMapped(true);
                                    attr.setOrgColName(orgCol.getColName());
//                                    hsItemAttrRepository.save(attr);
                                    orgCol.setColComment(attr.getDescription());
                                    orgCol.setDefaultValue(attr.getPDefaultValue());
                                    orgCol.setAttrName(attr.getQualifier());
                                    orgCol.setModelType(attr.getPType());
                                    orgCol.setModelName(code);
//                                    hsOrgTableRepository.save(orgCol);
                                }

                            }
                        }

                    }
                }

            }
        }

        // 모두 저장한다.
        log.warn("save HsType : {}", hsTypes.size());
        hsTypeRepository.saveAll(hsTypes);
        log.warn("save Attr : {}", saveHsItemAttrs.size());
        hsItemAttrRepository.saveAll(saveHsItemAttrs);
        log.warn("save table : {}", allTables.size());
        hsOrgTableRepository.saveAll(allTables);
        log.warn("save Cols : {}", saveOrgCols.size());
        hsOrgColRepository.saveAll(saveOrgCols);

        log.warn("col mapping finished!!");
        return allTables.size() + " 개 테이블 처리완료";
    }

    /**
     *
     * @param fileName
     * @return String
     */
    public String saveRelationTypes(String fileName)
    {
        log.info("saveItemTypes START--------------------------");
        Map<String, Object> returnMap = readItems();

        Map<String, Object> sitemap = (Map<String, Object>)returnMap.get(fileName);

        List<Map<String, Object>> list =  (List)sitemap.get("relations");

        log.debug(""+list);

        HsType hsType = null;

        List<HsType> typeList = new ArrayList<>();
        List<HsItemAttr> attrList = new ArrayList<>();
        int i = getMaxHsTypeIdx();
        for(Map<String, Object> map:list)
        {
            hsType = new HsType();
            hsType.setIdx(++i);
            hsType.setTypeName(HsConstants.RELATION_TYPE_NAME);
            hsType.setCode((String)map.get("code"));
            hsType.setLocalized(HsUtils.getStringFromObject(map.get("localized")));
            hsType.setAutoCreate(HsUtils.getStringFromObject(map.get("autocreate")));
            hsType.setGenerate(HsUtils.getStringFromObject(map.get("generate")));
            hsType.setDescription(HsUtils.getStringFromObject(map.get("description")));
            hsType.setRegDt(new Date());
            if (map.get("deployment") != null)
            {
                Map<String, String> deployMap = (Map<String, String>) map.get("deployment");

                hsType.setPTable(deployMap.get("table"));
                hsType.setTypeCode(HsUtils.getStringFromObject(deployMap.get("typecode")));
            }
            typeList.add(hsType);


            log.warn("=======================================================");

            // Attribute 추가
            Object attrObj = map.get("attributes");
            if (attrObj != null)
            {
                HsItemAttr attr;

                Map attrMap = (Map)attrObj;

                log.warn("code:{}", hsType.getCode());
                if (attrMap.get("attribute") instanceof  List)
                {

                    log.warn("att is LIST");
                    List valList = (List)attrMap.get("attribute");

//                    List<HsEnumValue> enumValues = new ArrayList<HsEnumValue>();
                    for(Object obj:valList)
                    {
                        attr = new HsItemAttr();
                        log.warn("qualifier:{}",(String)((Map)obj).get("qualifier"));

                        Map _map = (Map)obj;

                        attr.setCode(hsType.getCode());
                        attr.setQualifier((String)_map.get("qualifier"));
                        attr.setDescription(HsUtils.getStringFromObject(_map.get("description")));
                        attr.setPType((String)_map.get("type"));
                        attr.setPDefaultValue(HsUtils.getStringFromObject(_map.get("defaultvalue")));
                        attr.setOrgTableName(hsType.getPTable());
                        attr.setRegDt(new Date());

                        if (_map.get("modifiers") != null && _map.get("modifiers") instanceof Map)
                        {
                            Map modMap = (Map)_map.get("modifiers");
                            attr.setPUnique(HsUtils.getStringFromObject(modMap.get("unique")));
                            attr.setPOptional(HsUtils.getStringFromObject(modMap.get("optional")));
                        }
                        attrList.add(attr);
                    }
                }
                else
                {
                    attr = new HsItemAttr();
                    Map _map = (Map)attrMap.get("attribute");

                    log.warn("qualifier:{}", (String)_map.get("qualifier"));
                    attr.setCode(hsType.getCode());
                    attr.setQualifier((String)_map.get("qualifier"));
                    attr.setDescription((String)_map.get("description"));
                    attr.setPType((String)_map.get("type"));
                    attr.setPDefaultValue(HsUtils.getStringFromObject(_map.get("defaultvalue")));
                    attr.setRegDt(new Date());

                    if (_map.get("modifiers") != null && _map.get("modifiers") instanceof Map)
                    {
                        Map modMap = (Map)(_map.get("modifiers"));
                        attr.setPUnique(HsUtils.getStringFromObject(modMap.get("unique")));
                        attr.setPOptional(HsUtils.getStringFromObject(modMap.get("optional")));
                    }

                    attrList.add(attr);
                }
            }
        }
        hsTypeRepository.saveAll(typeList);
        hsItemAttrRepository.saveAll(attrList);
        log.info("saveItemTypes END--------------------------");

        return typeList.size() + " 건 저장 완료.(Relation Type)";
    }
}
