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

import se.ugli.jocote.Message;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.SessionStream;

class SessionStreamImpl implements SessionStream, Stream<Message> {

    private final SessionIterator iterator;
    private final Stream<Message> stream;

    SessionStreamImpl(final Stream<Message> stream, final SessionIterator iterator) {
        this.iterator = iterator;
        this.stream = stream;
    }

    @Override
    public Stream<Message> filter(final Predicate<? super Message> predicate) {
        return stream.filter(predicate);
    }

    @Override
    public <R> Stream<R> map(final Function<? super Message, ? extends R> mapper) {
        return stream.map(mapper);
    }

    @Override
    public IntStream mapToInt(final ToIntFunction<? super Message> mapper) {
        return stream.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(final ToLongFunction<? super Message> mapper) {
        return stream.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(final ToDoubleFunction<? super Message> mapper) {
        return stream.mapToDouble(mapper);
    }

    @Override
    public <R> Stream<R> flatMap(final Function<? super Message, ? extends Stream<? extends R>> mapper) {
        return stream.flatMap(mapper);
    }

    @Override
    public IntStream flatMapToInt(final Function<? super Message, ? extends IntStream> mapper) {
        return stream.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(final Function<? super Message, ? extends LongStream> mapper) {
        return stream.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(final Function<? super Message, ? extends DoubleStream> mapper) {
        return stream.flatMapToDouble(mapper);
    }

    @Override
    public Stream<Message> distinct() {
        return stream.distinct();
    }

    @Override
    public Stream<Message> sorted() {
        return stream.sorted();
    }

    @Override
    public Stream<Message> sorted(final Comparator<? super Message> comparator) {
        return stream.sorted(comparator);
    }

    @Override
    public Stream<Message> peek(final Consumer<? super Message> action) {
        return stream.peek(action);
    }

    @Override
    public Stream<Message> limit(final long maxSize) {
        return stream.limit(maxSize);
    }

    @Override
    public Stream<Message> skip(final long n) {
        return stream.skip(n);
    }

    @Override
    public void forEach(final Consumer<? super Message> action) {
        stream.forEach(action);
    }

    @Override
    public void forEachOrdered(final Consumer<? super Message> action) {
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
    public Message reduce(final Message identity, final BinaryOperator<Message> accumulator) {
        return stream.reduce(identity, accumulator);
    }

    @Override
    public Optional<Message> reduce(final BinaryOperator<Message> accumulator) {
        return stream.reduce(accumulator);
    }

    @Override
    public <U> U reduce(final U identity, final BiFunction<U, ? super Message, U> accumulator, final BinaryOperator<U> combiner) {
        return stream.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(final Supplier<R> supplier, final BiConsumer<R, ? super Message> accumulator, final BiConsumer<R, R> combiner) {
        return stream.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(final Collector<? super Message, A, R> collector) {
        return stream.collect(collector);
    }

    @Override
    public Optional<Message> min(final Comparator<? super Message> comparator) {
        return stream.min(comparator);
    }

    @Override
    public Optional<Message> max(final Comparator<? super Message> comparator) {
        return stream.max(comparator);
    }

    @Override
    public long count() {
        return stream.count();
    }

    @Override
    public boolean anyMatch(final Predicate<? super Message> predicate) {
        return stream.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(final Predicate<? super Message> predicate) {
        return stream.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(final Predicate<? super Message> predicate) {
        return stream.noneMatch(predicate);
    }

    @Override
    public Optional<Message> findFirst() {
        return stream.findFirst();
    }

    @Override
    public Optional<Message> findAny() {
        return stream.findAny();
    }

    @Override
    public Iterator<Message> iterator() {
        return stream.iterator();
    }

    @Override
    public Spliterator<Message> spliterator() {
        return stream.spliterator();
    }

    @Override
    public boolean isParallel() {
        return stream.isParallel();
    }

    @Override
    public Stream<Message> sequential() {
        return stream.sequential();
    }

    @Override
    public Stream<Message> parallel() {
        return stream.parallel();
    }

    @Override
    public Stream<Message> unordered() {
        return stream.unordered();
    }

    @Override
    public Stream<Message> onClose(final Runnable closeHandler) {
        return stream.onClose(closeHandler);
    }

    @Override
    public void close() {
        stream.close();
        iterator.close();
    }

    @Override
    public void ack() {
        iterator.ack();
    }

    @Override
    public void nack() {
        iterator.nack();
    }

    @Override
    public String sessionid() {
        return iterator.sessionid();
    }

}
