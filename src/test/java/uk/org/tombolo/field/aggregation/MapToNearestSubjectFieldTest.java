package uk.org.tombolo.field.aggregation;

import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import uk.org.tombolo.AbstractTest;
import uk.org.tombolo.FieldBuilder;
import uk.org.tombolo.TestFactory;
import uk.org.tombolo.core.Attribute;
import uk.org.tombolo.core.Subject;
import uk.org.tombolo.core.SubjectType;
import uk.org.tombolo.core.utils.SubjectUtils;
import uk.org.tombolo.recipe.FieldRecipe;
import uk.org.tombolo.recipe.RecipeDeserializer;
import uk.org.tombolo.importer.ons.AbstractONSImporter;

import java.util.Collections;

public class MapToNearestSubjectFieldTest extends AbstractTest {
    private Subject subject;
    private Subject nearbySubject;

    @Before
    public void setUp() {
        nearbySubject = TestFactory.makeNamedSubject("E09000001");
        subject = TestFactory.makeNamedSubject("E01000001");
        Attribute attribute = TestFactory.makeAttribute(TestFactory.DEFAULT_PROVIDER, "attr_label");
        SubjectType localAuthority = TestFactory.makeNamedSubjectType("localAuthority");
        TestFactory.makeTimedValue(localAuthority, "E09000001", attribute, "2011-01-01T00:00:00", 100d);
    }

    @Test
    public void testJsonValueForSubject() throws Exception {
        nearbySubject.setShape(TestFactory.makePointGeometry(0.09d, 0d)); // Just inside the given radius
        SubjectUtils.save(Collections.singletonList(nearbySubject));

        MapToNearestSubjectField field = new MapToNearestSubjectField("aLabel", AbstractONSImporter.PROVIDER.getLabel(),"localAuthority", 0.1d, makeFieldSpec());
        String jsonString = field.jsonValueForSubject(subject, true).toJSONString();
        JSONAssert.assertEquals("{" +
                "  aLabel: 100.0" +
                "}", jsonString,false);
    }

    @Test
    public void testJsonValueForSubjectWithNullMaxRadius() throws Exception {
        nearbySubject.setShape(TestFactory.makePointGeometry(0.0009d, 0d)); // Just inside the default radius
        SubjectUtils.save(Collections.singletonList(nearbySubject));

        MapToNearestSubjectField field = new MapToNearestSubjectField("aLabel", AbstractONSImporter.PROVIDER.getLabel(),"localAuthority", null, makeFieldSpec());
        String jsonString = field.jsonValueForSubject(subject, true).toJSONString();
        JSONAssert.assertEquals("{" +
                "  aLabel: 100.0" +
                "}", jsonString,false);
    }

    private FieldRecipe makeFieldSpec() {
        return RecipeDeserializer.fromJson(
                FieldBuilder.latestValue("default_provider_label", "attr_label").toJSONString(),
                FieldRecipe.class);
    }
}