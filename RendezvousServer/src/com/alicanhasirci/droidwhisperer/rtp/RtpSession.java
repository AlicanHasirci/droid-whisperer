package com.alicanhasirci.droidwhisperer.rtp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RtpSession {

    private InetAddress remoteAddress;
    private int remotePort;
    private DatagramSocket datagramSocket;
    private boolean running;
    private ExecutorService executorService;
    private List<RtpListener> rtpListeners;
    private RtpParser rtpParser;

    public RtpSession(DatagramSocket socket) {
        running = false;
        datagramSocket = socket;
        rtpListeners = new ArrayList<RtpListener>();
        rtpParser = new RtpParser();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void start() {
        running = true;
        executorService.submit(new Receiver());
    }

    public void stop() {
        running = false;
    }

    public void addRtpListener(RtpListener rtpListener) {
        rtpListeners.add(rtpListener);
    }

    public void send(RtpPacket rtpPacket) {
        byte[] buf = rtpParser.encode(rtpPacket);
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length,
                remoteAddress, remotePort);
        try {
            if (!datagramSocket.isClosed()) {
                datagramSocket.send(datagramPacket);
            }
        } catch (IOException e) {
        	
        }
    }

    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    class Receiver implements Runnable {

        @Override
        public void run() {
            if (!running) {
                datagramSocket.close();
                return;
            }
            int receiveBufferSize;
            try {
                receiveBufferSize = datagramSocket.getReceiveBufferSize();
            } catch (SocketException e) {
//                Cannot get the datagram socket size
                return;
            }
            byte[] buf = new byte[receiveBufferSize];
            DatagramPacket datagramPacket = new DatagramPacket(buf,
                    buf.length);
            try {
                datagramSocket.receive(datagramPacket);
            } catch (SocketTimeoutException e) {
                executorService.execute(this);
                return;
            } catch (IOException e) {
//                Cannot recieve the packet
                return;
            }
            InetAddress remoteAddress = datagramPacket.getAddress();
            if (remoteAddress != null &&
                    !remoteAddress.equals(RtpSession.this.remoteAddress)) {
                RtpSession.this.remoteAddress = remoteAddress;
            }
            int remotePort = datagramPacket.getPort();
            if (remotePort != RtpSession.this.remotePort) {
                RtpSession.this.remotePort = remotePort;
            }
            byte[] data = datagramPacket.getData();
            int offset = datagramPacket.getOffset();
            int length = datagramPacket.getLength();
            byte[] trimmedData = new byte[length];
            System.arraycopy(data, offset, trimmedData, 0, length);
            RtpPacket rtpPacket = rtpParser.decode(trimmedData);
            for (RtpListener rtpListener: rtpListeners) {
                rtpListener.receivedRtpPacket(rtpPacket);
            }
            executorService.execute(this);
        }

    }

    public boolean isSocketClosed() {
        return datagramSocket.isClosed();
    }

}
