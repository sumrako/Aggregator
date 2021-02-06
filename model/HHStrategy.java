package com.javarush.task.task28.task2810.model;

import com.javarush.task.task28.task2810.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HHStrategy implements Strategy {
    private final static String URL_FORMAT = "https://grc.ua/search/vacancy?text=java+%s&page=%d";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> vacancies = new ArrayList<>(20);

        try {
            int page = 0;
            while (true) {
                Document document = getDocument("https://javarush.ru/testdata/big28data.html", page);
                Elements elements = document.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy");

                if (!elements.isEmpty()) {
                    for (Element element : elements) {
                        Elements titles = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-title");
                        String title = titles.get(0).text();

                        String url = titles.get(0).attr("href");

                        Elements companyNames = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-employer");
                        String companyName = companyNames.get(0).text();

                        Elements cities = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-address");
                        String city = cities.get(0).text();

                        Elements salaries = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-compensation");
                        String salary = salaries.size() == 0 ? "" : salaries.get(0).text();

                        String siteName = "http://hh.ua";

                        vacancies.add(new Vacancy(title, salary, city, companyName, siteName, url));
                    }
                }
                page++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vacancies;
    }

    protected Document getDocument(String searchString, int page) throws IOException {
        return Jsoup.connect(searchString) //String.format(URL_FORMAT, searchString, page))
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:85.0) Gecko/20100101 Firefox/85.0")
                .referrer("")
                .get();
    }
}
