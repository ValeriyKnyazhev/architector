package valeriy.knyazhev.architector.application.project.file;

import lombok.RequiredArgsConstructor;
import org.bimserver.models.store.IfcHeader;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.application.util.ContentReadingException;
import valeriy.knyazhev.architector.application.util.IFCReader;
import valeriy.knyazhev.architector.domain.model.project.file.FileContent;

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
public class IFCFileReader extends IFCReader<FileData>
{

    @Nonnull
    public FileData readFromUrl(@Nonnull URL fileUrl)
    {
        try
        {
            InputStream fileStream = fileUrl.openStream();
            return read(fileStream);
        } catch (IOException e)
        {
            throw new ContentReadingException(fileUrl.getRef());
        }

    }

    @Nonnull
    public FileData readFromFile(@Nonnull InputStream fileContent)
    {
        return read(fileContent);
    }


    @Override
    @Nonnull
    protected FileData constructResult(@Nonnull String isoId, IfcHeader header, List<String> contentItems)
    {
        return new FileData(
            isoId,
            FileInfoExtractor.extractMetadata(header),
            FileInfoExtractor.extractDescription(header),
            FileContent.of(contentItems)
        );
    }

}
