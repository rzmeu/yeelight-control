package com.rzmeu.yeelightcontrol.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rzmeu.yeelightcontrol.Util;
import com.rzmeu.yeelightcontrol.model.Command;
import com.rzmeu.yeelightcontrol.model.DeviceInfo;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class ControlService {

    private static final String DISCOVERY_MSG = "M-SEARCH * HTTP/1.1\r\n" + "HOST:239.255.255.250:1982\r\n"
            + "MAN:\"ssdp:discover\"\r\n" + "ST:wifi_bulb\r\n";

    private static final Gson gson = new Gson();

    public void executeCommand() {
        List<DeviceInfo> deviceInfoList = getDevicesInfo();

        deviceInfoList.forEach(this::executeCommand);
    }

    @SneakyThrows
    private List<DeviceInfo> getDevicesInfo() {
        List<DeviceInfo> deviceInfoList;
        File file = new File("devices.json");

        if(file.exists()) {
            Reader reader = Files.newBufferedReader(Paths.get(file.getPath()));
            deviceInfoList = gson.fromJson(reader, new TypeToken<List<DeviceInfo>>() {}.getType());
        } else {
            deviceInfoList = searchDevices();
            try(FileWriter writer = new FileWriter(file)) {
                gson.toJson(deviceInfoList, writer);
                writer.flush();
            }
        }

        return deviceInfoList;
    }

    @SneakyThrows
    public List<DeviceInfo> searchDevices() {
        System.out.println("Searching...");

        byte[] sendData;
        byte[] receiveData = new byte[1024];

        sendData = DISCOVERY_MSG.getBytes();

        /* create a packet from our data destined for 239.255.255.250:1900 */
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("239.255.255.250"), 1982);

        /* send packet to the socket we're creating */
        DatagramSocket clientSocket = new DatagramSocket();
        clientSocket.send(sendPacket);

        /* receive response and store in our receivePacket */
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        /* get the response as a string */
        String response = new String(receivePacket.getData());
        clientSocket.close();

        System.out.println(response);
        String[] lines = response.split("\r\n");

        DeviceInfo deviceInfo = new DeviceInfo();
        Arrays.asList(lines).forEach(line -> {
            if (line.contains("id")) {
               deviceInfo.setId(line.split("id: ")[1]);
            }

            if (line.contains("Location")) {
                String[] address = line.split("Location: yeelight://")[1].split(":");
                deviceInfo.setHost(address[0]);
                deviceInfo.setPort(Integer.parseInt(address[1]));
            }
        });

        return List.of(deviceInfo);
    }

    @SneakyThrows
    private void executeCommand(DeviceInfo deviceInfo) {
        Command command = new Command();
        command.setId(1);
        command.setMethod("toggle");
        command.setParams(List.of());
        String jsonCommand = Util.convertObjectToJson(command);

        try (Socket socket = new Socket(deviceInfo.getHost(), deviceInfo.getPort())) {

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(jsonCommand);

            InputStream input = socket.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

}
