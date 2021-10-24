package cs410.lanbros.network.packets;

/**
 * @CreatedBy Sulaiman Bada
 *
 */
interface Packet<E> {
	E getData();
	boolean isServerPacket();
	void setPacketReceiver(String packetReceiver);
	String getPacketReceiver();
	void setPacketSender(String packetSender);
	String getPacketSender();
}
