package bgu.spl.mics;

/**
 * a callback is a function designed to be called when a message is received.
 */


/*
Dafna - please remember this 3 importants behvior of anonymus class:
An anonymous class has access to members of it’s enclosing class.
An anonymous class/lambda cannot access variables in it’s enclosing scope that are not declared as final or effectively final.
Anonymous classes only: A declaration of a variable in an anonymous class shadows any other declaration in it’s enclosing scope.(Lambdas cannot redeclare local variables from enclosing scope).
 */
public interface Callback<T> {

    public void call(T c);

}
