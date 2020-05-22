package com.real.estate.alekue.app.providers.alio;

import com.real.estate.alekue.app.dao.Apartment;
import com.real.estate.alekue.app.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.real.estate.alekue.app.utils.Utils.toAlioInt;

public class AlioMapper {

    private Apartment apartment;

    public AlioMapper(Apartment apartment) {
        this.apartment = apartment;
    }

    public void setProperty(String name, String value) {
        String strippedName = name.strip();
        if (strippedName.equalsIgnoreCase("Adresas")) {
            extractAddress(value);
        } else if (strippedName.equalsIgnoreCase("Kambarių skaičius")) {
            apartment.setNumberOfRooms(Integer.parseInt(value));
        } else if (strippedName.equalsIgnoreCase("Būsto būklė")) {
            apartment.setLayout(value);
        } else if (strippedName.equalsIgnoreCase("Šildymas")) {
            apartment.setHeatingSystem(value);
        } else if (strippedName.equalsIgnoreCase("Buto aukštas")) {
            apartment.setFloor(Integer.parseInt(value));
        } else if (strippedName.equalsIgnoreCase("Aukštų skaičius pastate")) {
            apartment.setTotalBuildingFloors(Integer.parseInt(value));
        } else if (strippedName.equalsIgnoreCase("Statybos metai")) {
            apartment.setYear(Integer.parseInt(value));
        } else if (strippedName.equalsIgnoreCase("Namo tipas")) {
            apartment.setBuildingType(value);
        } else if (strippedName.equalsIgnoreCase("Būsto plotas")) {
            apartment.setArea(new BigDecimal(Utils.removeSymbols(value).replaceAll(",", ".")));
        } else if (strippedName.equalsIgnoreCase("Kaina, €")) {
            apartment.setPrice(toAlioInt(value));
        } else if (strippedName.equalsIgnoreCase("Kaina, €/m²")) {
            apartment.setPricePerSquare(toAlioInt(value));
        } else if (strippedName.equalsIgnoreCase("Namo numeris")) {
            apartment.setBuildingNumber(value.strip());
        } else if (strippedName.equalsIgnoreCase("Buto numeris")) {
            apartment.setApartmentNumber(value.strip());
        }
    }

    private void extractAddress(String address) {
        String[] result = address.split(",");
        if (result.length > 0) {
            apartment.setCity(result[0]);
        }
        if (result.length > 1) {
            apartment.setDistrict(result[1]);
        }
        if (result.length > 2) {
            apartment.setStreet(result[2]);
        }
    }

    public Apartment getApartment() {
        apartment.setFullAddress(formatAddress());
        apartment.setSource("ALIO");
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
