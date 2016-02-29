package com.github.steveash.kylm.reader;

import java.util.ArrayList;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Steve Ash
 */
public class Filters {

    private static final CharMatcher splitPunc = CharMatcher.anyOf(",.!?;-&");
    private static final CharMatcher stripPunc = CharMatcher.JAVA_LETTER_OR_DIGIT
            .or(splitPunc)
            .or(CharMatcher.WHITESPACE)
            .negate()
            .precomputed();
    private static Function<String[], String[]> function = new Function<String[], String[]>() {
        @Override
        public String[] apply(String[] input) {
            ArrayList<String> result = Lists.newArrayListWithCapacity(input.length);
            for (String word : input) {
                word = word.toLowerCase();
                word = stripPunc.removeFrom(word);
                word = CharMatcher.WHITESPACE.trimAndCollapseFrom(word, ' ');
                int start = 0;
                for (int i = 0; i < word.length(); i++) {
                    if (splitPunc.matches(word.charAt(i))) {
                        if (start < i) {
                            result.add(word.substring(start, i));
                        }
                        result.add(Character.toString(word.charAt(i)));
                        start = i + 1;
                    }
                }
                if (start < word.length()) {
                    result.add(word.substring(start));
                }
            }
            return result.toArray(new String[0]);
        }
    };

    public static Iterable<String[]> standard(Iterable<String[]> input) {
        return Iterables.transform(input, function);
    }

    public static String[] standardSentence(String... word) {
        return function.apply(word);
    }
}
