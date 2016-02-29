# Kyoto Language Modeling toolkit (Kylm)

This is the Kyoto Language Modeling toolkit (Kylm), a language modeling toolkit written in Java.  This is a fork of the original code that has been cleaned up and prepared for production use under the LGPL license. The original code base is here: https://github.com/neubig/kylm

## Usage
To use this in your project, just use these maven coordinates:

```xml
<dependency>
  <groupId>com.github.steveash.kylm</groupId>
  <artifactId>kylm</artifactId>
  <version>1.1.0</version>
</dependency>
```

Then you can train a language model (e.g. 8-order, kneser-ney smoothing) like this:

```java
KNSmoother smoother = new KNSmoother();
smoother.setSmoothUnigrams(true);
NgramLM lm = new NgramLM(8, smoother);
Iterable<String[]> trainInput = // make "sentences" to train from, each sentence is a String[] of tokens
lm.trainModel(trainInput);
```

You probably don't want to serialize the `lm` instance and use that to score things at runtime directly, because it isn't thread safe and has some ineffeciencies with large data sets.  You can convert this to a more compact, thread-safe form by

```java
ImmutableLM safeLm = new ImmutableLMConverter().convert(lm);
```

The `safeLm` is serializable and safe to use across threads at runtime.  You can get the sentence score by calling:

```java
List<String> sentence = Arrays.toList("see", "skip", "run");
double score = lm.sentenceProbNormalized(sentence);
```

## Changes from the original github project
* Adds an ImmutableLM class and ImmutableLMConverter that allows you to take a trained NGramLM and convert it into an immutable, serializable version that is thread safe for use at runtime
* Available on Maven Central
* Doesn't write to stdout/stderr directly for logging, goes through slf4j
* Uses Java2+ collections instead of Vector (in most places)
* Uses correct equals/hashcode idioms
* Minor convention/style updates (package names)

Unfortunately, these are all fairly invasive changes, and the original codebase was pretty cold. Thus the fork.

## Roadmap for additional changes
* ~~Refactor to make things Thread Safe that should be (and document those that arent)~~ _done in 1.1.0_
* ~~Improve performance by using primitive collections where important~~ _done in 1.1.0_
* Finish removing the rest of the Vector classes
* ~~Update the serialization formats for everything. In some places it's building huge strings in memory and then calling writeObject on that. Odd.~~ _done in 1.1.0_
