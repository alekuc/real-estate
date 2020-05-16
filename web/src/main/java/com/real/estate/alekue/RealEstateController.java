package com.real.estate.alekue;

import com.real.estate.alekue.dao.Apartment;
import com.real.estate.alekue.dao.ApartmentRepository;
import com.real.estate.alekue.dao.PriceAverage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class RealEstateController {

    @Autowired
    private ApartmentRepository repository;

    @GetMapping({"real-estate", ""})
    public String realEstate(Model model) {
        model.addAttribute("averages", repository.getAverages());
        model.addAttribute("filter", new Filter());
        model.addAttribute("uniqueDistrict", repository.findUniqueDistrict());
        return "real-estate";
    }

    @PostMapping("real-estate")
    public String realEstateFormSubmit(@ModelAttribute Filter filter, Model model) {
        List<Apartment> apartments = repository.findAll(filter.toSpecification());

        //TODO: add returns by pricePerSquare or ttl price
        //TODO: show filtered dataset size
        List<PriceAverage> averages = getAverages(apartments);
        averages.sort(Comparator.comparing(PriceAverage::getDate));

        model.addAttribute("averages", averages);
        model.addAttribute("uniqueDistrict", repository.findUniqueDistrict());
        return "real-estate";
    }

    private List<PriceAverage> getAverages(List<Apartment> apartments){
        Map<LocalDate, List<Apartment>> apts = apartments.stream()
                .collect(Collectors.groupingBy(Apartment::getDateTime));

        List<PriceAverage> averages = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Apartment>> entry : apts.entrySet()) {
            int price = 0;
            for (Apartment apt : entry.getValue()) {
                price += apt.getPrice();
            }
            averages.add(new PriceAverage(price / entry.getValue().size(), entry.getKey()));
        }
        return averages;
    }

    private List<PriceAverage> getAveragesPerArea(List<Apartment> apartments){
        Map<LocalDate, List<Apartment>> apts = apartments.stream()
                .collect(Collectors.groupingBy(Apartment::getDateTime));

        List<PriceAverage> averages = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Apartment>> entry : apts.entrySet()) {
            int pricePerSquare = 0;
            for (Apartment apt : entry.getValue()) {
                pricePerSquare += apt.getPricePerSquare();
            }
            averages.add(new PriceAverage(pricePerSquare / entry.getValue().size(), entry.getKey()));
        }
        return averages;
    }

}
