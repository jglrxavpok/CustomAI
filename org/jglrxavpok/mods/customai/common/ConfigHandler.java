package org.jglrxavpok.mods.customai.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ConfigHandler
{

    private File configFile;
    private Properties propertiesMap;

    public ConfigHandler(File file)
    {
        this.configFile = file;
        if(file.exists())
        {
            load(file);
        }
        else
        {
            load(null);
        }
    }
    
    private void load(File file)
    {
        propertiesMap = new Properties();
        if(file != null)
        {
            try
            {
                FileInputStream in = new FileInputStream(file);
                propertiesMap.load(in);
                in.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean save()
    {
        try
        {
            FileOutputStream out = new FileOutputStream(configFile);
            propertiesMap.store(out, "CustomAI config file");
            out.flush();
            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public void set(String key, String value)
    {
        propertiesMap.setProperty(key, value);
        new Thread("Save CustomAI props thread")
        {
            public void run()
            {
                save();
            }
        }.start();
    }
    
    public String get(String key, String defaultValue)
    {
        if(propertiesMap.containsKey(key))
        {
            return propertiesMap.getProperty(key);
        }
        return defaultValue;
    }
    
    public float getFloat(String key, float defaultValue)
    {
        if(propertiesMap.containsKey(key))
        {
            return Float.parseFloat(propertiesMap.getProperty(key));
        }
        return defaultValue;
    }
    
    public double getDouble(String key, double defaultValue)
    {
        if(propertiesMap.containsKey(key))
        {
            return Double.parseDouble(propertiesMap.getProperty(key));
        }
        return defaultValue;
    }
    
    public boolean getBoolean(String key, boolean defaultValue)
    {
        if(propertiesMap.containsKey(key))
        {
            return Boolean.parseBoolean(propertiesMap.getProperty(key));
        }
        return defaultValue;
    }
    
    public int getInt(String key, int defaultValue)
    {
        if(propertiesMap.containsKey(key))
        {
            return Integer.parseInt(propertiesMap.getProperty(key));
        }
        return defaultValue;
    }

}
