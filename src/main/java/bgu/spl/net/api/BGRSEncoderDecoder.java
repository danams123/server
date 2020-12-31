package bgu.spl.net.api;

import bgu.spl.net.impl.rci.Command;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BGRSEncoderDecoder implements MessageEncoderDecoder<BGRSMessage> {

    /*
    TODO
     */

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private short OPcode = 0;
    private String output = null;

    @Override
    public BGRSMessage decodeNextByte(byte nextByte) {
      if(OPcode == 0){
          if(len == 1){
            bytes[len] = nextByte;
//            OPcode = ByteBuffer.wrap(bytes).getInt();
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
          short OP = OPcode;
          OPcode = 0;
          return new BGRSMessage(OP);
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
    public byte[] encode(BGRSMessage msg) {
//        return (msg.output + "\n").getBytes();
//        return serializeObject(msg);
        byte[] bytesArr = new byte[5];
        if(msg.getUserName() == "ACK"){
            OPcode = 12;
        }
        else{
            OPcode = 13;
        }
        bytesArr[0] = (byte)((OPcode >> 8) & 0xFF);
        bytesArr[1] = (byte)(OPcode & 0xFF);
        bytesArr[2] = (byte)((msg.getOPcode() >> 8) & 0xFF);
        bytesArr[3] = (byte)(msg.getOPcode() & 0xFF);
        bytesArr[4] = '\n';
        OPcode = 0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            outputStream.write(bytesArr);
            outputStream.write((msg.getOutput() + '\n').getBytes());
            return outputStream.toByteArray( );
        } catch (IOException e) {
            e.printStackTrace();
        }



    }//TODO need to check a different style

    //OPcode 1,2,3
    private BGRSMessage dCase1(byte nextByte){
        if (nextByte == 0 && output == null) {
            output = popString();
            return null;
        }
        else if(nextByte == 0) {
            String toSend = output;
            output = null;
            short OP = OPcode;
            OPcode = 0;
            return new BGRSMessage(OP, toSend, popString());
        }
        pushByte(nextByte);
        return null;
    }

    //OPcode 5,6,7,9,10
    private BGRSMessage dCase2(byte nextByte){
        if(len == 1){
            bytes[len] = nextByte;
            int courseNum = ByteBuffer.wrap(bytes).getInt();
            len = 0;
            short OP = OPcode;
            OPcode = 0;
            return new BGRSMessage(OP,courseNum);
        }
        bytes[len++] = nextByte;
        pushByte(nextByte);
        return null;
    }

    //OPcode 8
    private BGRSMessage dCase3(byte nextByte){
        if(nextByte == 0) {
            short OP = OPcode;
            OPcode = 0;
            return new BGRSMessage(popString(), OP);
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

    private byte[] serializeObject(BGRSMessage message) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            //placeholder for the object size
            for (int i = 0; i < 4; i++) {
                bytes.write(0);
            }

            ObjectOutput out = new ObjectOutputStream(bytes);
            out.writeObject(message);
            out.flush();
            byte[] result = bytes.toByteArray();

            //now write the object size
            ByteBuffer.wrap(result).putInt(result.length - 4);
            return result;

        } catch (Exception ex) {
            throw new IllegalArgumentException("cannot serialize object", ex);
        }
    }
}
