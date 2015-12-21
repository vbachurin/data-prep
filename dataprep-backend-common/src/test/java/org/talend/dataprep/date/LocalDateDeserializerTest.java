package org.talend.dataprep.date;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.Month.JANUARY;
import static org.hamcrest.Matchers.is;

public class LocalDateDeserializerTest {

    @Test
    public void testDeserialize() throws Exception {
        //given
        final JsonFactory jsonFactory = new JsonFactory();
        final JsonParser parser = jsonFactory.createParser("{" +
                "   \"dayOfMonth\": 5," +
                "   \"month\": \"JANUARY\"," +
                "   \"year\": 2015," +
                "   \"hour\": 5," +
                "   \"minute\": 6," +
                "   \"second\": 56" +
                "}");
        parser.setCodec(new ObjectMapper(jsonFactory));
        final LocalDateTimeDeserializer deserializer = new LocalDateTimeDeserializer();

        //when
        final LocalDateTime result = deserializer.deserialize(parser, null);

        //then
        MatcherAssert.assertThat(result, is(LocalDateTime.of(2015, JANUARY, 5, 5, 6, 56)));
    }
}