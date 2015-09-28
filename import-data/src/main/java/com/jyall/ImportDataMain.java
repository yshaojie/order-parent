package com.jyall;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * Created by shaojieyue on 9/11/15.
 */
public class ImportDataMain {
    public static void main(String[] args) throws Exception {
        Connection con = null; //定义一个MYSQL链接对象
        Class.forName("com.mysql.jdbc.Driver").newInstance(); //MYSQL驱动
        con = DriverManager.getConnection("jdbc:mysql://10.10.20.108:3306/ysj_cs", "w11", "w11"); //链接本地MYSQL
        Statement stmt; //创建声明
        stmt = con.createStatement();
        String fileName = "/home/shaojieyue/Desktop/省市区vsv.csv";
        List<String> lines = FileUtils.readLines(new File(fileName));
        final String remove = lines.remove(0);//删除第一行
        System.out.println("remove line:"+remove);
        BusinessDistrict.init(stmt);
        County.init(stmt);
        City.init(stmt);
        Province.init(stmt);
        Area.init(stmt);
        for (String line : lines) {
            String[] cols = line.split("\\^");
            if (cols.length != 13) {
                throw new RuntimeException("含有错误行:"+line);
            }
            //新增一条数据
            System.out.println("proccess line:"+line);
            final String area = cols[0];
            final String provice = cols[1];
            final String proviceQuanCheng = cols[3];
            final String proviceJianXie = cols[4];
            final String city = cols[5];
            final String cityQuanCheng = cols[7];
            final String cityJianXie = cols[8];
            final String county = cols[9];
            final String countyQuanCheng = cols[11];
            final String countyJianXie = cols[12];
            final int areaId = Area.queryOrInsert(stmt, area);
            final int provinceId = Province.queryOrInsert(stmt,provice, proviceQuanCheng, proviceJianXie);
            final int cityId = City.queryOrInsert(stmt, areaId, provinceId, city, cityQuanCheng, cityJianXie);
            final int countyId = County.queryOrInsert(stmt, cityId, county, countyQuanCheng, countyJianXie);
        }
    }



}
