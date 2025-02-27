package com.myprj.sample.api.masking;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * 마스킹 어노테이션을 이해하는 JSON 직렬화기
 */
public class MaskingSerializer extends StdSerializer<String> implements ContextualSerializer {

    private MaskingType type;

    public MaskingSerializer() { super(String.class); }

    public MaskingSerializer(MaskingType type) {
        super(String.class);
        this.type = type;
    }


    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty beanProperty) throws JsonMappingException {

        MaskingType type = null;
        Masking masking = null;
        if(beanProperty != null){
            masking = beanProperty.getAnnotation(Masking.class);
        }
        if(masking != null){
            type = masking.value();
        }

        return new MaskingSerializer(type);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if(MaskingContextHandler.shouldMask()){
            gen.writeString(MaskingUtils.mask(value,type));
        } else {
            gen.writeString(value);
        }

    }
}
