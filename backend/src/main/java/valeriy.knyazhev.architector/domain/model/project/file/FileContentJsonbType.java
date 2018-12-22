package valeriy.knyazhev.architector.domain.model.project.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class FileContentJsonbType implements UserType {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public FileContentJsonbType() {
        // empty
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.JAVA_OBJECT};
    }

    @Override
    public Class<FileContent> returnedClass() {
        return FileContent.class;
    }

    @Override
    public boolean equals(Object x, Object y)
            throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x)
            throws HibernateException {
        return Objects.hash(x);
    }

    @Override
    public FileContent nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        String json = rs.getString(names[0]);
        return json == null ? null : asObject(json);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, asString(assertType(value)), Types.OTHER);
        }
    }

    @Override
    public FileContent deepCopy(Object value)
            throws HibernateException {
        return doCopy(value);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value)
            throws HibernateException {
        return doCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return doCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return doCopy(original);
    }

    private FileContent doCopy(Object value) {
        return value == null ? null : asObject(asString(assertType(value)));
    }

    private FileContent asObject(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, FileContent.class);
        } catch (IOException e) {
            throw new HibernateException(e);
        }
    }

    private String asString(FileContent value)
            throws HibernateException {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new HibernateException(e);
        }
    }

    private FileContent assertType(Object o) {
        if (o instanceof FileContent) {
            return (FileContent) o;
        } else {
            throw new IllegalArgumentException("Illegal jsonb type: " + o.getClass());
        }
    }

}