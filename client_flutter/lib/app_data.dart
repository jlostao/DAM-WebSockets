import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:web_socket_channel/io.dart';

// Access appData globaly with:
// AppData appData = Provider.of<AppData>(context);
// AppData appData = Provider.of<AppData>(context, listen: false)

enum ConnectionStatus {
  disconnected,
  disconnecting,
  connecting,
  connected,
}

class AppData with ChangeNotifier {
  String ip = "localhost";
  String port = "8888";
  String userName = "";

  IOWebSocketChannel? _socketClient;
  ConnectionStatus connectionStatus = ConnectionStatus.disconnected;

  Map<String, Color?> colorMap = {
    "Red": Colors.red,
    "Blue": Colors.blueGrey,
    "Yellow": Colors.yellow,
    "Green": Colors.green,
    "Orange": Colors.orange,
    "Purple": Colors.purple,
    "Pink": Colors.cyan,
    "Black": Colors.black
  };
  List<Color?> cardColors = List<Color?>.filled(16, null);

  String? mySocketId;
  List<String> clients = [];
  String selectedClient = "";
  int? selectedClientIndex;
  String messages = "";
  String color = "";
  String turn = "";
  String wait = "";

  int? firstClickedIndex;
  Color? firstClickedColor;

  bool file_saving = false;
  bool file_loading = false;

  AppData() {
    _getLocalIpAddress();
  }

  void _getLocalIpAddress() async {
    try {
      final List<NetworkInterface> interfaces = await NetworkInterface.list(
          type: InternetAddressType.IPv4, includeLoopback: false);
      if (interfaces.isNotEmpty) {
        final NetworkInterface interface = interfaces.first;
        final InternetAddress address = interface.addresses.first;
        ip = address.address;
        notifyListeners();
      }
    } catch (e) {
      // ignore: avoid_print
      print("Can't get local IP address : $e");
    }
  }

  void connectToServer() async {
    connectionStatus = ConnectionStatus.connecting;
    notifyListeners();

    // Simulate connection delay
    await Future.delayed(const Duration(seconds: 1));

    _socketClient = IOWebSocketChannel.connect("ws://$ip:$port");
    _socketClient!.stream.listen(
      (message) {
        final data = jsonDecode(message);

        if (connectionStatus != ConnectionStatus.connected) {
          connectionStatus = ConnectionStatus.connected;
        }

        switch (data['type']) {
          case 'id':
            mySocketId = data['value'];
            messages += "Id received: ${data['value']}\n";
            break;
          case 'list':
            // Existing 'list' case
            break;
          case 'connected':
            // Existing 'connected' case
            break;
          case 'disconnected':
            // Existing 'disconnected' case
            break;
          case 'newTurn':
            turn = data['plays'];
            wait = data["waits"];
            break;
          case 'flip':
            color = data['color'];
            final row = data["row"];
            final col = data["col"];
            final index = row * 4 + col;
            cardColors[index] = colorMap[color];
            checkColors(index);
            break;
          default:
            messages += "Message from '${data['from']}': ${data['value']}\n";
            break;
        }

        notifyListeners();
      },
      onError: (error) {
        connectionStatus = ConnectionStatus.disconnected;
        mySocketId = "";
        selectedClient = "";
        clients = [];
        messages = "";
        notifyListeners();
      },
      onDone: () {
        connectionStatus = ConnectionStatus.disconnected;
        mySocketId = "";
        selectedClient = "";
        clients = [];
        messages = "";
        notifyListeners();
      },
    );

    notifyListeners();
  }

  disconnectFromServer() async {
    connectionStatus = ConnectionStatus.disconnecting;
    notifyListeners();

    // Simulate connection delay
    await Future.delayed(const Duration(seconds: 1));

    _socketClient!.sink.close();
  }

  selectClient(int index) {
    if (selectedClientIndex != index) {
      selectedClientIndex = index;
      selectedClient = clients[index];
    } else {
      selectedClientIndex = null;
      selectedClient = "";
    }
    notifyListeners();
  }

  refreshClientsList() {
    final message = {
      'type': 'list',
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  send(String msg) {
    if (selectedClientIndex == null) {
      broadcastMessage(msg);
    } else {
      print("No client selected");
    }
  }

  broadcastMessage(String msg) {
    final message = {
      'type': 'broadcast',
      'value': msg,
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  addPlayer(String user) {
    final message = {
      'type': 'hello',
      'name': user,
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  void flipCard(int row, int col, String user) async {
    final message = {'type': 'flip', 'row': row, 'col': col, 'name': user};
    _socketClient!.sink.add(jsonEncode(message));
    notifyListeners();
  }

  checkColors(int index) {
    if (firstClickedIndex == null) {
      firstClickedIndex = index;
      firstClickedColor = cardColors[index];
    } else {
      if (cardColors[index] != firstClickedColor) {
        Future.delayed(const Duration(seconds: 1), () {
          if (firstClickedIndex != null) {
            cardColors[firstClickedIndex!] = Colors.white;
            cardColors[index] = Colors.white;
          }
        });
      }
    }
  }

  void hideCards(List<int> cardIndices) {
    // Logic to hide cards at the specified indices
    // For example, set the corresponding cardColors to null or some default color
    for (int index in cardIndices) {
      cardColors[index] =
          null; // Set the color to null or another default color
    }

    // Notify listeners to update the UI
    notifyListeners();
  }
}
