package valeriy.knyazhev.architector.port.adapter.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;

/**
 * @author Valeriy Knyazhev
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(Include.NON_NULL)
public class ResponseMessage {

    private String error;

    private String info;

    public ResponseMessage error(@Nullable String error) {
        if (StringUtils.hasText(error)) {
            this.error = error;
        }
        return this;
    }

    public ResponseMessage info(@Nullable String info) {
        if (StringUtils.hasText(info)) {
            this.info = info;
        }
        return this;
    }

}
