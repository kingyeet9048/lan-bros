package cs410.lanbros.networkhandler;

import java.io.IOException;
import java.util.Map;

import cs410.lanbros.networkhandler.Server.Request;

public abstract class NetPacket {

    abstract String getCommand();

    public abstract void clientExecute(Map map);

    public abstract void serverExecute(Request request) throws IOException;
}
