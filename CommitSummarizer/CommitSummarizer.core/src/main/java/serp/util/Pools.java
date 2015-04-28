package serp.util;

import java.io.*;
import java.util.*;

/**
 * <p>
 * Static methods that operate on pools.
 * </p>
 *
 * @author Abe White
 */
public class Pools
{
    /**
     * Return a synchronized pool backed by the given instance.
     */
    public static Pool synchronizedPool(Pool pool)
    {
        if (pool == null)
            throw new NullPointerException();
        return new SynchronizedPool(pool);
    }

    /**
     * Synchronized wrapper for Pools.
     */
    private static class SynchronizedPool
            implements Pool, Serializable
    {
        private Pool _pool = null;

        public SynchronizedPool(Pool pool)
        {
            _pool = pool;
        }

        @Override
        public synchronized int getMaxPool()
        {
            return _pool.getMaxPool();
        }

        @Override
        public synchronized void setMaxPool(int max)
        {
            _pool.setMaxPool(max);
        }

        @Override
        public synchronized int getMinPool()
        {
            return _pool.getMinPool();
        }

        @Override
        public synchronized void setMinPool(int min)
        {
            _pool.setMinPool(min);
        }

        @Override
        public synchronized int getWait()
        {
            return _pool.getWait();
        }

        @Override
        public synchronized void setWait(int millis)
        {
            _pool.setWait(millis);
        }

        @Override
        public synchronized int getAutoReturn()
        {
            return _pool.getAutoReturn();
        }

        @Override
        public synchronized void setAutoReturn(int millis)
        {
            _pool.setAutoReturn(millis);
        }

        @Override
        public Iterator iterator()
        {
            return _pool.iterator();
        }

        @Override
        public synchronized int size()
        {
            return _pool.size();
        }

        @Override
        public synchronized boolean isEmpty()
        {
            return _pool.isEmpty();
        }

        @Override
        public synchronized boolean contains(Object obj)
        {
            return _pool.contains(obj);
        }

        @Override
        public synchronized boolean containsAll(Collection c)
        {
            return _pool.containsAll(c);
        }

        @Override
        public synchronized Object[] toArray()
        {
            return _pool.toArray();
        }

        @Override
        public synchronized Object[] toArray(Object[] fill)
        {
            return _pool.toArray(fill);
        }

        @Override
        public synchronized boolean add(Object obj)
        {
            return _pool.add(obj);
        }

        @Override
        public synchronized boolean addAll(Collection c)
        {
            return _pool.addAll(c);
        }

        @Override
        public synchronized boolean remove(Object obj)
        {
            return _pool.remove(obj);
        }

        @Override
        public synchronized boolean removeAll(Collection c)
        {
            return _pool.removeAll(c);
        }

        @Override
        public synchronized boolean retainAll(Collection c)
        {
            return _pool.retainAll(c);
        }

        @Override
        public synchronized void clear()
        {
            _pool.clear();
        }

        @Override
        public synchronized boolean equals(Object obj)
        {
            return _pool.equals(obj);
        }

        @Override
        public synchronized int hashCode()
        {
            return _pool.hashCode();
        }

        @Override
        public synchronized Object get()
        {
            return _pool.get();
        }

        @Override
        public synchronized Object get(Object match)
        {
            return _pool.get(match);
        }

        @Override
        public synchronized Object get(Object match, Comparator comp)
        {
            return _pool.get(match, comp);
        }

        @Override
        public synchronized Set takenSet()
        {
            return _pool.takenSet();
        }
    }
}
