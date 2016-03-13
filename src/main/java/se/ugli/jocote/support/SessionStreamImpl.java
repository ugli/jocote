package se.ugli.jocote.support;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.SessionStream;

class SessionStreamImpl implements SessionStream {

    private final Stream<byte[]> stream;
    private final SessionIterator<byte[]> sessionIterator;

    SessionStreamImpl(final Stream<byte[]> stream, final SessionIterator<byte[]> sessionIterator) {
        this.stream = stream;
        this.sessionIterator = sessionIterator;
    }

    @Override
    public Stream<byte[]> filter(final Predicate<? super byte[]> predicate) {
        return stream.filter(predicate);
    }

    @Override
    public <R> Stream<R> map(final Function<? super byte[], ? extends R> mapper) {
        return stream.map(mapper);
    }

    @Override
    public IntStream mapToInt(final ToIntFunction<? super byte[]> mapper) {
        return stream.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(final ToLongFunction<? super byte[]> mapper) {
        return stream.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(final ToDoubleFunction<? super byte[]> mapper) {
        return stream.mapToDouble(mapper);
    }

    @Override
    public <R> Stream<R> flatMap(final Function<? super byte[], ? extends Stream<? extends R>> mapper) {
        return stream.flatMap(mapper);
    }

    @Override
    public IntStream flatMapToInt(final Function<? super byte[], ? extends IntStream> mapper) {
        return stream.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(final Function<? super byte[], ? extends LongStream> mapper) {
        return stream.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(final Function<? super byte[], ? extends DoubleStream> mapper) {
        return stream.flatMapToDouble(mapper);
    }

    @Override
    public Stream<byte[]> distinct() {
        return stream.distinct();
    }

    @Override
    public Stream<byte[]> sorted() {
        return stream.sorted();
    }

    @Override
    public Stream<byte[]> sorted(final Comparator<? super byte[]> comparator) {
        return stream.sorted(comparator);
    }

    @Override
    public Stream<byte[]> peek(final Consumer<? super byte[]> action) {
        return stream.peek(action);
    }

    @Override
    public Stream<byte[]> limit(final long maxSize) {
        return stream.limit(maxSize);
    }

    @Override
    public Stream<byte[]> skip(final long n) {
        return stream.skip(n);
    }

    @Override
    public void forEach(final Consumer<? super byte[]> action) {
        stream.forEach(action);
    }

    @Override
    public void forEachOrdered(final Consumer<? super byte[]> action) {
        stream.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return stream.toArray();
    }

    @Override
    public <A> A[] toArray(final IntFunction<A[]> generator) {
        return stream.toArray(generator);
    }

    @Override
    public byte[] reduce(final byte[] identity, final BinaryOperator<byte[]> accumulator) {
        return stream.reduce(identity, accumulator);
    }

    @Override
    public Optional<byte[]> reduce(final BinaryOperator<byte[]> accumulator) {
        return stream.reduce(accumulator);
    }

    @Override
    public <U> U reduce(final U identity, final BiFunction<U, ? super byte[], U> accumulator, final BinaryOperator<U> combiner) {
        return stream.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(final Supplier<R> supplier, final BiConsumer<R, ? super byte[]> accumulator, final BiConsumer<R, R> combiner) {
        return stream.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(final Collector<? super byte[], A, R> collector) {
        return stream.collect(collector);
    }

    @Override
    public Optional<byte[]> min(final Comparator<? super byte[]> comparator) {
        return stream.min(comparator);
    }

    @Override
    public Optional<byte[]> max(final Comparator<? super byte[]> comparator) {
        return stream.max(comparator);
    }

    @Override
    public long count() {
        return stream.count();
    }

    @Override
    public boolean anyMatch(final Predicate<? super byte[]> predicate) {
        return stream.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(final Predicate<? super byte[]> predicate) {
        return stream.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(final Predicate<? super byte[]> predicate) {
        return stream.noneMatch(predicate);
    }

    @Override
    public Optional<byte[]> findFirst() {
        return stream.findFirst();
    }

    @Override
    public Optional<byte[]> findAny() {
        return stream.findAny();
    }

    @Override
    public Iterator<byte[]> iterator() {
        return stream.iterator();
    }

    @Override
    public Spliterator<byte[]> spliterator() {
        return stream.spliterator();
    }

    @Override
    public boolean isParallel() {
        return stream.isParallel();
    }

    @Override
    public Stream<byte[]> sequential() {
        return stream.sequential();
    }

    @Override
    public Stream<byte[]> parallel() {
        return stream.parallel();
    }

    @Override
    public Stream<byte[]> unordered() {
        return stream.unordered();
    }

    @Override
    public Stream<byte[]> onClose(final Runnable closeHandler) {
        return stream.onClose(closeHandler);
    }

    @Override
    public void close() {
        stream.close();
        sessionIterator.close();
    }

    @Override
    public void acknowledgeMessages() {
        sessionIterator.acknowledgeMessages();
    }

    @Override
    public void leaveMessages() {
        sessionIterator.leaveMessages();
    }

}
