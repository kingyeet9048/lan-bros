package cs410.lanbros.network;

/**
 * @CreatedBy Sulaiman Bada
 *
 */
interface Packet<E> {

	 E getData();
	 PacketType getPacketType();
	 void isServerPacket();
}
