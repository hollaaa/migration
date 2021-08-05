package com.hansum.migration.controller;

import com.hansum.migration.domain.db.HsUser;
import com.hansum.migration.service.HsUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HsController {

    @Resource
    private HsUserService hsUserService;

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

}
