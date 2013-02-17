# Juju

Juju is an information extraction framework.

## Terms

Gram
Token
Sentence

Filter
Weighter

## Installation

Juju's Dependencies are handled with Maven (pom.xml).

### SBT

Add the following lines to your build.sbt.

```scala
resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies += "fi.metropolia.ereading" % "Juju" % "0.0.1-SNAPSHOT"
```

## Examples

### A simple keyphrase extractor with default weighting (based on Wikipedia's corpus)

```java
import fi.metropolia.mediaworks.juju.syntax.parser.DocumentBuilder;
import fi.metropolia.mediaworks.juju.document.Document;
import fi.metropolia.mediaworks.juju.extractor.keyphrase.KeyphraseExtractor;

String input = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc vitae dui lacus.";

Document document = DocumentBuilder.parseDocument(input, "fi"); // "en" is also available
KeyphraseExtractor extractor = new KeyphraseExtractor(document);

return extractor.process()
```

Calling ```process()``` will return a ```Map<Grams, Double>```. Grams represent a word and the latter it's frequency/weight.
