package valeriy.knyazhev.architector.domain.model.util.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Valeriy Knyazhev
 */
public class ArchitectorLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime>
{

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ArchitectorLocalDateTimeDeserializer()
    {
        this(null);
    }

    private ArchitectorLocalDateTimeDeserializer(Class<LocalDateTime> timeClass)
    {
        super(timeClass);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws
        IOException
    {
        return LocalDateTime.parse(parser.getText(), FORMATTER);
    }

}
