package com.vertiq.portfolio.service.v1.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class InstrumentId implements Serializable {
    private String isin;
    private String exchange;
}
