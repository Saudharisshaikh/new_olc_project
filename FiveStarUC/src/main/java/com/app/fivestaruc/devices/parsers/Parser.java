package com.app.fivestaruc.devices.parsers;


import java.util.ArrayList;

public abstract class Parser<E>
{
    public static final String AUTHSUCCESS = "AUTHSUCCESS";
    public static final String AUTHFAIL="AUTHFAIL";
    public static final String NULL="null";

    protected String result;
    public Parser(String result)
    {
        this.result=result;
    }

    public boolean isAuthSuccess()
    {
        if (result.trim().equals(AUTHSUCCESS))
            return true;
        else
            return false;
    }

    public boolean isTokenValid()
    {
        if (result.trim().equals(AUTHFAIL))
            return false;
        else
            return true;
    }


    public abstract void getData(ArrayList<E> list);
}
