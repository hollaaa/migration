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

    /**
     * 원 테이블 col 과 모델 attr 명을 비교하여 같은 놈인지 판단
     * @param qualifier
     * @param colName
     * @return boolean
     */
    public static boolean isSameAttr(String qualifier, String colName) {

        if (StringUtils.equals(StringUtils.replace(colName, "p_", ""), qualifier))
        {
            return true;
        }
        return false;
    }
}
