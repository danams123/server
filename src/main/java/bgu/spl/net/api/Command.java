package bgu.spl.net.api;

import java.io.Serializable;

public interface Command<T> extends Serializable {

    Serializable execute(T arg);
}
