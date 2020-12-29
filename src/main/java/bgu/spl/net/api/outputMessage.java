package bgu.spl.net.api;

import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class outputMessage implements Serializable {

    private String OPmessage;
    private String output;

    public void setOPmessage(String OPmessage){this.OPmessage = OPmessage;}
    public void setOutput(String output){this.output = output;}


}
