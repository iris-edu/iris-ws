package edu.iris.dmc.sacpz;

import edu.iris.dmc.TestFile;
import edu.iris.dmc.sacpz.model.NumberUnit;
import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.ws.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SacpzReaderTest {

    @Test
    public void readOO()throws Exception{

                //* CREATED           : 2022-11-03T14:06:50
                //* START             : 2014-08-01T00:00:00
                //* END               :


        try(Reader ss=new FileReader(TestFile.getFile("oo.sacpz")); SacpzReader reader=new SacpzReader(ss)){
            Sacpz record = reader.readRecord();
            assertNotNull(record);
            assertEquals("OO", record.getNetwork());
            assertEquals("AXBA1", record.getStation());
            assertEquals("HDH", record.getChannel());
            assertEquals("", record.getLocation());
            assertEquals("RSN Axial Base 1", record.getDescription());
            assertEquals(DateUtil.parseAny("2022-11-03T14:06:50"), record.getCreated());
            assertEquals(DateUtil.parseAny("2014-08-01T00:00:00"), record.getStart());
            assertNull(record.getEnd());

            assertEquals( -129.736694, record.getLongitude(), 0.000001);
            assertEquals(45.820179, record.getLatitude(), 0.000001);
            assertEquals(-2607.2, record.getElevation(), 0.000001);
            assertEquals(0, record.getDepth(), 0.000001);
            assertEquals(90, record.getDip(), 0.000001);
            assertEquals(0, record.getAzimuth(), 0.000001);
            assertEquals(200, record.getSampleRate(), 0.000001);

            assertEquals("PASCAL", record.getInputUnit());
            assertEquals("COUNTS", record.getOutputUnit());
            assertEquals("HT1-90-U/Diff Hydrophone-DM-24 Mk3 Fixed Gain, g", record.getInsttype());
            assertEquals(NumberUnit.builder().value(1.820000e-03).unit("(PA)").build(),record.getInstgain());
            assertTrue(record.getComment()==null||record.getComment().isEmpty());
            assertEquals(NumberUnit.builder().value(2.257530e+03).unit("(PA)").build(), record.getSensitivity());
            assertEquals(1.000000e+00, record.getA0());


            assertNotNull(record.getZeros());
            assertEquals(0, record.getZeros().size());
            assertNotNull(record.getPoles());
            assertEquals(0, record.getPoles().size());

        }
    }

    @Test
    public void readIU()throws Exception{
        Map<Integer,Double> constants=new HashMap<>();
        constants.put(0, 1.093951e-10);
        constants.put(1, 1.093951e-10);
        constants.put(2, 1.093951e-10);
        constants.put(3, 1.161175e-10);
        constants.put(4, 1.161175e-10);
        constants.put(5, 1.161175e-10);

        Map<Integer,Double> aZeros=new HashMap<>();
        aZeros.put(0, 5.511780e-20);
        aZeros.put(1, 5.511780e-20);
        aZeros.put(2, 5.511780e-20);
        aZeros.put(3, 4.743750e-20);
        aZeros.put(4, 4.743750e-20);
        aZeros.put(5, 4.743750e-20);

        Map<Integer, Date> startDates=new HashMap<>();
        startDates.put(0, DateUtil.parseAny("2018-07-09T20:45:00"));
        startDates.put(1, DateUtil.parseAny("2018-07-09T20:45:00"));
        startDates.put(2, DateUtil.parseAny("2018-07-09T20:45:00"));
        startDates.put(3, DateUtil.parseAny("2019-06-07T20:00:00"));
        startDates.put(4, DateUtil.parseAny("2019-06-07T20:00:00"));
        startDates.put(5, DateUtil.parseAny("2019-06-07T20:00:00"));



        try(Reader ss=new FileReader(TestFile.getFile("iu.anmo.sacpz")); SacpzReader reader=new SacpzReader(ss)){
            List<Sacpz> records=new ArrayList<>();
            while(true){
                Sacpz record = reader.readRecord();
                if(record==null){
                    break;
                }
                records.add(record);
            }
            assertEquals(6, records.size());
            int index = 0;
            for(Sacpz record:records){
                assertEquals("IU", record.getNetwork());
                assertEquals("ANMO", record.getStation());

                assertEquals(9, record.getZeros().size());
                assertEquals(7, record.getPoles().size());
                assertEquals(constants.get(index), record.getConstant());
                assertEquals(aZeros.get(index), record.getA0());
                assertEquals(startDates.get(index), record.getStart());
                assertNull(record.getEnd());
                index++;
            }
        }
    }
}
