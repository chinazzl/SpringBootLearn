package org.template.service;

import org.template.dto.QueryBO;
import org.template.dto.ResultResponse;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月14日 14:49
 * @Description:
 **********************************/
public interface BasicQuery {

    ResultResponse basicQueryResult(QueryBO queryBO);

}
