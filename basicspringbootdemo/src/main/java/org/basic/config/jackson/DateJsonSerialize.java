package org.basic.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/5 23:31
 * @Description: date类型进行序列化
 */
public class DateJsonSerialize extends JsonSerializer {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            if (value.getClass().isAssignableFrom(Date.class)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                String format = sdf.format((Date) value);
                gen.writeString(format);
            }
        }
    }
}
