package bgu.spl.net.api;

import bgu.spl.net.impl.rci.Command;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BGRSEncoderDecoder implements MessageEncoderDecoder<Serializable> {

    /*
    TODO change the impl of BGRS message to lambda in encoder decoder that extends command, in process just run execute.
    TODO in execute make 11 options calling the functions in database and add database as a field in encdec.
    TODO change ecndec to a similiar version of RCI using serializable.
    TODO check about the cin and the strings that we send as output, is it good for coursestat etc?
    TODO check if we need a main to run the server and where to use DB.init(its a pacakage only method - from within).
    TODO add a clear method to database when restarting the server
    TODO understand how sendbytes and getbytes work in client c++
     */

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private int OPcode = 0;
    private String output = null;

    @Override
    public Serializable decodeNextByte(byte nextByte) {
      if(OPcode == 0){
          if(len == 1){
            bytes[len] = nextByte;
            OPcode = ByteBuffer.wrap(bytes).getInt();
            len = 0;
          }
          else{
              bytes[len++] = nextByte;
          }
      }
      else if(OPcode == 1 || OPcode == 2 || OPcode == 3){
          return dCase1(nextByte);
      }
      else if(OPcode == 4 || OPcode == 11){
          return new BGRSMessage(OPcode);
      }
      else if(OPcode == 8 || OPcode == 10){
          return dCase2(nextByte);
      }
      else{
          return dCase3(nextByte);
      }
        return null;
    }

    @Override
    public byte[] encode(Serializable msg) {
//        return (msg.output + "\n").getBytes();
        return null;
    }//TODO need to check a different style

    //OPcode 1,2,3
    private Serializable dCase1(byte nextByte){
        if (nextByte == 0 && output == null) {
            output = popString();
            return null;
        }
        else if(nextByte == 0) {
            String toSend = output;
            output = null;
            return new BGRSMessage(OPcode, toSend, popString());
        }
        pushByte(nextByte);
        return null;
    }

    //OPcode 5,6,7,9,10
    private Serializable dCase2(byte nextByte){
        if(len == 1){
            bytes[len] = nextByte;
            int courseNum = ByteBuffer.wrap(bytes).getInt();
            len = 0;
            return new BGRSMessage(OPcode,courseNum);
        }
        bytes[len++] = nextByte;
        pushByte(nextByte);
        return null;
    }

    //OPcode 8
    private Serializable dCase3(byte nextByte){
        if(nextByte == 0) {
            return new BGRSMessage(popString(), OPcode);
        }
        pushByte(nextByte);
        return null;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }
}
