package com.real.estate.alekue.app.utils;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class Utils {

    @SneakyThrows
    public static HtmlPage getPage(String url) {
        try (final WebClient webClient = new WebClient()) {
            webClient.setCssErrorHandler(new SilentCssErrorHandler());
            webClient.getOptions().setJavaScriptEnabled(false);
            return webClient.getPage(url);
        } catch (Exception ex) {
            log.error("Failed to get page for url {}. ", url, ex);
            log.info("Retrying in 60 seconds...");
            Thread.sleep(60 * 10_000);
            log.info("Retrying...");
            return getPage(url);
        }
    }

    public static String removeSymbols(String input) {
        return input.replaceAll("[^\\x20-\\x7e]|\\s+|€|/|[a-z]|[A-Z]|²|\\)|\\(", "");
    }

    public static int toAlioInt(String input) {
        Matcher matcher = Pattern.compile("\\d+").matcher(input);
        matcher.find();
        return Integer.parseInt(matcher.group());
    }

    public static int toInt(String input){
        return Integer.parseInt(removeSymbols(input));
    }

}
