package valeriy.knyazhev.architector.application;

import org.apache.commons.io.IOUtils;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.emf.PackageMetaData;
import org.bimserver.emf.Schema;
import org.bimserver.ifc.step.deserializer.Ifc2x3tc1StepDeserializer;
import org.bimserver.models.ifc2x3tc1.Ifc2x3tc1Package;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.Project;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;

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
    private Project readProjectStream(@Nonnull InputStream projectStream) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(projectStream, byteStream);
        } catch (IOException e) {
            throw new ProjectReadingException();
        }
        try {
            IfcModelInterface model = this.deserializer.read(new ByteArrayInputStream(byteStream.toByteArray()), "", byteStream.size(), null);
            return ProjectBuilder.buildProject(model);
        } catch (DeserializeException e) {
            throw new IllegalStateException("Unable to deserialize project.");
        }
    }

}
