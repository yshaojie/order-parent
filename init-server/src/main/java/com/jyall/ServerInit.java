package com.jyall;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by shaojieyue on 9/13/15.
 */
public class ServerInit {
    public static final String LOG_ROOT = "/home/shaojieyue/logs/";
    public static final String SERVER_ROOT = "/home/shaojieyue/servers/";
    public static void main(String[] args) throws IOException {
        if (args == null || args.length<1) {
            System.err.println("please set zhe config file.");
            System.exit(1);
        }
        File file = new File(args[0]);
        if (!file.exists()) {
            System.err.println("file["+file.getAbsolutePath()+"] not exist.");
            System.exit(1);
        }
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.err.println("file["+file.getAbsolutePath()+"] not exist.");
            System.exit(1);
        }
        Properties properties = new Properties();
        properties.load(inputStream);
        String server_args= StringUtils.trimToEmpty(properties.getProperty("server_args"));
        String server_jvm_args= StringUtils.trimToNull(properties.getProperty("server_jvm_args"));
        String server_name= StringUtils.trimToNull(properties.getProperty("server_name"));
        String server_main_class= StringUtils.trimToNull(properties.getProperty("server_main_class"));
        boolean valid = true;
        if (server_jvm_args == null) {
            System.err.println("property server_jvm_args not exist.");
            valid = false;
        }
        if (server_name == null) {
            System.err.println("property server_name not exist.");
            valid = false;
        }
        if (server_main_class == null) {
            System.err.println("property server_main_class not exist.");
            valid = false;
        }

        if (!valid) {//参数检测不通过
            System.exit(1);
        }
        String server_home = SERVER_ROOT+server_name;
        String server_log_home = LOG_ROOT+server_name;
        File logHome = new File(server_log_home);
        if (!logHome.exists()) {
            logHome.mkdirs();
            System.out.println("create dir:" + logHome.getAbsolutePath());
        }
        File serverHome = new File(server_home);
        if (!serverHome.exists()) {
            serverHome.mkdirs();
            System.out.println("create dir:" + serverHome.getAbsolutePath());
        }

        Map params = new HashMap();
        params.put("server_args",server_args);
        params.put("server_jvm_args",server_jvm_args);
        params.put("server_name",server_name);
        params.put("server_main_class",server_main_class);
        params.put("server_home",server_home);
        params.put("server_log_home", server_log_home);
        exec(params);
    }

    public static final void exec(Map<String,String> params) throws IOException {
        final InputStream resourceAsStream = ServerInit.class.getClassLoader().getResourceAsStream("server_template.sh");
        Reader source = new InputStreamReader(resourceAsStream);
        final String server_home = params.get("server_home");
        final String server_name = params.get("server_name");
        File shellFile = new File(server_home +"/server.sh");
        if (shellFile.exists()) {
            System.err.println(params.get("server_name")+" 已经初始化.");
            System.exit(1);
        }
        //创建文件
        FileUtils.touch(shellFile);
        shellFile.setExecutable(true);//设置shell脚本可执行
        final Template compile = Mustache.compiler().compile(source);
        final FileWriter fileWriter = new FileWriter(shellFile);
        compile.execute(params, fileWriter);
        fileWriter.flush();
        IOUtils.closeQuietly(fileWriter);
        System.out.println("init server ["+server_name+"]succcess");
    }
}
