package zlh.com.combinepluginhotfix.hook.classloader;

import dalvik.system.DexClassLoader;

/**
 * Created by shs1330 on 2018/3/14.
 */

public class PluginClassLoader extends DexClassLoader {
    public PluginClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }
}
