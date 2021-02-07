package com.javarush.task.task28.task2810.model;

import com.javarush.task.task28.task2810.vo.Vacancy;
import org.jsoup.HttpStatusException;
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
                Document document = getDocument(searchString, page);
                Elements elements = document.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy");

                if (!elements.isEmpty()) {
                    for (Element element : elements) {
                        Vacancy vacancy = new Vacancy();
                        vacancies.add(vacancy);

                        Elements titles = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-title");
                        vacancy.setTitle(titles.get(0).text());

                        vacancy.setUrl(titles.get(0).attr("href"));

                        Elements companyNames = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-employer");
                        vacancy.setCompanyName(companyNames.get(0).text());

                        Elements cities = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-address");
                        vacancy.setCity(cities.get(0).text());

                        Elements salaries = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-compensation");
                        vacancy.setSalary(salaries.size() == 0 ? "" : salaries.get(0).text());

                        vacancy.setSiteName(URL_FORMAT);
                    }
                    page++;
                }
                else {
                    page = 0;
                    break;
                }
            }
        } catch (IOException e) {
            return vacancies;
        }
        return vacancies;
    }

    protected Document getDocument(String searchString, int page) throws IOException {
        return Jsoup.connect(String.format(URL_FORMAT, searchString, page))
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:85.0) Gecko/20100101 Firefox/85.0")
                .referrer("")
                .get();
    }
}
