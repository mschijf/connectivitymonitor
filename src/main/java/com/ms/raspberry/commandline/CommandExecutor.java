package com.ms.raspberry.commandline;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class CommandExecutor {
    public String execCommand(String cmd) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(cmd);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));


        String s;
        StringBuilder outputLines = new StringBuilder();
        while ((s = stdInput.readLine()) != null) {
            outputLines.append(s+"\n");
        }

        while ((s = stdError.readLine()) != null) {
            outputLines.append(s+"\n");
        }
        return outputLines.toString();
    }
}
