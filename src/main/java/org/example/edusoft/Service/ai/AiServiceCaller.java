package org.example.edusoft.service.ai;

import java.util.Map;

/**
 * AI服务调用接口
 * 用于解耦依赖关系
 */
public interface AiServiceCaller {
    
    /**
     * 直接调用AI服务
     * @param endpoint API端点
     * @param request 请求参数
     * @return 响应结果
     */
    Map<String, Object> callAiServiceDirectly(String endpoint, Map<String, Object> request);
}
