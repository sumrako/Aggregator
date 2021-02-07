package com.javarush.task.task28.task2810.model;

import com.javarush.task.task28.task2810.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerStrategy implements Strategy {
    private final static String URL_FORMAT = "https://career.habr.com/vacancies?page=%d&q=java+%s&type=all";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> vacancies = new ArrayList<>(20);

        try {
            int page = 1;
            while (true) {
                Document document = getDocument(searchString, page);
                Elements elements = document.getElementsByClass("vacancy-card");

                if (!elements.isEmpty()) {
                    for (Element element : elements) {
                        Vacancy vacancy = new Vacancy();
                        vacancies.add(vacancy);

                        Elements titles = element.getElementsByClass("vacancy-card__title");
                        vacancy.setTitle(titles.get(0).text());

                        vacancy.setUrl(titles.get(0).attr("href"));

                        Elements companyNames = element.getElementsByClass("vacancy-card__company");
                        vacancy.setCompanyName(companyNames.get(0).text());

                        Elements cities = element.getElementsByClass("link-comp link-comp--appearance-dark");
                        vacancy.setCity(cities.get(0).text());

                        Elements salaries = element.getElementsByClass("vacancy-card__salary");
                        vacancy.setSalary(salaries.size() == 0 ? "" : salaries.get(0).text());

                        vacancy.setSiteName(URL_FORMAT);
                    }
                    page++;
                }
                else {
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
                .referrer("https://career.habr.com/")
                .get();
    }
}
