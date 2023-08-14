package org.template.config.wrapper;

import org.springframework.stereotype.Component;
import org.template.config.annotation.SpecificAlter;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月10日 11:01
 * @Description:
 **********************************/
@Component
@SpecificAlter(name = "sn")
public class SnSpecialWrapper implements SpecialWrapper{
    @Override
    public String getName() {

        System.out.println("sn special");
        return null;
    }
}
