package com.mercadopago.util;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by mromar on 5/3/17.
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
