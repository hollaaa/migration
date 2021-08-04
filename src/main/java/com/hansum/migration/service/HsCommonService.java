package com.hansum.migration.service;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class HsCommonService {

    @Value("${migration.item.files}")
    private String MIG_FILES;

    /**
     * 프로퍼티 파일에 정의된 items.xml 파일 목록을 읽어온다.  (migration.item.files)
     * @return List<String>
     */
    public List<String> getItemFileList()
    {

        if (StringUtils.isBlank(MIG_FILES)) {
            return new ArrayList<String>();
        }

        String[] arrFiles = MIG_FILES.split(",");
        List<String> list = new ArrayList<String>();

        for (String path : arrFiles)
        {
            list.add(StringUtils.deleteWhitespace(path));
        }

        return list;
    }

    /**
     * 프로퍼티 파일에 정의된 items.xml 파일 목록이 실제로 존재하는지 확인한다.
     * @return boolean
     */
    public boolean isItemFilesExists()
    {
        List<String> itemFileList = getItemFileList();

        if (itemFileList.size() == 0)
        {
            return false;
        }

        for(String filePath : itemFileList)
        {
            File file = new File(StringUtils.deleteWhitespace(filePath));

            if (!file.exists())
            {
                return false;
            }
        }
        return true;

    }



}
