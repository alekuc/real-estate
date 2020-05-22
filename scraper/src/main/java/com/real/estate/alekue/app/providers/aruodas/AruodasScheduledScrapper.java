package com.real.estate.alekue.app.providers.aruodas;

import com.gargoylesoftware.htmlunit.html.*;
import com.real.estate.alekue.app.dao.Apartment;
import com.real.estate.alekue.app.dao.ApartmentRepository;
import com.real.estate.alekue.app.utils.Utils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.real.estate.alekue.app.utils.Utils.toInt;

@Log4j2
@Component
public class AruodasScheduledScrapper {

    private static final String BASE_URL = "https://www.aruodas.lt";
    private static final String PAGING_PATTERN = BASE_URL + "/butai/vilniuje/puslapis/%d/";

    private static final String HREF_ATTRIBUTE = "href";
    private static final String TOTAL_PAGE_NAVIGATION_CLASS = ".pagination";
    private static final String PAGE_REFS_CLASS = ".page-bt";
    private int pageNumber;

    @Autowired
    private ApartmentRepository repository;

    @SneakyThrows
    @Scheduled(initialDelay = 5_000, fixedDelay = Long.MAX_VALUE)
    public void start() {
        log.info("ARUODAS: schedule started");
        pageNumber = 1;
        String url = getPageUrl(1);
        HtmlPage page = Utils.getPage(url);
        int totalPages = extractTotalPages(page);
        pollApartments(page, url, 0);

        for (int i = 2; i <= totalPages; i++) {
            pageNumber = i;
            log.info("ARUODAS:Extract apartments from page {}", i);
            String nextUrl = getPageUrl(i);
            HtmlPage nextPage = Utils.getPage(nextUrl);
            pollApartments(nextPage, nextUrl, 0);
        }
        log.info("ARUODAS:Extracted.");

        log.info("Removing duplicated ids");
        Collection<Long> idents = repository.findMaxIdentsForDuplicateUrlsForDateTime(LocalDate.now());
        idents.forEach(repository::deleteById);
        log.info("Finished");
    }

    @SneakyThrows
//    @Scheduled(initialDelay = 5_000, fixedDelay = Long.MAX_VALUE)
    public void continueFromFailure() {
        pageNumber = 80;
        int skip = 7;
        int totalPages = 109;
        String url = getPageUrl(pageNumber);
        HtmlPage page = Utils.getPage(url);
        pollApartments(page, url, skip);

        for (int i = 81; i <= totalPages; i++) {
            pageNumber = i;
            log.info("ARUODAS:Extract apartments from page {}", i);
            String nextUrl = getPageUrl(i);
            HtmlPage nextPage = Utils.getPage(nextUrl);
            pollApartments(nextPage, nextUrl, 0);
        }
        log.info("ARUODAS:Extracted.");
    }

    private void pollApartments(HtmlPage page, String url, int skip) {
        log.info("ARUODAS:Trying to poll apartments for page {}", url);
        HtmlTable table = page.querySelector(".list-search");
        table.getRows().stream()
                .skip(skip)
                .map(this::getHref)
                .flatMap(Optional::stream)
                .map(this::addBaseUrl)
                .map(aptUrl -> safeExtractApartment(url, aptUrl))
                .filter(Objects::nonNull)
                .forEach(repository::save);
    }

    private Apartment safeExtractApartment(String pageUrl, String aptUrl) {
        log.info("ARUODAS:Trying to extract apartment from page {} and aptUrl {}", pageUrl, aptUrl);
        try {
            return extractApartment(aptUrl);
        } catch (RuntimeException ex) {
            log.error("ARUODAS:Failed to extract apartment for url {} in page {}", aptUrl, pageUrl, ex);
            return null;
        }
    }

    @SneakyThrows
    public Apartment extractApartment(String url) {
        Thread.sleep(1_000);
        HtmlPage page = Utils.getPage(url);
        HtmlDivision content = page.querySelector(".obj-cont");
        AruodasMapper mapper = extractBase(content, url);

        HtmlDefinitionList htmlList = content.querySelector(".obj-details");

        String name = "";
        String value = "";
        for (DomNode node : htmlList.getChildNodes()) {
            if (node instanceof HtmlDefinitionTerm) {
                name = node.getVisibleText();
            } else if (node instanceof HtmlDefinitionDescription) {
                value = node.getVisibleText();
                setProperty(mapper, name, value, url);
            } else if (node instanceof HtmlDivision) {
                break;
            }
        }


        return mapper.getApartment();
    }

    private AruodasMapper extractBase(HtmlDivision content, String url) {
        String headerText = content.querySelector(".obj-header-text").getVisibleText();
        String[] headerDetails = headerText.split(",");
        String city = headerDetails[0];
        String distinct = headerDetails[1];
        if (distinct.length() > 1) {
            distinct = distinct.substring(1);
        }
        String street = headerDetails[2];

        int price = extractPrice(content);
        int pricePerSquare = extractPricePerSquare(content);

        Apartment apartment = new Apartment();
        apartment.setCity(city);
        apartment.setDistrict(distinct);
        apartment.setStreet(street);
        apartment.setPrice(price);
        apartment.setPricePerSquare(pricePerSquare);
        apartment.setUrl(url);
        apartment.setPageNumber(pageNumber);
        return new AruodasMapper(apartment);
    }

    private void setProperty(AruodasMapper mapper, String name, String value, String url) {
        try {
            mapper.setProperty(name, value);
        } catch (RuntimeException ex) {
            log.error("ARUODAS:Failed to parse record {} for property name {} value {} url {}", mapper, name, value, url, ex);
        }
    }

    private int extractPrice(HtmlDivision content) {
        return toInt(content.querySelector(".price-eur").getVisibleText());
    }

    private int extractPricePerSquare(HtmlDivision content) {
        return toInt(content.querySelector(".price-per").getVisibleText());
    }

    protected int extractTotalPages(HtmlPage page) {
        List<DomNode> anchors = page.querySelector(TOTAL_PAGE_NAVIGATION_CLASS).querySelectorAll(PAGE_REFS_CLASS);
        HtmlAnchor lastPageNode = ((HtmlAnchor) anchors.get(anchors.size() - 2));
        String lastPageLink = lastPageNode.getAttribute(HREF_ATTRIBUTE);

        log.info("ARUODAS:Trying to resolve total pages for href " + lastPageLink);
        return Integer.parseInt(lastPageLink.replaceAll("[^-?0-9]+", ""));
    }

    private Optional<String> getHref(HtmlTableRow row) {
        return Optional.ofNullable(row.querySelector(".list-photo"))
                .map(e -> ((DomNode) e).querySelector("a"))
                .map(HtmlAnchor.class::cast)
                .map(a -> a.getAttribute("href"));
    }

    private String addBaseUrl(String productUrl) {
        return BASE_URL + productUrl;
    }

    private String getPageUrl(int i) {
        return String.format(PAGING_PATTERN, i);
    }

}
