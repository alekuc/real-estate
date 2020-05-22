package com.real.estate.alekue.app.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Entity
@Getter
@Setter
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int price;
    private int pricePerSquare;
    private String buildingNumber;
    private String apartmentNumber;
    private BigDecimal area;
    private int numberOfRooms;
    private int floor;
    private int totalBuildingFloors;
    private int year;
    private String buildingType;
    private String heatingSystem;
    private String layout;
    private String city;
    private String district;
    private String street;
    private String fullAddress;
    private String source;
    private LocalDate dateTime;
    private String url;
    private int pageNumber;
    private LocalDateTime timestamp;

}
