package com.real.estate.alekue.app.providers.alio;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.real.estate.alekue.app.dao.Apartment;
import com.real.estate.alekue.app.dao.ApartmentRepository;
import com.real.estate.alekue.app.utils.Utils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Log4j2
@Component
public class AlioScheduledScrapper {

    public static final String INITIAL_URL = "https://www.alio.lt/paieska/vilnius/?category_id=1373";
    private static final String PAGE_URL_PATTERN = "https://www.alio.lt/paieska/vilnius/" + "/?category_id=1373";
    private int pageNumber = 1;

    @Autowired
    private ApartmentRepository repository;

    @SneakyThrows
//    @Scheduled(initialDelay = 5_000, fixedDelay = Long.MAX_VALUE)
    public void start2() {
        log.info("ALIO: schedule started");
        String url = INITIAL_URL;
        while (url != null) {
            Thread.sleep(5_000);
            HtmlPage page = Utils.getPage(url);
            pollApartments(page, url);
            url = getNextUrl(page);
            pageNumber++;
        }
    }

    public String getNextUrl(HtmlPage page) {
        DomNode node = page.querySelector(".next_page").querySelector("a");
        if (node instanceof HtmlAnchor) {
            return ((HtmlAnchor) node).getAttribute("href");
        } else {
            return null;
        }
    }

    private void pollApartments(HtmlPage page, String url) {
        //Skip first, as it appears on every page
        pollApartments(page, url, 1);
    }

    private void pollApartments(HtmlPage page, String url, int skip) {
        page.querySelectorAll(".desc_m_a_b").stream()
                .skip(skip)
                .map(this::getHref)
                .flatMap(Optional::stream)
                .map(aptUrl -> safeExtractApartment(url, aptUrl))
                .filter(Objects::nonNull)
                .forEach(repository::save);
    }

    private Optional<String> getHref(DomNode node) {
        return Optional.ofNullable(node.querySelector("a"))
                .map(HtmlAnchor.class::cast)
                .map(a -> a.getAttribute("href"));

    }

    private Apartment safeExtractApartment(String pageUrl, String aptUrl) {
        log.info("ALIO:Trying to extract ALIO.LT apartment from page {} and aptUrl {}", pageUrl, aptUrl);
        try {
            Apartment apartment = extractApartment(aptUrl);
            return apartment;
        } catch (RuntimeException ex) {
            log.error("ALIO:Failed to extract apartment for url {} in page {}", aptUrl, pageUrl, ex);
            return null;
        }
    }

    @SneakyThrows
    public Apartment extractApartment(String aptUrl) {
        Thread.sleep(5_000);
        AlioMapper mapper = getMapper(aptUrl);
        HtmlPage page = Utils.getPage(aptUrl);
        DomNodeList<DomNode> listAttributes = page.querySelector(".addit_i").querySelectorAll(".data_moreinfo_b");

        for (DomNode node : listAttributes) {
            String key = node.querySelector(".a_line_key").getVisibleText();
            String value = node.querySelector(".a_line_val").getVisibleText();
            try {
                mapper.setProperty(key, value);
            } catch (RuntimeException ex) {
                log.error("ALIO:Failed to parse record {} for property name {} value {} url {}", mapper, key, value, aptUrl, ex);
            }
        }
        return mapper.getApartment();
    }

    private AlioMapper getMapper(String aptUrl) {
        Apartment apartment = new Apartment();
        apartment.setUrl(aptUrl);
        apartment.setPageNumber(pageNumber);
        return new AlioMapper(apartment);
    }

}
