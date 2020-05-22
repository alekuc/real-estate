package com.real.estate.alekue.app.providers.aruodas;

import com.real.estate.alekue.app.dao.Apartment;
import com.real.estate.alekue.app.utils.Utils;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
public class AruodasMapper {

    private Apartment apartment;

    public AruodasMapper(Apartment apartment) {
        this.apartment = apartment;
    }

    public void setProperty(String name, String value) {
        String strippedName = name.strip();
        if (strippedName.equalsIgnoreCase("Namo numeris:")) {
            apartment.setBuildingNumber(value);
        } else if (strippedName.equalsIgnoreCase("Buto numeris:")) {
            apartment.setApartmentNumber(value);
        } else if (strippedName.equalsIgnoreCase("Plotas:")) {
            apartment.setArea(new BigDecimal(Utils.removeSymbols(value).replaceAll(",", ".")));
        } else if (strippedName.equalsIgnoreCase("Kambarių sk.:")) {
            apartment.setNumberOfRooms(Integer.parseInt(value));
        } else if (strippedName.equalsIgnoreCase("Aukštas:")) {
            apartment.setFloor(Integer.parseInt(value));
        } else if (strippedName.equalsIgnoreCase("Aukštų sk.:")) {
            apartment.setTotalBuildingFloors(Integer.parseInt(value));
        } else if (strippedName.equalsIgnoreCase("Metai:")) {
            if (value.contains(" ")) {
                value = value.substring(0, value.indexOf(" "));
            }
            apartment.setYear(Integer.parseInt(value));
        } else if (strippedName.equalsIgnoreCase("Pastato tipas:")) {
            apartment.setBuildingType(value);
        } else if (strippedName.equalsIgnoreCase("Šildymas:")) {
            apartment.setHeatingSystem(value);
        } else if (strippedName.equalsIgnoreCase("Įrengimas:")) {
            apartment.setLayout(value.substring(0, value.indexOf(" NAUDINGA")));
        }
    }

    public Apartment getApartment() {
        apartment.setFullAddress(formatAddress());
        apartment.setSource("ARUODAS");
        apartment.setTimestamp(LocalDateTime.now());
        apartment.setDateTime(LocalDate.now());
        return apartment;
    }

    private String formatAddress() {
        String fullAddress = apartment.getCity() + ", " + apartment.getDistrict() + ", " + apartment.getStreet();
        if (StringUtils.isNotEmpty(apartment.getBuildingNumber())) {
            fullAddress += " " + apartment.getBuildingNumber();
            if (StringUtils.isNotEmpty(apartment.getApartmentNumber())) {
                fullAddress += " - " + apartment.getApartmentNumber();
            }
        }
        return fullAddress;
    }

}
