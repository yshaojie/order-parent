package com.jyall;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shaojieyue on 9/11/15.
 */
public class BuildStructure {
    public static final Map<String,String> queryAll(Statement stmt) throws SQLException {
        Map map = new HashMap();
        //查询数据并输出
        String selectSql = "SELECT * FROM m_build_structure";
        ResultSet selectRes = stmt.executeQuery(selectSql);
        while (selectRes.next()) { //循环输出结果集
            String username = selectRes.getString("type");
            String code = selectRes.getString("code");
            map.put(username,code);
        }
        return map;
    }
}
