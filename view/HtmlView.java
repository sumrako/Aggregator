package com.javarush.task.task28.task2810.view;

import com.javarush.task.task28.task2810.Controller;
import com.javarush.task.task28.task2810.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.List;

public class HtmlView implements View {
    private Controller controller;
    private final String filePath ="./4.JavaCollections/src/" + this.getClass().getPackage().getName().replace('.', '/') + "/vacancies.html";

    @Override
    public void update(List<Vacancy> vacancies) {
        try {
            System.out.println(filePath);
            String content = getUpdatedFileContent(vacancies);
            updateFile(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void userCitySelectEmulationMethod() {
        controller.onCitySelect("Odessa");
    }

    private String getUpdatedFileContent(List<Vacancy> vacancies) {
        try {
            Document document = getDocument();
            Elements elements = document.getElementsByClass("template");
            Element template = elements.get(0).clone();
            template.removeAttr("style");
            template.removeClass("template");

            document.getElementsByTag("tr").removeIf(element -> {
                String className = element.className();
                return className.equals("vacancy");
            });


            for (Vacancy vacancy : vacancies) {
                Element copyTemplate = template.clone();

                Element city = copyTemplate.getElementsByClass("city").get(0);
                city.appendText(vacancy.getCity());

                Element companyName = copyTemplate.getElementsByClass("companyName").get(0);
                companyName.appendText(vacancy.getCompanyName());

                Element salary = copyTemplate.getElementsByClass("salary").get(0);
                salary.appendText(vacancy.getSalary());

                Element title = copyTemplate.getElementsByTag("a").get(0);
                title.appendText(vacancy.getTitle());
                title.attr("href", vacancy.getUrl());

                template.before(copyTemplate.outerHtml());
            }

            return document.html();
        } catch (IOException e) {
            e.printStackTrace();
            return "Some exception occurred";
        }
    }

    private void updateFile(String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Document getDocument() throws IOException {
        try (InputStream inputStream = new FileInputStream(filePath)){
            return Jsoup.parse(inputStream, "UTF-8", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
