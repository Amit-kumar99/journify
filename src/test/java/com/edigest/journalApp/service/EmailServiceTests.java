package com.edigest.journalApp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTests {

    @Autowired
    private  EmailService emailService;

    @Test
    public void testSendEmail() {
        emailService.sendEmail("1941012571.n.amitkumar@gmail.com",
                "testing java mail sender",
                "Hello world");
    }

}
