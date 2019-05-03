package valeriy.knyazhev.architector.domain.model.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RequiredArgsConstructor
public class JsonbType implements UserType, DynamicParameterizedType
{

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Class targetClass;

    @Override
    public int[] sqlTypes()
    {
        return new int[]{Types.JAVA_OBJECT};
    }

    @Override
    public Class returnedClass()
    {
        return this.targetClass;
    }

    @Override
    public boolean equals(Object x, Object y)
    {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x)
    {
        return Objects.hash(x);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
        throws
        SQLException
    {
        String json = rs.getString(names[0]);
        if (json == null)
        {
            return null;
        } else
        {
            return returnedClass().cast(asObject(json));
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
        throws
        SQLException
    {
        if (value == null)
        {
            st.setNull(index, Types.OTHER);
        } else
        {
            st.setObject(index, asString(value), Types.OTHER);
        }
    }

    @Override
    public Object deepCopy(Object value)
    {
        return returnedClass().cast(nullSafeDeepCopy(value));
    }

    @Override
    public boolean isMutable()
    {
        return true;
    }

    @Override
    public Serializable disassemble(Object value)
    {
        return asString(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
    {
        return asObject((String) cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
    {
        return nullSafeDeepCopy(original);
    }

    @Override
    public void setParameterValues(Properties parameters)
    {
        ParameterType parameterType = (ParameterType) parameters.get(DynamicParameterizedType.PARAMETER_TYPE);
        this.targetClass = parameterType.getReturnedClass();
    }

    private Object nullSafeDeepCopy(Object value)
    {
        if (value == null)
        {
            return null;
        } else
        {
            return asObject(asString(value));
        }
    }

    @SuppressWarnings("unchecked")
    private Object asObject(String json)
    {
        try
        {
            return OBJECT_MAPPER.readValue(json, returnedClass());
        } catch (IOException e)
        {
            throw new HibernateException("Unable to read JSON-formatted value", e);
        }
    }

    private static String asString(Object value)
    {
        try
        {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e)
        {
            throw new HibernateException("Unable to write JSON-formatted value.", e);
        }
    }

}