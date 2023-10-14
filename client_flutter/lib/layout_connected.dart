import 'package:flutter/cupertino.dart';
import 'package:provider/provider.dart';
import 'app_data.dart';

class LayoutConnected extends StatefulWidget {
  const LayoutConnected({Key? key}) : super(key: key);

  @override
  State<LayoutConnected> createState() => _LayoutConnectedState();
}

class _LayoutConnectedState extends State<LayoutConnected> {
  final _messageController = TextEditingController();
  final FocusNode _messageFocusNode = FocusNode();

  @override
  Widget build(BuildContext context) {
    AppData appData = Provider.of<AppData>(context);

    return CupertinoPageScaffold(
        navigationBar: const CupertinoNavigationBar(
          middle: Text("WebSockets Client"),
        ),
        child: Column(
          children: [
            const SizedBox(height: 52),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const SizedBox(width: 8),
                const Text(
                  "Connected to ",
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.w200),
                ),
                const SizedBox(width: 8),
                Text(
                  "ws://${appData.ip}:${appData.port}",
                  style: const TextStyle(
                      fontSize: 16, fontWeight: FontWeight.w400),
                ),
                Expanded(child: Container()),
                SizedBox(
                  width: 140,
                  height: 32,
                  child: CupertinoButton(
                    onPressed: () {
                      appData.disconnectFromServer();
                    },
                    padding: EdgeInsets.zero,
                    child: const Text(
                      "Disconnect",
                      style: TextStyle(fontSize: 14),
                    ),
                  ),
                ),
                const SizedBox(width: 8),
              ],
            ),
            const SizedBox(height: 8),
            Expanded(
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const SizedBox(width: 8),
                  Expanded(
                      flex: 2,
                      child: ListView.builder(
                          primary: false,
                          padding: EdgeInsets.zero,
                          itemCount: 1,
                          itemBuilder: (BuildContext context, int index) {
                            return Text(
                              appData.messages,
                              style: const TextStyle(
                                  fontSize: 16, fontWeight: FontWeight.w200),
                            );
                          })),
                  const SizedBox(width: 8),
                  Container(
                      color: const Color.fromRGBO(240, 240, 240, 1),
                      width: 142,
                      child: ListView.builder(
                        itemCount: appData.clients.length,
                        itemBuilder: (context, index) {
                          final client = appData.clients[index];
                          return Text(
                              client); // Afegeix el Text com a fill del ListView
                        },
                      )),
                  const SizedBox(width: 8),
                ],
              ),
            ),
            const SizedBox(height: 8),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const SizedBox(width: 8),
                const Text(
                  "Message: ",
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.w200),
                ),
                Expanded(
                  child: CupertinoTextField(
                    controller: _messageController,
                    focusNode: _messageFocusNode,
                    onSubmitted: (value) {
                      appData.broadcastMessage(_messageController.text);
                      _messageController.text = "";
                      FocusScope.of(context).requestFocus(_messageFocusNode);
                    },
                  ),
                ),
                const SizedBox(width: 8),
                SizedBox(
                  width: 140,
                  height: 32,
                  child: CupertinoButton.filled(
                    onPressed: () {
                      appData.broadcastMessage(_messageController.text);
                      _messageController.text = "";
                      FocusScope.of(context).requestFocus(_messageFocusNode);
                    },
                    padding: EdgeInsets.zero,
                    child: Text(
                      appData.selectedClient == "" ? "Broadcast" : "Send",
                      style: const TextStyle(fontSize: 14),
                    ),
                  ),
                ),
                const SizedBox(width: 8),
              ],
            ),
            const SizedBox(height: 8),
          ],
        ));
  }
}
