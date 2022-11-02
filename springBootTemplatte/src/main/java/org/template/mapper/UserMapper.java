package org.template.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**********************************
 * @author zhang zhao lin
 * @date 2022年11月02日 22:08
 * @Description:
 **********************************/
@Mapper
@Component
public interface UserMapper {

    void batchUser(List<Map<String,String>> users);
}
