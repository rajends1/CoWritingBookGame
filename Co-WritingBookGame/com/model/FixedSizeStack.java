package com.model;
	
import java.util.NoSuchElementException;



public class FixedSizeStack<E> implements Stack<E> 
{
    private E[] holder;
    private int size;
    private int top;

    @SuppressWarnings("unchecked")
    public FixedSizeStack(int size)
    {
        this.holder = (E[]) new Object[size];
        this.top = -1;
        this.size = size;
    }

    public void push(E element)
    {
        if (top >= size) throw new StackOverflowError("Size = " + size);
        holder[++top] = element;
    }

    public E pop()
    {
    	if (size() == 0) {
            throw new NoSuchElementException("Attempt to pop from empty holder");
        }
    	if (top < 0) throw new IndexOutOfBoundsException();
        
        E element = holder[top--];
        holder[top + 1] = null;
        return element;
    }

    public E peek()
    {
    	if (size() == 0) {
            throw new NoSuchElementException("Attempt to peek from empty holder");
        }
    	if (top < 0) throw new IndexOutOfBoundsException();
        E element = holder[top];
        return element;
    }

    public boolean isEmpty() {
        return size() == 0;
    }
    
    public int size()
    {
        return size;
    }

    public int elements()
    {
        return top + 1;
    }
}

interface Stack<E> {

    void push(E element);

    E pop();

    E peek();

    int size();

    boolean isEmpty();

}