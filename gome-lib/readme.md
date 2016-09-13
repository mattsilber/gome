# gtools
A collection of Java-based tools for our software

##### Properties and Configurations
gtools comes built with a `Properties` system centered around a configation file supplied at build time, and selected by Gradle based on your build target.

To supply properties to the system, simply add them the `environment.config` file that resides in your flavor's `resources` folder (e.g. /src/main/resources).

See [src/test/configuration.config](https://github.com.guardanis/gtools/blob/master/src/test/resources/environment.config) for more examples.

Properties can then very simply be accessed via:

```java
Properties.getInstance()
    .getString("your_key", "some_default_value);
```

##### Network Requests
gtools now includes a consolidated networking system allowing for pretty monadic requests:

```java
new ApiRequest<SomeObject>(ConnectionType.GET, "some/api/endpoint")
    .setData(new JSONObject())
    .setResponseParser(request -> buildSomeObject(response.getResponseJson())
    .onSuccess(result -> doSomething(result))
    .onFail(errors -> showErrors(errors))
    .execute();
```

The `ApiRequest` class is a helper already preconfigured for basic Jackpocket connections (based on the supplied environment.config file). For more detailed control, look into the abstract `WebRequest` class.

##### ListUtils
This is just a helper class I've been playing around with for chaining list augmentations in places where Observables felt like overkill. It supports some basic functions like map, reduce, filter, zipWith, take, etc.

Let's say I have a list of Strings, which I want to convert to integers, multiply by 100, select only those > 300, and then sum those values (why? Idk, it's an example):

```java
String[] values = new String[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
int sum = ListUtils.from(values)
        .map(v -> Interger.parseInt(v) * 100)
        .filter(v -> v > 300)
        .reduce(0, (last, current) -> last + current);
```

##### Other notables:

* *Logger* - A logging helper implementing sfl4j/log4j
* *FontHelper* - A helper for managing typefaces / font families.
* *TextFileHelper* - A helper for reading text files
* *MappedValuesHelper* - A helper for loading mapped values from text files (e.g. Properties)

