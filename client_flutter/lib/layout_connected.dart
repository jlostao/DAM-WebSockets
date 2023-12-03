import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'app_data.dart';
import 'layout_game.dart';

class LayoutConnected extends StatefulWidget {
  const LayoutConnected({Key? key}) : super(key: key);

  @override
  State<LayoutConnected> createState() => _LayoutConnectedState();
}

class _LayoutConnectedState extends State<LayoutConnected> {
  final _textController = TextEditingController();

  Widget _buildTextFormField(String label, TextEditingController controller) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Container(
          constraints: const BoxConstraints(maxWidth: 200),
          child: CupertinoTextField(controller: controller),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    AppData appData = Provider.of<AppData>(context);

    return Center(
      child: SizedBox(
        width: double.infinity,
        height: double.infinity,
        child: CupertinoPageScaffold(
          navigationBar: const CupertinoNavigationBar(
            middle: Text("WebSockets Client"),
          ),
          child: Center(
            child: ListView(
              padding: const EdgeInsets.all(20),
              children: [
                const SizedBox(height: 50),
                _buildTextFormField("Enter Username", _textController),
                const SizedBox(height: 20),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    SizedBox(
                      width: 96,
                      height: 32,
                      child: CupertinoButton.filled(
                        onPressed: () {
                          // Call the AppData method to set the username
                          appData.setUsername(_textController.text);
                        },
                        padding: EdgeInsets.zero,
                        child: const Text(
                          "Submit",
                          style: TextStyle(
                            fontSize: 14,
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(width: 20),
                    SizedBox(
                      width: 96,
                      height: 32,
                      child: CupertinoButton(
                        onPressed: () {
                          // Add code to navigate to the game layout
                          Navigator.of(context).push(
                            CupertinoPageRoute(
                              builder: (context) => const LayoutGame(),
                            ),
                          );
                        },
                        padding: EdgeInsets.zero,
                        child: const Text(
                          "Start game",
                          style: TextStyle(
                            fontSize: 14,
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 20),
                // Show the selected username or "Select an username"
                Text(
                  appData.username.isNotEmpty
                      ? "Selected Username: ${appData.username}"
                      : "Select an Username",
                  style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: Colors.blue),
                  textAlign: TextAlign.center,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
