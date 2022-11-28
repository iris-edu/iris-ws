package edu.iris.dmc;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class TestFile {

    public static File getFile() throws URISyntaxException {
        return getFile("fdsnws-dataselect_2020-07-31t18_24_42z.mseed");
    }

    public static File getFile(String name) throws URISyntaxException {
        URL resource = TestFile.class.getClassLoader().
                getResource(name);
        return new File(resource.toURI());
    }
}
