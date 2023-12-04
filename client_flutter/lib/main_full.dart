import 'package:flutter/material.dart';
import 'package:web_socket_channel/io.dart';
import 'dart:convert';

enum ConnectionStatus {
  disconnected,
  connecting,
  connected,
}

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'WebSocket Client',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  String? _myId; // Afegit: Propietat per guardar el nostre ID

  IOWebSocketChannel? _channel;
  final _textController = TextEditingController();
  List<String> _clients = [];
  String? _selectedClient;
  String _messages = "";
  ConnectionStatus _connectionStatus = ConnectionStatus.disconnected;

  String _serverIp = 'localhost';
  String _serverPort = '8888';
  final _ipController = TextEditingController();
  final _portController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _ipController.text = _serverIp;
    _portController.text = _serverPort;
    //_connectToServer();
  }

  @override
  void dispose() {
    _channel!.sink.close();
    super.dispose();
  }

  void _connectToServer() {
    setState(() {
      _connectionStatus = ConnectionStatus.connecting;
    });

    String server = "ws://$_serverIp:$_serverPort";
    _channel = IOWebSocketChannel.connect(server);

    _channel!.stream.listen(
      (message) {
        final data = jsonDecode(message);

        if (_connectionStatus != ConnectionStatus.connected) {
          setState(() {
            _connectionStatus = ConnectionStatus.connected;
          });
        }

        switch (data['type']) {
          case 'list':
            setState(() {
              _clients =
                  (data['list'] as List).map((e) => e.toString()).toList();
              _clients.remove(_myId); // Eliminem el nostre ID de la llista
              _messages += "List of clients: ${data['list']}\n";
            });
            break;
          case 'id':
            _myId = data['value'];
            _messages += "Id received: ${data['value']}\n";
            break;
          case 'connected':
            setState(() {
              _clients.add(data['id']);
              _clients.remove(_myId);
              _messages += "Connected client: ${data['id']}\n";
            });
            break;
          case 'disconnected':
            setState(() {
              String removeId = data['id'];
              if (_selectedClient == removeId) {
                _selectedClient = null;
              }
              _clients.remove(data['id']);
              _messages += "Disconnected client: ${data['id']}\n";
            });
            break;
          default:
            setState(() {
              _messages += "Message: ${data['from']}: ${data['value']}\n";
            });
            break;
        }
      },
      onError: (error) {
        setState(() {
          _connectionStatus = ConnectionStatus.disconnected;
        });
      },
      onDone: () {
        setState(() {
          _connectionStatus = ConnectionStatus.disconnected;
        });
      },
    );
  }

  _disconnectFromServer() {
    _channel!.sink.close();
    setState(() {
      _connectionStatus = ConnectionStatus.disconnected;
      _myId = "";
      _selectedClient = null;
      _clients = [];
      _messages = "";
    });
  }

  _refreshClientsList() {
    final message = {
      'type': 'list',
    };
    _channel!.sink.add(jsonEncode(message));
  }

  _broadcastMessage() {
    final message = {
      'type': 'broadcast',
      'value': _textController.text,
    };
    _channel!.sink.add(jsonEncode(message));
  }

  _privateMessage() {
    if (_selectedClient == null) return;
    final message = {
      'type': 'private',
      'value': _textController.text,
      'destination': _selectedClient,
    };
    _channel!.sink.add(jsonEncode(message));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("WebSocket Client"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          children: <Widget>[
            TextField(
              controller: _ipController,
              decoration: const InputDecoration(labelText: 'Server IP'),
              enabled: _connectionStatus == ConnectionStatus.disconnected,
            ),
            TextField(
              controller: _portController,
              decoration: const InputDecoration(labelText: 'Server Port'),
              enabled: _connectionStatus == ConnectionStatus.disconnected,
            ),
            Row(
              children: [
                ElevatedButton(
                  onPressed: _connectionStatus != ConnectionStatus.disconnected
                      ? null
                      : () {
                          _serverIp = _ipController.text;
                          _serverPort = _portController.text;
                          _connectToServer();
                        },
                  child: const Text('Connectar'),
                ),
                ElevatedButton(
                  onPressed: _connectionStatus == ConnectionStatus.disconnected
                      ? null
                      : () {
                          _disconnectFromServer();
                        },
                  child: const Text('Desconnectar'),
                ),
              ],
            ),
            Text("Id: $_myId"),
            TextField(
                controller: _textController,
                decoration: const InputDecoration(labelText: 'Enter message'),
                enabled: _connectionStatus != ConnectionStatus.disconnected),
            Row(
              children: <Widget>[
                DropdownButton<String>(
                  value: _selectedClient,
                  hint: const Text("Select a client"),
                  items: _clients
                      .map((client) => DropdownMenuItem(
                            value: client,
                            child: Text(client),
                          ))
                      .toList(),
                  onChanged: (value) {
                    setState(() {
                      _selectedClient = value!;
                    });
                  },
                ),
                ElevatedButton(
                  onPressed: _connectionStatus == ConnectionStatus.disconnected
                      ? null
                      : _privateMessage,
                  child: const Text('Private'),
                ),
                ElevatedButton(
                  onPressed: _connectionStatus == ConnectionStatus.disconnected
                      ? null
                      : _broadcastMessage,
                  child: const Text('Broadcast'),
                ),
                ElevatedButton(
                  // Botó afegit per refrescar la llista
                  onPressed: _connectionStatus == ConnectionStatus.disconnected
                      ? null
                      : _refreshClientsList,
                  // Botó afegit per refrescar la llista
                  child: const Text('Refresh Clients'),
                ),
              ],
            ),
            Expanded(
              child: SingleChildScrollView(
                child: Text(_messages),
              ),
            )
          ],
        ),
      ),
    );
  }
}
