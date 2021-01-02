package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.BGRSEncoderDecoder;
import bgu.spl.net.api.BGRSProtocol;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        BaseServer TPCserver = new BaseServer(Integer.parseInt(args[0]), () -> new BGRSProtocol(), BGRSEncoderDecoder::new) {
            @Override
            protected void execute(BlockingConnectionHandler handler) {
                System.out.println("executed");
                new Thread(handler).start();
            }
        };
        TPCserver.serve();
    }
}
