package com.funan.serverinsight.controller;

import com.funan.serverinsight.domain.Server;
import com.funan.serverinsight.service.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * 服务器监控
 *
 * @author funan
 */
@RestController
public class ServerController {
    /**
     * 状态码
     */
    public static final String CODE_TAG = "code";

    /**
     * 数据对象
     */
    public static final String DATA_TAG = "data";

    /**
     * 声明一个SLF4JLogger实例
     */
    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @GetMapping()
    public HashMap<String, Object> getInfo() {
        HashMap<String, Object> result = new HashMap<>(2);
        try {
            Server server = new ServerService().collect();
            result.put(CODE_TAG, 200);
            result.put(DATA_TAG, server);
        } catch (Exception e) {
            logger.error("获取服务器信息时发生错误: ", e);
            result.put(CODE_TAG, 500);
            result.put(DATA_TAG, null);
        }
        return result;
    }

}
