package com.github.steveash.kylm.model.ngram.writer;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import com.github.steveash.kylm.model.ngram.NgramLM;
import com.github.steveash.kylm.model.ngram.NgramWalker;
import com.github.steveash.kylm.model.ngram.WalkerVisitor;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.io.CharSink;
import com.google.common.primitives.Floats;

/**
 * @author Steve Ash
 */
public class ArpaNgramWriter2 {

    public void write(NgramLM model, CharSink sink) {
        try (Writer writer = sink.openBufferedStream()) {
            writeWith(model, writer);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private void writeWith(final NgramLM model, Writer writer) {
        final PrintWriter pw = new PrintWriter(writer);
        final Joiner joiner = Joiner.on(' ');
        new NgramWalker(model).walk(new WalkerVisitor() {
            int lastOrder = -1;
            @Override
            public void visit(int order, List<String> symbols, float score, float backoffScore, boolean hasChildren, boolean isLastOrder) {
                if (order != lastOrder) {
                    pw.println("\\" + order + "-grams: ");
                    lastOrder = order;
                }
                printFloat(pw, score);
                pw.print("\t");
                pw.print(joiner.join(symbols));
                pw.print("\t");
                printFloat(pw, backoffScore);
                pw.println();
            }

            private void printFloat(PrintWriter pw, float value) {
                if (Floats.isFinite(value)) {
                    pw.print(value);
                } else {
                    pw.print(-99.0f);
                }
            }
        });
    }
}
