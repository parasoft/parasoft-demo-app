package com.parasoft.demoapp.grpc.util;

import com.google.gson.Gson;
import io.grpc.MethodDescriptor.Marshaller;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
public class Marshallers {
    public static <T> Marshaller<T> marshallerFor(Class<T> clz) {
        Gson gson = new Gson();
        return new Marshaller<T>() {
            @Override
            public InputStream stream(T value) {
                return new ByteArrayInputStream(gson.toJson(value, clz).getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public T parse(InputStream stream) {
                T t = null;
                try {
                    t = gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), clz);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                return t;
            }
        };
    }
}
