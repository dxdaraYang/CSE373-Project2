package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!
    private int capacity;
    private int slots;
    
    public ChainedHashDictionary() {
        this.capacity = 0;
        this.slots = 5;
        this.chains = this.makeArrayOfChains(slots);
        fill(chains);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    @Override
    public V get(K key) {
        int hash = hashing(key);
        return chains[indexing(hash, slots)].get(key); 
    }

    @Override
    public void put(K key, V value) {
        if (capacity >= slots) {
            IDictionary<K, V>[] newChains = this.makeArrayOfChains(slots * 2);
            fill(newChains);
            for (int i = 0; i < slots; i++) {
                for (int j = 0; j < chains[i].size(); j++) {
                    if (chains[i].iterator().hasNext()) {
                        KVPair<K, V> pair = chains[i].iterator().next();
                        int oldHash = pair.getKey().hashCode();
                        newChains[indexing(oldHash, slots*2)]
                                .put(pair.getKey(), pair.getValue());
                    }
                }
            }
            this.chains = newChains;
            slots *= 2;
        } 
        if (!containsKey(key)) {
            capacity++;
        }
        int hash = hashing(key);
        chains[indexing(hash, slots)].put(key, value);
    }

    @Override
    public V remove(K key) {
        int hash = hashing(key);
        capacity--;
        return chains[indexing(hash, slots)].remove(key); 
    }

    @Override
    public boolean containsKey(K key) {
        int hash = hashing(key);
        return chains[indexing(hash, slots)].containsKey(key);
    }

    @Override
    public int size() {
        return capacity;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }
    
    private int hashing(K key) {
        int hash = 0;
        if (key != null) {
            hash = key.hashCode();
        }
        return hash;
    }
    
    private int indexing(int hash, int slot) {
        if (hash%slot >= 0) {
            return hash%slot;
        }
        return hash%slot+slot;
    }
    
    private void fill(IDictionary<K, V>[] chain) {
        for (int i = 0; i < chain.length; i++) {
            chain[i] = new ArrayDictionary<K, V>();
        }
    }
    
    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Think about what exactly your *invariants* are. Once you've
     *    decided, write them down in a comment somewhere to help you
     *    remember.
     *
     * 3. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 4. Think about what exactly your *invariants* are. As a 
     *    reminder, an *invariant* is something that must *always* be 
     *    true once the constructor is done setting up the class AND 
     *    must *always* be true both before and after you call any 
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int index;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            if (!this.chains[index].iterator().hasNext()) {
                if (index+1 >= chains.length) {
                    return false;
                } else if (!this.chains[index+1].iterator().hasNext()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public KVPair<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (!this.chains[index].iterator().hasNext()) {
                index++;
            }
            return this.chains[index].iterator().next();
        }
    }
}
