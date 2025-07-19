package com.simpleWeb.config;

import com.simpleWeb.entity.db.cmp.CmpResourceDO;
import com.simpleWeb.mapper.cmp.CmpResourceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.simpleWeb.constant.VmProvider.*;

/**
 * @author: zhaolin
 * @Date: 2025/7/18
 * @Description:
 **/
@Component
@Slf4j
public class ResourceCollectFactory {

    @Resource
    private CmpResourceMapper cmpResourceMapper;

    public List<CmpResourceDO> queryCmpResourceList(String provider){
        List<CmpResourceDO> cmpResourceDOList = new ArrayList<>();
        switch (provider){
            case VMWARE:
                return cmpResourceMapper.queryVmwareResourceDOList();
            case FUSION_CLOUD:
                return cmpResourceMapper.queryFusionCloudResourceDOList();
            default:
                log.error("未知的云厂商{}",provider);
                return cmpResourceDOList;
        }
    }
}
