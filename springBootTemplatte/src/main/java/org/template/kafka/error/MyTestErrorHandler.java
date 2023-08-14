package org.template.kafka.error;

import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**********************************
 * @author zhang zhao lin
 * @date 2023年02月04日 12:05
 * @Description:
 **********************************/
@Component(value = "myTestErrorHandler")
public class MyTestErrorHandler implements KafkaListenerErrorHandler {
    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
        System.out.println("进入ErrorHandler");
        return null;
    }
}
