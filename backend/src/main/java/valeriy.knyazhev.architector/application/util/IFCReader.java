package valeriy.knyazhev.architector.application.util;

import org.bimserver.ifc.step.deserializer.IfcHeaderParser;
import org.bimserver.models.store.IfcHeader;
import org.bimserver.plugins.deserializers.DeserializeException;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public abstract class IFCReader<T> {

    @Nonnull
    private static String readFullLine(@Nonnull String line, @Nonnull BufferedReader reader) {

        StringBuilder fullLine = new StringBuilder();
        while (!line.endsWith(";")) {
            fullLine.append(line);
            line = readNextLine(reader);
        }
        return fullLine.append(line).toString();
    }

    @Nonnull
    private static String readNextLine(@Nonnull BufferedReader reader) {
        try {
            return Optional.ofNullable(reader.readLine())
                    .orElseThrow(() -> new IllegalStateException("Unable to read next line of the content stream."));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read stream.", e);
        }
    }

    @Nonnull
    protected T read(@Nonnull InputStream contentStream) {
        String isoId = null;
        StringBuilder header = new StringBuilder();
        List<String> contentItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(contentStream))) {

            boolean isHeader = false;
            boolean isData = false;
            String line = null;

            isoId = readNextLine(reader);
            while ((line = reader.readLine()) != null) {
                if (line.equalsIgnoreCase("ENDSEC;")) {
                    isHeader = false;
                    isData = false;
                }

                if (isHeader) {
                    header.append(readFullLine(line, reader)).append("\n");
                }

                if (isData) {
                    String itemLine = readFullLine(line, reader);
                    int startIndexItem = itemLine.indexOf("=");
                    contentItems.add(itemLine.substring(startIndexItem));
                }

                if (line.equalsIgnoreCase("HEADER;")) {
                    isHeader = true;
                }
                if (line.equalsIgnoreCase("DATA;")) {
                    isData = true;
                }
            }

        } catch (IOException ioe) {
            throw new IllegalStateException("Unable to read input stream.", ioe);
        }
        try {
            IfcHeader resultHeader = new IfcHeaderParser().parseFileName(header.toString());
            return constructResult(isoId, resultHeader, contentItems);
        } catch (DeserializeException e) {
            throw new IllegalStateException("Unable to deserialize input content.");
        } catch (ParseException e) {
            throw new IllegalStateException("Unable to read header input content.");
        }
    }

    @Nonnull
    protected abstract T constructResult(String isoId, IfcHeader header, List<String> contentItems);
}
