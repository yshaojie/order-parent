package com.jsjy;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

/**
 *  启动主类
 * Created by shaojieyue on 9/10/15.
 */
public class WebServer {
    public void start(String serverAddress, int serverPort, String contextpath) throws Exception {
        run(serverAddress,serverPort,contextpath);
    }

    private void run(String serverAddress, int serverPort, String contextpath) throws Exception {
        if (!contextpath.startsWith("/")) {
            contextpath = "/"+contextpath;
        }
        contextpath = contextpath.replaceAll("//","/");

        //设置系统参数,方便dubbo使用
        System.setProperty("server.ip",serverAddress);
        System.setProperty("server.port",serverPort+"");
        System.setProperty("server.contextpath",contextpath);
        String webappDir = getWebappPath();;
        Tomcat tomcat = new Tomcat();
        String workDir = getWorkDir(serverPort);
        if (workDir != null) {
            System.out.println("tomcat work dir ="+workDir);
            tomcat.setBaseDir(workDir);
        }
        tomcat.setHostname(serverAddress);
        tomcat.setPort(serverPort);
        tomcat.addWebapp(contextpath, webappDir);
        tomcat.start();
        tomcat.getServer().await();
    }

    private String getWebappPath() {
        //开发环境webapp相对路径
        String webappDirLocation = "src/main/webapp/";
        File webappDir = new File(webappDirLocation);

        final String pdir = System.getProperty("webapp.dir");
        if (notBlank(pdir)) {
            webappDir = new File(pdir.trim());
        }

        //获取server路径,参数server_home由服务启动时指定
        final String serverHome = System.getProperty("server_home");
        //serverHome 非空
        if (notBlank(serverHome)) {//优先级最高
            webappDir = new File(serverHome.trim()+"/resources/webapp");
        }
        String webappPath = webappDir.getAbsolutePath();
        System.out.println("use webapp: "+webappPath);

        //webapp目录没有找到
        if (!webappDir.exists()) {
            throw new IllegalArgumentException("webapp dir not exist. dir="+webappPath);
        }
        return webappPath;
    }

    private boolean notBlank(String str) {
        return str!=null && !"".equals(str.trim());
    }

    /**
     * 获取tomcat的工作目录
     * @param serverPort
     * @return
     */
    private String getWorkDir(int serverPort) {
        String workDir = null;
        final String serverHome = System.getProperty("server_home");
        if (notBlank(serverHome)) {
            workDir = serverHome.trim()+"/tomcat."+serverPort;
        }else {
            String tmpdir = System.getProperty("java.io.tmpdir");
            if (notBlank(tmpdir)) {
                workDir = tmpdir+"/tomcat."+serverPort;
            }
        }
        return workDir;
    }
}
