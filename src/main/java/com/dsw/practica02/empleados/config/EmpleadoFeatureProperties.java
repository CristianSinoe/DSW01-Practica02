package com.dsw.practica02.empleados.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix = "empleados.pagination")
public class EmpleadoFeatureProperties {

    @Min(value = 1, message = "empleados.pagination.default-size debe ser mayor a 0")
    private int defaultSize = 10;

    @Min(value = 1, message = "empleados.pagination.max-size debe ser mayor a 0")
    private int maxSize = 100;

    @NotBlank(message = "empleados.pagination.default-sort es obligatorio")
    private String defaultSort = "email";

    public int getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(int defaultSize) {
        this.defaultSize = defaultSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getDefaultSort() {
        return defaultSort;
    }

    public void setDefaultSort(String defaultSort) {
        this.defaultSort = defaultSort;
    }
}