package networkhandler.java;

import java.io.IOException;
import java.util.Map;

import networkhandler.server.java.Request;

public abstract class NetPacket {

    abstract String getCommand();

    public abstract void clientExecute(Map map);

    public abstract void serverExecute(Request request) throws IOException;
}
