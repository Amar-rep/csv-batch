package com.example.batch.processor;

import com.example.batch.model.UserRecord;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class UserProcessor implements ItemProcessor<UserRecord, UserRecord> {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ZIP_PATTERN = Pattern.compile("^[A-Z0-9][A-Z0-9 -]{2,9}$", Pattern.CASE_INSENSITIVE);

    @Override
    public UserRecord process(UserRecord user) {
        if (user == null || !isValidEmail(user.getEmail()) || !isValidZipcode(user.getZipcode())) {
            return null;
        }

        user.setFirstName(trimAndUppercase(user.getFirstName()));
        user.setLastName(trim(user.getLastName()));
        user.setEmail(trim(user.getEmail()).toLowerCase(Locale.ROOT));
        user.setZipcode(trim(user.getZipcode()).toUpperCase(Locale.ROOT));
        user.setPhone1(trim(user.getPhone1()));
        user.setPhone2(trim(user.getPhone2()));
        user.setWeb(trim(user.getWeb()));
        return user;
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    private boolean isValidZipcode(String zipcode) {
        return zipcode != null && ZIP_PATTERN.matcher(zipcode.trim()).matches();
    }

    private String trimAndUppercase(String value) {
        String trimmed = trim(value);
        return trimmed == null ? null : trimmed.toUpperCase(Locale.ROOT);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
