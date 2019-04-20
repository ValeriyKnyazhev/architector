package valeriy.knyazhev.architector.application.project.file;

import lombok.RequiredArgsConstructor;
import org.bimserver.models.store.IfcHeader;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.application.util.ContentReadingException;
import valeriy.knyazhev.architector.application.util.IFCReader;
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
public class IFCFileReader extends IFCReader<File> {

    @Nonnull
    public File readFromUrl(@Nonnull URL fileUrl) {
        try {
            InputStream fileStream = fileUrl.openStream();
            return read(fileStream);
        } catch (IOException e) {
            throw new ContentReadingException(fileUrl.getRef());
        }

    }

    @Nonnull
    public File readFromFile(@Nonnull InputStream fileContent) {
        return read(fileContent);
    }


    @Override
    @Nonnull
    protected File constructResult(String isoId, IfcHeader header, List<String> contentItems) {
        return File.constructor()
                .fileId(FileId.nextId())
            .withDescription(FileInfoExtractor.extractDescription(header))
            .withMetadata(FileInfoExtractor.extractMetadata(header))
            .withContent(FileContent.of(contentItems))
            .construct();
    }

}
