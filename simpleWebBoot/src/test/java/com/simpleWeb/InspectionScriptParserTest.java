package com.simpleWeb;

import com.alibaba.fastjson2.JSONObject;
import com.simpleWeb.entity.vo.InspectionResultVO;
import com.simpleWeb.util.InspectionScriptParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * 巡检脚本解析器测试
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class InspectionScriptParserTest {

    //@Resource
    private InspectionScriptParser parser;

    public InspectionScriptParserTest() {
        parser = new InspectionScriptParser();
    }


    @Test
    public void testParseSampleScript() {
        String sampleScript = "" +
                "==================================================\n" +
                "                 iKnow中间件主机一键巡检报告           \n" +
                "执行时间: 2026-09-22 17:00:00\n" +
                "主机地址: 1***\n" +
                "主机名称: **\n" +
                "系统版本: Red Hat Enterprise Linux Server 7.6 (Maipo)\n" +
                "内核版本: 3.10.0-957.el7.x86_64\n" +
                "架构/核数: x86_64/8核\n" +
                "==================================================\n" +
                "                                                   \n" +
                "==================================================\n" +
                "[1] 主机运行时间\n" +
                "==================================================\n" +
                "系统启动时间: 2026-04-01 09:52:55\n" +
                "已运行时长: 174 天 7 小时 13 分钟\n" +
                "系统负载: 0.14, 0.34, 0.43\n" +
                "                                                   \n" +
                "==================================================\n" +
                "[2] CPU使用率(阈值80 - 90)\n" +
                "==================================================\n" +
                "CPU使用率正常(当前: 6%)\n" +
                "                                                   \n" +
                "                                                   \n" +

                "==================================================\n" +
                "[3] 内存使用率(阈值80 - 90)\n" +
                "==================================================\n" +

                "内存总量: 15789 MB\n" +
                "已用内存: 13824 MB\n" +
                "可用内存: 2380 MB\n" +
                "内存使用率异常: 85.9%\n" +
                "                                                   \n" +
                "                                                   \n" +
                "==================================================\n" +
                "[4] Swap使用率(阈值80 - 90)\n" +
                "==================================================\n" +

                "Swap 总量: 16383 MB\n" +
                "Swap 已用: 8274 MB\n" +
                "Swap 使用率正常: (当前: 50.5%)\n" +
                "                                                   \n" +
                "                                                   \n" +
                "==================================================\n " +
                "[5] 文件系统使用率\n" +
                "==================================================\n" +
                "MOUNT     TYPE    SIZE    USED    AVAIL    USAGE% \n" +
                "----------------------------------------------------\n" +
                "/         xfs     30G     13G     18G      42%\n" +
                "/boot     xfs     1014M   148M    867M     15%\n" +
                "                                                   \n" +
                "==================================================\n" +
                "[6] 高危文件系统使用率(阈值80 - 90)\n" +
                "==================================================\n" +
                "高占用文件系统: /     42%\n" +
                "高占用文件系统: /kdump    23%\n" +
                "高占用文件系统: /pkg     3%\n" +
                "高占用文件系统: /nfstest    16%\n" +
                "高占用文件系统: /  20%\n" +
                //"所有文件系统使用率均正常: (当前均 < 90%)\n" +
                "                                                   \n" +

                "==================================================\n" +
                "[7] 关键服务运行状态(Nginx、Redis、Kafka、Zookeeper、Nacos、Xxljob)\n" +
                "Nginx: 未运行/不存在\n" +
                "Xxljob: 正在运行(PID: 433 454)\n" +
                "Redis: 未运行/不存在\n" +
                "Kafka: 未运行/不存在\n" +
                "Zookeeper: 未运行/不存在\n" +
                "Nacos: 未运行/不存在\n" +
                "                                                   \n" +
                "                                                   \n" +
                "==================================================\n" +
                "巡检结束，当出现\"未运行/不存在\"、\"高占用\"、\"异常\"时，需要重点关注\n";

        InspectionResultVO result = parser.parse(sampleScript);

        // 验证解析成功
        assertTrue("解析应该成功", result.getSuccess());
        assertNull("成功时不应有错误信息", result.getMessage());

        // 验证主机信息
        assertNotNull("主机信息不应为空", result.getHostInfo());
        assertEquals("***", result.getHostInfo().getHostIp());
        assertEquals("zhiknow0ap002", result.getHostInfo().getHostname());
        assertEquals("Red Hat Enterprise Linux Server 7.6 (Maipo)", result.getHostInfo().getOsVersion());
        assertEquals("3.10.0-957.el7.x86_64", result.getHostInfo().getKernelVersion());
        assertEquals("x86_64/8核", result.getHostInfo().getArchitecture());
        assertEquals(Integer.valueOf(8), result.getHostInfo().getCpuCores());
        assertEquals("2026-09-22 17:00:00", result.getHostInfo().getInspectionTime());
        assertEquals("2026-04-01 09:52:55", result.getHostInfo().getSystemStartTime());
        assertEquals("174 天 7 小时 13 分钟", result.getHostInfo().getUptime());
        assertEquals("0.14, 0.34, 0.43", result.getHostInfo().getSystemLoad());

        // 验证 CPU 使用率
        assertNotNull("CPU使用率不应为空", result.getCpuUsage());
        assertEquals(Double.valueOf(6.0), result.getCpuUsage().getUsagePercent());
        assertEquals("正常", result.getCpuUsage().getStatus());
        assertEquals(Integer.valueOf(80), result.getCpuUsage().getWarningThresholdMin());
        assertEquals(Integer.valueOf(90), result.getCpuUsage().getWarningThresholdMax());

        // 验证内存使用率
        assertNotNull("内存使用率不应为空", result.getMemoryUsage());
        assertEquals(Integer.valueOf(15789), result.getMemoryUsage().getTotalMb());
        assertEquals(Integer.valueOf(13824), result.getMemoryUsage().getUsedMb());
        assertEquals(Integer.valueOf(2380), result.getMemoryUsage().getAvailableMb());
        assertEquals(Double.valueOf(85.9), result.getMemoryUsage().getUsagePercent());
        assertEquals("异常", result.getMemoryUsage().getStatus());

        // 验证 Swap 使用率
        assertNotNull("Swap使用率不应为空", result.getSwapUsage());
        assertEquals(Integer.valueOf(16383), result.getSwapUsage().getTotalMb());
        assertEquals(Integer.valueOf(8274), result.getSwapUsage().getUsedMb());
        assertEquals(Double.valueOf(50.5), result.getSwapUsage().getUsagePercent());
        assertEquals("正常", result.getSwapUsage().getStatus());

        // 验证文件系统使用率
        assertNotNull("文件系统列表不应为空", result.getFilesystemUsages());
        assertEquals(2, result.getFilesystemUsages().size());
        assertEquals("/", result.getFilesystemUsages().get(0).getMountPoint());
        assertEquals("xfs", result.getFilesystemUsages().get(0).getFsType());
        assertEquals(Integer.valueOf(42), result.getFilesystemUsages().get(0).getUsagePercent());

        // 验证高危文件系统
        assertNotNull("高危文件系统列表不应为空", result.getHighRiskFilesystems());
        //assertEquals(0, result.getHighRiskFilesystems().size()); // 均正常

        // 验证关键服务
        assertNotNull("服务状态列表不应为空", result.getServiceStatuses());
        assertEquals(6, result.getServiceStatuses().size());

        InspectionResultVO.ServiceStatusVO xxljob = result.getServiceStatuses().stream()
                .filter(s -> "Xxljob".equals(s.getServiceName()))
                .findFirst()
                .orElse(null);
        assertNotNull("应该找到 Xxljob 服务", xxljob);
        assertEquals("UP", xxljob.getStatus());
        assertEquals("433 454", xxljob.getPid());

        InspectionResultVO.ServiceStatusVO nginx = result.getServiceStatuses().stream()
                .filter(s -> "Nginx".equals(s.getServiceName()))
                .findFirst()
                .orElse(null);
        assertNotNull("应该找到 Nginx 服务", nginx);
        assertEquals("DOWN", nginx.getStatus());
        log.info("解析脚本结果：{}", JSONObject.toJSONString(result));
    }

    @Test
    public void testParseEmptyScript() {
        InspectionResultVO result = parser.parse("");
        assertFalse("空脚本解析应该失败", result.getSuccess());
        assertEquals("脚本输出为空", result.getMessage());
    }

    @Test
    public void testParseNullScript() {
        InspectionResultVO result = parser.parse(null);
        assertFalse("null脚本解析应该失败", result.getSuccess());
        assertEquals("脚本输出为空", result.getMessage());
    }
}
