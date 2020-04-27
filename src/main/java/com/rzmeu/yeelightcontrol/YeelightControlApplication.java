package com.rzmeu.yeelightcontrol;

import com.rzmeu.yeelightcontrol.service.ControlService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@AllArgsConstructor
@SpringBootApplication
public class YeelightControlApplication implements CommandLineRunner {

    private final ControlService controlService;

    public static void main(String[] args) {
        SpringApplication.run(YeelightControlApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        controlService.executeCommand();
    }
}
