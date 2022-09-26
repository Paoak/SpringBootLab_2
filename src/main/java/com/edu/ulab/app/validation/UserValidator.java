package com.edu.ulab.app.validation;

import com.edu.ulab.app.dto.UserDto;

public class UserValidator {
    public static boolean isValidUser(UserDto userDto){
        boolean valid = false;
        if (userDto.getFullName() != null) {
            if ((!userDto.getFullName().isBlank()) && (userDto.getAge() > 0)) {
                valid = true;
            }
        }
        return valid;
    }
}
