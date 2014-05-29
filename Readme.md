### tabula

First there's table types. They represent the static immutable data about a table: the region, the name and the key conf.


#### table ops

For each table type you define item types, which are groups of attributes that you want to retrieve from it; each of these item types represents a group of attributes. Given a value of the table key (hash or composite) you can retrieve the corresponding item. Writing items is done in the same way. For query ops in tables with range keys, you get as a result a list of items, plus a paging pointer.
