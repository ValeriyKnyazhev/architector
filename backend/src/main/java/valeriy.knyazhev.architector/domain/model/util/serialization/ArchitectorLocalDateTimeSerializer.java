package valeriy.knyazhev.architector.domain.model.util.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Valeriy Knyazhev
 */
public class ArchitectorLocalDateTimeSerializer extends StdSerializer<LocalDateTime>
{

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ArchitectorLocalDateTimeSerializer()
    {
        this(null);
    }

    private ArchitectorLocalDateTimeSerializer(Class<LocalDateTime> timeClass)
    {
        super(timeClass);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws
        IOException
    {
        gen.writeString(FORMATTER.format(value));
    }
}
