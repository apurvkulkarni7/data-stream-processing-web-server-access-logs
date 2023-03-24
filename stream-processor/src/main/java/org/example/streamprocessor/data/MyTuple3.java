package org.example.streamprocessor.data;

import org.apache.flink.api.java.tuple.Tuple3;

public class MyTuple3<T1, T2, T3> extends Tuple3<T1, T2, T3> {

    public MyTuple3(T1 t1, T2 t2, T3 t3) {
        super(t1, t2, t3);
    }

    @Override
    public String toString() {
        return "(" + f0.toString() + ", " + f1.toString() + ", " + f2.toString() + ")";
    }
}