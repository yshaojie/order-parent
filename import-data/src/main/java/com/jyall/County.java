package com.jyall;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by shaojieyue on 9/11/15.
 */
public class County {
    public static final ConcurrentMap<String,Integer> cache = new ConcurrentHashMap<String, Integer>();
    private static int id=100000;
    private synchronized static int  getId(){
        id++;
        return id;
    }


    public static int queryOrInsert(Statement stmt,int cityId,String name,String quanpin,String jianpin) throws SQLException {
        final String key = name + cityId;
        Integer id = cache.get(key);
        if (id == null) {
            id = getId();
            final String sql = "INSERT INTO m_county (id,city_id, name,name_quanpin,name_jianpin) VALUES (" + id +","+cityId+",'" + name+ "', '" + quanpin+ "', '" + jianpin + "')";
            int count = stmt.executeUpdate(sql);
            if (count<1) {
                throw new RuntimeException("insert area fail.name="+name);
            }
            cache.putIfAbsent(key,id);
        }
        return id;
    }

    public static void init(Statement stmt) throws SQLException {
        stmt.executeUpdate("delete from m_county where id>0");
    }

    public static final Map<String,Integer> queryAll(Statement stmt) throws SQLException {
        Map map = new HashMap();
        //查询数据并输出
        String selectSql = "SELECT * FROM m_county";
        ResultSet selectRes = stmt.executeQuery(selectSql);
        while (selectRes.next()) { //循环输出结果集
            String username = selectRes.getString("name");
            String city_id = selectRes.getString("city_id");
            int id = selectRes.getInt("id");
            map.put(username+city_id,id);
        }
        return map;
    }


}
