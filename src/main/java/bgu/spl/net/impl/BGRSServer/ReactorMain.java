package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.BGRSEncoderDecoder;
import bgu.spl.net.api.BGRSProtocol;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Server;

import java.io.Serializable;
import java.util.function.Supplier;

public class ReactorMain {
    public static void main(String[] args) {
        Server.reactor(
                Integer.parseInt(args[1]),
                Integer.parseInt(args[0]), //port
                () -> new BGRSProtocol(), //protocol factory
                BGRSEncoderDecoder::new //message encoder decoder factory
        ).serve();
        Database DB = Database.getInstance();
        DB.clear();
    }
}
