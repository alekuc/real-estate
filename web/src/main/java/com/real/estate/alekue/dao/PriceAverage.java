package com.real.estate.alekue.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@ToString
public class PriceAverage {

    private int average;
    private LocalDate date;

}
