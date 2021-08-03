package com.hansum.migration.service;

import com.hansum.migration.domain.db.HsUser;
import com.hansum.migration.domain.db.repository.HsUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class HsUserService {

    @Resource
    private HsUserRepository hsUserRepository;

    public HsUser create(HsUser param)
    {
        HsUser hsUser = new HsUser();
        hsUser.setId(param.getId());
        hsUser.setAge(param.getAge());
        hsUser.setName(param.getName());
        hsUser.setInsTm(param.getInsTm());

        return hsUserRepository.save(hsUser);
    }

    public Optional<HsUser> read(String id)
    {
        return hsUserRepository.findById(id);
    }

    public HsUser update(HsUser param)
    {
        hsUserRepository.save(param);

        HsUser result;
        Optional<HsUser> user = hsUserRepository.findById(param.getId());
        if(user.isPresent()) // id에 해당하는 값이 있었는지 확인
        {
            HsUser updateHsUser = user.get();
            updateHsUser.setName(param.getName());
            result = hsUserRepository.save(updateHsUser);
        }
        else
        {
            result = null;
        }

        return result;

    }

    public void delete(HsUser param)
    {
        hsUserRepository.delete(param);
    }

    public long getCount()
    {
        return hsUserRepository.count();
    }

}
