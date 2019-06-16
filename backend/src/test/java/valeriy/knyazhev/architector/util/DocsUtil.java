package valeriy.knyazhev.architector.util;

import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Snippet;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

/**
 * @author Valeriy Knyazhev
 */
public class DocsUtil
{

    public static RestDocumentationResultHandler restDocument(String methodName, Snippet... snippets)
    {
        return document(
            methodName,
            preprocessRequest(prettyPrint(), removeHeaders("Host")),
            preprocessResponse(prettyPrint(), removeHeaders("X-Application-Context",
                "X-Content-Type-Options", "X-XSS-Protection", "Cache-Control",
                "Pragma", "Expires", "X-Frame-Options")),
            snippets
        );
    }

}
