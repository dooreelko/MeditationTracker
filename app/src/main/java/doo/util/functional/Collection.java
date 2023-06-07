package doo.util.functional;

import java.util.List;

public class Collection {
	public static <TSource, TResult> TResult fold(List<TSource> list, TResult initial, Folder<TSource, TResult> folderOp) {
		for (TSource val : list){
			initial = folderOp.fold(initial, val);
		}
		
		return initial;
	}

}
