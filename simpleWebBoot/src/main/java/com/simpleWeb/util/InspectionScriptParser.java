package com.simpleWeb.util;

import cn.hutool.core.util.StrUtil;
import com.simpleWeb.entity.vo.InspectionResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 巡检脚本输出解析器
 * 
 * @author auto-generated
 */
@Slf4j
//@Component
public class InspectionScriptParser {

    /** [N] 标题（阈值80 - 90） / [N] 标题(阈值80 - 90)，全角半角括号均支持 */
    private static final Pattern SECTION_TITLE =
            Pattern.compile("^\\[\\d+\\]\\s*(.+?)(?:[（(]\\s*阈值\\s*([^）)]+)[）)])?\\s*$");
    
    /** 通用 key：value 或 key: value */
    private static final Pattern KV = Pattern.compile("^([^：:]+)[：:]\\s*(.+?)\\s*$");
    
    /** 文件系统表格行：MOUNT TYPE SIZE USED AVAIL USAGE% */
    private static final Pattern FS_ROW = Pattern.compile("^(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)%\\s*$");
    
    /** 高占用文件系统行：高占用文件系统: /  89% */
    private static final Pattern HIGH_RISK_FS_LINE = Pattern.compile("^高占用文件系统[：:]\\s*(\\S+)\\s+(\\d+)%\\s*$");
    
    /** 服务行：Nginx: 未运行/不存在  或  Xxljob: 正在运行(PID: 433 454) */
    private static final Pattern SVC_LINE = Pattern.compile("^([A-Za-z\\u4e00-\\u9fa5]+)\\s*[：:]\\s*(.+?)\\s*$");
    
    /** 提取数字百分比 */
    private static final Pattern PERCENT = Pattern.compile("([0-9.]+)\\s*%");
    
    /** 提取 PID */
    private static final Pattern PID_PATTERN = Pattern.compile("PID[：:]\\s*([0-9\\s,]+)");

    /**
     * 解析脚本完整输出
     */
    public InspectionResultVO parse(String raw) {
        try {
            if (StrUtil.isBlank(raw)) {
                return buildErrorResult("脚本输出为空");
            }
            
            // 统一换行；去 BOM
            String text = raw.replace("\uFEFF", "").replace("\r\n", "\n");
            List<String> lines = new ArrayList<>();
            for (String l : text.split("\n")) {
                String t = l.trim();
                if (!t.isEmpty() && !t.matches("=+")) { // 跳过空行和分隔线
                    lines.add(t);
                }
            }

            InspectionResultVO.InspectionResultVOBuilder builder = InspectionResultVO.builder();
            builder.success(true);
            
            // 解析主机基本信息（头部）
            InspectionResultVO.HostInfoVO hostInfo = parseHostInfo(lines);
            builder.hostInfo(hostInfo);
            
            // 按章节解析
            int cursor = findFirstSection(lines);
            while (cursor < lines.size()) {
                Section section = extractSection(lines, cursor);
                if (section == null) break;
                
                applySection(builder, section, hostInfo);
                cursor = section.endIndex + 1;
            }
            
            return builder.build();
            
        } catch (Exception e) {
            log.error("解析巡检脚本失败", e);
            return buildErrorResult("解析失败: " + e.getMessage());
        }
    }

    /**
     * 解析主机基本信息（头部行）
     */
    private InspectionResultVO.HostInfoVO parseHostInfo(List<String> lines) {
        InspectionResultVO.HostInfoVO hostInfo = InspectionResultVO.HostInfoVO.builder().build();
        
        for (int i = 0; i < lines.size() && i < 15; i++) { // 头部最多看15行
            String line = lines.get(i);
            if (line.startsWith("[")) break; // 遇到章节标题停止
            
            Matcher m = KV.matcher(line);
            if (!m.find()) continue;
            
            String key = m.group(1).trim();
            String val = m.group(2).trim();
            
            switch (key) {
                case "执行时间":
                    hostInfo.setInspectionTime(val);
                    break;
                case "主机地址":
                    hostInfo.setHostIp(val);
                    break;
                case "主机名称":
                    hostInfo.setHostname(val);
                    break;
                case "系统版本":
                    hostInfo.setOsVersion(val);
                    break;
                case "内核版本":
                    hostInfo.setKernelVersion(val);
                    break;
                case "架构/位数":
                case "架构/核数":
                    hostInfo.setArchitecture(val);
                    // 提取核数，如 "x86_64/8核"
                    Pattern corePattern = Pattern.compile("(\\d+)核");
                    Matcher coreMatcher = corePattern.matcher(val);
                    if (coreMatcher.find()) {
                        hostInfo.setCpuCores(Integer.parseInt(coreMatcher.group(1)));
                    }
                    break;
            }
        }
        
        return hostInfo;
    }

    /**
     * 找到第一个章节标题的位置
     */
    private int findFirstSection(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            if (SECTION_TITLE.matcher(lines.get(i)).find()) {
                return i;
            }
        }
        return lines.size();
    }

    /**
     * 提取一个完整章节
     */
    private Section extractSection(List<String> lines, int start) {
        if (start >= lines.size()) return null;
        
        Matcher titleMatcher = SECTION_TITLE.matcher(lines.get(start));
        if (!titleMatcher.find()) return null;
        
        Section section = new Section();
        section.startIndex = start;
        section.title = titleMatcher.group(1).trim();
        section.threshold = titleMatcher.group(2); // 可能为 null
        section.contentLines = new ArrayList<>();
        
        // 收集内容行，直到下一个章节或结束
        int cursor = start + 1;
        while (cursor < lines.size()) {
            String line = lines.get(cursor);
            if (SECTION_TITLE.matcher(line).find()) {
                break; // 遇到下一章节
            }
            section.contentLines.add(line);
            cursor++;
        }
        section.endIndex = cursor - 1;
        
        return section;
    }

    /**
     * 应用章节数据到 builder
     */
    private void applySection(InspectionResultVO.InspectionResultVOBuilder builder, 
                              Section sec, 
                              InspectionResultVO.HostInfoVO hostInfo) {
        String title = sec.title;
        
        if (title.contains("主机运行时间")) {
            parseUptime(sec.contentLines, hostInfo);
        } else if (title.contains("CPU使用率")) {
            builder.cpuUsage(parseCpu(sec));
        } else if (title.contains("内存使用率")) {
            builder.memoryUsage(parseMemory(sec));
        } else if (title.contains("Swap使用率")) {
            builder.swapUsage(parseSwap(sec));
        } else if (title.contains("文件系统使用率") && !title.contains("高危")) {
            builder.filesystemUsages(parseFilesystem(sec));
        } else if (title.contains("高危文件系统")) {
            InspectionResultVO.HighRiskFilesystemVO highRisk = parseHighRiskFs(sec);
            builder.highRiskFilesystemResult(highRisk);
            builder.highRiskFilesystems(highRisk.getItems());
        } else if (title.contains("关键服务")) {
            builder.serviceStatuses(parseServices(sec));
        }
    }

    /**
     * 解析 [1] 主机运行时间（补充到 hostInfo）
     */
    private void parseUptime(List<String> lines, InspectionResultVO.HostInfoVO hostInfo) {
        for (String line : lines) {
            Matcher m = KV.matcher(line);
            if (!m.find()) continue;
            
            String key = m.group(1).trim();
            String val = m.group(2).trim();
            
            if (key.equals("系统启动时间")) {
                hostInfo.setSystemStartTime(val);
            } else if (key.equals("已运行时长")) {
                hostInfo.setUptime(val);
            } else if (key.equals("系统负载")) {
                hostInfo.setSystemLoad(val);
            }
        }
    }

    /**
     * 解析 [2] CPU使用率
     */
    private InspectionResultVO.CpuUsageVO parseCpu(Section sec) {
        InspectionResultVO.CpuUsageVO vo = InspectionResultVO.CpuUsageVO.builder().build();
        parseThreshold(sec.threshold, vo);
        
        for (String line : sec.contentLines) {
            // "CPU使用率正常（当前：6%）"
            Matcher percentMatcher = PERCENT.matcher(line);
            if (percentMatcher.find()) {
                vo.setUsagePercent(Double.parseDouble(percentMatcher.group(1)));
            }
            
            if (line.contains("正常")) {
                vo.setStatus("正常");
            } else if (line.contains("异常") || line.contains("高占用")) {
                vo.setStatus("异常");
            }
        }
        
        return vo;
    }

    /**
     * 解析 [3] 内存使用率
     */
    private InspectionResultVO.MemoryUsageVO parseMemory(Section sec) {
        InspectionResultVO.MemoryUsageVO vo = InspectionResultVO.MemoryUsageVO.builder().build();
        parseThreshold(sec.threshold, vo);
        
        for (String line : sec.contentLines) {
            Matcher m = KV.matcher(line);
            if (m.find()) {
                String key = m.group(1).trim();
                String val = m.group(2).trim();
                
                if (key.equals("内存总量")) {
                    vo.setTotalMb(parseMb(val));
                } else if (key.equals("已用内存")) {
                    vo.setUsedMb(parseMb(val));
                } else if (key.equals("可用内存")) {
                    vo.setAvailableMb(parseMb(val));
                }
            }
            
            // "内存使用率异常：84.9%"
            if (line.contains("使用率")) {
                Matcher percentMatcher = PERCENT.matcher(line);
                if (percentMatcher.find()) {
                    vo.setUsagePercent(Double.parseDouble(percentMatcher.group(1)));
                }
                
                if (line.contains("正常")) {
                    vo.setStatus("正常");
                } else if (line.contains("异常") || line.contains("高占用")) {
                    vo.setStatus("异常");
                }
            }
        }
        
        return vo;
    }

    /**
     * 解析 [4] Swap使用率
     */
    private InspectionResultVO.SwapUsageVO parseSwap(Section sec) {
        InspectionResultVO.SwapUsageVO vo = InspectionResultVO.SwapUsageVO.builder().build();
        parseThreshold(sec.threshold, vo);
        
        for (String line : sec.contentLines) {
            Matcher m = KV.matcher(line);
            if (m.find()) {
                String key = m.group(1).trim();
                String val = m.group(2).trim();
                
                if (key.contains("Swap") && key.contains("总量")) {
                    vo.setTotalMb(parseMb(val));
                } else if (key.contains("Swap") && key.contains("已用")) {
                    vo.setUsedMb(parseMb(val));
                }
            }
            
            if (line.contains("使用率")) {
                Matcher percentMatcher = PERCENT.matcher(line);
                if (percentMatcher.find()) {
                    vo.setUsagePercent(Double.parseDouble(percentMatcher.group(1)));
                }
                
                if (line.contains("正常")) {
                    vo.setStatus("正常");
                } else if (line.contains("异常") || line.contains("高占用")) {
                    vo.setStatus("异常");
                }
            }
        }
        
        return vo;
    }

    /**
     * 解析 [5] 文件系统使用率
     */
    private List<InspectionResultVO.FilesystemUsageVO> parseFilesystem(Section sec) {
        List<InspectionResultVO.FilesystemUsageVO> list = new ArrayList<>();
        
        for (String line : sec.contentLines) {
            Matcher m = FS_ROW.matcher(line);
            if (m.find()) {
                InspectionResultVO.FilesystemUsageVO vo = InspectionResultVO.FilesystemUsageVO.builder()
                        .mountPoint(m.group(1))
                        .fsType(m.group(2))
                        .totalSize(m.group(3))
                        .usedSize(m.group(4))
                        .availableSize(m.group(5))
                        .usagePercent(Integer.parseInt(m.group(6)))
                        .status(determineStatus(Integer.parseInt(m.group(6))))
                        .build();
                list.add(vo);
            }
        }
        
        return list;
    }

    /**
     * 解析 [6] 高危文件系统
     */
    private InspectionResultVO.HighRiskFilesystemVO parseHighRiskFs(Section sec) {
        List<InspectionResultVO.FilesystemUsageVO> list = new ArrayList<>();
        
        InspectionResultVO.HighRiskFilesystemVO.HighRiskFilesystemVOBuilder resultBuilder =
                InspectionResultVO.HighRiskFilesystemVO.builder();
        
        // 阈值来自标题，如 "(阈值80 - 90)"
        Integer thresholdMin = null;
        Integer thresholdMax = null;
        Pattern p = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");
        if (StrUtil.isNotBlank(sec.threshold)) {
            Matcher tm = p.matcher(sec.threshold);
            if (tm.find()) {
                thresholdMin = Integer.parseInt(tm.group(1));
                thresholdMax = Integer.parseInt(tm.group(2));
            }
        }
        resultBuilder.warningThresholdMin(thresholdMin);
        resultBuilder.warningThresholdMax(thresholdMax);
        
        String message = null;
        boolean normal = false;
        
        for (String line : sec.contentLines) {
            if (line.contains("均正常") || line.contains("均 <")) {
                normal = true;
                message = line;
                continue;
            }
            
            // "高占用文件系统: /  89%"
            Matcher hrm = HIGH_RISK_FS_LINE.matcher(line);
            if (hrm.find()) {
                int usage = Integer.parseInt(hrm.group(2));
                InspectionResultVO.FilesystemUsageVO vo = InspectionResultVO.FilesystemUsageVO.builder()
                        .mountPoint(hrm.group(1))
                        .usagePercent(usage)
                        .status("异常")
                        .warningThresholdMin(thresholdMin)
                        .warningThresholdMax(thresholdMax)
                        .build();
                list.add(vo);
                continue;
            }
            
            // 兼容表格行格式
            Matcher m = FS_ROW.matcher(line);
            if (m.find()) {
                InspectionResultVO.FilesystemUsageVO vo = InspectionResultVO.FilesystemUsageVO.builder()
                        .mountPoint(m.group(1))
                        .fsType(m.group(2))
                        .totalSize(m.group(3))
                        .usedSize(m.group(4))
                        .availableSize(m.group(5))
                        .usagePercent(Integer.parseInt(m.group(6)))
                        .status("异常")
                        .warningThresholdMin(thresholdMin)
                        .warningThresholdMax(thresholdMax)
                        .build();
                list.add(vo);
            }
        }
        
        resultBuilder.items(list);
        resultBuilder.message(message);
        resultBuilder.status(!list.isEmpty() ? "异常" : "正常");
        
        return resultBuilder.build();
    }

    /**
     * 解析 [7] 关键服务运行状态
     */
    private List<InspectionResultVO.ServiceStatusVO> parseServices(Section sec) {
        List<InspectionResultVO.ServiceStatusVO> list = new ArrayList<>();
        
        for (String line : sec.contentLines) {
            Matcher m = SVC_LINE.matcher(line);
            if (!m.find()) continue;
            
            String name = m.group(1).trim();
            String body = m.group(2).trim();
            
            InspectionResultVO.ServiceStatusVO vo = InspectionResultVO.ServiceStatusVO.builder()
                    .serviceName(name)
                    .build();
            
            if (body.contains("正在运行")) {
                vo.setStatus("UP");
                // 提取 PID
                Matcher pidMatcher = PID_PATTERN.matcher(body);
                if (pidMatcher.find()) {
                    vo.setPid(pidMatcher.group(1).trim());
                }
            } else {
                vo.setStatus("DOWN");
                vo.setPid("");
            }
            
            list.add(vo);
        }
        
        return list;
    }

    // ========== 工具方法 ==========

    /**
     * 解析阈值字符串到 VO（通过反射设置 warningThresholdMin/Max）
     */
    private void parseThreshold(String threshold, Object vo) {
        if (StrUtil.isBlank(threshold)) return;
        
        // "80 - 90" 或 "88 - 90"
        Pattern p = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");
        Matcher m = p.matcher(threshold);
        if (m.find()) {
            try {
                java.lang.reflect.Field minField = vo.getClass().getDeclaredField("warningThresholdMin");
                java.lang.reflect.Field maxField = vo.getClass().getDeclaredField("warningThresholdMax");
                minField.setAccessible(true);
                maxField.setAccessible(true);
                minField.set(vo, Integer.parseInt(m.group(1)));
                maxField.set(vo, Integer.parseInt(m.group(2)));
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 解析 "15789 MB" 类似格式
     */
    private Integer parseMb(String text) {
        Pattern p = Pattern.compile("(\\d+)\\s*MB");
        Matcher m = p.matcher(text);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return null;
    }

    /**
     * 根据使用率判断状态
     */
    private String determineStatus(int usagePercent) {
        if (usagePercent >= 90) {
            return "异常";
        } else if (usagePercent >= 80) {
            return "预警";
        } else {
            return "正常";
        }
    }

    /**
     * 构建错误结果
     */
    private InspectionResultVO buildErrorResult(String message) {
        return InspectionResultVO.builder()
                .success(false)
                .message(message)
                .build();
    }

    /**
     * 内部章节结构
     */
    private static class Section {
        int startIndex;
        int endIndex;
        String title;
        String threshold;
        List<String> contentLines;
    }
}
