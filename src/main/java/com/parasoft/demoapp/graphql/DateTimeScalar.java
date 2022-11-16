package com.parasoft.demoapp.graphql;

import graphql.language.StringValue;
import graphql.schema.*;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Function;

public class DateTimeScalar {

    private final static SimpleDateFormat sdf;

    static {
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static GraphQLScalarType getInstance() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("An DateTime Scalar")
                .coercing(new DateTimeCoercing())
                .build();
    }

    private static class DateTimeCoercing implements Coercing<Date, String> {
        @Override
        public String serialize(Object input) throws CoercingSerializeException {
            if(input instanceof Date) {
                return sdf.format(input);
            } else {
                throw new CoercingSerializeException(
                        "Expected a 'Date' but was '" + typeName(input) + "'.");
            }
        }

        @Override
        public Date parseValue(Object input) throws CoercingParseValueException {
            if (input instanceof String) {
                return parseDateTime((String)input, CoercingParseValueException::new);
            } else {
                throw new CoercingParseValueException(
                        "Expected a 'String' but was '" + typeName(input) + "'."
                );
            }
        }

        @Override
        public Date parseLiteral(Object input) throws CoercingParseLiteralException {
            if (!(input instanceof StringValue)) {
                throw new CoercingParseLiteralException(
                        "Expected AST type 'StringValue' but was '" + typeName(input) + "'."
                );
            }
            return parseDateTime(((StringValue) input).getValue(), CoercingParseLiteralException::new);
        }

        @Override
        public graphql.language.Value<?> valueToLiteral(Object input) {
            String s = serialize(input);
            return StringValue.newStringValue(s).build();
        }

        private Date parseDateTime(String s, Function<String, RuntimeException> exceptionMaker) {
            try {
                return Date.from(
                        OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant());
            } catch (DateTimeParseException e) {
                throw exceptionMaker.apply("Invalid RFC3339 value : '" + s + "'. because of : '" + e.getMessage() + "'");
            }
        }

        public String typeName(Object input) {
            if (input == null) {
                return "null";
            }
            return input.getClass().getSimpleName();
        }
    }
}
