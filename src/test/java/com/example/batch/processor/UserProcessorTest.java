package com.example.batch.processor;

import com.example.batch.model.UserRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserProcessorTest {

    private final UserProcessor processor = new UserProcessor();

    @Test
    void transformsAValidUser() {
        UserRecord user = user(" alice ", " 10001 ", "ALICE@EXAMPLE.COM ");

        UserRecord result = processor.process(user);

        assertThat(result).isSameAs(user);
        assertThat(result.getFirstName()).isEqualTo("ALICE");
        assertThat(result.getZipcode()).isEqualTo("10001");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void filtersAnInvalidEmail() {
        assertThat(processor.process(user("Alice", "10001", "alice.example.com"))).isNull();
    }

    @Test
    void filtersAnInvalidZipcode() {
        assertThat(processor.process(user("Alice", "!", "alice@example.com"))).isNull();
    }

    private UserRecord user(String firstName, String zipcode, String email) {
        UserRecord user = new UserRecord();
        user.setFirstName(firstName);
        user.setLastName("Johnson");
        user.setZipcode(zipcode);
        user.setEmail(email);
        return user;
    }
}
