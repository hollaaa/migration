package com.hansum.migration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansum.migration.domain.db.HsUser;
import com.hansum.migration.domain.db.repository.HsUserRepository;
import com.hansum.migration.service.HsUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Optional;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class HsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HsUserService hsUserService;

    @Autowired
    private HsUserRepository hsUserRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createRepositoryTest()
    {
        HsUser hsUser = new HsUser();
        hsUser.setId("test_id");
        hsUser.setName("tesT_name");
        hsUser.setAge(22);
        hsUserRepository.save(hsUser);

    }

    @Test
    public void readRepositoryTest()
    {
        log.debug("---reqdRepositoryTest----");
        log.debug(""+hsUserRepository.findById("test_id").get());
    }

    @Test
    public void updateRepositoryTest() {
        Optional<HsUser> getUser = hsUserRepository.findById("test_id");
        if(getUser.isPresent())
        {
            HsUser updateuser = getUser.get();
            updateuser.setName("변경하기");
            hsUserRepository.save(updateuser);
        }

        log.debug("--------------updateRepositoryTest--------------");
        log.debug(""+hsUserRepository.findById("test_id").get());
    }

    @Test
    public void deleteRepositoryTest()
    {
        HsUser hsUser = new HsUser();
        hsUser.setId("test_id");

        Optional<HsUser> getUser = hsUserRepository.findById("test_id");
        if(getUser.isPresent())
        {
            HsUser deleteUser = getUser.get();
            hsUserRepository.delete(deleteUser);
        }

        log.debug("----- delete test------");
        log.debug(""+hsUserRepository.findById("test_id").get());

    }


    @Test
    public void creatTest() throws Exception
    {
        HsUser hsUser = new HsUser();
        hsUser.setId("test_id");
        hsUser.setName("test_name");
        hsUser.setAge(22);

        String json = objectMapper.writeValueAsString(hsUser);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        resultActions.andDo(MockMvcResultHandlers.print());
    }


}
