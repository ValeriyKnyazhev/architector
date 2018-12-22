package valeriy.knyazhev.architector.application;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.emf.PackageMetaData;
import org.bimserver.emf.Schema;
import org.bimserver.ifc.step.deserializer.Ifc2x3tc1StepDeserializer;
import org.bimserver.models.ifc2x3tc1.Ifc2x3tc1Package;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.Project;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Valeriy Knyazhev
 */
@Service
public class IFCProjectReader {

    private Ifc2x3tc1StepDeserializer deserializer;

    public IFCProjectReader() {
        this.deserializer = new Ifc2x3tc1StepDeserializer();
        PackageMetaData packageMetaData = new PackageMetaData(Ifc2x3tc1Package.eINSTANCE, Schema.IFC2X3TC1, Paths.get("tmp"));
        this.deserializer.init(packageMetaData);
    }

    @Nonnull
    public Project readProjectFromUrl(@Nonnull URL projectUrl) {
        try {
            InputStream projectStream = projectUrl.openStream();
            return readProjectStream(projectStream);
        } catch (IOException e) {
            throw new ProjectReadingException(projectUrl.getRef());
        }

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
    private Project readProjectStream(@Nonnull InputStream projectStream) {
        String isoId = null;
        StringBuilder header = new StringBuilder();
        List<String> contentItems = new ArrayList<>();
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(projectStream))) {

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
                    contentItems.add(readFullLine(line, reader));
                }

                if (line.equalsIgnoreCase("HEADER;")) {
                    isHeader = true;
                }
                if (line.equalsIgnoreCase("DATA;")) {
                    isData = true;
                }
            }

        } catch (IOException ioe) {
            throw new IllegalStateException("Unable to read input stream.");
        }

        try {
            String resultHeader = isoId + "\nHEADER;\n" + header.toString() + "ENDSEC;\nDATA;\n" +
                    String.join("\n", contentItems)
                    + "\nENDSEC;\nEND-" + isoId;
            ByteArrayInputStream headerStream = new ByteArrayInputStream(resultHeader.getBytes(StandardCharsets.UTF_8));

            IfcModelInterface model = this.deserializer.read(headerStream, "", headerStream.available(), null);
            return ProjectBuilder.buildProject(model.getModelMetaData(), contentItems);
        } catch (DeserializeException e) {
            throw new IllegalStateException("Unable to deserialize project.");
        }
    }

    private String readFullLine(@Nonnull String line, @Nonnull BufferedReader reader) {

        StringBuilder fullLine = new StringBuilder();
        while (!line.endsWith(";")) {
            fullLine.append(line);
            line = readNextLine(reader);
        }
        return fullLine.append(line).toString();
    }

}
