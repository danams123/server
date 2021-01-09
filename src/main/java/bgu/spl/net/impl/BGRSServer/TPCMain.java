package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.BGRSEncoderDecoder;
import bgu.spl.net.api.BGRSMessage;
import bgu.spl.net.api.BGRSProtocol;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        BaseServer<BGRSMessage> TPCserver = new BaseServer<BGRSMessage>(Integer.parseInt(args[0]), () -> new BGRSProtocol(), BGRSEncoderDecoder::new) {
            @Override
            protected void execute(BlockingConnectionHandler<BGRSMessage> handler) {
                new Thread(handler).start();
            }
        };
        TPCserver.serve();
        //clearing the database for next use
        Database DB = Database.getInstance();
        DB.clear();
    }
}
