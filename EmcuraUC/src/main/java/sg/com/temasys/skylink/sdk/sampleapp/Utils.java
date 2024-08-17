package sg.com.temasys.skylink.sdk.sampleapp;

public class Utils {

    /**
     * Get the number of remote Peers connected to us.
     *
     * @return
     */
    public static int getNumRemotePeers() {
        int totalInRoom = 1;//getTotalInRoom();
        if (totalInRoom == 0) {
            return 0;
        }
        // The first Peer is the local Peer.
        return totalInRoom - 1;
    }

}
