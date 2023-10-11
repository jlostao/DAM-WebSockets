# JavaFx + Flutter websocket clients for Java server
 
En aquest projecte hi ha dos clients de chat Flutter i JavaFX per un servidor de websockets Java

### Funcionament Servidor Java ###

Cal el 'Maven' per compilar el projecte
```bash
cd server_java
mvn clean
mvn compile
```

Per executar el projecte a Windows cal
```bash
cd server_java
.\run.ps1 com.project.Main
```

Per executar el projecte a Linux/macOS cal
```bash
cd server_java
./run.sh com.project.Main
```

El servidor té un client basat en text per provar el funcionament del servidor:
```bash
cd server_java
./run.sh com.project.Client
```

### Funcionament Client JavaFX ###

Cal el 'Maven' per compilar el projecte
```bash
cd client_javafx
mvn clean
mvn compile
```

Per executar el projecte a Windows cal
```bash
cd client_javafx
.\run.ps1 com.project.Main
```

Per executar el projecte a Linux/macOS cal
```bash
cd client_javafx
./run.sh com.project.Main
```

### Funcionament Client Flutter ###
Adaptar les següents linies al sistema que calgui (macos, linux, windows)
```bash
cd client_flutter
flutter clean
flutter create . --platforms=linux
flutter run -d linux
```
