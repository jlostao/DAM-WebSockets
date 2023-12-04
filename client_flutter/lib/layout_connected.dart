import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flip_card/flip_card.dart';
import 'app_data.dart';

class LayoutConnected extends StatefulWidget {
  const LayoutConnected({Key? key}) : super(key: key);

  @override
  State<LayoutConnected> createState() => _LayoutConnectedState();
}

class _LayoutConnectedState extends State<LayoutConnected> {
  final List<bool> _hovering = List<bool>.filled(16, false);
  List<GlobalKey<FlipCardState>> cardKeys =
      List.generate(16, (_) => GlobalKey<FlipCardState>());

  int? firstClickedIndex;
  Color? firstClickedColor;

  @override
  void initState() {
    super.initState();
    AppData appData = Provider.of<AppData>(context, listen: false);
    appData.addPlayer(appData.userName);
  }

  @override
  Widget build(BuildContext context) {
    AppData appData = Provider.of<AppData>(context);

    return CupertinoPageScaffold(
      navigationBar: const CupertinoNavigationBar(
        middle: Text("Memory Game"),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Left Half: Player Info and End Game Button
            Expanded(
              flex: 1,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  // Player Info
                  Text(
                    "Current Player: ${appData.turn}",
                    style: const TextStyle(
                        fontSize: 16, fontWeight: FontWeight.w400),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    "Opponent: ${appData.wait}",
                    style: const TextStyle(
                        fontSize: 16, fontWeight: FontWeight.w400),
                  ),
                  const SizedBox(height: 16),
                  // End Game Button
                  CupertinoButton(
                    onPressed: () {
                      appData.disconnectFromServer();
                    },
                    padding: EdgeInsets.zero,
                    color: Colors.red,
                    child: const Text(
                      "End Game",
                      style: TextStyle(fontSize: 14),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(width: 16), // Space between halves
            // Right Half: Game Grid
            Expanded(
              flex: 2,
              child: Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: List.generate(
                    4,
                    (i) => Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: List.generate(4, (j) {
                        int index = i * 4 + j;
                        return MouseRegion(
                          onEnter: (_) {
                            setState(() {
                              _hovering[index] = true;
                            });
                          },
                          onExit: (_) {
                            setState(() {
                              _hovering[index] = false;
                            });
                          },
                          child: GestureDetector(
                            onTap: () {
                              // Pass the context to the flipCard function
                              appData.flipCard(i, j, appData.userName, context);
                            },
                            child: FlipCard(
                              key: cardKeys[index],
                              flipOnTouch: false,
                              onFlip: () {
                                setState(() {
                                  _hovering[index] = false;
                                });
                              },
                              front: Card(
                                shape: RoundedRectangleBorder(
                                  side:
                                      BorderSide(color: Colors.black, width: 2),
                                  borderRadius: BorderRadius.circular(12),
                                ),
                                elevation: _hovering[index] ? 8 : 2,
                                child: Container(
                                  decoration: BoxDecoration(
                                    color: _hovering[index]
                                        ? Colors.grey
                                        : appData.cardColors[index] ??
                                            Colors.white,
                                    borderRadius: BorderRadius.circular(12),
                                  ),
                                  height: 80,
                                  width: 80,
                                  child: const Center(
                                    child: Text(
                                      "Tap",
                                      style: TextStyle(
                                        fontSize: 16,
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ),
                                  ),
                                ),
                              ),
                              back: Card(
                                shape: RoundedRectangleBorder(
                                  side:
                                      BorderSide(color: Colors.black, width: 2),
                                  borderRadius: BorderRadius.circular(12),
                                ),
                                elevation: _hovering[index] ? 8 : 2,
                                child: Container(
                                  decoration: BoxDecoration(
                                    color: _hovering[index]
                                        ? Colors.grey
                                        : appData.cardColors[index] ??
                                            Colors.white,
                                    borderRadius: BorderRadius.circular(12),
                                  ),
                                  height: 80,
                                  width: 80,
                                  child: const Center(
                                    child: Text(
                                      "Tap",
                                      style: TextStyle(
                                        fontSize: 16,
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ),
                                  ),
                                ),
                              ),
                            ),
                          ),
                        );
                      }),
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
