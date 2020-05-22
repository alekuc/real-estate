package com.real.estate.alekue;

import com.real.estate.alekue.app.dao.Apartment;
import com.real.estate.alekue.app.dao.ApartmentRepository;
import com.real.estate.alekue.app.providers.alio.AlioScheduledScrapper;
import com.real.estate.alekue.app.providers.aruodas.AruodasScheduledScrapper;
import com.real.estate.alekue.app.utils.Utils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

import static com.real.estate.alekue.app.providers.alio.AlioScheduledScrapper.INITIAL_URL;

@SpringBootTest
class RealEstateApplicationTests {

    @Autowired
    private AruodasScheduledScrapper scheduledScrapper;

    @Autowired
    private AlioScheduledScrapper alioScheduledScrapper;

    @Autowired
    private ApartmentRepository repository;

    @Test
    void contextLoads() {
        String url = "https://www.aruodas.lt/butai-vilniuje-bajoruose-bajoru-kel--balandzio-4-5-dienomis-kvieciame-i-1-2866233/";
        Apartment apartment = scheduledScrapper.extractApartment(url);
        System.out.println(apartment);
    }

    @Test
    public void testtt() {
        String result = Utils.removeSymbols("28,14 m²").replaceAll(",", ".");
        BigDecimal aaa = new BigDecimal(result);
        System.out.println(aaa);
    }

    @Test
    public void testExtractsAlioApart() {
        String url = "https://www.alio.lt/skelbimai/2-kamb-butas-vilnius-naujamiestis-algirdo-g/ID60821455.html";
        Apartment apartment = alioScheduledScrapper.extractApartment(url);
        System.out.println(apartment);
    }

    @Test
    public void testGetNextUrl() {
        alioScheduledScrapper.getNextUrl(Utils.getPage(INITIAL_URL));
    }

    @Test
    public void removeDuplicateUrls() {
        LocalDate date = LocalDate.now();
        Collection<Long> idents = repository.findMaxIdentsForDuplicateUrlsForDateTime(date);
        idents.forEach(repository::deleteById);
    }

}
