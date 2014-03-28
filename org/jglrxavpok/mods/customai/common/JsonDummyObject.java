package org.jglrxavpok.mods.customai.common;

import org.jglrxavpok.mods.customai.json.JSONObject;

public class JsonDummyObject extends JSONObject
{

    private Class<?> clazz;
    private int priority;
    public JsonDummyObject(Class<?> c, int p)
    {
        this.clazz = c;
        this.priority = p;
    }
    
    public String getString(String d)
    {
        if(d.equals("type"))
        {
            return clazz.getCanonicalName();
        }
        if(!has(d))
        {
            return d;
        }
        return super.getString(d);
    }
    
    public boolean getBoolean(String d)
    {
        if(!has(d))
        {
            return false;
        }
        return super.getBoolean(d);
    }
    
    public int getInt(String d)
    {
        if(d.equals("priority"))
            return priority;
        if(!has(d))
        {
            return 1;
        }
        return super.getInt(d);
    }
    public double getDouble(String d)
    {
        if(!has(d))
        {
            return 1D;
        }
        return super.getInt(d);

    }
}
