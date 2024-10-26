# Pattern bag of tasks avec base de données
Ce projet implémente un système de gestion de tâches utilisant le pattern "Bag of Tasks" avec détection de pannes. Les tâches sont soumises par des clients et exécutées en parallèle par des workers. Si un worker tombe en panne, le système détecte la panne et réassigne la tâche à un autre worker disponible.

### Fonctionnalités principales
- Exécution parallèle de tâches soumises par des clients.
- Détection de panne de workers via un mécanisme de heartbeat.
- Réassignation automatique des tâches en cas de panne de worker.

### Structure du projet
- Client : soumet des tâches et reçoit les résultats via un callback.
- Serveur (BagOfTasks) : gère la file de tâches et la réassignation des tâches en cas de panne.
- Worker : Exécute les tâches, envoie des heartbeats pour indiquer qu'il est actif, et informe le serveur de la fin de chaque tâche.


### Avant démarrage du projet
Avant de démarrer le projet, il faut exécuter les commandes suivantes dans le répertoire racine du projet pour créer et remplir les tables dans les deux SGBD :
> `export PATH=/usr/gide/jdk-1.8/bin:$PATH`

- Pour la première SGBD (eluard) :
> `javac -cp lib/ojdbc8.jar:. InsertData.java`

> `java -cp lib/ojdbc8.jar:. InsertData`

- Pour la deuxième SGBD (butor) :
> `javac -cp lib/ojdbc8.jar:. InsertDatabdd2.java`

> `java -cp lib/ojdbc8.jar:. InsertDatabdd2`

### Démarrage du projet
- **Compiler le projet** :

Dans le répertoire racine du projet, exécuter la commande suivante :
>`javac -d bin -classpath "lib/ojdbc8.jar" src/partage/*.java src/tachesmono/*.java src/tachesmulti/*.java src/serveur/*.java src/client/*.java src/worker/*.java`

Les classes compilées seront placées dans le répertoire `bin`.

- **Démarrer le serveur RMI** :

Avant de lancer le `BagOfTasks`, assurez-vous que le serveur RMI est actif. Démarrez le registre RMI sur le port par défaut (1099) avec la commande suivante dans le répertoire `bin` :
> `rmiregistry &`

Cette commande lance le registre RMI en arrière-plan.

- **Démarrer le serveur BagOfTasks** :

Lancez le serveur `BagOfTasks` pour gérer les tâches et les workers. Exécutez la commande suivante dans le répertoire racine du projet :
> `java -classpath "lib/ojdbc8.jar:bin" serveur.BagOfTasks`

Le serveur `BagOfTasks` sera maintenant en attente de tâches et de heartbeats des workers.

- **Démarrer les Workers** :

Lancer le `WorkerManager` pour créer 10 workers. Chaque worker s'enregistre automatiquement auprès du serveur et commence à envoyer des heartbeats pour sognaler qu'il est actif. Exécutez la commande suivante dans le répertoire racine du projet :
> `java -classpath "lib/ojdbc8.jar:bin" worker.WorkerManager`

- **Démarrer un client** :

Le client soumet des tâches et reçoit les résultats via un callback. Pour lancer un client, exécutez la commande suivante dans le répertoire racine du projet :
> `java -classpath "lib/ojdbc8.jar:bin" client.Client`

Le client propose un menu interactif permettant de choisir différentes tâches (dépôt, retrait, consultation de solde, création d'assurance, etc.). Suivez les instruction pour soumettre une tâche au `BagOfTasks`.


### Simulation de pannes
Le projet simule les pannes de workers en interrompant aléatoirement l'exécution d'une tâche dans `WorkerImpl`. Cette panne est détectée par le `BagOfTasks` grâce à l'absence de heartbeats, ce qui entraîne la réassignation de la tâche.


### Structure du code
Répertoires `src` :
- **src/client** : contient le client et le callback.
- **src/serveur** : contient `BagOfTasks`, le serveur principal de gestion des tâches.
- **src/worker** : contient `WorkerImpl` et `WorkerManager` pour l'exécution des tâches.
- **src/partage** : contient les interfaces partagées entre les composants (`Task`, `Callback`, `Bot`, etc.).
- **src/tachesmono** : contient les tâches qui sont exécutées sur une seule SGBD.
- **src/tachesmulti** : contient les tâches qui sont exécutées sur deux SGBD.

Répertoire `bin` :
- Les classes compilées seront placées dans ce répertoires.

Répertoire `lib` :
- contient le `ojdbc8.jar`.

`InsertData.java` et `InsertDatabdd2.java` pour créer et remplir les tables des deux SGBD.