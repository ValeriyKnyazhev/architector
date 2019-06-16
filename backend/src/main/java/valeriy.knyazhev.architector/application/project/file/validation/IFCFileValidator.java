package valeriy.knyazhev.architector.application.project.file.validation;

import org.apache.http.util.Args;
import org.bimserver.emf.IdEObject;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.emf.PackageMetaData;
import org.bimserver.emf.Schema;
import org.bimserver.ifc.step.deserializer.Ifc2x3tc1StepDeserializer;
import org.bimserver.ifc.step.deserializer.Ifc4StepDeserializer;
import org.bimserver.ifc.step.deserializer.IfcStepDeserializer;
import org.bimserver.ifc.step.serializer.Ifc2x3tc1StepSerializer;
import org.bimserver.ifc.step.serializer.Ifc4StepSerializer;
import org.bimserver.ifc.step.serializer.IfcStepSerializer;
import org.bimserver.models.ifc2x3tc1.Ifc2x3tc1Package;
import org.bimserver.models.ifc4.Ifc4Package;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.commit.ChangeType;
import valeriy.knyazhev.architector.domain.model.commit.CommitItem;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static valeriy.knyazhev.architector.application.project.file.validation.IFCFileValidator.ValidationType.NONE;

/**
 * @author Valeriy Knyazhev
 */
@Service
public class IFCFileValidator
{

    private final Ifc2x3tc1StepDeserializer IFC2X3Deserializer;

    private final Ifc2x3tc1StepSerializer IFC2X3Serializer = new Ifc2x3tc1StepSerializer(null);

    private final Ifc4StepDeserializer IFC4Deserializer;

    private final Ifc4StepSerializer IFC4Serializer = new Ifc4StepSerializer(null);

    public IFCFileValidator()
    {
        this.IFC2X3Deserializer = new Ifc2x3tc1StepDeserializer();
        PackageMetaData packageMetaDataIFC2X3 = new PackageMetaData(Ifc2x3tc1Package.eINSTANCE, Schema.IFC2X3TC1, Paths
            .get("."));
        this.IFC2X3Deserializer.init(packageMetaDataIFC2X3);

        this.IFC4Deserializer = new Ifc4StepDeserializer(Schema.IFC4);
        PackageMetaData packageMetaDataIFC4 = new PackageMetaData(Ifc4Package.eINSTANCE, Schema.IFC2X3TC1, Paths
            .get("."));
        this.IFC4Deserializer.init(packageMetaDataIFC4);
    }

    public List<ChangedEntity> validateContent(@Nonnull ValidationType type,
                                               @Nonnull String schema,
                                               @Nonnull List<String> fileContent,
                                               @Nonnull List<CommitItem> changes)
    {
        Args.notNull(type, "Validation type is required.");
        Args.notNull(schema, "File schema is required.");
        Args.notNull(fileContent, "File content is required.");
        Args.notNull(changes, "Changes are required.");
        if (schema.equals("IFC4"))
        {
            // FIXME for ifc4 model standard was not completely implemented
            type = NONE;
        }
        List<String> content = fileContent.stream()
            .filter(item -> !item.isBlank())
            .collect(Collectors.toList());
        switch (type)
        {
            case NONE:
            {
                return List.of();
            }
            case REFERENCES:
            {
                return validateReferences(schema, content, changes);
            }
            default:
            {
                throw new IllegalArgumentException("Unsupported validation type: " + type);
            }
        }
    }

    private List<ChangedEntity> validateReferences(@Nonnull String schema,
                                                   @Nonnull List<String> content,
                                                   @Nonnull List<CommitItem> changes)
    {
        IfcStepDeserializer deserializer = defineDeserializer(schema);
        byte[] fileData = prepareData(schema, content);
        IfcModelInterface model = null;
        try
        {
            model = deserializer.read(new ByteArrayInputStream(fileData), "", fileData.length, null);
        } catch (DeserializeException e)
        {
            throw new IllegalStateException("Unable to deserialize file.");
        }
        Map<Integer, IdEObject> modelValues = model.getValues().stream()
            .collect(toMap(IdEObject::getExpressId, Function.identity()));

        Map<Integer, String> rootEntities = model.getValues().stream()
            .filter(IFCFileValidator::isRoot)
            .collect(toMap(IdEObject::getExpressId, IFCFileValidator::extractSimpleName));

        Set<Integer> notValidReferences = new HashSet<>();

        int initialCapacity = 2 * content.size();

        Set<Integer> changedIdentifiers = changes.stream()
            .map(CommitItem::value)
            .map(IFCFileValidator::extractExpressId)
            .collect(toSet());

        Map<Integer, Set<Integer>> itemsWithParents = new HashMap<>(initialCapacity);
        Map<Integer, Set<Integer>> itemsWithChildren = new HashMap<>(initialCapacity);
        content.forEach(item -> itemsWithParents.put(extractExpressId(item), new HashSet<>()));
        content.forEach(item -> itemsWithChildren.put(extractExpressId(item), new HashSet<>()));

        content.forEach(item -> {
                int expressId = extractExpressId(item);
                Set<Integer> children = extractParameters(item);
                itemsWithChildren.put(expressId, children);
                for (int child : children)
                {
                    Set<Integer> parents = itemsWithParents.get(child);
                    if (parents == null)
                    {
                        notValidReferences.add(expressId);
                        Set<Integer> newParents = Stream.of(expressId).collect(toSet());
                        itemsWithParents.put(child, newParents);
                    } else
                    {
                        parents.add(expressId);
                    }
                }
            }
        );

        if (!notValidReferences.isEmpty())
        {
            throw new InvalidFileContentException(
                notValidReferences.stream()
                    .map(modelValues::get)
                    .map(value -> new ChangedEntity(value.getExpressId(), extractSimpleName(value)))
                    .collect(toList())
            );
        }

        Set<Integer> visitedIdentifiers = new HashSet<>(initialCapacity);

        Set<Integer> foundRoots = new HashSet<>();
        Queue<Integer> visitors = new LinkedBlockingDeque<>();

        for (CommitItem change : changes)
        {
            int expressId = extractExpressId(change.value());
            if (change.type() == ChangeType.ADDITION)
            {
                visitors.add(expressId);
            } else
            {
                Set<Integer> parents = itemsWithParents.getOrDefault(expressId, Set.of())
                    .stream()
                    .filter(id -> !changedIdentifiers.contains(id))
                    .collect(Collectors.toUnmodifiableSet());
                visitors.addAll(parents);
            }
        }

        while (!visitors.isEmpty())
        {
            int id = visitors.poll();
            visitedIdentifiers.add(id);

            IdEObject ifcObject = modelValues.get(id);
            if (ifcObject == null)
            {
                throw new IllegalStateException("[validation] IFC object with express id " + id + " not found.");
            }

            if (isRoot(ifcObject))
            {
                foundRoots.add(id);
            } else
            {
                Set<Integer> parents = itemsWithParents.get(id).stream()
                    .filter(parentId -> !visitedIdentifiers.contains(parentId))
                    .collect(Collectors.toUnmodifiableSet());
                visitors.addAll(parents);
            }
        }

        return foundRoots.stream()
            .map(rootId -> new ChangedEntity(rootId, rootEntities.get(rootId)))
            .collect(toList());
    }

    private IfcStepDeserializer defineDeserializer(@Nonnull String schema)
    {
        if (schema.equals(Schema.IFC2X3TC1.getHeaderName()))
        {
            return this.IFC2X3Deserializer;
        } else if (schema.equals(Schema.IFC4.getHeaderName()))
        {
            return this.IFC4Deserializer;
        } else
        {
            throw new IllegalStateException("Unsupported IFC schema version " + schema);
        }
    }

    private static boolean isRoot(@Nonnull IdEObject value)
    {
        return value instanceof org.bimserver.models.ifc2x3tc1.impl.IfcProductImpl ||
               value instanceof org.bimserver.models.ifc4.impl.IfcProductImpl;
    }

    @Nonnull
    private static String extractSimpleName(IdEObject value)
    {
        String simpleName = value.getClass().getSimpleName();
        return simpleName.substring(0, simpleName.indexOf("Impl"));
    }

    private static byte[] prepareData(@Nonnull String schema, @Nonnull List<String> items)
    {
        String content = String.join("\n", items);
        String fileData =
            "ISO-10303-21;\nHEADER;\nFILE_DESCRIPTION((''),'');\nFILE_NAME('','" + LocalDateTime.now().toString() +
            "',(''),(''),'','','');\nFILE_SCHEMA(('" + schema + "'));\nENDSEC;\nDATA;\n" + content +
            "\nENDSEC;\nEND-ISO-10303-21;";
        return fileData.getBytes();
    }

    private static int extractExpressId(@Nonnull String item)
    {
        int endIndex = item.indexOf('=');
        return Integer.valueOf(item.substring(1, endIndex).trim());
    }

    @Nonnull
    private static Optional<String> findByExpressId(@Nonnull List<String> items, int expressId)
    {
        return items.stream().filter(item -> item.startsWith("$" + expressId)).findFirst();
    }

    @Nonnull
    private static Set<Integer> extractParameters(@Nonnull String item)
    {
        int startIndex = item.indexOf('=');
        Pattern pattern = Pattern.compile("#[0-9]+");
        Matcher matcher = pattern.matcher(item.substring(startIndex));
        if (matcher.find())
        {
            int firstResult = Integer.valueOf(matcher.group().substring(1));
            Set<Integer> result = matcher.results()
                .map(MatchResult::group)
                .map(id -> id.substring(1))
                .map(Integer::valueOf)
                .collect(toSet());
            result.add(firstResult);
            return result;
        } else
        {
            return Set.of();
        }
    }

    public enum ValidationType
    {
        NONE, REFERENCES
    }

}
