package com.ms.raspberry.controller;

import org.springframework.web.bind.annotation.*;


@RequestMapping(value = "/")
@RestController
public class HtmlController {

    @GetMapping("/html")
    public String getSpeedTestRecord() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.4/Chart.min.js\"></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<a href=\"/swagger-ui/\">GA NAAR SWAGGER</a>\n" +
                "<table>\n" +
                "    <thead>\n" +
                "        <tr>\n" +
                "            <th colspan=\"2\">The table header</th>\n" +
                "        </tr>\n" +
                "    </thead>\n" +
                "    <tbody>\n" +
                "        <tr>\n" +
                "            <td><canvas id=\"line-chart\" width=\"800\" height=\"450\"></canvas></td>\n" +
                "            <td>Tweede kolom</td>\n" +
                "        </tr>\n" +
                "    </tbody>\n" +
                "</table>\n" +
                "<script>\n" +
                "new Chart(document.getElementById(\"line-chart\"), {\n" +
                "  type: 'line',\n" +
                "  data: {\n" +
                "    labels: [1500,1600,1700,1750,1800,1850,1900,1950,1999,2050],\n" +
                "    datasets: [{ \n" +
                "        data: [86,114,106,106,107,111,133,221,783,2478],\n" +
                "        label: \"Africa\",\n" +
                "        borderColor: \"#3e95cd\",\n" +
                "        fill: false\n" +
                "      }, { \n" +
                "        data: [282,350,411,502,635,809,947,1402,3700,5267],\n" +
                "        label: \"Asia\",\n" +
                "        borderColor: \"#8e5ea2\",\n" +
                "        fill: false\n" +
                "      }, { \n" +
                "        data: [168,170,178,190,203,276,408,547,675,734],\n" +
                "        label: \"Europe\",\n" +
                "        borderColor: \"#3cba9f\",\n" +
                "        fill: false\n" +
                "      }, { \n" +
                "        data: [40,20,10,16,24,38,74,167,508,784],\n" +
                "        label: \"Latin America\",\n" +
                "        borderColor: \"#e8c3b9\",\n" +
                "        fill: false\n" +
                "      }, { \n" +
                "        data: [6,3,2,2,7,26,82,172,312,433],\n" +
                "        label: \"North America\",\n" +
                "        borderColor: \"#c45850\",\n" +
                "        fill: false\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  options: {\n" +
                "    title: {\n" +
                "      display: true,\n" +
                "      text: 'World population per region (in millions)'\n" +
                "    }\n" +
                "  }\n" +
                "});\n" +
                "</script>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
    }


}