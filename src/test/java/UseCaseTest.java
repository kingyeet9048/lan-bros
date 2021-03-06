package test.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import networkhandler.client.java.Client;
import networkhandler.server.java.Server;
import networkhandler.shared.java.Factory;

@Category(UseCaseTest.class)
public class UseCaseTest {

    static Server server;
    static Client client;
    static Factory factory;
    static Thread testingThread;
    static Thread clientThread;

    @BeforeClass
    public static void setUp() {
        factory = new Factory();
        factory.startFactory();
        factory.setPlayerUsername("thisNewUser");
        factory.setHost(true);
        try {
            factory.setServerAddress(InetAddress.getLocalHost().getHostAddress().toString());
        } catch (UnknownHostException e) {
            fail(e.getMessage());
        }
        server = factory.makeServer();
        testingThread = new Thread(server);
        testingThread.start();
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
