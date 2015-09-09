package com.jsjy.order.manager;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * 服务关闭前所执行的方法
 * Created by shaojieyue on 9/9/15.
 */
@Component
public class ServerDisposableBean implements DisposableBean {
    public void destroy() throws Exception {
        System.out.println("server destroy");
    }
}
