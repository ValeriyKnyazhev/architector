package valeriy.knyazhev.architector.application.util;

import org.bimserver.ifc.step.deserializer.IfcHeaderParser;
import org.bimserver.models.store.IfcHeader;
import org.bimserver.models.store.StoreFactory;
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
        String fileDescription = null;
        String fileName = null;
        String fileSchema = null;
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
                    String fullLine = readFullLine(line, reader);
                    // like as in IfcStepDeserializer::processHeader
                    if (fullLine.startsWith("FILE_DESCRIPTION")) {
                        fileDescription = fullLine.substring("FILE_DESCRIPTION".length() + 1, fullLine.length() - 2).trim();
                    }
                    if (fullLine.startsWith("FILE_NAME")) {
                        fileName = fullLine.substring("FILE_NAME".length() + 1, fullLine.length() - 2).trim();
                    }
                    if (fullLine.startsWith("FILE_SCHEMA")) {
                        fileSchema = fullLine.substring("FILE_SCHEMA".length() + 1, fullLine.length() - 2).trim();
                    }
                }

                if (isData) {
                    String itemLine = readFullLine(line, reader);
                    int startIndexItem = itemLine.indexOf("=") + 1;
                    contentItems.add(itemLine.substring(startIndexItem));
                }

                if (line.equalsIgnoreCase("HEADER;")) {
                    isHeader = true;
                }
                if (line.equalsIgnoreCase("DATA;")) {
                    isData = true;
                }
            }
            if (fileDescription == null || fileName == null || fileSchema == null) {
                throw new IllegalStateException("File description, name or schema not found.");
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Unable to read input stream.", ioe);
        }
        try {
            IfcHeader resultHeader = StoreFactory.eINSTANCE.createIfcHeader();
            new IfcHeaderParser().parseDescription(fileDescription, resultHeader);
            new IfcHeaderParser().parseFileName(fileName, resultHeader);
            // TODO add reading file schema
            new IfcHeaderParser().parseFileSchema(fileSchema, resultHeader);
            return constructResult(isoId, resultHeader, contentItems);
        } catch (DeserializeException e) {
            throw new IllegalStateException("Unable to deserialize input content.");
        } catch (ParseException e) {
            throw new IllegalStateException("Unable to read header input content.");
        }
    }

    @Nonnull
    protected abstract T constructResult(@Nonnull String isoId, IfcHeader header, List<String> contentItems);
}
