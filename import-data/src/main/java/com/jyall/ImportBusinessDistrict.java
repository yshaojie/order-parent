package com.jyall;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * Created by shaojieyue on 9/11/15.
 */
public class ImportBusinessDistrict {
    public static void main(String[] args) throws Exception {
        Connection con = null; //定义一个MYSQL链接对象
        Class.forName("com.mysql.jdbc.Driver").newInstance(); //MYSQL驱动
        con = DriverManager.getConnection("jdbc:mysql://10.10.20.108:3306/ysj_cs", "w11", "w11"); //链接本地MYSQL
        Statement stmt; //创建声明
        stmt = con.createStatement();
        BusinessDistrict.init(stmt);

        String fileName = "/home/shaojieyue/Desktop/商圈库csv.csv";
        List<String> lines = FileUtils.readLines(new File(fileName));
        final String remove = lines.remove(0);//删除第一行
        System.out.println("remove line:" + remove);
        final Map<String, Integer> cityMap = City.queryAll(stmt);
        final Map<String, Integer> countyMap = County.queryAll(stmt);
        int i=0;
        for (String line : lines) {
            try {
                String[] cols = line.split("\\^");
                if (cols.length != 12) {
                    throw new RuntimeException("含有错误行:"+line);
                }
                //新增一条数据
//                System.out.println("proccess line:"+line);
                String city = cols[0];
                String county = cols[4];
                String bussName = cols[8];
                String bussQuanPin = cols[10];
                String bussJianXie = cols[11];
                int cityId = cityMap.get(city);
                int countyId = countyMap.get(county+cityId);
                BusinessDistrict.queryOrInsert(stmt,cityId,countyId,bussName,bussQuanPin,bussJianXie);
                i++;
            }catch (Exception e){
                System.out.println("errorLine:"+line);
//                e.printStackTrace();
            }

        }
        System.out.println("导入行数:"+i);
    }

}
