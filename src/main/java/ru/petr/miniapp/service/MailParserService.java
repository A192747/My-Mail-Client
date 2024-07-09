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

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailParserService {

    private final HttpRequestsBuilder requestsBuilder;
    @Value("${mail.url}")
    private String MAIL_URL;

    private final HttpClient httpClient;

    public UserMails getNewMails(Date lastMessageDate, String page) {
        Document document = Jsoup.parse(page);

        List<Element> mails = getUnreadMailTrs(document, lastMessageDate);

        UserMails userMails = new UserMails();
        userMails.setUserMessages(getMails(mails));

        return userMails;
    }


    public List<Element> getUnreadMailTrs(Document document, Date lastMessageDate) {
        List<Element> elements = document.getElementsByTag("tr").stream()
                .filter(element -> element.hasAttr("style") && element.attr("style").equals("font-weight:bold;"))
                .toList();

        List<Element> result = new ArrayList<>();
        for (Element el : elements) {
            boolean add = el.childNodes().stream()
                    .filter(elem -> elem.attr("class").contains("sc"))
                    .allMatch(elem -> formDate(elem.childNodes().get(0).toString()).after(lastMessageDate));
            if (add) {
                result.add(el);
            }
        }

        return result;
    }


    @SneakyThrows
    private Date getDate(Element element) {
        return formDate(element.getElementsByTag("td").stream()
                .filter(el1 -> el1.hasAttr("nowrap") &&
                        !el1.childNodes().isEmpty() &&
                        el1.childNode(0).toString().matches("\\d{2}\\.\\d{2}\\.\\d{4}&nbsp;\\d{2}:\\d{2}&nbsp;"))
                .map(element1 -> element1.childNode(0).toString())
                .toList().get(0));
    }

    private String getAuthor(Element element) {
        return element.getElementsByTag("td").stream()
                .filter(el1 -> el1.hasAttr("nowrap") && !el1.childNodes().isEmpty() &&
                        !el1.childNode(0).toString().equals("&nbsp;") &&
                        !el1.childNode(0).toString().contains("КБ") &&
                        !el1.childNode(0).toString().contains("МБ") &&
                        !el1.childNode(0).toString().contains("Стр") &&
                        (el1.childNode(0).attributesSize() == 0 || (el1.childNode(0).attr("class").contains("frst"))) &&
                        !el1.toString().contains("h1") &&
                        !el1.childNode(0).toString().matches("\\d{2}\\.\\d{2}\\.\\d{4}&nbsp;\\d{2}:\\d{2}&nbsp;"))
                .map(ela -> formAuthor(ela.childNode(0).toString()))
                .toList().get(0);

    }

    @SneakyThrows
    private Date formDate(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String newStr = str.replace("&nbsp;", " ");
        return dateFormat.parse(newStr.substring(0, newStr.length() - 1));
    }

    private String formAuthor(String str) {
        return str.replace("&nbsp;", "");
    }


    @SneakyThrows
    private List<MailMessage> getMails(List<Element> mails) {

        List<MailMessage> list = new ArrayList<>();
        for (Element element : mails) {
            list.add(formMail(element));
        }
        return list;
    }

    private MailMessage formMail(Element mail) {
        MailMessage msg = new MailMessage();
        msg.setAuthor(getAuthor(mail));
        msg.setDate(getDate(mail));

        Document document = Jsoup.parse(getMainAttr(mail).body());

        msg.setTitle(getTitle(document));
        msg.setBody(getBody(document));
        return msg;
    }


    @SneakyThrows
    private HttpResponse<String> getMainAttr(Element element) {
        String mailId = getMailId(element);
        return httpClient.send(
                requestsBuilder.getHttpRequest(MAIL_URL + "owa/?ae=Item&t=IPM.Note&a=Open&s=Draft&id=" + URLEncoder.encode(mailId)),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private String getMailId(Element element) {
        return element.getElementsByTag("input").stream().filter(elem -> elem.hasAttr("onclick")
                && elem.attr("name").equals("chkmsg")).toList().get(0).attr("value");
    }

    private String getTitle(Document response) {
        return response.getElementsByTag("input").stream().filter(eleee -> eleee.attr("id").equals("txtsbjldr")).toList().get(0).val();
    }

    private String getBody(Document response) {
        return response.getElementsByTag("textarea").stream().filter(elee -> !elee.hasAttr("name")).toList().get(0).val();
    }

}
