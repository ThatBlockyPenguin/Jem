package com.blockypenguin.gemini.jem.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ForkingList<T> {
    private final List<T> data = new ArrayList<>();
    private int pointer = 0;
    
    public Optional<T> peek() {
        if(pointer >= 0 && pointer < data.size())
            return Optional.of(data.get(pointer));
        
        return Optional.empty();
    }
    
    public boolean hasAt(int offset) {
        var newPointer = pointer + offset;
        return 0 <= newPointer && newPointer < data.size();
    }
    
    public void scroll(int amt) {
        pointer = Math.clamp(pointer + amt, 0, data.size() - 1);
    }
    
    public void add(T entry) {
        var sublist = new ArrayList<>(data.subList(0, Math.min(pointer + 1, data.size())));
        data.clear();
        data.addAll(sublist);
        data.add(entry);
        
        scroll(1);
    }
    
    @Override
    public String toString() {
        return "ForkingList(" + pointer + ")" + Arrays.toString(data.toArray());
    }
}
