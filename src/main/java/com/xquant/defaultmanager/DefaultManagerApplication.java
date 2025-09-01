package com.xquant.defaultmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class DefaultManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DefaultManagerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("=========================================");
        System.out.println("✅ DefaultManager 启动成功！");
        System.out.println("✅ 访问地址: http://localhost:8082");
        System.out.println("✅ 健康检查: http://localhost:8082/actuator/health");
        System.out.println("✅ 应用信息: http://localhost:8082/actuator/info");
        System.out.println("=========================================");
    }
}
