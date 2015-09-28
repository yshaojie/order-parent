package com.jyall;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by shaojieyue on 9/11/15.
 */
public class ImportHouseMain {
   public static final  String[] columns = {
            "",
            "name",
            "alias",
            "name_jianpin",
            "name_quanpin",
            "",//商圈id
            "",
            "",//地铁站点
            "longitude",
            "latitude",//纬度
            "property_type_code",//物业类型
            "",
            "building_type_code",//12 建筑类别
            "build_structure_code",//13 建筑结构
            "",
            "delivery_time",//15 交房时间
            "property_right_year",//16 产权年限
            "floor_space",//17 建筑面积（）
            "covere_areas",//18占地面积（）
            "volume_rate",//19 容积率
            "greening_rate",//20绿化率
            "",//21
            "",//22
            "households_total_number",//23 总户数（
            "",//24
            "",//25
            "",//26
            "property_fee",//27 物业费
            "",//28
            "heating_type_code",//29 采暖方式
            "",//30
            "",//31
            "",//32
            "",//33
            "",//34
            "",//35
            "",//36
            "",//37
            "",//38
            "",//39
            "business_district_id"//40 商圈id
    };

     static Map<String, String> buildMap ;
     static Map<String, String> buildStructureMap ;
     static Map<String, String> heatMap ;
     static Map<String, String> propertyMap;
     static Map<String,Integer> businessMap;
    static File shangerror = new File("/home/shaojieyue/Desktop/shangerror"+DateFormatUtils.format(new Date(),"MMddHHmmss")+".csv");
    static File ohtererror = new File("/home/shaojieyue/Desktop/ohtererror"+DateFormatUtils.format(new Date(),"MMddHHmmss")+".csv");
    public static void main(String[] args) throws Exception {

        if (columns.length != 41) {
            throw new RuntimeException("错误的列");
        }

        Class.forName("com.mysql.jdbc.Driver").newInstance(); //MYSQL驱动
        final Connection con = DriverManager.getConnection("jdbc:mysql://10.10.20.108:3306/ysj_cs", "w11", "w11"); //链接本地MYSQL
        Statement stmt; //创建声明
        stmt = con.createStatement();
        buildMap = BuildType.queryAll(stmt);
        buildStructureMap = BuildStructure.queryAll(stmt);
        heatMap = HeatingType.queryAll(stmt);
        propertyMap = PropertyType.queryAll(stmt);
        businessMap = BusinessDistrict.combineQueryAll(stmt);
//        System.out.println(buildMap);
//        System.out.println(buildStructureMap);
//        System.out.println(heatMap);
//        System.out.println(propertyMap);
        District.init(con);
        String fileName = "/home/shaojieyue/Desktop/小区库（改）";
        final Collection<File> files = FileUtils.listFiles(new File(fileName), new IOFileFilter() {

            public boolean accept(File file) {
                return true;
            }

            public boolean accept(File file, String s) {
                return true;
            }
        }, new IOFileFilter() {

            public boolean accept(File file) {
                return true;
            }

            public boolean accept(File file, String s) {
                return true;
            }
        });
        final ComboPooledDataSource comboPooledDataSource = databasePool();
        final ConnectionPoolDataSource connectionPoolDataSource = comboPooledDataSource.getConnectionPoolDataSource();
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        final AtomicInteger count = new AtomicInteger();
        FileUtils.touch(shangerror);
        FileUtils.touch(ohtererror);
        AtomicInteger ini = new AtomicInteger();
        for (File file : files) {
            List<String> lines = FileUtils.readLines(file);
            lines.remove(0);
            System.out.println("proocess :" + file.getName());
            for (final String line : lines) {
                final int ss = ini.incrementAndGet();
                if (ss%1000 ==0) {
                    System.out.println("ss="+ss);
                }
                final List<String> fields = new ArrayList<String>();
                final List<String> values = new ArrayList<String>();
                try {
                    format(line, fields, values);//格式化数据
                    executorService.submit(new Runnable() {
                        public void run() {
                            PooledConnection pooledConnection = null;
                            try {
                                 pooledConnection = connectionPoolDataSource.getPooledConnection();
                                final int count = District.insert(pooledConnection.getConnection(), fields, values);
                                if (count < 1) {
                                    FileUtils.writeStringToFile(ohtererror, line + IOUtils.LINE_SEPARATOR,true);
                                }
                            } catch (Exception e) {
                                try {
                                    FileUtils.writeStringToFile(ohtererror, line + IOUtils.LINE_SEPARATOR,true);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }finally {
                                try {
                                    pooledConnection.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            final int dd = count.incrementAndGet();
                            if (dd % 100 == 0) {
                                System.out.println("dd="+dd);
                            }
                        }
                    });

                }catch (Exception e){
                    FileUtils.writeStringToFile(ohtererror, line + IOUtils.LINE_SEPARATOR,true);
                }

            }
            Thread.sleep(lines.size()*6);
        }


    }

    public static final ComboPooledDataSource databasePool() throws PropertyVetoException {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.jdbc.Driver");
        cpds.setJdbcUrl("jdbc:mysql://10.10.20.108:3306/ysj_cs");
        cpds.setUser("w11");
        cpds.setPassword("w11");
// 下面的设置是可选的，c3p0可以在默认条件下工作，也可以设置其他条件
        cpds.setMinPoolSize(20);
        cpds.setMaxPoolSize(20);
        return cpds;
    }

    public static final void format(String line,List<String> fields,List values){
        final String[] cols = line.split("\\^");
        //名称
        fields.add(columns[1].trim());
        values.add(cols[1]);

        //简称
        fields.add(columns[3]);
        values.add(cols[3]);

        //全拼
        fields.add(columns[4]);
        values.add(cols[4]);

        //经度
        fields.add(columns[8]);
        values.add(new BigDecimal(cols[8]).setScale(5,BigDecimal.ROUND_DOWN));

        //纬度
        fields.add(columns[9]);
        values.add(new BigDecimal(cols[9]).setScale(5,BigDecimal.ROUND_DOWN));

        //物业类型
        fields.add(columns[10]);
        String propertyId = propertyMap.get(cols[10]);
        if (propertyId  == null) {
            propertyId = "07";
        }
        values.add(propertyId);

        //建筑类别CD
        fields.add(columns[12]);
        String buildTypeId = buildMap.get(cols[12]);
        if (buildTypeId  == null) {
            buildTypeId = "08";
        }
        values.add(buildTypeId);

        //建筑结构CD
        fields.add(columns[13]);
        String buildStructureId = buildStructureMap.get(cols[13]);
        if (buildStructureId  == null) {
            buildStructureId = "08";
        }
        values.add(buildStructureId);

        //交房时间
        if (cols[15] != null && !"".equals(cols[15].trim())) {
            try {
                final Date date = DateUtils.parseDate(cols[15], new String[]{"yyyy-MM-dd"});
                if (date.getYear()>90) {//1990年后房子
                    fields.add(columns[15]);
                    values.add(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        //16 产权年限
        final int rightYear = getRightYear(cols[16]);
        fields.add(columns[16]);
        values.add(rightYear);

        //17 建筑面积
        if (cols[17]!=null && !"".equals(cols[17].trim())) {
            try{
                Integer value = Integer.valueOf(cols[17]);
                fields.add(columns[17]);
                values.add(value);
            }catch (Exception e){

            }
        }

        //18 占地面积
        if (cols[18]!=null && !"".equals(cols[18].trim())) {
            try{
                Integer value = Integer.valueOf(cols[18]);
                fields.add(columns[18]);
                values.add(value);
            }catch (Exception e){

            }
        }

        //19 容积率
        if (cols[19]!=null && !"".equals(cols[19].trim())) {
            try{
                String str = cols[19].trim().replaceAll("%", "");
                BigDecimal bigDecimal = new BigDecimal(str).setScale(2, BigDecimal.ROUND_DOWN);
                fields.add(columns[19]);
                values.add(bigDecimal.doubleValue());
            }catch (Exception e){

            }
        }

        //20 绿化率
        if (cols[20]!=null && !"".equals(cols[20].trim())) {
            try{
                String str = cols[20].trim();
                BigDecimal bigDecimal = new BigDecimal(str).setScale(2, BigDecimal.ROUND_DOWN);
                fields.add(columns[20]);
                values.add(bigDecimal.doubleValue());
            }catch (Exception e){

            }
        }

        //23 总户数
        if (cols[23]!=null && !"".equals(cols[23].trim())) {
            try{
                String str = cols[23].trim().replaceAll("户","");
                BigDecimal bigDecimal = new BigDecimal(str).setScale(0, BigDecimal.ROUND_DOWN);
                fields.add(columns[23]);
                values.add(bigDecimal.intValue());
            }catch (Exception e){

            }
        }

        //27 物业费
        if (cols[27]!=null && !"".equals(cols[27].trim())) {
            try{
                final int length = cols[27].trim().length() > 3 ? 4 : cols[27].trim().length();
                String str = cols[27].trim().substring(0,length);
                BigDecimal bigDecimal = new BigDecimal(str).setScale(2, BigDecimal.ROUND_DOWN);
                fields.add(columns[27]);
                values.add(bigDecimal.doubleValue());
            }catch (Exception e){

            }
        }

        //40 商圈
        if (cols[40]!=null && !"".equals(cols[40].trim())) {
            try{
                String str = cols[38].trim()+cols[39].trim()+cols[40].trim();
                final Integer businessId = businessMap.get(str);
                if (businessId == null) {
                    FileUtils.writeStringToFile(shangerror,line+IOUtils.LINE_SEPARATOR,true);
                }
                fields.add(columns[40]);
                values.add(businessId);
            }catch (Exception e){
            }
        }
    }

    public static final int getRightYear(String str){
        int year = 70;
        if (str!=null) {
            if (str.contains("40")) {
                year = 40;
            }
            if (str.contains("50")) {
                year = 50;
            }
            if (str.contains("70")) {
                year = 70;
            }
        }
        return year;
    }
}
