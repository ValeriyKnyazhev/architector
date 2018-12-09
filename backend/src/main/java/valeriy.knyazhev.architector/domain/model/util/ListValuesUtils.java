package valeriy.knyazhev.architector.domain.model.util;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class ListValuesUtils {

    //TODO replace this algorithm by aggregator ('value1', 'value2')
    private static final String DEFAULT_SEPARATOR = ";;";

    @Nonnull
    public static String mapValue(@Nonnull Collection<String> values) {
        Args.notNull(values, "Array of string values");
        return String.join(DEFAULT_SEPARATOR, values);
    }

    @Nonnull
    public static List<String> extractValues(@Nonnull String value) {
        Args.notNull(value, "String value");
        return Arrays.stream(value.split(DEFAULT_SEPARATOR)).collect(toList());
    }

}
