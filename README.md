# About
This is an embeddable Java/Scala library for deduplicating text documents. The aim is to provide an assortment of deduplication algorithms unified by a single easy-to-use interface.

Additionally, this library is designed for being used in distributed systems. It assumes that multiple highly-concurrent processes will need access to the global state of deduplication information. Therefore, it provides mechanisms for persisting the deduplication state in a variety of different ways to make integration with distributed systems easier.

# Roadmap
1. Locality sensitive hashing (LSH) implementation
2. Redis support
3. Dockerized REST API

# Acknowledgements
The algorithms implemented in this library are not novel, they are adapted or built on top of other people's hard work and it is therefore important to credit them.

### Shingleprint Deduplication
This is a Scala adaption of the algorithm originally proposed by Dustin Boswell in his post [Real-Time Document DeDuplication](https://medium.com/@dustinboswell/real-time-document-deduplication-d5fb5982812).

The original Python implementation can be found [on GitHub](https://gist.github.com/dustinboswell/71c07324965783190a24fb4fb677ed70).

# Disclaimer
This library uses [ChronicleMap](https://github.com/OpenHFT/Chronicle-Map) for one of the implementations of the persistent deduplication state. While ChronicleMap is a powerful library and an excellent tool, the developers seem to think that it is okay to send telemetry to Google Analytics [without explicit consent](https://github.com/OpenHFT/Chronicle-Map/blob/ea/DISCLAIMER.adoc). Therefore, it is highly recommended that in your applications you make sure to set the system property `chronicle.announcer.disable` to `true`.

It should not be a "paid" feature to turn off spying.
