package com.parasoft.demoapp.service;

import com.parasoft.demoapp.exception.ParameterException;
import org.apache.commons.lang3.StringUtils;

public class ParameterValidator {

    public static <T> T requireNonNull(T obj, String message) throws ParameterException {
        if (obj == null)
            throw new ParameterException(message);
        return obj;
    }

    public static CharSequence requireNonBlank(final CharSequence cs, String message) throws ParameterException {
        if (StringUtils.isBlank(cs))
            throw new ParameterException(message);
        return cs;
    }

    public static int requireNonNegative(int number, String message) throws ParameterException {
        if (number < 0)
            throw new ParameterException(message);
        return number;
    }

    public static int requireNonZero(int number, String message) throws ParameterException {
        if (number == 0)
            throw new ParameterException(message);
        return number;
    }

}
