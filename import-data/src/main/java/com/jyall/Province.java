package com.jyall;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by shaojieyue on 9/11/15.
 */
public class Province {
    public static final ConcurrentMap<String,Integer> cache = new ConcurrentHashMap<String, Integer>();
    private static int id=1000;
    private synchronized static int  getId(){
        id++;
        return id;
    }


    public static int queryOrInsert(Statement stmt,String name,String quanpin,String jianpin) throws SQLException {
        Integer id = cache.get(name);
        if (id == null) {
            id = getId();
            final String sql = "INSERT INTO m_province (id, name,name_quanpin,name_jianpin) VALUES (" + id + ", '" + name+ "', '" + quanpin+ "', '" + jianpin + "')";
            int count = stmt.executeUpdate(sql);
            if (count<1) {
                throw new RuntimeException("insert area fail.name="+name);
            }
            cache.putIfAbsent(name,id);
        }
        return id;
    }

    public static void init(Statement stmt) throws SQLException {
        stmt.executeUpdate("delete from m_province where id>0");
    }
}
