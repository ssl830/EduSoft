package org.example.edusoft.mapper.ai;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.entity.ai.AiServiceCallLog;

import java.time.LocalDate;

@Mapper
public interface AiServiceCallLogMapper extends BaseMapper<AiServiceCallLog> {

    @Insert("INSERT INTO aiservicecalllog (user_id, endpoint, duration_ms, status, error_message) " +
            "VALUES (#{userId}, #{endpoint}, #{durationMs}, #{status}, #{errorMessage})")
    void insertLog(AiServiceCallLog log);

    /**
     * 计算指定时间段内，特定AI服务接口的平均调用耗时。
     *
     * @param endpoint 接口名称，如 '/rag/generate'
     * @param start    开始日期
     * @param end      结束日期
     * @return 平均耗时（毫秒），如果没有数据则返回0.0
     */
    @Select("""
            SELECT IFNULL(AVG(duration_ms), 0.0) FROM aiservicecalllog
            WHERE endpoint = #{endpoint} AND DATE(call_time) BETWEEN #{start} AND #{end}
            """)
    Double getAverageDuration(@Param("endpoint") String endpoint,
                              @Param("start") LocalDate start,
                              @Param("end") LocalDate end);

    /**
     * 计算指定时间段内，特定AI服务接口的调用次数。
     *
     * @param endpoint 接口名称，如 '/rag/generate'
     * @param start    开始日期
     * @param end      结束日期
     * @return 调用次数
     */
    @Select("""
            SELECT COUNT(*) FROM aiservicecalllog
            WHERE endpoint = #{endpoint} AND DATE(call_time) BETWEEN #{start} AND #{end}
            """)
    int getCallCount(@Param("endpoint") String endpoint,
                     @Param("start") LocalDate start,
                     @Param("end") LocalDate end);
} 