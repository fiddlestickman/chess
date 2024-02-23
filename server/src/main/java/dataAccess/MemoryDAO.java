package dataAccess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

class MemoryDAO<T> {
    protected ArrayList<T> database;
    protected int index;
    private interface SearchOperation {
        Object doSomething(Object searchedfor, Object nextinbase);
    }

    protected MemoryDAO() {
        database = new ArrayList<>();
        index = 0;
    }

    public int create(T t) {
        database.add(t);
        return index++;
    }

    //finds the first object in the database that share a specific subfield 'var' of the given object 't'
    //should only be one object if service is done correctly
    public T read(T t, String var) {
        SearchOperation read = (search, inbase) -> {return inbase;};
        ArrayList<T> output = search(t, var, read, database.iterator());
        if (output.isEmpty()) {
            return null;
        }
        else {
            return output.getFirst();
        }
    }

    //finds all objects in the database that share a specific subfield of that object
    public Collection<T> readAll(T t, String var) {
        SearchOperation readAll = (search, inbase) -> {return inbase;};
        return search(t, var, readAll, database.iterator());
    }
    public Collection<T> readAll() {
        return database;
    }

    public void update(T newvalue, String var) {
        Iterator<T> iter = database.iterator();
        SearchOperation update = (search, inbase) -> { iter.remove(); return null;};
        search(newvalue, var, update, iter);
        database.add(newvalue);
    }

    public void delete(T t) {
        database.remove(t);
    }

    public void clear() {
        database.clear();
    }

    private ArrayList<T> search(T t, String var, SearchOperation operate, Iterator<T> iter) {
        ArrayList<T> output = new ArrayList<>();
        try {
            Method temp = t.getClass().getMethod(var, null);
            while (iter.hasNext()) {
                T next = iter.next();
                Object nextinbase = temp.invoke(next);
                Object searchedfor = temp.invoke(t);
                if (nextinbase!= null && nextinbase.equals(searchedfor)) {
                    output.add((T) operate.doSomething(t, next));
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return output;
    }
}
