package cs410.lanbros.Testing;

import org.junit.Test;

import cs410.lanbros.networkhandler.Server.Server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

public class UseCaseTesting {

    Server server;
    Thread testingThread;

    @Before
    public void setUp() {
        server = new Server(4321, 5);
        testingThread = new Thread(server);
        testingThread.start();

        System.out.println("Setup successfull");
    }

    @Test
    public void serverStartedSuccessfully() {
        System.out.println("Making sure server can start successfully");
        assertEquals(server.getServer().isBound(), true);
    }

    @After
    public void tearDown() {
        try {
            server.getServer().close();
        } catch (IOException e) {
        }

        assertEquals(server.getServer().isClosed(), true);
        System.out.println("Teardown successfull");
    }

}
