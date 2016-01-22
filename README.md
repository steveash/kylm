# Kyoto Language Modeling toolkit (Kylm)

This is the Kyoto Language Modeling toolkit (Kylm), a language modeling toolkit written in Java.  This is a fork of the original code that has been cleaned up and prepared for production use under the LGPL license. The original code base is here: https://github.com/neubig/kylm

## Usage
To use this in your project, just use these maven coordinates:

```xml
<dependency>
  <groupId>com.github.steveash.kylm</groupId>
  <artifactId>kylm</artifactId>
  <version>1.0.0</version>
</dependency
```

Then you can train a language model (e.g. 8-order, kneser-ney smoothing) like this:

```java
KNSmoother smoother = new KNSmoother();
smoother.setSmoothUnigrams(true);
NgramLM lm = new NgramLM(8, smoother);
Iterable<String[]> trainInput = // make "sentences" to train from, each sentence is a String[] of tokens
lm.trainModel(trainInput);
```

You can serialize the `lm` instance and later use it to score candidate sentences like:

```java
lm.getSentenceProbNormalized(gramSeq)
```

## Changes from the original github project
* Available on Maven Central
* Doesn't write to stdout/stderr directly for logging, goes through slf4j
* Uses Java2+ collections instead of Vector (in most places)
* Uses correct equals/hashcode idioms
* Minor convention/style updates (package names)

Unfortunately, these are all fairly invasive changes, and the original codebase was pretty cold. Thus the fork.

## Roadmap for additional changes
* Refactor to make things Thread Safe that should be (and document those that arent)
* Improve performance by using primitive collections where important
* Finish removing the rest of the Vector classes
* Update the serialization formats for everything. In some places it's building huge strings in memory and then calling writeObject on that. Odd.
