package com.root.mailbox.domain.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class CopyClassProperties<C> {

    public void copyNonNull(C source, C target) {
        BeanWrapper sourceBean = new BeanWrapperImpl(source);
        BeanWrapper targetBean = new BeanWrapperImpl(target);
        List<String> nonMutableProps = List.of("class", "id", "createdAt");

        List<PropertyDescriptor> properties = Stream.of(sourceBean.getPropertyDescriptors()).filter(prop ->
            !nonMutableProps.contains(prop.getName())
        ).toList();

        properties.forEach(property -> {
            String propName = property.getName();
            Object propValue = sourceBean.getPropertyValue(propName);

            if (Objects.isNull(propValue)) return;

            targetBean.setPropertyValue(propName, propValue);
        });
    }
}
