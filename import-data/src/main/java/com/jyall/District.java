package com.jyall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by shaojieyue on 9/11/15.
 */
public class District {
    private static int id=10000000;
    private synchronized static int  getId(){
        id++;
        return id;
    }

    public static final int init(Connection conn) throws SQLException {
        final Statement statement = conn.createStatement();
        return statement.executeUpdate("delete from ysj_cs.m_district where id>0");
    }

    public static final int insert(Connection conn, List<String> cols,List values) throws SQLException {
        try {
            if (cols.size() != values.size()) {
                throw new RuntimeException("列和参数不匹配");
            }

            StringBuilder sqlBuild = new StringBuilder("INSERT INTO ysj_cs.m_district(");
            for (String col : cols) {
                sqlBuild.append(col);
                sqlBuild.append(",");
            }
            sqlBuild.append("id)values(");
            for (String col : cols) {
                sqlBuild.append("?");
                sqlBuild.append(",");
            }
            sqlBuild.append("?)");
            final String insertSql = sqlBuild.toString();
            final PreparedStatement preparedStatement = conn.prepareStatement(insertSql);
            for (int i=0;i<values.size();i++) {
                preparedStatement.setObject(i+1,values.get(i));
            }
            preparedStatement.setInt(values.size()+1,getId());
            final int count = preparedStatement.executeUpdate();
            return count;
        }catch (Throwable e){

        }
        return 0;

    }
}
