package org.template.service;

import org.springframework.stereotype.Component;
import org.template.dto.QueryBO;
import org.template.dto.QueryrResultDto;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月14日 15:02
 * @Description:
 **********************************/
@Component
public class BasicQueryImpl implements BasicQuery{

    @Override
    public QueryrResultDto basicQueryResult(QueryBO queryBO) {
        return null;
    }
}
