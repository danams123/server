package bgu.spl.net.impl.echo;

import bgu.spl.net.api.outputMessage;
import bgu.spl.net.impl.rci.Command;

import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class EchoClient {

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            args = new String[]{"localhost", "hello"};
        }

        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }

//        BufferedReader and BufferedWriter automatically using UTF-8 encoding
        try (Socket sock = new Socket(args[0], 7777);
//        try (Socket sock = new Socket(InetAddress.getLocalHost(), 7777);
             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {

            System.out.println("sending message to server");
//
//                outputMessage e = new outputMessage();
//                e.setOutput("ACK" + 7);
//                ArrayList<String> d = new ArrayList<>();
//                d.add("ahufferson");
//                d.add("hhhaddock");
//                ConcurrentHashMap<Object, Object> courseStat = new ConcurrentHashMap<>();
//                courseStat.putIfAbsent("Course", "(" + 352 + ") Dan" );
//                courseStat.putIfAbsent("Seats Available",22 + "/" + 25);
//                courseStat.putIfAbsent("Students", d);
//                e.setCourseStat(courseStat);
//        System.out.println(courseStat);
//            FileOutputStream fileOut =
//                    new FileOutputStream("./c.txt");
//            ObjectOutputStream OUT = new ObjectOutputStream(fileOut);
//            OUT.writeObject(courseStat.toString());
//            OUT.close();
//            fileOut.close();

            out.write(args[1]);
//            out.write("Hello");
//            out.write(String.valueOf(c));
            out.newLine();
            out.flush();

            System.out.println("awaiting response");
            String line = in.readLine();
            System.out.println("message from server: " + line);
        }
    }
}
