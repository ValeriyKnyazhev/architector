package valeriy.knyazhev.architector.domain.model.util.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Valeriy Knyazhev
 */
public class ArchitectorLocalDateDeserializer extends StdDeserializer<LocalDate>
{

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public ArchitectorLocalDateDeserializer()
    {
        this(null);
    }

    private ArchitectorLocalDateDeserializer(Class<LocalDate> timeClass)
    {
        super(timeClass);
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws
        IOException
    {
        String text = parser.getText();
        return text != null ? LocalDate.parse(text, FORMATTER) : null;
    }

}
