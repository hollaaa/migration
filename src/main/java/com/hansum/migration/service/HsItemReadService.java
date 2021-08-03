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
    private HsMigrateDao hsMigrateDao;

    @Autowired
    private HsTypeRepository hsTypeRepository;

    @Autowired @Qualifier("sqlSessionMain")
    protected SqlSession sqlSessionH2;

//    private String shilla = "C:\\SDF-SDS\\4.Hybris\\hybris\\bin\\custom\\multisite\\shilladfscore\\resources\\shilladfscore-items.xml";
    private String hansome = "C:\\handsome\\workspace\\hybris\\bin\\custom\\handsome\\handsomecore\\resources\\handsomecore-items.xml";
    private String core = "C:\\SDF-SDS\\4.Hybris\\hybris\\bin\\custom\\multisite\\shilladfscore\\resources\\core-items.xml";

    public Map<String, Object> readItems()
    {

        List<String> fileList = new ArrayList<String>();

//        fileList.add(shilla);
        fileList.add(hansome);
        fileList.add(core);

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
        Map<String, Object> returnMap = null;

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

            if (strJson != null) {
                Map<String, Object> retMap = new HashMap<>();
                retMap.put("collectiontypes", collectionTypeList);
                retMap.put("enumtypes", enumtypeList);
                retMap.put("relations", relationList);
                retMap.put("itemtypes", itemtypeList);
                retMap.put("maptypes", maptypeList);

//                hsMigrateDao.testConnection();

                return retMap;
            }

            // 아래 코드는 의미없음

//			System.out.println(strJson);

            // collectiontypes 처리
//            JSONArray collectiontypeArray = jObject.getJSONObject("items").getJSONObject("collectiontypes").getJSONArray("collectiontype");
            Map<String, String> collectionTypeMap = new HashMap<String, String>();
//            List<Map<String, String>> collectionTypeList = new ArrayList<Map<String, String>>();

            for (int i = 0; i < collectiontypeArray.length(); i++) {
                JSONObject obj = collectiontypeArray.getJSONObject(i);
                collectionTypeMap = new HashMap<String, String>();
                collectionTypeMap.put("code", obj.getString("code"));
                collectionTypeMap.put("elementtype", obj.getString("elementtype"));
                collectionTypeMap.put("autocreate", String.valueOf(obj.get("autocreate")));
                collectionTypeMap.put("generate", String.valueOf(obj.get("generate")));
                collectionTypeMap.put("type", obj.getString("type"));

                collectionTypeList.add(collectionTypeMap);
            }
//			System.out.println(collectionTypeList);

            // enumtypes 처리
//            JSONArray enumtypeArray = jObject.getJSONObject("items").getJSONObject("enumtypes").getJSONArray("enumtype");
            Map<String, String> enumtypeMap;
//            List<Map<String, String>> enumtypeList = new ArrayList<Map<String, String>>();

            for (int i = 0; i < enumtypeArray.length(); i++) {

                JSONObject obj = enumtypeArray.getJSONObject(i);
                enumtypeMap = new HashMap<String, String>();
                enumtypeMap.put("code", obj.getString("code"));
                enumtypeMap.put("generate", String.valueOf(obj.get("generate")));
                enumtypeMap.put("autocreate", String.valueOf(obj.get("autocreate")));
                try {
                    enumtypeMap.put("dynamic", String.valueOf(obj.getString("dynamic")));
                } catch (Exception e) {
                    enumtypeMap.put("dynamic", "");
                }

                try {
                    enumtypeMap.put("description", obj.getString("description"));
                } catch (Exception e) {
                    enumtypeMap.put("description", "");
                }

                enumtypeList.add(enumtypeMap);
            }
//			System.out.println(enumtypeList);


            // relations 처리
//            JSONArray relationArray = jObject.getJSONObject("items").getJSONObject("relations").getJSONArray("relation");
            Map<String, String> relationMap;
//            List<Map<String, String>> relationList = new ArrayList<Map<String, String>>();

            for (int i = 0; i < relationArray.length(); i++) {

                JSONObject obj = relationArray.getJSONObject(i);
                relationMap = new HashMap<String, String>();
                relationMap.put("code", obj.getString("code"));
                relationMap.put("generate", String.valueOf(obj.get("generate")));
                relationMap.put("autocreate", String.valueOf(obj.get("autocreate")));
                relationMap.put("localized", String.valueOf(obj.get("localized")));
                try {
                    relationMap.put("description", obj.getString("description"));
                } catch (Exception e) {
                    relationMap.put("description", "");
//								System.out.println("description err:"+i);
                }

                JSONObject sourceObj = obj.getJSONObject("sourceElement");
                relationMap.put("sourcetype", sourceObj.getString("type"));
                relationMap.put("sourcequalifier", sourceObj.getString("qualifier"));
                relationMap.put("sourcecardinality", sourceObj.getString("cardinality"));
                JSONObject targetObj = obj.getJSONObject("targetElement");
                relationMap.put("targettype", targetObj.getString("type"));
                relationMap.put("targetqualifier", targetObj.getString("qualifier"));
                relationMap.put("targetcardinality", targetObj.getString("cardinality"));
                relationMap.put("targetcollectiontype", targetObj.getString("collectiontype"));

                relationList.add(relationMap);
            }
//            System.out.println(relationList);


            // itemtypes 처리
//            JSONArray itemtypeArray = jObject.getJSONObject("items").getJSONObject("itemtypes").getJSONObject("typegroup").getJSONArray("itemtype");
            Map<String, Object> itemtypeMap;
//            List<Map<String, Object>> itemtypeList = new ArrayList<Map<String, Object>>();

            for (int i = 0; i < itemtypeArray.length(); i++) {

                JSONObject obj = itemtypeArray.getJSONObject(i);
                itemtypeMap = new HashMap<String, Object>();
                itemtypeMap.put("code", obj.getString("code"));
                itemtypeMap.put("generate", String.valueOf(obj.get("generate")));
                itemtypeMap.put("autocreate", String.valueOf(obj.get("autocreate")));
                try {
                    itemtypeMap.put("extends", obj.getString("extends"));
                } catch (Exception e) {
                    itemtypeMap.put("extends", "");
                }

                try {
                    itemtypeMap.put("description", obj.getString("description"));
                } catch (Exception e) {
                    itemtypeMap.put("description", "");
                }

                try {
                    itemtypeMap.put("description", obj.getString("description"));
                } catch (Exception e) {
                    itemtypeMap.put("description", "");
                }

                // deployment
                try {
                    JSONObject deploymentObj = obj.getJSONObject("deployment");
                    itemtypeMap.put("table", deploymentObj.getString("table"));
                    itemtypeMap.put("typecode", deploymentObj.getString("typecode"));
                } catch (Exception e) {
                    e.printStackTrace();
                    if (ObjectUtils.equals(itemtypeMap.get("table"), null)) {
                        itemtypeMap.put("table", "");
                    }
                    if (ObjectUtils.equals(itemtypeMap.get("typecode"), null)) {
                        itemtypeMap.put("typecode", "");
                    }
//                    System.out.println("deployment err:" + i);
                }

                try {
                    JSONArray attributeArray = obj.getJSONObject("attributes").getJSONArray("attribute");


                    Map<String, String> attributeMap;
                    List<Map<String, String>> attributeList = new ArrayList<Map<String, String>>();

                    for (int _i = 0; _i < attributeArray.length(); _i++) {
                        JSONObject _obj = attributeArray.getJSONObject(_i);

                        attributeMap = new HashMap<String, String>();

                        attributeMap.put("qualifier", _obj.getString("qualifier"));
                        attributeMap.put("type", _obj.getString("type"));
                        attributeMap.put("generate", String.valueOf(_obj.get("generate")));

                        try {
                            attributeMap.put("description", _obj.getString("description"));
                        } catch (Exception e) {
                            attributeMap.put("description", "");
                        }
                        try {
                            attributeMap.put("defaultvalue", _obj.getString("defaultvalue"));
                        } catch (Exception e) {
                            attributeMap.put("defaultvalue", "");
                        }

                        attributeList.add(attributeMap);

                    }
                    itemtypeMap.put("attributes", attributeList);


                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("attributes err:" + i);
                }

                itemtypeList.add(itemtypeMap);
            }
//						System.out.println(enumtypeList);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return returnMap;
    }


    public String convertXmlToJsonString()
    {
        String strJson = "";

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

    public JSONObject convertXmlToJsonObject()
    {
        JSONObject jObject = null;

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
            hsType.setElementType(String.valueOf(map.get("dynamic")));
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
