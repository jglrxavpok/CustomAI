package org.jglrxavpok.mods.customai.common;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Reflect
{

    public static boolean isInstanceof(Class<?> c1, Class<?> c)
    {
        Class<?>[] ints = c1.getInterfaces();
        if(ints != null)
            for(int i = 0;i<ints.length;i++)
            {
                if(c == ints[i])
                    return true;
            }
        if(c1 == c)
        {
            return true;
        }
        while(!verifySuperclass(c1, c))
        {
            c1 = c1.getSuperclass();
            if(c1 == null) return false;
        }
        return true;

    }
    
	public static boolean isInstanceof(Object o, Class<?> c)
	{
		if(o == null)return false;
		return isInstanceof(o.getClass(), c);
}
	
	private static boolean verifySuperclass(Class<?> daughter, Class<?> mother)
	{
		if(daughter.getSuperclass() == mother)
		{
			return true;
		}
		else
			return false;
	}
	
	public static boolean tryToAddToClassPath(URL path)
	{
		URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		try
		{
			Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class<?>[]{URL.class}); 
			addURL.setAccessible(true);
			addURL.invoke(classLoader, new Object[]{path});
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean tryToAddToClassPath(File f)
	{
		try
		{
			return Reflect.tryToAddToClassPath(f.toURI().toURL());
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
