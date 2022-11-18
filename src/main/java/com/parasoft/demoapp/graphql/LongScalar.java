package com.parasoft.demoapp.graphql;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Reference: <br/>
 * <a href="https://github.com/graphql-java/graphql-java-extended-scalars">github: graphql-java-extended-scalars</a>
 * <br/>
 * <a href="https://github.com/graphql-java/graphql-java-extended-scalars/blob/master/src/main/java/graphql/scalars/java/JavaPrimitives.java">JavaPrimitives</a>
 */
public class LongScalar {

    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);

    public static GraphQLScalarType getInstance() {
        return GraphQLScalarType.newScalar()
                .name("Long")
                .description("A 64-bit signed integer")
                .coercing(new LongCoercing())
                .build();
    }

    private static class LongCoercing implements Coercing<Long, Long> {
        private Long convertImpl(Object input) {
            if (input instanceof Long) {
                return (Long) input;
            } else if (isNumberIsh(input)) {
                BigDecimal value;
                try {
                    value = new BigDecimal(input.toString());
                } catch (NumberFormatException e) {
                    return null;
                }
                try {
                    return value.longValueExact();
                } catch (ArithmeticException e) {
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        public Long serialize(Object input) {
            Long result = convertImpl(input);
            if (result == null) {
                throw new CoercingSerializeException(
                        "Expected type 'Long' but was '" + ScalarUtil.typeName(input) + "'."
                );
            }
            return result;
        }

        @Override
        public Long parseValue(Object input) {
            Long result = convertImpl(input);
            if (result == null) {
                throw new CoercingParseValueException(
                        "Expected type 'Long' but was '" + ScalarUtil.typeName(input) + "'."
                );
            }
            return result;
        }

        @Override
        public Long parseLiteral(Object input) {
            if (input instanceof StringValue) {
                try {
                    return Long.parseLong(((StringValue) input).getValue());
                } catch (NumberFormatException e) {
                    throw new CoercingParseLiteralException(
                            "Expected value to be a Long but it was '" + input + "'"
                    );
                }
            } else if (input instanceof IntValue) {
                BigInteger value = ((IntValue) input).getValue();
                if (value.compareTo(LONG_MIN) < 0 || value.compareTo(LONG_MAX) > 0) {
                    throw new CoercingParseLiteralException(
                            "Expected value to be in the Long range but it was '" + value + "'"
                    );
                }
                return value.longValue();
            }
            throw new CoercingParseLiteralException(
                    "Expected AST type 'IntValue' or 'StringValue' but was '" + ScalarUtil.typeName(input) + "'."
            );
        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            Long result = Objects.requireNonNull(convertImpl(input));
            return IntValue.newIntValue(BigInteger.valueOf(result)).build();
        }

        private static boolean isNumberIsh(Object input) {
            return input instanceof Number || input instanceof String;
        }
    }
}