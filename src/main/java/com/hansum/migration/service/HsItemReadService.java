package com.hansum.migration.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hansum.migration.dao.HsMigrateDao;
import com.hansum.migration.domain.db.HsType;
import com.hansum.migration.domain.db.repository.HsTypeRepository;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
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

@Service
public class HsItemReadService {

    @Autowired
    private HsCommonService hsCommonService;

    @Autowired
    private HsMigrateDao hsMigrateDao;

    @Autowired
    private HsTypeRepository hsTypeRepository;

    @Autowired @Qualifier("sqlSessionMain")
    protected SqlSession sqlSessionH2;

//    private String shilla = "C:\\SDF-SDS\\4.Hybris\\hybris\\bin\\custom\\multisite\\shilladfscore\\resources\\shilladfscore-items.xml";
//    private String hansome = "C:\\handsome\\workspace\\hybris\\bin\\custom\\handsome\\handsomecore\\resources\\handsomecore-items.xml";
//    private String core = "C:\\handsome\\workspace\\hybris\\bin\\platform\\ext\\core\\resources\\core-items.xml";

    public Map<String, Object> readItems()
    {

        if (!hsCommonService.isItemFilesExists())
        {
            System.out.println("ERROR:Item Files not Exists!!!");
            return null;
        }

        List<String> fileList = hsCommonService.getItemFileList();

        Map<String, Object> retMap = new HashMap<String, Object>();

        String fileName = "";

        for (String filePath : fileList)
        {

            File file = new File(filePath);
            fileName = file.getName();

            System.out.println("fileName=" + fileName);

            retMap.put(fileName, readItem(filePath)) ;

        }
        return retMap;
    }


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

//            System.out.println(collectionTypeList);


            // 2.enumtypes 처리
            JSONArray enumtypeArray = jObject.getJSONObject("items").getJSONObject("enumtypes").getJSONArray("enumtype");
            List<Map<String, String>> enumtypeList = null;
            enumtypeList = gson.fromJson(enumtypeArray.toString(), new TypeToken<List<Map<String, Object>>>() {
            }.getType());


//            System.out.println(enumtypeList);
//            System.out.println(enumtypeList.size());

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


//            System.out.println(itemtypeList);
//            System.out.println(itemtypeList.size());

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

//        System.out.println(jObject.toString(2));

        return jObject;

    }


    public Object getCollectionTypesMap()
    {
        Map<String, Object> returnMap = readItems();
        return returnMap.get("collectionTypeList");
    }


    public Object getEnumTypesMap()
    {
        Map<String, Object> returnMap = readItems();

        return returnMap.get("enumtypeList");

    }

    public Object getRelationTypesMap()
    {

        Map<String, Object> returnMap = readItems();
        return returnMap.get("relationList");
    }


    public Object getItemTypesMap()
    {
        Map<String, Object> returnMap = readItems();
        return returnMap.get("itemtypeList");

    }

    public int saveCollectionTypes()
    {
        Map<String, Object> returnMap = readItems();
        List<Map<String, Object>> list =  (List)returnMap.get("collectiontypes");

        HsType hsType = null;

        int i = getMaxHsTypeIdx();
        for(Map<String, Object> map:list)
        {
            hsType = new HsType();
            hsType.setIdx(++i);
            hsType.setTypeName("CollectionType");
            hsType.setCode((String)map.get("code"));
            hsType.setElementType((String)map.get("elementtype"));
            hsType.setAutoCreate(String.valueOf(map.get("autocreate")));
            hsType.setPType((String)map.get("type"));
            hsType.setGenerate(String.valueOf(map.get("generate")));
            hsType.setRegDt(new Date());
            hsTypeRepository.save(hsType);
        }
        return list.size();
    }

    public int saveEnumTypes()
    {
        Map<String, Object> returnMap = readItems();
        List<Map<String, Object>> list =  (List)returnMap.get("enumtypes");

        HsType hsType = null;

        int i = getMaxHsTypeIdx();
        for(Map<String, Object> map:list)
        {
            hsType = new HsType();
            hsType.setIdx(++i);
            hsType.setTypeName("enumtype");
            hsType.setCode((String)map.get("code"));
            hsType.setDynamic(String.valueOf(map.get("dynamic")));
            hsType.setAutoCreate(String.valueOf(map.get("autocreate")));
            hsType.setGenerate(String.valueOf(map.get("generate")));
            hsType.setRegDt(new Date());

            if (map.get("value") != null)
            {
                if (map.get("value") instanceof  List)
                {
                    List valList = (List)map.get("value");
                    for(Object obj:valList)
                    {
                        System.out.println(((Map)obj).get("code"));
                    }
                }
                else
                {
                    System.out.println(map.get("value"));
                }

            }

            hsTypeRepository.save(hsType);
        }
        return list.size();
    }


    public int saveItemTypes(String fileName)
    {
        Map<String, Object> returnMap = readItems();

        Map<String, Object> sitemap = (Map<String, Object>)returnMap.get(fileName);

        List<Map<String, Object>> list =  (List)sitemap.get("itemtypes");

        System.out.println(list);

        HsType hsType = null;

        int i = getMaxHsTypeIdx();
        for(Map<String, Object> map:list)
        {
            hsType = new HsType();
            hsType.setIdx(++i);
            hsType.setTypeName("itemtype");
            hsType.setCode((String)map.get("code"));
            hsType.setItemExtends(String.valueOf(map.get("extends")));
            hsType.setAutoCreate(String.valueOf(map.get("autocreate")));
            hsType.setDescription(String.valueOf(map.get("description")));
            hsType.setRegDt(new Date());
            if (map.get("deployment") != null)
            {
                Map<String, String> deployMap = (Map<String, String>) map.get("deployment");

                hsType.setPTable(deployMap.get("table"));
                hsType.setTypeCode(deployMap.get("typcode"));
            }
            hsTypeRepository.save(hsType);

        }
        return list.size();
    }


    public int getMaxHsTypeIdx()
    {
        return sqlSessionH2.selectOne("getHsTypeMaxIdx");
    }

    public void printJson()
    {
        Map map = readItems();

        System.out.println(map);


    }
}
