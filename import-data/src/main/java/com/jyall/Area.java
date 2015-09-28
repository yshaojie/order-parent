package com.jyall;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by shaojieyue on 9/11/15.
 */
public class Area {
    public static final ConcurrentMap<String,Integer> cache = new ConcurrentHashMap<String, Integer>();
    private static int id=1000;
    private synchronized static int  getId(){
        id++;
        return id;
    }

    public static int queryOrInsert(Statement stmt,String name) throws SQLException {
        Integer id = cache.get(name);
        if (id == null) {
            id = getId();
            final String sql = "INSERT INTO m_area (id, name,user_id,tel,password) VALUES (" + id + ", '" + name + "','1','13717670215','11')";
            int count = stmt.executeUpdate(sql);
            if (count<1) {
                throw new RuntimeException("insert area fail.name="+name);
            }
            cache.putIfAbsent(name,id);
        }
        return id;
    }

    public static void init(Statement stmt) throws SQLException {
        stmt.executeUpdate("delete from m_area where id>0");
    }
}
