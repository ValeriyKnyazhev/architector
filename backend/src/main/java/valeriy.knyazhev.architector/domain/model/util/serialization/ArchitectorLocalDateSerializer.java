package valeriy.knyazhev.architector.domain.model.util.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Valeriy Knyazhev
 */
public class ArchitectorLocalDateSerializer extends StdSerializer<LocalDate>
{

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public ArchitectorLocalDateSerializer()
    {
        this(null);
    }

    private ArchitectorLocalDateSerializer(Class<LocalDate> timeClass)
    {
        super(timeClass);
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws
        IOException
    {
        gen.writeString(FORMATTER.format(value));
    }
}
