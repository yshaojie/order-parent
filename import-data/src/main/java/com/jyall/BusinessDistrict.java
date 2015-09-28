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
public class BusinessDistrict {
    public static final ConcurrentMap<String,Integer> cache = new ConcurrentHashMap<String, Integer>();
    private static int id=10000000;
    private synchronized static int  getId(){
        id++;
        return id;
    }

    public static int queryOrInsert(Statement stmt,int cityId,int countyId,String name,String quanpin,String jianpin) throws SQLException {
        Integer id = null;
        if (id == null) {
            id = getId();
            final String sql = "INSERT INTO m_business_district (id,city_id,county_id, name,name_quanpin,name_jianpin) VALUES (" + id +","+cityId+","+countyId+",'" + name+ "', '" + quanpin+ "', '" + jianpin + "')";
            int count = stmt.executeUpdate(sql);
            if (count<1) {
                throw new RuntimeException("insert area fail.name="+name);
            }
            cache.putIfAbsent(name,id);
        }
        return id;
    }

    public static void init(Statement stmt) throws SQLException {
        stmt.executeUpdate("delete from m_business_district where id>0");
    }

    public static final Map<String,Integer> queryAll(Statement stmt) throws SQLException {
        Map map = new HashMap();
        //查询数据并输出
        String selectSql = "SELECT * FROM m_business_district";
        ResultSet selectRes = stmt.executeQuery(selectSql);
        while (selectRes.next()) { //循环输出结果集
            String username = selectRes.getString("name");
            int id = selectRes.getInt("id");
            map.put(username,id);
        }
        return map;
    }

    /**
     * city_name+county_name+business_district_name
     * @param stmt
     * @return
     * @throws SQLException
     */
    public static final Map<String,Integer> combineQueryAll(Statement stmt) throws SQLException {
        Map map = new HashMap();
        //查询数据并输出
        String selectSql = "select city.name city_name,county.name county_name,mbd.name business_district_name,mbd.id from m_county county,m_city city,m_business_district mbd " +
                "where mbd.city_id = city.id " +
                "and mbd.county_id = county.id";
        ResultSet selectRes = stmt.executeQuery(selectSql);
        while (selectRes.next()) { //循环输出结果集
            String username = selectRes.getString("city_name")+selectRes.getString("county_name")+selectRes.getString("business_district_name");
            int id = selectRes.getInt("id");
            map.put(username,id);
        }
        return map;
    }
}
