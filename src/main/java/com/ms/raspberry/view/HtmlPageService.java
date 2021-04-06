package com.ms.raspberry.view;

import com.ms.raspberry.entity.PingSummary;
import com.ms.raspberry.service.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class HtmlPageService {
    private static final String pageTemplate =
            "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.4/Chart.min.js\"></script>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<a href=\"/swagger-ui/\">GA NAAR SWAGGER</a>\n" +
                    "<table>\n" +
                    "    <thead>\n" +
                    "        <tr>\n" +
                    "            <th colspan=\"2\">Ping statistics</th>\n" +
                    "        </tr>\n" +
                    "    </thead>\n" +
                    "    <tbody>\n" +
                    "        <tr>\n" +
                    "            <td>%s</td>\n" +
                    "            <td>Ruimte voor tweede grafiek</td>\n" +
                    "        </tr>\n" +
                    "    </tbody>\n" +
                    "</table>\n" +
                    "<script>\n" +
                    "%s" +
                    "</script>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";

    @Autowired
    private PingService pingService;

    public String getPage() {

        Collection<PingSummary> summary = pingService.getPingSummary();
        ArrayList<Integer>data = summary.stream()
                .map(s->s.getMaxTimeMillis())
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> labels = summary.stream()
                .map(s->s.getFromDate().toString())
                .collect(Collectors.toCollection(ArrayList::new));

        Chart chart = Chart.newBuilder()
                .setIdentifier("graphMaxTime")
                .setLabels(labels)
                .setData(data)
                .build();
        return String.format(pageTemplate,
                chart.getHtml(),
                chart.getJs());
    }

}
