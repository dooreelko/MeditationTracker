package doo.util.functional;

public interface Folder<TSource, TResult> {
	TResult fold(TResult accumulator, TSource current);
}