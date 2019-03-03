package com.agoda.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;

@Value
@Builder
@AllArgsConstructor
public class ApplicationArguments {
    private String folderPath;

    private Collection<String> resources;

    private String ignoreWrongResource;
}
