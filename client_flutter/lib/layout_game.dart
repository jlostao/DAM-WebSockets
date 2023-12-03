import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'app_data.dart';

class LayoutGame extends StatefulWidget {
  const LayoutGame({Key? key}) : super(key: key);

  @override
  _LayoutGameState createState() => _LayoutGameState();
}

class _LayoutGameState extends State<LayoutGame> {
  @override
  Widget build(BuildContext context) {
    AppData appData = Provider.of<AppData>(context);

    return CupertinoPageScaffold(
      navigationBar: const CupertinoNavigationBar(
        middle: Text("Memory Game"),
      ),
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            // Player Info (Top-left corner)
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Row(
                children: [
                  Text(
                    "Player: ${appData.username}",
                    style: const TextStyle(
                        fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(width: 16),
                  Text(
                    "Points: ${appData.points}",
                    style: const TextStyle(
                        fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                ],
              ),
            ),
            // Cards Grid (Center)
            Container(
              width:
                  MediaQuery.of(context).size.width * 0.8, // Adjust as needed
              height:
                  MediaQuery.of(context).size.height * 0.5, // Adjust as needed
              child: GridView.builder(
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 4,
                  crossAxisSpacing: 8.0,
                  mainAxisSpacing: 8.0,
                  childAspectRatio: 1.0, // Maintain square aspect ratio
                ),
                itemCount: 16, // Adjust as needed
                itemBuilder: (context, index) {
                  // Add your card widget here
                  return Card(
                    // Your card content
                    child: Center(
                      child: Text("$index"),
                    ),
                  );
                },
                shrinkWrap:
                    true, // Allow the GridView to take the minimum space
                physics:
                    const NeverScrollableScrollPhysics(), // Disable scrolling
              ),
            ),
            // Other game-related widgets can be added below the GridView
          ],
        ),
      ),
    );
  }
}
