package com.mercadopago.utils;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by mreverter on 2/13/17.
 */

public class ResourcesUtil {

    private ResourcesUtil() {}

    public static String getStringResource(String fileName) {
        String resource;
        try {
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName);
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            resource = scanner.hasNext() ? scanner.next() : "";
        } catch (Exception e) {
            resource = "";
        }
        return resource;
    }
}
