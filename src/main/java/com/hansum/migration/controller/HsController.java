package com.hansum.migration.controller;

import com.google.gson.JsonArray;
import com.hansum.migration.domain.db.HsUser;
import com.hansum.migration.service.GridDbService;
import com.hansum.migration.service.HsUserService;
import com.hansum.migration.vo.GridConfigVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HsController {

    @Resource
    private HsUserService hsUserService;

    @Resource
    private GridDbService gridDbService;

    /**
     * 데이터추출 페이지
     * @return
     */
    @GetMapping("/migration")
    public String hello(){
        return "migration.html";
    }

    @ResponseBody
    @GetMapping("/valueTest")
    public String valueTest() {
        String value = "테스트 String";

        return value;
    }

    @RequestMapping("/user/create")
    @ResponseBody
    public HsUser create(@RequestBody HsUser param)
    {
        HsUser _param = new HsUser();
        _param.setId("test_id");
        _param.setName("test_name");
        _param.setAge(22);

        HsUser hsUser = hsUserService.create(_param);
        return hsUser;
    }

    @RequestMapping("/user/read")
    @ResponseBody
    public Optional<HsUser> read()//@RequestBody HsUser param)
    {
        HsUser param = new HsUser();
        param.setId("test_id");

        Optional<HsUser> hsUser = hsUserService.read(param.getId());
        return hsUser;
    }

    @RequestMapping("/user/update")
    @ResponseBody
    public HsUser update(@RequestBody HsUser param)
    {
        return hsUserService.update(param);
    }

    @RequestMapping("/user/delete")
    @ResponseBody
    public void delete (@RequestBody HsUser param)
    {
        hsUserService.delete(param);
    }

    @RequestMapping(value = "/test", method = {RequestMethod.GET, RequestMethod.POST})
    public String kundoTest(HttpServletRequest request, Model model) {

        String configName = request.getParameter("configName");

        if (StringUtils.isBlank(configName)) {
            configName = "0.default";
        }

        log.info("configName={}", configName);

        List<String> nameList = gridDbService.getSaveNames();

        if (nameList == null || nameList.isEmpty())
        {
            nameList = new ArrayList<String>();
            nameList.add("default");
        }

        List<Map> cols = gridDbService.getGridConfigs(configName);

        List<Map> sorts = gridDbService.getGridSort(configName);

        model.addAttribute("names", nameList);
        model.addAttribute("cols", cols);
        model.addAttribute("configName", configName);
        model.addAttribute("sorts", sorts);
        return "test";
    }


    @RequestMapping(value = "/test/saveGridConfig",  produces="application/json; charset=UTF-8")
    @ResponseBody
    public Map saveGridConfig(@RequestBody String data) {

        log.info(data);

        List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
        params = JSONArray.fromObject(data);

        gridDbService.saveGridConfig(params);

        Map<String, String> result = new HashMap<String, String>();
        result.put("resultCode", "success");
        return result;
    }

    @RequestMapping(value = "/test/deleteGridConfig")
    @ResponseBody
    public Map deleteGridConfig(@RequestParam Map<String, String> param) {

        String saveName = param.get("saveName");
        log.info("delete----------------------------------");
        log.info(param.get("saveName"));
        log.info("delete----------------------------------");
        gridDbService.deleteGridConfig(saveName);

        Map<String, String> result = new HashMap<String, String>();
        result.put("resultCode", "success");
        return result;
    }


    @RequestMapping(value = "/test/saveGridSort",  produces="application/json; charset=UTF-8")
    @ResponseBody
    public Map saveGridSort(@RequestBody String data) {

        log.info("saveGridSort:{}", data);

        List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
        params = JSONArray.fromObject(data);

        gridDbService.saveGridSort(params);

        Map<String, String> result = new HashMap<String, String>();
        result.put("resultCode", "success");
        return result;
    }

}
