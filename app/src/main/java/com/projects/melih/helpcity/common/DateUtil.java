package com.projects.melih.helpcity.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by melih on 12.12.2017
 */

public class DateUtil {
    public static final String FORMAT_SERVICE_DATE = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String FORMAT_VIEW_DATE = "dd MMM yyyy";
    public static final SimpleDateFormat DATE_UI_FORMATTER = new SimpleDateFormat(FORMAT_VIEW_DATE, Locale.getDefault());
    public static final SimpleDateFormat DATE_SERVICE_FORMATTER = new SimpleDateFormat(FORMAT_SERVICE_DATE, Locale.getDefault());

    public static String getCurrentDate() {
        return DATE_SERVICE_FORMATTER.format(new Date());
    }
}
