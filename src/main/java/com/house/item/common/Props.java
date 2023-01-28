package com.house.item.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "properties")
@ConstructorBinding
@Getter
@AllArgsConstructor
public class Props {
    private Dir dir;

    @Getter
    @AllArgsConstructor
    public static class Dir {
        private String photo;
    }
}
