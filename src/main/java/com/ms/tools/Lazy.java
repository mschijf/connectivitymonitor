package com.ms.tools;

/**
 * Class for lazy initializing of a variable. Guarded to be thread safe.
 *
 * for in-depth details, see:
 *  - https://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html#dcl
 *  - https://stackoverflow.com/a/3519736/493759
 *
 * @param <TYPE>
 */
public abstract class Lazy<TYPE> {

    private volatile TYPE value;

    private synchronized TYPE lazy() {
        if (value == null) value = init();
        return value;
    }

    public TYPE get() {
        TYPE result = value;
        return result == null ? lazy() : result;
    }

    abstract protected TYPE init();
}