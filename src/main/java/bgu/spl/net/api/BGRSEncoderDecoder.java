package bgu.spl.net.api;

import bgu.spl.net.impl.rci.Command;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BGRSEncoderDecoder implements MessageEncoderDecoder<BGRSMessage> {

    private byte[] bytes = new byte[1 << 10];
    private int len = 0;
    private short OPcode = 0;
    private String output = null;

    @Override
    public BGRSMessage decodeNextByte(byte nextByte) {
        //getting the first to bytes - the OPcode.
        if (OPcode == 0) {
            //seconde byte of the OPcode
            if (len == 1) {
                OPcode = (short) ((bytes[0] & 0xff) << 8);
                OPcode += (short) (nextByte & 0xff);
                //reset the bytes array after saving the OPcode
                len = 0;
                if(OPcode != 4 && OPcode != 11){
                    //in case OPcode is 4 or 11 we automatically send it to the protocol because we dont need any more
                    // bytes to the message
                    return null;
                }
            } else {
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
        byte[] bytesArr = new byte[5];
        //checking if it's ack or error message
        if(msg.getACKER().equals("ACK")){
            OPcode = 12;
        }
        else{
            OPcode = 13;
        }
        //encoding the OPcode
        bytesArr[0] = (byte)((OPcode >> 8) & 0xFF);
        bytesArr[1] = (byte)(OPcode & 0xFF);
        bytesArr[2] = (byte)((msg.getOPcode() >> 8) & 0xFF);
        bytesArr[3] = (byte)(msg.getOPcode() & 0xFF);
        OPcode = 0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            //if we need to add optional output
            if(msg.getCase()) {
                bytesArr[4] = '\n';
                outputStream.write(bytesArr);
                outputStream.write((msg.getOutput() + '\0').getBytes());
            }
            else{
                // no optional output is added - '\0' ends the message
                bytesArr[4] = '\0';
                outputStream.write(bytesArr);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("cannot serialize object", e);
        }
    }
    //TODO need to check a different style

    //OPcode 1,2,3
    private BGRSMessage dCase1(byte nextByte){
        //the first word
        if (nextByte == '\0' && output == null) {
            output = popString();
            return null;
        }
        //the second word
        else if(nextByte == '\0') {
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
        //the second byte of courseNum
        if(len == 1){
            short courseNum = (short) ((bytes[0] & 0xff) << 8);
            courseNum += (short) (nextByte & 0xff);
            len = 0;
            short OP = OPcode;
            OPcode = 0;
            return new BGRSMessage(OP,courseNum);
        }
        //the first byte of courseNum
        pushByte(nextByte);
        return null;
    }

    //OPcode 8
    private BGRSMessage dCase3(byte nextByte){
        //get the first and only word
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
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }
}
