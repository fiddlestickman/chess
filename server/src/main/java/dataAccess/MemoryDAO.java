package dataAccess;

import model.GameData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

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
        SearchOperation update = (search, inbase) -> {return inbase;};
        ArrayList<T> output = search(t, var, update);
        if (output.isEmpty()) {
            return null;
        }
        else {
            return output.getFirst();
        }
    }

    //finds all objects in the database that share a specific subfield of that object
    public Collection<T> readAll(T t, String var) {
        SearchOperation update = (search, inbase) -> {return inbase;};
        return search(t, var, update);
    }

    public void update(T newvalue, String var) {
        SearchOperation update = (search, inbase) -> { database.remove((T) inbase); database.add((T) search); return null;};
        search(newvalue, var, update);
    }

    public void delete(T t) {
        database.remove(t);
    }

    public void clear() {
        database.clear();
    }

    private ArrayList<T> search(T t, String var, SearchOperation operate) {
        ArrayList<T> output = new ArrayList<>();
        Iterator<T> iter = database.iterator();
        try {
            Method temp = t.getClass().getMethod(var, null);
            while (iter.hasNext()) {
                T next = iter.next();
                Object nextinbase = temp.invoke(next);
                Object searchedfor = temp.invoke(t);
                if (nextinbase.equals(searchedfor)) {
                    output.add((T) operate.doSomething(t, next));
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return output;
    }
}
