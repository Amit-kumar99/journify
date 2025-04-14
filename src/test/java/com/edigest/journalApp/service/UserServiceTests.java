package com.edigest.journalApp.service;

import com.edigest.journalApp.entity.User;
import com.edigest.journalApp.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    @Disabled
    @ParameterizedTest
    @ValueSource(strings = {
            "Gaurav-11",
            "Pandey-11",
            "Rajesh-11"
    })
    public void testFindByUsername(String username) {
        assertNotNull(userRepository.findByUsername(username), "failed for " + username);
    }

    @ParameterizedTest
    @ArgumentsSource(UserArgumentsProvider.class)
    public void testSaveNewUser(User user) {
        assertTrue(userService.saveNewUser(user));
    }


//    @ParameterizedTest
//    @CsvSource({
//            "3, 2, 3",
//            "9, 4, 5",
//            "16, 7, 9"
//    })
//    public void testSum(int sum, int num1, int num2) {
//        assertEquals(sum, num1 + num2);
//    }

}
