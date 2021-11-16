package test.java;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import cs410.lanbros.networkhandler.Factory;
import cs410.lanbros.networkhandler.Client.Client;
import cs410.lanbros.networkhandler.Server.Server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

@Category(UseCaseTest.class)
public class UseCaseTest {

    static Server server;
    static Client client;
    static Factory factory;
    static Thread testingThread;
    static Thread clientThread;

    @BeforeClass
    public static void setUp() {
        server = new Server(4321, 5, factory);
        testingThread = new Thread(server);
        testingThread.start();
        factory = new Factory();
        factory.makeBaseAPIRegistry();
        factory.setHost(true);
        factory.setPort(4321);
        factory.setMAX_PLAYERS(5);
        try {
            factory.setServerAddress(client.getThisMachineIP());
        } catch (UnknownHostException e) {
            fail(e.getMessage());
        }
        factory.setServerPort(4321);
        client = factory.makeClient();
        clientThread = new Thread(client);

        System.out.println("Setup successfull");
    }

    /**
     * Addresses use case number 5 (player can host a game)
     */
    @Test
    public void serverStartedSuccessfully() {
        System.out.println("Making sure server can start successfully");
        assertEquals(server.getServer().isBound(), true);
    }

    /**
     * Addresses use case number 2 (player can join a game)
     */
    @Test
    public void clientJoined() {
        System.out.println("Making sure the client was able to join a 'game'");
        assertEquals(client.joinGame(), true);
        clientThread.start();
    }

    @AfterClass
    public static void tearDown() {
        try {
            server.getServer().close();
            client.getSocket().close();
        } catch (IOException e) {
            fail(e.getMessage());
        }

        assertEquals(server.getServer().isClosed(), true);
        assertEquals(client.getSocket().isClosed(), true);
        System.out.println("Teardown successfull");
    }

}
