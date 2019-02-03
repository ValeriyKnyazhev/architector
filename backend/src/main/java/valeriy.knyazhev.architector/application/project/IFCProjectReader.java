package valeriy.knyazhev.architector.application.project;

import lombok.RequiredArgsConstructor;
import org.bimserver.models.store.IfcHeader;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.application.util.ContentReadingException;
import valeriy.knyazhev.architector.application.util.IFCReader;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileContent;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * @author Valeriy Knyazhev
 */
@Service
@RequiredArgsConstructor
public class IFCProjectReader extends IFCReader<Project> {

    @Nonnull
    public Project readFromUrl(@Nonnull URL projectUrl) {
        try {
            InputStream fileStream = projectUrl.openStream();
            return read(fileStream);
        } catch (IOException e) {
            throw new ContentReadingException(projectUrl.getRef());
        }

    }

    @Nonnull
    public Project readFromFile(@Nonnull InputStream projectContent) {
        return read(projectContent);
    }

    @Override
    @Nonnull
    protected Project constructResult(String isoId, IfcHeader header, List<String> contentItems) {
        File file = File.builder()
                .fileId(FileId.nextId())
                .content(FileContent.of(contentItems))
                .build();
        return Project.constructor()
                .projectId(ProjectId.nextId())
                .withDescription(ProjectInfoExtractor.extractDescription(header))
                .withMetadata(ProjectInfoExtractor.extractMetadata(header))
                .withFile(file)
                .construct();
    }
}
