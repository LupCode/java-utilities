package com.lupcode.Utilities.reflection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Offers basic functions to load resources and reflections
 * @author LupCode.com (Luca Vogels)
 * @since 2021-04-04
 */
public class ReflectionUtils {

	/**
	 * Loads all classes from a given {@link JarFile}
	 * @param packageName Package name with '.' as separator (e.g. 'com.lupcode.Utilities')
	 * @param recursive If true all sub-packages will also be scanned
	 * @param jar Jar file that should be scanned for classes
	 * @return Set containing all found classes
	 * @throws NullPointerException if package name or {@link JarFile} is null
	 */
	public static Set<Class<?>> getClasses(String packageName, boolean recursive, JarFile jar) throws NullPointerException {
		if(packageName == null) throw new NullPointerException("Package name cannot be null");
		if(jar == null) throw new NullPointerException("JarFile cannot be null");
		packageName = packageName.replace('/', '.');
		int packageNameLen = packageName.length();
		HashSet<Class<?>> result = new HashSet<>();
		Enumeration<JarEntry> entries = jar.entries();
		JarEntry entry;
		String name;
		while(entries.hasMoreElements() && (entry = entries.nextElement()) != null) {
			name = entry.getName();
			if(!name.endsWith(".class")) continue;
			name = name.substring(0, name.length()-6).replace('/', '.');
			if(name.startsWith(packageName) && 
					(recursive || name.indexOf('.', packageNameLen) < 0)) {
				try { result.add(Class.forName(name)); } catch (Exception ex) {}
			}
		} return result;
	}
	
	/**
	 * Loads all classes from a given {@link JarURLConnection}
	 * @param packageName Package name with '.' as separator (e.g. 'com.lupcode.Utilities')
	 * @param recursive If true all sub-packages will also be scanned
	 * @param jar Jar file that should be scanned for classes
	 * @return Set containing all found classes
	 * @throws NullPointerException if package name or {@link JarURLConnection} is null
	 */
	public static Set<Class<?>> getClasses(String packageName, boolean recursive, JarURLConnection jarConnection) throws NullPointerException {
		if(jarConnection == null) throw new NullPointerException("JarURLConnection cannot be null");
		try {
			return getClasses(packageName, recursive, jarConnection.getJarFile());
		} catch (Exception ex) {}
		return new HashSet<>();
	}
	
	/**
	 * Loads all classes from a given {@link File}
	 * @param packageName Package name with '.' as separator 
	 * that will be used as prefix (e.g. 'com.lupcode.Utilities')
	 * @param recursive If true all sub-packages will also be scanned
	 * @param con Connection that should be scanned for classes
	 * @return Set containing all found classes
	 * @throws NullPointerException if package name or {@link File} is null
	 */
	public static Set<Class<?>> getClasses(String packageName, boolean recursive, File file) throws NullPointerException {
		if(file == null) throw new NullPointerException("File cannot be null");
		HashSet<Class<?>> result = new HashSet<>();
		if(!file.exists()) return result;
		if(file.isFile()) {
			String name = file.getName().replace('/', '.').replace('\\', '.');
			if(!name.endsWith(".class")) return result;
			try {
				result.add(Class.forName(packageName+"."+name.substring(0, name.length()-6)));
			} catch (Exception ex) {}
			return result;
		} else if(file.isDirectory()) {
			for(File f : file.listFiles()) {
				if(f.isFile()) {
					result.addAll(getClasses(packageName, recursive, f));
				} else if(f.isDirectory() && recursive)
					result.addAll(getClasses(packageName+"."+f.getName(), recursive, f));
			}
		} return result;
	}
		
	/**
	 * Loads all classes from a given {@link URLConnection}
	 * @param packageName Package name with '.' as separator 
	 * (will be used as prefix if URLConnection points to a file) (e.g. 'com.lupcode.Utilities')
	 * @param recursive If true all sub-packages will also be scanned
	 * @param con Connection that should be scanned for classes
	 * @return Set containing all found classes
	 * @throws NullPointerException if package name or {@link URLConnection} is null
	 */
	public static Set<Class<?>> getClasses(String packageName, boolean recursive, URLConnection con) throws NullPointerException {
		if(packageName == null) throw new NullPointerException("Package name cannot be null");
		if(con == null) throw new NullPointerException("URLConnection cannot be null");
		if(con instanceof JarURLConnection) return getClasses(packageName, recursive, (JarURLConnection)con);
		try {
			return getClasses(packageName, recursive, new File(URLDecoder.decode(con.getURL().getPath(), "UTF-8")));
		} catch (Exception ex) {}
		return new HashSet<>();
	}
	
	/**
	 * Loads all classes from a given package name
	 * @param packageName Package name with '.' as separator (e.g. 'com.lupcode.Utilities')
	 * @param recursive If true all sub-packages will also be scanned
	 * @return Set containing all found classes
	 * @throws NullPointerException if package name is null
	 */
	public static Set<Class<?>> getClasses(String packageName, boolean recursive) throws NullPointerException {
		return getClasses(packageName, (ClassLoader)null, recursive);
	}
	
	/**
	 * Loads all classes from a given package name
	 * @param packageName Package name with '.' as separator (e.g. 'com.lupcode.Utilities')
	 * @param classLoader Class loader that should be used to find classes (can be null for default)
	 * @param recursive If true all sub-packages will also be scanned
	 * @return Set containing all found classes
	 * @throws NullPointerException if package name is null
	 */
	public static Set<Class<?>> getClasses(String packageName, ClassLoader classLoader, boolean recursive) throws NullPointerException {
		if(packageName == null) throw new NullPointerException("Package name cannot be null");
		if(classLoader == null) classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader == null) classLoader = ReflectionUtils.class.getClassLoader();
		HashSet<Class<?>> result = new HashSet<>();
		try {
			Enumeration<URL> urls = classLoader.getResources(packageName.replace('.', '/'));
			URL url;
			while(urls.hasMoreElements() && (url = urls.nextElement()) != null) {
				try {
					result.addAll(getClasses(packageName, recursive, url.openConnection()));
				} catch (Exception ex) {}
			}
		} catch (IOException ex) {}
		return result;
	}
	
	
	
	/**
	 * Loads the content of a file inside a package
	 * @param packagePath Full path to the resource with '.' as separator (e.g. com.lupcode.Text.txt)
	 * @return Stream fetching the content of the resource or null if resource not found
	 * @throws NullPointerException if package pack is null
	 */
	public static InputStream getResourceContent(String packagePath) throws NullPointerException {
		return getResourceContent(packagePath, null);
	}
	
	/**
	 * Loads the content of a file inside a package
	 * @param packagePath Full path to the resource with '.' as separator (e.g. com.lupcode.Text.txt)
	 * @param classLoader Class loader that should be used to find classes (can be null for default)
	 * @return Stream fetching the content of the resource or null if resource not found
	 * @throws NullPointerException if package pack is null
	 */
	public static InputStream getResourceContent(String packagePath, ClassLoader classLoader) throws NullPointerException {
		if(packagePath == null) throw new NullPointerException("Package path cannot be null");
		if(classLoader == null) classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader == null) classLoader = ReflectionUtils.class.getClassLoader();
		int idx = packagePath.lastIndexOf('.');
		packagePath = (idx >= 0 ? packagePath.substring(0, idx).replace('.', '/')+packagePath.substring(idx) : packagePath);
		return classLoader.getResourceAsStream(packagePath);
	}
	
	/**
	 * Loads an image from a package
	 * @param packagePath Full path to the image with '.' as separator (e.g. com.lupcode.Image.jpg)
	 * @return Image or null if image could not be found or read
	 * @throws NullPointerException if package path is null
	 */
	public static BufferedImage getImage(String packagePath) throws NullPointerException {
		return getImage(packagePath, null);
	}
	
	/**
	 * Loads an image from a package
	 * @param packagePath Full path to the image with '.' as separator (e.g. com.lupcode.Image.jpg)
	 * @param classLoader Class loader that should be used to find classes (can be null for default)
	 * @return Image or null if image could not be found or read
	 * @throws NullPointerException if package path is null
	 */
	public static BufferedImage getImage(String packagePath, ClassLoader classLoader) throws NullPointerException {
		InputStream input = getResourceContent(packagePath, classLoader);
		try {
			return input != null ? ImageIO.read(input) : null;
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Loads an image icon from a package
	 * @param packagePath Full path to the image icon with '.' as separator (e.g. com.lupcode.Image.jpg)
	 * @return Image icon or null if image icon could not be found or read
	 * @throws NullPointerException if package path is null
	 */
	public static ImageIcon getImageIcon(String packagePath) throws NullPointerException {
		return getImageIcon(packagePath, null);
	}
	
	/**
	 * Loads an image icon from a package
	 * @param packagePath Full path to the image icon with '.' as separator (e.g. com.lupcode.Image.jpg)
	 * @param classLoader Class loader that should be used to find classes (can be null for default)
	 * @return Image icon or null if image icon could not be found or read
	 * @throws NullPointerException if package path is null
	 */
	public static ImageIcon getImageIcon(String packagePath, ClassLoader classLoader) throws NullPointerException {
		BufferedImage image = getImage(packagePath, classLoader);
		return image!=null ? new ImageIcon(image) : null;
	}
}
