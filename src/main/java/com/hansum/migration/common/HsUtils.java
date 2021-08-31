package com.hansum.migration.common;

import org.apache.commons.lang.StringUtils;

public class HsUtils {


    public static String getStringFromObject(Object obj)
    {
        if (obj == null)
        {
            return null;
        }

        String val = String.valueOf(obj);

        if (StringUtils.equals("null", val))
        {
            return null;
        }

        return val;

    }

}
