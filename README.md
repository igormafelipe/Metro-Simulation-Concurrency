# Metro-Simulation-Concurrency - Tufts University
A metro simulator where metros work in concurrency. Passenger can board and unboard, and metros move in specific paths. Simulation ends once all passengers have reached their destination.

# Multi Thread Design
Locks are on stations. Each station has two locks, one for passengers and one for trains. These locks are reentrant locks, therefore they also have conditions. There are two conditions, one for trains and one for passengers.

A train first attemps to acquire the lock of the next station it will move to. While the next station is occupied, this train will await. Once the next station is freed, the current train will acquire the lock of the current station it is at, and the lock of the passengers in the next station it will move to. It will then move to the next station. After moving, it will signall all passengers in the next station to awake, then all trains trying to get to the next station will also awake (these will not be able to move, however, because the station is occupied. They will have to wait until the station is empty), and will notify all trains trying to get to the station the train just left that the station is empty.

The passengers work as follows:

They acquire the lock of the current station or the next station they are trying to get to depending if they are boarding or deboarding.

If a passenger is boarding, they acquire the lock of the current station. While the train at the current station is not a train that takes them to their next desired location, they await. Once such train arrives, the train will notify them upon arrival, and they will board the train.

If a passenger is trying to deboard, they will acquire the lock of the next station they are trying to get to. While the train they are currently boarding is not at the next station, they will await. Once the train arrives, the train will notify the passenger, and they will deboard.

The lock of the passenger is unlocked after all updates are made.
