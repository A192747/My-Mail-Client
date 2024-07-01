package ru.petr.miniapp.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.petr.miniapp.model.MailMessage;
import ru.petr.miniapp.model.UserMails;
import ru.petr.miniapp.util.HttpRequestsBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailParserService {

    private final HttpRequestsBuilder requestsBuilder;
    @Value("${mail.url}")
    private String MAIL_URL;

    private final HttpClient httpClient;

    public UserMails getNewMails(Date lastMessageDate, String page) throws ParseException {
        Document document = Jsoup.parse(page);
        Map<Date, String> datesAuthorsMap = getDatesAuthorsMap(document, lastMessageDate);
        log.info("Count = " + datesAuthorsMap.size());
        UserMails userMails = new UserMails();
        if (!datesAuthorsMap.isEmpty()) {
            userMails.setUserMessages(getMails(document, datesAuthorsMap));
            return userMails;
        }
        return null;
    }

    private Map<Date, String> getDatesAuthorsMap(Document document, Date lastMessageDate) throws ParseException {
        List<Date> dates = getListDatesNewMassages(getDates(document), lastMessageDate);
        Map<Date, String> map = new HashMap<>();
        List<String> authors = getAuthors(document);
        for(int i = 0; i < dates.size(); i++) {
            map.put(dates.get(i), authors.get(i));
        }
        return map;
    }

    private List<Date> getDates(Document document) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        List<String> datesDirty = document.getElementsByTag("td").stream()
                .filter(el1 -> el1.hasAttr("nowrap") &&
                        !el1.childNodes().isEmpty() &&
                        el1.childNode(0).toString().matches("\\d{2}\\.\\d{2}\\.\\d{4}&nbsp;\\d{2}:\\d{2}&nbsp;"))
                .map(element -> element.childNode(0).toString())
                .toList();


        List<Date> dates = new ArrayList<>();
        for(String str : datesDirty) {
            dates.add(dateFormat.parse(formDate(str)));
        }

        return dates;
    }

    private List<String> getAuthors(Document document) {
        return  document.getElementsByTag("td").stream()
                .filter(el1 -> el1.hasAttr("nowrap") && !el1.childNodes().isEmpty() &&
                        !el1.childNode(0).toString().equals("&nbsp;") &&
                        !el1.childNode(0).toString().contains("КБ") &&
                        !el1.childNode(0).toString().contains("МБ") &&
                        !el1.childNode(0).toString().contains("Стр") &&
                        (el1.childNode(0).attributesSize() == 0 || (el1.childNode(0).attr("class").contains("frst"))) &&
                        !el1.toString().contains("h1") &&
                        !el1.childNode(0).toString().matches("\\d{2}\\.\\d{2}\\.\\d{4}&nbsp;\\d{2}:\\d{2}&nbsp;"))
                .map(ela -> formAuthor(ela.childNode(0).toString()))
                .toList();

    }


    private String formDate(String str) {
        String newStr = str.replace("&nbsp;", " ");
        return newStr.substring(0, newStr.length() - 1);
    }
    private String formAuthor(String str) {
        return str.replace("&nbsp;", "");
    }


    private List<Date> getListDatesNewMassages(List<Date> list, Date current) {
        List<Date> newMessages = new ArrayList<>();
        for(Date date: list) {
            if(date.toInstant().isAfter(current.toInstant())) {
                newMessages.add(date);
            } else {
                break;
            }
        }
        return newMessages;
    }

    private MailMessage getMessage(Element element) throws URISyntaxException, IOException, InterruptedException {
        MailMessage message = new MailMessage();

        HttpResponse<String> response = getMainAttr(element);
        Document document1 = Jsoup.parse(response.body());

        message.setTitle(getTitle(document1));
        message.setBody(getBody(document1));
        return message;
    }
    @SneakyThrows
    private List<MailMessage> getMails(Document document, Map<Date, String> datesNamesMap) {
        //Лист id сообщений
        List<Element> elements = document.getElementsByTag("input").stream().filter(elem -> elem.hasAttr("onclick") && elem.attr("name").equals("chkmsg")).toList();

        List<MailMessage> list = new ArrayList<>();
        int counter = 0;
        for (Map.Entry<Date, String> entry: datesNamesMap.entrySet()) {
            MailMessage message = getMessage(elements.get(counter));
            message.setDate(entry.getKey());
            message.setAuthor(entry.getValue());
            counter++;
            list.add(message);
        }
        return list;

    }


    private HttpResponse<String> getMainAttr(Element element) throws URISyntaxException, IOException, InterruptedException {
        return httpClient.send(
                requestsBuilder.getHttpRequest(MAIL_URL + "owa/?ae=Item&t=IPM.Note&a=Open&s=Draft&id=" + URLEncoder.encode(element.attr("value"))),
                HttpResponse.BodyHandlers.ofString()
        );
    }
    private String getTitle(Document response) {
        return response.getElementsByTag("input").stream().filter(eleee -> eleee.attr("id").equals("txtsbjldr")).toList().get(0).val();
    }

    private String getBody(Document response) {
        return response.getElementsByTag("textarea").stream().filter(elee -> !elee.hasAttr("name")).toList().get(0).val();
    }

}
