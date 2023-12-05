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
  final List<bool> _flipped =
      List<bool>.filled(16, false); // New list to track flipped state
  List<GlobalKey<FlipCardState>> cardKeys =
      List.generate(16, (_) => GlobalKey<FlipCardState>());

  int? firstClickedIndex;
  Color? firstClickedColor;

  bool isCooldown = false;
  int? lastFlippedIndex;

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
                        return GestureDetector(
                          onTap: () {
                            if (!isCooldown) {
                              int currentIndex = i * 4 + j;
                              print(appData.matchedCards);
                              if (appData.matchedCards.contains(currentIndex)) {
                                return;
                              }

                              appData.flipCard(i, j, appData.userName);

                              // If there is a last flipped card, check if it's different from the current one
                              if (lastFlippedIndex != null &&
                                  lastFlippedIndex != currentIndex) {
                                // Check if the two selected cards have the same color

                                print(appData.cardColors);
                                if (appData.cardColors[lastFlippedIndex!] ==
                                    appData.cardColors[currentIndex]) {
                                  // Matching pair, update game logic as needed (e.g., scoring)
                                  // You might not need to do anything specific here if the game handles matching pairs elsewhere
                                  appData.matchedCards.add(lastFlippedIndex!);
                                  appData.matchedCards.add(currentIndex);
                                } else {
                                  // Non-matching pair, hide both cards after a delay
                                  // Set cooldown to prevent further clicks during the animation
                                  setState(() {
                                    isCooldown = true;
                                  });

                                  // Reset the last flipped index after a delay (e.g., 1 second)
                                  Future.delayed(Duration(seconds: 1), () {
                                    setState(() {
                                      isCooldown = false;
                                      // Hide both cards after a second if they are not a matching pair
                                      appData.hideCards(
                                          [lastFlippedIndex!, currentIndex]);
                                      lastFlippedIndex = null;
                                    });
                                  });
                                }
                              } else {
                                // Set last flipped index for the first card
                                lastFlippedIndex = currentIndex;
                              }

                              // Set cooldown for tapping cells
                              setState(() {
                                isCooldown = true;
                              });

                              // Reset cooldown after a delay (e.g., 1 second)
                              Future.delayed(Duration(seconds: 1), () {
                                setState(() {
                                  isCooldown = false;
                                });
                              });
                            }
                          },
                          child: FlipCard(
                            key: cardKeys[index],
                            flipOnTouch: false,
                            onFlip: () {
                              // Handle onFlip if needed
                            },
                            front: Card(
                              shape: RoundedRectangleBorder(
                                side: const BorderSide(
                                    color: Colors.black, width: 2),
                                borderRadius: BorderRadius.circular(12),
                              ),
                              elevation: 2,
                              child: Container(
                                decoration: BoxDecoration(
                                  color:
                                      appData.cardColors[index] ?? Colors.white,
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
                                side: const BorderSide(
                                    color: Colors.black, width: 2),
                                borderRadius: BorderRadius.circular(12),
                              ),
                              elevation: 2,
                              child: Container(
                                decoration: BoxDecoration(
                                  color:
                                      appData.cardColors[index] ?? Colors.white,
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
