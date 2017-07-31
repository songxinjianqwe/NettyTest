package cn.sinjinsong.netty.rpc.util;

import cn.sinjinsong.netty.rpc.annotation.RPCService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SinjinSong on 2017/7/31.
 */
public class AnnotationUtil {

    public static Map<String, Object> getServices() {
        Map<String,Object> handlerMap = new ConcurrentHashMap<>();
        for (Class<?> cls : getClassesWithAnnotation(RPCService.class, "cn.sinjinsong.netty.rpc.service")) {
            String key = cls.getAnnotation(RPCService.class).value().getName();
            try {
                handlerMap.put(key,cls.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return handlerMap;
    }

    public static List<Class<?>> getClassesWithAnnotation(Class annotation, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        if (annotation.isAnnotation()) {
            try {
                List<Class<?>> allClass = getClasses(packageName);//获得当前包以及子包下的所有类  
                for (Class<?> cls : allClass) {
                    if (cls.isAnnotationPresent(annotation)) {
                        classes.add(cls);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    private static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace(".", "/");
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String newPath = resource.getFile().replace("%20", " ");
            dirs.add(new File(newPath));
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClass(directory, packageName));
        }
        return classes;
    }

    private static List<Class<?>> findClass(File directory, String packageName)
            throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClass(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
