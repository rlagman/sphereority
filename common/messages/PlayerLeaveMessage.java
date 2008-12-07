package common.messages;

import common.Constants;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PlayerLeaveMessage - Notifies that a player is leaving a game.
 * @author rlagman
 */
public class PlayerLeaveMessage extends Message implements MessageConstants, Constants {
	public static Logger logger = Logger.getLogger(CLIENT_LOGGER_NAME);

    
    /**
     * Constructor - Creates a new PlayerLeaveMessage.
     * @param playerId The id of the player sending the message.
     */
    public PlayerLeaveMessage(byte playerId, byte sequenceNumber) {
        super(MessageType.PlayerLeave, playerId, PlayerLeaveLength,sequenceNumber);
    }

    /**
     * Constructor - Creates a new PlayerLeaveMessage.
     * @param header Representation of a Header in bytes.
     * @param data Representation of the data portion in bytes.
     */
    public PlayerLeaveMessage(byte[] header, byte[] data) {
        // Create the Message superclass
        super(header,PlayerLeaveLength);    
    }
    
    /**
	 * Creates an array of bytes to be sent across the network
	 * that represents a Message object.
	 * @return A byte representation of the Message object.
	 */
	public byte[] getByteMessage() throws Exception {
        // Get the header
        byte[] header = getByteHeader();        
        byte[] message = new byte[header.length + dataLength];
        
        // Place the header information
        ByteBuffer buffer = ByteBuffer.wrap(message);
        buffer.put(header);
        
        // Return the fully created message
        return message;
	}
}
