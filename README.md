# Muramasa

Muramasa is a [datomic](http://www.datomic.com) interface for git, primarily for analytics and mining.

It provides a single function `sync!`, which syncs a git repository and a datomic db.

After this, the db can be queried independently using Datomic apis.

It also provides library functions for common queries.

## License

Copyright © 2016 Navgeet Agrawal

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
