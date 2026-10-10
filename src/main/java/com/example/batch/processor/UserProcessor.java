package com.example.batch.processor;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.batch.Exception.InvalidRecordException;
import com.example.batch.model.UserRecord;

@Component
public class UserProcessor implements ItemProcessor<UserRecord, UserRecord> {

	@Override
	public UserRecord process(UserRecord user) {

		if (user == null) {
			throw new InvalidRecordException("UserRecord Null error");
		}

		if (!isValidEmail(user.getEmail())) {
			throw new InvalidRecordException("Invalid email");
		}
		if (!isValidSalary(user.getSalary())) {
			throw new InvalidRecordException("Invalid salary info");
		}

		if (!isValidName(user.getFirstName(), user.getLastName())) {
			throw new InvalidRecordException("invalid name");
		}

		user.setFirstName(trimAndUppercase(user.getFirstName()));
		user.setLastName(trim(user.getLastName()));
		user.setEmail(trim(user.getEmail()).toLowerCase());
		return user;
	}

	private boolean isValidName(String FirstName, String LastName) {
		return FirstName != null && LastName != null;
	}

	private boolean isValidEmail(String email) {
		return email != null && EmailValidator.getInstance().isValid(email);
	}

	private boolean isValidSalary(Double salary) {
		return salary != null && salary >= 0;
	}

	private String trimAndUppercase(String value) {
		String trimmed = trim(value);
		return trimmed == null ? null : trimmed.toUpperCase();
	}

	private String trim(String value) {
		return value == null ? null : value.trim();
	}
}
