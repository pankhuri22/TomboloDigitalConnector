package uk.org.tombolo.importer.utils.extraction;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import uk.org.tombolo.TestFactory;
import uk.org.tombolo.core.Provider;
import uk.org.tombolo.core.TimedValue;
import uk.org.tombolo.core.TimedValueId;

import static org.junit.Assert.assertEquals;

public class TimedValueExtractorTest extends AbstractExtractorTest {

    Provider provider = TestFactory.DEFAULT_PROVIDER;
    RowCellExtractor subjectLabelExtractor = new RowCellExtractor(0, CellType.STRING);
    RowCellExtractor valueExtractor = new RowCellExtractor(1, CellType.NUMERIC);

    TimedValueExtractor extractor;


    @Before
    public void setUp() throws Exception {
        TestFactory.makeNamedSubject("E01000001");
        TestFactory.makeNamedSubject("E01000002");
        TestFactory.makeAttribute(provider, "Attribute");

        extractor = new TimedValueExtractor(
                provider,
                TestFactory.makeNamedSubjectType("lsoa"),
                subjectLabelExtractor,
                new ConstantExtractor("Attribute"),
                new ConstantExtractor("2016"),
                valueExtractor
        );
    }

    @Test
    public void extract() throws Exception {
        Workbook workbook = makeDummyWorkbook();

        subjectLabelExtractor.setRow(workbook.getSheet("sheet").getRow(0));
        valueExtractor.setRow(workbook.getSheet("sheet").getRow(0));

        TimedValue value1 = extractor.extract();
        assertEquals("E01000001", value1.getId().getSubject().getLabel());
        assertEquals("2016-12-31T23:59:59", value1.getId().getTimestamp().format(TimedValueId.DATE_TIME_FORMATTER));
        assertEquals(provider, value1.getId().getAttribute().getProvider());
        assertEquals("Attribute", value1.getId().getAttribute().getLabel());
        assertEquals(5.0d, value1.getValue(), 0.01d);

        subjectLabelExtractor.setRow(workbook.getSheet("sheet").getRow(1));
        valueExtractor.setRow(workbook.getSheet("sheet").getRow(1));

        TimedValue value2 = extractor.extract();
        assertEquals("E01000002", value2.getId().getSubject().getLabel());
        assertEquals("2016-12-31T23:59:59", value2.getId().getTimestamp().format(TimedValueId.DATE_TIME_FORMATTER));
        assertEquals(provider, value2.getId().getAttribute().getProvider());
        assertEquals("Attribute", value2.getId().getAttribute().getLabel());
        assertEquals(6.0d, value2.getValue(), 0.01d);
    }

    @Test
    public void getValueExtractor() throws Exception {
        assertEquals(valueExtractor, extractor.getValueExtractor());
    }

}