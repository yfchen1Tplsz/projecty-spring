package com.wind.rider.ioc01.infrastructure.componet;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyCustomDateEditor extends PropertyEditorSupport {

    private String dateFormatPattern; // 日期格式化模式

    public MyCustomDateEditor(String dateFormatPattern) {
        this.dateFormatPattern = dateFormatPattern;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
        try {
            Date parsedDate = dateFormat.parse(text);
            setValue(parsedDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please provide a date in format: " + dateFormatPattern);
        }
    }

    @Override
    public String getAsText() {
        Date value = (Date) getValue();
        if (value == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
        return dateFormat.format(value);
    }
}