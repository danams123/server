package bgu.spl.net.api;

import bgu.spl.net.impl.rci.Command;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BGRSEncoderDecoder implements MessageEncoderDecoder<BGRSMessage> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private short OPcode = 0;
    private String output = null;

    @Override
    public BGRSMessage decodeNextByte(byte nextByte) {
        System.out.println("in decode, OPcode: " + OPcode);
        System.out.println("the byte: " + nextByte);
        if (OPcode == 0) {
            System.out.println("in OP = 0");
            if (len == 1) {
                System.out.println("in len = 1");
//            OPcode = ByteBuffer.wrap(bytes).getInt();
                OPcode = (short) ((bytes[0] & 0xff) << 8);
                OPcode += (short) (nextByte & 0xff);
                len = 0;
                if(OPcode != 4 && OPcode != 11){
                    System.out.println("in finish");
                    return null;
                }
//              System.out.println("in len = 1, OPcode: " + OPcode);
            } else {
                System.out.println("in len != 1");
//              System.out.println("in len != 1");
                bytes[len++] = nextByte;
            }
        }
        switch (OPcode) {
            case 1:
            case 2:
            case 3:
                return dCase1(nextByte);
            case 4:
            case 11:
                short OP = OPcode;
                OPcode = 0;
                System.out.println(OP);
                return new BGRSMessage(OP);
            case 8:
                return dCase3(nextByte);
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
                return dCase2(nextByte);
            }
        return null;
        }

    @Override
    public byte[] encode(BGRSMessage msg) {
        System.out.println("in encode");
//        return (msg.output + "\n").getBytes();
//        return serializeObject(msg);
        byte[] bytesArr = new byte[5];
        if(msg.getACKER().equals("ACK")){
            System.out.println("in ack 2");
            OPcode = 12;
        }
        else{
            System.out.println("in error 2");
            OPcode = 13;
        }
        bytesArr[0] = (byte)((OPcode >> 8) & 0xFF);
        bytesArr[1] = (byte)(OPcode & 0xFF);
        bytesArr[2] = (byte)((msg.getOPcode() >> 8) & 0xFF);
        bytesArr[3] = (byte)(msg.getOPcode() & 0xFF);
        System.out.println(bytesArr[2]);
        System.out.println(bytesArr[3]);
        bytesArr[4] = '\n';
        OPcode = 0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            outputStream.write(bytesArr);
            if(msg.getCase()) {
                System.out.println("in special case 2");
                outputStream.write((msg.getOutput() + '\n').getBytes());
            }
            System.out.println("finished encode now send");
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("cannot serialize object", e);
        }
    }
    //TODO need to check a different style

    //OPcode 1,2,3
    private BGRSMessage dCase1(byte nextByte){
        System.out.println("in dcase1");
        System.out.println(nextByte);
        if (nextByte == '\0' && output == null) {
            output = popString();
            System.out.println(output);
            return null;
        }
        else if(nextByte == '\0') {
            String toSend = output;
            output = null;
            short OP = OPcode;
            OPcode = 0;
            System.out.println(toSend);
            return new BGRSMessage(OP, toSend, popString());
        }
        pushByte(nextByte);
        return null;
    }

    //OPcode 5,6,7,9,10
    private BGRSMessage dCase2(byte nextByte){
        System.out.println("in dcase 2");
        if(len == 1){
            System.out.println("in len = 1 in dcase 2");
            short courseNum = (short) ((bytes[0] & 0xff) << 8);
            courseNum += (short) (nextByte & 0xff);
            len = 0;
            short OP = OPcode;
            OPcode = 0;
            return new BGRSMessage(OP,courseNum);
        }
        pushByte(nextByte);
        return null;
    }

    //OPcode 8
    private BGRSMessage dCase3(byte nextByte){
        System.out.println("in dcase 3");
        if(nextByte == '\0') {
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
}
