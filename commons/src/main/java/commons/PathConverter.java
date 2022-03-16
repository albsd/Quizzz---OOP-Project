package commons;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter // may want to set autoApply to true
public class PathConverter implements AttributeConverter<Path, String> {

    @Override
    public String convertToDatabaseColumn(final Path attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public Path convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : Paths.get(dbData);
    }

}
