//========================================================================
//Copyright 2007-2008 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * An executable jar ,containing the dependencies of an application, bundling the entire
 * application in a single contained jar.
 * 
 * @author David Yu
 * @created Feb 25, 2008
 */

public class Application
{

    
    public static void run(String[] args) throws Exception
    {        
        ClassLoader loader = Application.class.getClassLoader();
        URL appPropertiesURL = loader.getResource("app/app.properties");
        if(appPropertiesURL==null)
            throw new IllegalStateException("The resource /app/app.properties is missing");        
        Properties props = new Properties();
        props.load(appPropertiesURL.openStream());
        
        String deleteOnExitParam = props.getProperty("app.lib.deleteOnExit");
        boolean deleteOnExit = deleteOnExitParam==null ? true : Boolean.parseBoolean(deleteOnExitParam);
        String name = props.getProperty("app.name");        
        if(name==null)
            throw new IllegalStateException("app.name must be specified");
        String mainClass = props.getProperty("app.main.class");
        if(mainClass==null)
            throw new IllegalStateException("app.main.class must be specified");
        
        String version = props.getProperty("app.version", "1.0-SNAPSHOT");
        String uniqueName = name.concat("-").concat(version);
        
        File appTempDir = new File(ArchiveUtil.getTempDir(), uniqueName);
        if(!appTempDir.exists())
            appTempDir.mkdirs();
        __tempDir = appTempDir;
        ArchiveUtil.extract(loader.getResource("app"), appTempDir, deleteOnExit);
        File appDir = new File(appTempDir, "app");
        Application app = new Application();        
        String libPath = props.getProperty("app.lib.path");        
        if(libPath!=null)
        {            
            StringTokenizer tokenizer = new StringTokenizer(libPath, ",;");
            while(tokenizer.hasMoreTokens())
            {
                String next = tokenizer.nextToken().trim();
                File file = new File(appDir, next);
                if(file.exists())
                {
                    if(file.isDirectory())
                    {
                        List<File> jars = ResourceUtil.getFilesByExtension(file, EXTENSIONS);
                        for(File f: jars)
                            app.addCanonicalFile(f);
                    }
                    else
                        app.addCanonicalFile(file);
                }                    
            }
        }        
        Thread.currentThread().setContextClassLoader(app.getClassLoader()); 
        System.setProperty("java.class.path", app.getClassLoader().toString());
        try
        {
            Policy policy=Policy.getPolicy();
            if (policy!=null)
                policy.refresh();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }              
        Class<?> clazz = app.getClassLoader().loadClass(mainClass);
        Method main = clazz.getMethod("main", new Class[]{args.getClass()});
        main.invoke(null, new Object[]{args});
        System.setProperty("java.class.path", app.getAppClassLoader().toString());
    }
    
    public static Application newApplication()
    {
        return new Application();
    }    

    static void createJar() throws Exception
    {
        System.out.println("creating jar...");
        String outputDirParam = System.getProperty("outputDir");
        String appDirParam = System.getProperty("appDir");        
        File outputDir = outputDirParam==null ? new File(".") : new File(outputDirParam);
        if(!outputDir.exists())
            throw new IllegalStateException("outputDir doesnt not exist");
        File appDir = appDirParam==null ? new File("app") : new File(appDirParam);
        if(!appDir.exists())
            throw new IllegalStateException("appDir doesnt not exist");
        
        String uniqueName = "dyuproject-util-".concat(Application.class.getPackage().getImplementationVersion());
        File tmp = new File(ArchiveUtil.getTempDir(), uniqueName);
        if(!tmp.exists())
            tmp.mkdirs();        
        String jarName = uniqueName.concat(".jar");             
        File props = new File(appDir, "app.properties");
        if(!props.exists())
            throw new IllegalStateException("app/app.properties not found");
        Properties appProps = new Properties();
        appProps.load(new FileInputStream(props));
        
        String appName = appProps.getProperty("app.name");        
        if(appName==null)
            throw new IllegalStateException("app.name must be specified");
        if(appProps.getProperty("app.main.class")==null)
            throw new IllegalStateException("app.main.class must be specified");
        String appVersion = appProps.getProperty("app.version", "1.0-SNAPSHOT");
        String appUniqueName = appName.concat("-").concat(appVersion).concat(".jar");        
        
        StringTokenizer tokenizer = new StringTokenizer(System.getProperty("java.class.path"), ",;");
        while(tokenizer.hasMoreTokens())
        {
            String next = tokenizer.nextToken().trim();
            if(next.indexOf("dyuproject-util")!=-1)
            {
                File jar = new File(tmp, jarName);
                if(!jar.exists())
                {
                    System.out.println("caching jar...");
                    ResourceUtil.copy(new File(next), jar);
                }                   
                ArchiveUtil.extract(jar, tmp, false);
                break;
            }
        }        
        File outputJar = new File(outputDir, appUniqueName);
        File parent = outputJar.getParentFile();
        if(!parent.exists())
            parent.mkdirs();
        File manifest = new File(tmp, "com/dyuproject/util/app-manifest.txt");
        StringBuilder buffer = new StringBuilder();
        buffer.append("jar cfm ").append(outputJar.getPath()).append(' ').append(manifest.getPath());
        buffer.append(" -C ").append(tmp.getPath()).append(" com -C ");
        buffer.append(appDir.getParentFile().getPath()).append(" app");        

        Runtime.getRuntime().exec(buffer.toString());
    }
    
    public static void runExploded(String[] args) throws Exception
    {
        String explodedProps = System.getProperty("explodedProps");
        if(explodedProps==null)
            throw new IllegalStateException("explodedProps must be specified");
        File propFile = new File(explodedProps);
        if(!propFile.exists())
            throw new IllegalStateException("explodedProps \"" + propFile.getPath() + "\" does not exist.");        
        Properties props = new Properties();
        props.load(new FileInputStream(propFile));
        
        String mainClass = props.getProperty("mainClass");
        if(mainClass==null)
            throw new IllegalStateException("mainClass must be specified");
        String libPath = props.getProperty("libPath");
        Application app = new Application();
        if(libPath!=null)
        {
            StringTokenizer tokenizer = new StringTokenizer(libPath, ",;");
            while(tokenizer.hasMoreTokens())
            {
                String next = tokenizer.nextToken().trim();
                File file = new File(next);
                if(file.exists())
                {
                    if(file.isDirectory())
                    {
                        if(file.getName().equals("classes"))
                            app.addCanonicalFile(file);
                        else
                        {
                            List<File> jars = ResourceUtil.getFilesByExtension(file, EXTENSIONS);
                            for(File f: jars)
                                app.addCanonicalFile(f);
                        }
                    }
                    else
                        app.addCanonicalFile(file);
                }
                else
                    System.out.println("libPath: " + file.getPath() + " doest noe exist");
            }
        }
        Thread.currentThread().setContextClassLoader(app.getClassLoader()); 
        System.setProperty("java.class.path", app.getClassLoader().toString());
        try
        {
            Policy policy=Policy.getPolicy();
            if (policy!=null)
                policy.refresh();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }              
        Class<?> clazz = app.getClassLoader().loadClass(mainClass);
        Method main = clazz.getMethod("main", new Class[]{args.getClass()});
        main.invoke(null, new Object[]{args});
        System.setProperty("java.class.path", app.getAppClassLoader().toString());
    }
    
    public static void main(String[] args) throws Exception
    {
        String create = System.getProperty("createJar");
        if(create!=null)
        {
            createJar();
            return;
        }
        String runExploded = System.getProperty("runExploded");
        if(runExploded!=null)
        {
            runExploded(args);
            return;
        }
        run(args);
    }
    
    public static File getTempDir()
    {
        return __tempDir;
    }
    
    private static File __tempDir;
    
    public static final String[] EXTENSIONS = new String[]{".jar"};
    
    private Map<String, URL> _urls = new HashMap<String, URL>();
    private StringBuilder _builder = new StringBuilder();
    private ClassLoader _parent;
    private AppClassLoader _loader;
    
    public Application()
    {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        _parent = parent!=null ? parent : Application.class.getClassLoader();
        if(_parent==null) 
            _parent = AppClassLoader.getSystemClassLoader();
        String oldPath = System.getProperty("java.class.path");
        StringTokenizer tokenizer = new StringTokenizer(oldPath, ",;");
        while(tokenizer.hasMoreTokens())            
            addCanonicalFile(new File(tokenizer.nextToken().trim())); 
    }
    
    private URL addCanonicalFile(File f)
    {
        if(!f.exists())
            return null;
        try
        {
            File cf = f.getCanonicalFile();
            URL url= cf.toURI().toURL();
            String key = url.toString();
            URL old = _urls.put(key, url);
            if(old==null)
                _builder.append(cf.getPath()).append(File.pathSeparatorChar);
            return url;
        }
        catch(IOException ioe) 
        {
            return null;
        } 
    }
    
    public ClassLoader getParentClassLoader()
    {
        return _parent;
    }
    
    public AppClassLoader getClassLoader()
    {
        return _loader;
    }    
    
    public AppClassLoader getAppClassLoader()
    {
        return _loader;
    }
    
    public void addURL(URL url)
    {
        URL newUrl = addCanonicalFile(new File(url.toString()));
        if(newUrl!=null)
            _loader.addURL(newUrl);
    }
    
    public void addFile(File file)
    {
        URL url = addCanonicalFile(file);
        if(url!=null)
            _loader.addURL(url);            
    }
    
    public void addLib(File dir)
    {
        if(!dir.exists() || !dir.isDirectory())
            return;
        List<File> jars = ResourceUtil.getFilesByExtension(dir, EXTENSIONS);        
        for(File f : jars)
        {
            URL url = addCanonicalFile(f);
            if(url!=null)
                _loader.addURL(url);
        }            
    }
    
    public String toString()
    {
        return _builder.toString();
    }
    
    private class AppClassLoader extends URLClassLoader
    {
        public AppClassLoader()
        {
            super(new URL[]{}, getParentClassLoader());            
        }
        
        public void addURL(URL url)
        {
            super.addURL(url);
        }
        
        public String toString()
        {
            return _builder.toString();
        }
        
        public Application getApplication()
        {
            return Application.this;
        }
        
    }
    
}
