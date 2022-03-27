package networkhandler.shared.java;

import networkhandler.server.java.Request;

import java.io.IOException;
import java.util.Map;

public abstract class NetPacket {

    abstract String getCommand();

    public abstract void clientExecute(Map map);

    public abstract void serverExecute(Request request) throws IOException;
}
